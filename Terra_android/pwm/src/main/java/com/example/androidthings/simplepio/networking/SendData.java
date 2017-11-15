package com.example.androidthings.simplepio.networking;

import com.example.androidthings.simplepio.model.MetricRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;

/**
 * Created by aditya on 9/9/17.
 */

public interface SendData {
    @POST("/sensordata/")
    Call<ResponseBody> addData (@Body MetricRequest metricRequest);
}