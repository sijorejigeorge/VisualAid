# VisualAid: Enhancing Accessibility for Visually Impaired Individuals

![Award Badge](https://www.w4a.info/2025/awards/)  
Delegates Award Winner – W4A 2025 Awards

## Overview

VisualAid is an AI-powered Android application designed to improve accessibility for visually impaired individuals by providing real-time assistance with object recognition, image captioning, OCR, and voice interaction. The app transforms a smartphone into a smart assistant that narrates the environment through descriptive audio, helping users interpret and navigate their surroundings independently.

This project was presented at the [21st International Web for All Conference (W4A’24)](https://www.w4a.info/2025/awards/) and received recognition for its impact and innovation.

## Features

- 📷 Real-Time Object Detection using YOLOv11
- 📝 Image Captioning via Salesforce BLIP
- 🔤 Optical Character Recognition (OCR) using Tesseract
- ❓ Visual Question Answering (VQA) with ViLT
- 🗣️ Text-to-Speech (TTS) and Speech-to-Text (STT) Integration
- 🌐 Multi-language support: English and Spanish
- 🎙️ Voice-guided tutorials and commands

## Architecture

The application is structured as a client-server model:

### 📱 Android Frontend
- Built with accessibility in mind using Android's `TextToSpeech` and `SpeechRecognizer`
- Voice-guided instructions and command flow
- API-based interaction with backend Flask server

### 🧠 Flask Backend
- Image Processing: OpenCV for preprocessing, YOLOv11x for object detection
- Caption Generation: BLIP (blip-image-captioning-base)
- Text Recognition: Tesseract OCR
- Query Processing: NLTK for semantic parsing, ViLT for answering visual questions
- Fast and efficient response pipeline for real-time interaction

## System Flow

1. User captures an image or speaks a command.
2. The image is sent via API to the backend.
3. The backend performs:
   - Object detection (YOLO)
   - Scene captioning (BLIP)
   - OCR if requested
4. User can ask follow-up questions about the image.
5. The backend uses ViLT to answer and responds via TTS.

## Installation

### Android App
Clone the repo and build the app in Android Studio:
```bash
git clone https://github.com/your-org/VisualAid-App.git
