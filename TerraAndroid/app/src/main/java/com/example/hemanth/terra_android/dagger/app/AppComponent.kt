package com.example.hemanth.DiasporaAndroid.Dagger.app

import com.example.hemanth.DiasporaAndroid.Dagger.controller.ControllerComponent
import com.example.hemanth.DiasporaAndroid.Dagger.controller.ControllerModule
import com.example.hemanth.DiasporaAndroid.Dagger.controller.ViewMvcModule
import com.example.hemanth.DiasporaAndroid.Dagger.service.ServiceModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by hemanth on 8/28/17.
 */


@Singleton
@Component(modules = arrayOf( AppModule::class,ServiceModule::class))
interface ApplicationComponent {


    fun newControllerComponent(
            controllerModule: ControllerModule,
            viewMvcModule: ViewMvcModule): ControllerComponent
}