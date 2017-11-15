package com.example.hemanth.terra_android.model;

/**
 * Created by dev on 9/13/17.
 */

public class MetricBean{
    public MetricBean(String id, String data, String metricType) {
        this.id = id;
        this.data = data;
        this.metricType = metricType;
    }
    public String id;
    public String data;
    public String metricType;
}
