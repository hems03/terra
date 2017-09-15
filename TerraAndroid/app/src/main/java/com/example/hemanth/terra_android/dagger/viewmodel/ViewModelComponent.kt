package com.example.hemanth.terra_android.dagger.viewmodel

import com.example.hemanth.terra_android.ThingViewModel
import com.example.hemanth.terra_android.dagger.app.AppModule
import com.example.hemanth.terra_android.dagger.service.ServiceModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by dev on 9/13/17.
 */

@Singleton
@Component(modules = arrayOf(ServiceModule::class,AppModule::class))
interface ViewModelComponent{
    fun inject(model:ThingViewModel)

    interface Injectable{
        fun inject(component: ViewModelComponent)
    }
}