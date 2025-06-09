package com.example.aiblindapp;

import com.example.aiblindapp.pojo.ImageQuery;
import com.example.aiblindapp.pojo.ImageResponse;
import com.example.aiblindapp.pojo.MultipleResource;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

interface APIInterface {

    @GET("/api/unknown")
    Call<MultipleResource> doGetListResources();

    @Multipart
    @POST("/process-image")
    Call<ImageResponse> processImage(@Part MultipartBody.Part image);

    @Multipart
    @POST("/process-query")
    Call<ImageQuery> processQuery(@Part MultipartBody.Part query);
}