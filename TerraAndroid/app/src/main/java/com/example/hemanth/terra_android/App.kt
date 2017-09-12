package com.example.hemanth.terra_android

import android.app.Application
import com.example.hemanth.DiasporaAndroid.Dagger.app.AppModule
import com.example.hemanth.DiasporaAndroid.Dagger.app.ApplicationComponent
import com.example.hemanth.DiasporaAndroid.Dagger.app.DaggerApplicationComponent
import com.example.hemanth.DiasporaAndroid.Dagger.service.ServiceModule
import kotlin.properties.Delegates

/**
 * Created by hemanth on 9/12/17.
 */
 class App:Application(){
    var mApplicationComponent: ApplicationComponent by Delegates.notNull()

    override fun onCreate() {
        super.onCreate()
    }

    fun getApplicationComponent(): ApplicationComponent{
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .appModule(AppModule(this))
                    .serviceModule(ServiceModule())
                    .build()
        }
        return mApplicationComponent

    }
}