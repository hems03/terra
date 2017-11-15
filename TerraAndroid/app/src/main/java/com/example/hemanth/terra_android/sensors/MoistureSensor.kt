package com.example.hemanth.terra_android.sensors

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManagerService
import java.io.IOException

/**
 * Created by hemanth on 9/12/17.
 */
class MoistureSensor {
    private val TAG = "Sensor Data"

    private val OBSTACLE_SENSOR_GPIO_PIN_NAME = "GPIO_34"

    private var mObstacleSensorGpio: Gpio

    init {
        val service = PeripheralManagerService()
        Log.d(TAG, "Available GPIOs: " + service.gpioList)
        mObstacleSensorGpio = service.openGpio(OBSTACLE_SENSOR_GPIO_PIN_NAME)
    }


    fun setUpSensor(): String {
        try {
            mObstacleSensorGpio.setDirection(Gpio.DIRECTION_IN)
            mObstacleSensorGpio.setActiveType(Gpio.ACTIVE_HIGH)
            // Step 3. Enable edge trigger events.
            // Step 4. Set Active type to LOW, then it will trigger HIGH events
            mObstacleSensorGpio.registerGpioCallback(mCallback)
            return mObstacleSensorGpio.value.toString() + ""
        } catch (e: IOException) {
            Log.e(TAG, "Error on sensor", e)
            return "false"
        }

    }

    private val mCallback = object : GpioCallback() {
        override fun onGpioEdge(gpio: Gpio?): Boolean {
            return true
        }
    }


    fun destroy() {
        // unregister from the callback
        mObstacleSensorGpio.unregisterGpioCallback(mCallback)
        try {
            mObstacleSensorGpio.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error on sensor", e)
        }

    }
}