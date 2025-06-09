package com.example.aiblindapp.pojo;

import java.util.List;

public class ImageResponse {
    private String image;

    private List<String> labels;
    private String additional_text;

    public String getImage() {
        return image;
    }


    public List<String> getLabels() {
        return labels;
    }

    public String getAdditionalText() {
        return additional_text;
    }
}
