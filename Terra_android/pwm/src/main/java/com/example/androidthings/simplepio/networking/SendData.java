package com.example.androidthings.simplepio.networking;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.POST;

/**
 * Created by aditya on 9/9/17.
 */

public interface SendData {
    @POST("/sensordata")
    Call<String> addData (@Field("sensor_id") String sensor_id, @Field("temperature") double temperature, @Field("moisture") double moisture);
}