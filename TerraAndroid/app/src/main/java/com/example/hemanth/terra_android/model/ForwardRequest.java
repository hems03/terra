package com.example.hemanth.terra_android.model;

import java.util.List;
import java.util.UUID;

/**
 * Created by hemanth on 9/9/17.
 */

public class ForwardRequest {
    String type="forward";
    String uuid;
    List<String>prevVisited;

    public ForwardRequest( List<String> prevVisited,String uuid) {
        this.prevVisited = prevVisited;
        this.uuid=uuid;
    }

    public String getType() {
        return type;
    }

    public List<String> getPrevVisited() {
        return prevVisited;
    }

    public String getUuid() {
        return uuid;
    }
}
