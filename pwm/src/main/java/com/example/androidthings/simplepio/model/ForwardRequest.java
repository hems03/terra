package com.example.androidthings.simplepio.model;

import java.util.List;

/**
 * Created by hemanth on 9/9/17.
 */

public class ForwardRequest {
    String type="forward";
    List<String>prevVisited;

    public ForwardRequest( List<String> prevVisited) {
        this.prevVisited = prevVisited;
    }

    public String getType() {
        return type;
    }

    public List<String> getPrevVisited() {
        return prevVisited;
    }
}
