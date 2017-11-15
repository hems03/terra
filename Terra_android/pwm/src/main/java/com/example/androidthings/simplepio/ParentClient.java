package com.example.androidthings.simplepio;

import android.util.Log;

import com.example.androidthings.simplepio.model.MetricRequest;
import com.example.androidthings.simplepio.networking.SendData;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aditya on 9/9/17.
 */

public class ParentClient {
    Retrofit retrofit;
    SendData dataService;

    public ParentClient(String dataURL) {
        retrofit = new Retrofit.Builder()
                .baseUrl(dataURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dataService = retrofit.create(SendData.class);
    }

    public void addData(String sensorId, double temperature, double moisture) {

        dataService.addData(new MetricRequest(sensorId,temperature,moisture)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Parenghhvg",response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.d("hello dude",throwable.toString());
            }
        });
    }

}
