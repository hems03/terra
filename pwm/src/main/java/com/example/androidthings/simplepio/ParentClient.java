package com.example.androidthings.simplepio;

import com.example.androidthings.simplepio.networking.SendData;

import retrofit2.Retrofit;

/**
 * Created by aditya on 9/9/17.
 */

public class ParentClient {
    Retrofit retrofit;
    SendData service;

    public ParentClient(String baseURL) {
        retrofit = new Retrofit.Builder().baseUrl(baseURL).build();
        service = retrofit.create(SendData.class);
    }

    public void addData(String sensorId, double temperature, double moisture) {
        service.addData(sensorId, temperature, moisture);
    }
}
