package com.example.androidthings.simplepio.model;

import java.util.List;

/**
 * Created by hemanth on 9/9/17.
 */

public class BackwardResponse {
    String type="backward";
    List<MetricBean> metrics;
    String rootId;
    List<String>prevVisited;

    public BackwardResponse(List<MetricBean> metrics,List<String> prevVisited) {
        this.metrics = metrics;
        this.prevVisited=prevVisited;

    }

    public class MetricBean{
        MetricBean(String id, String data, String metricType) {
            this.id = id;
            this.data = data;
            this.metricType = metricType;
        }
        public String id;
        public String data;
        public String metricType;
    }

    public String getType() {
        return type;
    }

    public List<MetricBean> getMetrics() {
        return metrics;
    }

    public String getRootId() {
        return rootId;
    }

    public List<String> getPrevVisited() {
        return prevVisited;
    }

    public void insertMetricBean(String id, String data, String metricType) {
        metrics.add(new MetricBean(id, data, metricType));
    }
}
