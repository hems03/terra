package com.example.androidthings.simplepio;

import com.example.androidthings.simplepio.networking.SendData;

import retrofit2.Retrofit;

/**
 * Created by aditya on 9/9/17.
 */

public class ParentClient {
    Retrofit retrofit;
    SendData dataService;

    public ParentClient(String dataURL) {
        retrofit = new Retrofit.Builder().baseUrl(dataURL).build();
        dataService = retrofit.create(SendData.class);
    }

    public void addData(String sensorId, double temperature, double moisture) {
        dataService.addData(sensorId, temperature, moisture);
    }

}
