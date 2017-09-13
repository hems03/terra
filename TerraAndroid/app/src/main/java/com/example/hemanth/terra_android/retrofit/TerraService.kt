package com.example.hemanth.terra_android.retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.POST

/**
 * Created by hemanth on 9/12/17.
 */
interface TerraService {

    @POST("/sensordata")
    fun addData(@Field("sensor_id") sensor_id: String, @Field("temperature") temperature: Double, @Field("moisture") moisture: Double): Call<String>
}