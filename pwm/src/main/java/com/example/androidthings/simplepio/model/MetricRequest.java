package com.example.androidthings.simplepio.model;

import retrofit2.http.Field;

/**
 * Created by vineethpuli on 9/10/17.
 */

public class MetricRequest {
     String sensor_id;
     double temperature;
     double moisture;

    public MetricRequest(String sensor_id, double temperature, double moisture) {
        this.sensor_id = sensor_id;
        this.temperature = temperature;
        this.moisture = moisture;
    }

    public String getSensor_id() {
        return sensor_id;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getMoisture() {
        return moisture;
    }
}
