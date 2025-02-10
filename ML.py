print("Script is running")
import cv2
import argparse
from ultralytics import YOLO
#import supervision as sv
import numpy as np
from flask import Flask, request, jsonify, send_file
from io import BytesIO
import base64
import zlib
import logging
import os
from PIL import Image
import matplotlib.pyplot as plt
from transformers import VisionEncoderDecoderModel, ViTImageProcessor, AutoTokenizer
import torch  # Add this line
from werkzeug.utils import secure_filename
from transformers import Blip2Processor, Blip2ForConditionalGeneration
from transformers import BlipProcessor, BlipForConditionalGeneration
from transformers import ViltProcessor, ViltForQuestionAnswering
import requests
from PIL import Image
import nltk
import re
import pytesseract

from nltk.corpus import stopwords
from nltk.corpus import wordnet        

nltk.download('wordnet')
nltk.download('stopwords')

stop_words = set(stopwords.words('english'))
pytesseract.pytesseract.tesseract_cmd = '' #put path to Tesseract
image_path = ""
detections = ""
bboxes = ""
class_ids = ""
confidences = ""
labels = []
_text = ""

app = Flask(__name__)

# Configure logging
logging.basicConfig(filename='app.log', level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

ZONE_POLYGON = np.array([
    [1, 1],
    [0.5, 0],
    [0.5, 1],
    [0, 1]
])

# Define a directory for safe file uploads (Renamed to secure_upload_folder)
secure_upload_folder = 'uploads'
if not os.path.exists(secure_upload_folder):
    os.makedirs(secure_upload_folder)
app.config['UPLOAD_FOLDER'] = secure_upload_folder

# Load the image captioning model and tokenizer
#model = VisionEncoderDecoderModel.from_pretrained("nlpconnect/vit-gpt2-image-captioning")
#model_name = "Salesforce/blip2-opt-2.7b"
#feature_extractor = ViTImageProcessor.from_pretrained("nlpconnect/vit-gpt2-image-captioning")
tokenizer = AutoTokenizer.from_pretrained("nlpconnect/vit-gpt2-image-captioning")
#Blip_processor = Blip2Processor.from_pretrained(model_name)
#Blip_model = Blip2ForConditionalGeneration.from_pretrained(model_name)
blip_processor = BlipProcessor.from_pretrained("Salesforce/blip-image-captioning-base")
blip_model = BlipForConditionalGeneration.from_pretrained("Salesforce/blip-image-captioning-base")
#vilt_model = ViltForQuestionAnswering.from_pretrained("dandelin/vilt-b32-finetuned-vqa")
#vilt_processor = ViltProcessor.from_pretrained("dandelin/vilt-b32-finetuned-vqa")
vilt_model = ViltForQuestionAnswering.from_pretrained("dandelin/vilt-b32-finetuned-vqa")
vilt_processor = ViltProcessor.from_pretrained("dandelin/vilt-b32-finetuned-vqa")

#device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
#model.to(device)

max_length = 16
num_beams = 4
gen_kwargs = {"max_length": max_length, "num_beams": num_beams}

def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="YOLOv8 live")
    parser.add_argument("--image-path", required=True, help="Path to the input image")
    args = parser.parse_args()
    return args
_yolo = YOLO("yolov8n.pt")

def predict_step(image_paths):
    # Step 4: Load and preprocess the image
    image = []
    for image_path in image_paths:
        i_image = Image.open(image_path)
        if i_image.mode != "RGB":
            i_image = i_image.convert(mode="RGB")
        image.append(i_image)

    # Step 5: Prepare inputs for BLIP
    inputs = blip_processor(images=image, return_tensors="pt")

    # Step 6: Generate a caption with BLIP
    output = blip_model.generate(**inputs)
    caption = blip_processor.decode(output[0], skip_special_tokens=True)
    return caption

def compress_image(image):
    try:
        logger.info("Compressing image")
        # Compress the image data before sending
        compressed_image = zlib.compress(image.tobytes())
        logger.info("Image compression completed")
        return compressed_image

    except Exception as e:
        logger.error(f"Error compressing image: {str(e)}")
        return None



def user_query_synonyms(user_query):

  synonyms = []
  for synset in wordnet.synsets('user_query'):
      for hypernym in synset.hypernyms():
          for lemma in hypernym.lemmas():
              synonyms.append(lemma.name())
  for syn in wordnet.synsets(user_query):
    for lemma in syn.lemmas():
        synonyms.append(lemma.name()) 
  for w in synonyms:
    if re.search('man', w, re.IGNORECASE):
      synonyms.append('person')
  synonyms = list(set(synonyms)) 
  #print("Synonyms for", user_query + ":", synonyms)
  return synonyms

def element_level_caption(user_query, image_path, filtered_words):
  _text = ""
  image = cv2.imread(image_path)
  if image is None:
     raise ValueError(f"Failed to load image from path: {image_path}")
  image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
  #image_pil = Image.fromarray(image_rgb)
  cropped_object = ""
  #cv2.imwrite("out.jpg", image)
  #print(user_query)
  #print(class_ids)
  #synonyms = user_query_synonyms(user_query)
  for bbox, class_id, confidence in zip(bboxes, class_ids, confidences):
    x1, y1, x2, y2 = map(int, bbox)
    #print(_yolo.model.names[class_id])
    if _yolo.model.names[class_id] == user_query:
            #print(_yolo.model.names[class_id])
            cropped_object = image_rgb[y1:y2, x1:x2]
            break
  cropped_image_pil = Image.fromarray(cropped_object)
  cropped_image_pil.save(os.path.join(app.config['UPLOAD_FOLDER'], "cropped.jpg"))
  if 'text' in filtered_words or 'written' in filtered_words:
        _text = pytesseract.image_to_string(cropped_image_pil)
  # Step 5: Prepare inputs for BLIP
  inputs = blip_processor(images=cropped_image_pil, return_tensors="pt")

  # Step 6: Generate a caption with BLIP
  output = blip_model.generate(**inputs)
  caption = blip_processor.decode(output[0], skip_special_tokens=True)
  #print(caption)
  # Step 7: Print the generated caption
  if caption is None:
    caption = ""
  if _text is None:
    text = ""
  return caption, _text


def element_level(filtered_words, image_np):
    _text = ""
    caption = ""
    #print(filtered_words)
    #print(labels)
    if labels:
        label_set = list(set(labels))
        for l in label_set: 
            _l = re.sub(r"\s*\d+(\.\d+)?", "", l)
            #print(_l)
            for w in filtered_words:
                if _l in user_query_synonyms(w):
                    #print('yes')
                    caption, _text = element_level_caption(_l, image_np, filtered_words)
                    return caption, _text
                    break

        if not caption:
            image = cv2.imread(image_np)
            if image is None:
                raise ValueError(f"Failed to load image from path: {image_np}")
            image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
            if 'text' in filtered_words or 'written' in filtered_words:
                _text = pytesseract.image_to_string(image_rgb)
            if caption is None:
                caption = ""
            if _text is None:
                text = ""
            return caption, _text
    else :
        caption = ""
        _text = ""
        image = cv2.imread(image_np)
        if image is None:
            raise ValueError(f"Failed to load image from path: {image_np}")
        image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        if 'text' in filtered_words or 'written' in filtered_words:
            _text = pytesseract.image_to_string(image_rgb)
        if caption is None:
            caption = ""
        if _text is None:
            text = ""
        return caption, _text

@app.route("/process-image", methods=["POST"])

def process_image():
    global image_path, detections, bboxes, class_ids, confidences, labels
    bboxes = ""
    class_ids = ""
    confidences = ""
    labels = []
    try:
        print("Script is running")
        uploaded_image = request.files['image']
        print(uploaded_image)
        if uploaded_image.filename != '':
            filename = secure_filename(uploaded_image.filename)
            print(filename)
            uploaded_image.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

            image_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            #image = cv2.imdecode(np.fromstring(uploaded_image.read(), np.uint8), cv2.IMREAD_COLOR)
            image = cv2.imread(image_path)
            #image = cv2.rotate(image, cv2.ROTATE_90_CLOCKWISE)
            image_np = np.array(image)

            results = _yolo(image_np)
            # Extract bounding boxes, class IDs, and confidences
            bboxes = results[0].boxes.xyxy.cpu().numpy()
            class_ids = results[0].boxes.cls.cpu().numpy().astype(int)
            confidences = results[0].boxes.conf.cpu().numpy()
            
            # Annotate the image
            for bbox, class_id, confidence in zip(bboxes, class_ids, confidences):
                x1, y1, x2, y2 = map(int, bbox)
                label = f"{_yolo.model.names[class_id]} {confidence:.2f}" #remove confidence if you dont have confidence
                labels.append(label)
                cv2.rectangle(image_np, (x1, y1), (x2, y2), (0, 255, 0), 10)
                cv2.putText(image_np, label, (x1, y1 - 10), cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 255, 0), 8)

            # Display the annotated image

            processed_image_path = os.path.join(app.config['UPLOAD_FOLDER'], "updated.png")
            cv2.imwrite(processed_image_path, image_np)

            success, encoded_image = cv2.imencode(".jpg", image_np)
            if not success:
                return jsonify({"error": "Failed to encode the image"})
            
            # labels_str = ', '.join(labels)

            # Return both image and labels in the response
            # return jsonify({"image": encoded_image.tobytes().decode("latin1"), "labels": labels_str})

            #image_io = BytesIO(encoded_image.tobytes())

            #image_io.seek(0)
            image_io = base64.b64encode(encoded_image.tobytes()).decode("utf-8")
            
            captions = predict_step([image_path])
            print(captions)
            additional_text = "Example additional text."
            #return send_file(image_io, mimetype="image/jpeg")
            #return jsonify({"labels" : labels, "additional_text": captions})
            return jsonify({"image": image_io, "labels" : labels, "additional_text": captions})


            
            

    except Exception as e:
        return jsonify({"error": str(e)})
    

@app.route("/process-query", methods=["POST"])

def process_query():
    cropped_object = ""
    image = ""
    _text = ""
    try:
        print("Script is running")
        user_Q = request.form['query']
        #model_name = "Salesforce/blip2-opt-2.7b"

        image = cv2.imread(image_path)
        image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        image_pil = Image.fromarray(image_rgb)
        #cv2.imwrite("output.jpg", image)
        #Blip_processor = Blip2Processor.from_pretrained(model_name)
        #Blip_model = Blip2ForConditionalGeneration.from_pretrained(model_name)


        #url = image_path
        #image = Image.open(requests.get(url, stream=True).raw)

        # Step 5: Prepare inputs
        question = user_Q

       
        filtered = [word for word in question.split() if word.lower() not in stop_words]
        filtered_words = [word.rstrip("'s").rstrip("s") for word in filtered]
        caption, _text = element_level(filtered_words, image_path)
        
        inputs = vilt_processor(text=question, images=image_pil, return_tensors="pt")

        # Step 6: Perform a forward pass to get the answer
        outputs = vilt_model(**inputs)
        logits = outputs.logits

        # Step 7: Decode the answer
        answer_id = torch.argmax(logits, dim=-1).item()
        answer = vilt_model.config.id2label[answer_id] if answer_id in vilt_model.config.id2label else "Unknown"

        # Step 8: Print the question and answer
        print(f"Question: {question}")
        print(f"Answer: {answer}")
        print(f"caption: {caption}")
        print(f"text: {_text}")

        return jsonify({"additional_text": f"[{answer}] description : {caption} text : {_text}"})

    except Exception as e:
        return jsonify({"error": str(e)})


if __name__ == "__main__":
    print("Script is running")
    app.debug = True
    app.run(host='0.0.0.0', port=5000)

