package com.example.hemanth.terra_android

import android.app.Application
import com.example.hemanth.terra_android.dagger.app.AppModule

import com.example.hemanth.terra_android.dagger.app.ApplicationComponent
import com.example.hemanth.terra_android.dagger.app.DaggerApplicationComponent
import com.example.hemanth.terra_android.dagger.service.ServiceModule
import com.example.hemanth.terra_android.dagger.viewmodel.DaggerViewModelComponent
import com.example.hemanth.terra_android.dagger.viewmodel.ViewModelComponent
import kotlin.properties.Delegates

/**
 * Created by hemanth on 9/12/17.
 */
 class App:Application(){
    var mApplicationComponent: ApplicationComponent?=null
    var mViewModelComponent: ViewModelComponent?=null

    override fun onCreate() {
        super.onCreate()
    }

    fun getApplicationComponent(): ApplicationComponent {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .appModule(AppModule(this))
                    .serviceModule(ServiceModule())
                    .build()
        }
        return mApplicationComponent!!
    }

    fun getViewModelComponent():ViewModelComponent{
        if(mViewModelComponent==null){
            mViewModelComponent=DaggerViewModelComponent.builder()
                    .appModule(AppModule(this))
                    .serviceModule(ServiceModule())
                    .build()
        }
        return mViewModelComponent!! //TODO: BETTER WAY TO DO THIS
    }


}