package com.example.androidthings.simplepio.singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by hemanth on 9/9/17.
 */

public class Singletons {
    private static Gson gson=new GsonBuilder().create();

    public static Gson getGson(){
        return gson;
    }


}
