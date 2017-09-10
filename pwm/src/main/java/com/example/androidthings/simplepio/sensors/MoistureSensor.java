package com.example.androidthings.simplepio.sensors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class MoistureSensor {

    private static final String TAG = "Sensor Data";

    private static final String OBSTACLE_SENSOR_GPIO_PIN_NAME = "GPIO_34";

    private Gpio mObstacleSensorGpio;


    public void setUpSensor() {
        PeripheralManagerService service = new PeripheralManagerService();
        Log.d(TAG, "Available GPIOs: " + service.getGpioList());


        try {
            // Step 1. Create GPIO connection.
            mObstacleSensorGpio = service.openGpio(OBSTACLE_SENSOR_GPIO_PIN_NAME);
            // Step 2. Configure as an input.
            mObstacleSensorGpio.setDirection(Gpio.DIRECTION_IN);
            mObstacleSensorGpio.setActiveType(Gpio.ACTIVE_HIGH);
            // Step 3. Enable edge trigger events.
            // Step 4. Set Active type to LOW, then it will trigger HIGH events
            Observable.interval(0,1000, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    Log.d(TAG,Boolean.toString(mObstacleSensorGpio.getValue()));
                }
            });
            // Step 5. Register an event callback.
            mObstacleSensorGpio.registerGpioCallback(mCallback);
        } catch (IOException e) {
            Log.e(TAG, "Error on sensor", e);
        }

    }


    // Step 5. Register an event callback.
    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {

            // Step 6. Return true to keep callback active.
            return true;
        }
    };



    public void destroy() {
        // Step 7. Close the resource
        if (mObstacleSensorGpio != null) {
            // unregister from the callback
            mObstacleSensorGpio.unregisterGpioCallback(mCallback);
            try {
                mObstacleSensorGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on sensor", e);
            }
        }

        // Step 7. Close the resource
    }
}