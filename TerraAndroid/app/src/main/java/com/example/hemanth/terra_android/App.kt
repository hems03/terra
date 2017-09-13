package com.example.hemanth.terra_android

import android.app.Application

import com.example.hemanth.terra_android.dagger.app.ApplicationComponent
import kotlin.properties.Delegates

/**
 * Created by hemanth on 9/12/17.
 */
 class App:Application(){
    var mApplicationComponent: ApplicationComponent by Delegates.notNull()

    override fun onCreate() {
        super.onCreate()
    }

    fun getApplicationComponent(): ApplicationComponent {
        if (mApplicationComponent == null) {

        }
        return mApplicationComponent

    }
}