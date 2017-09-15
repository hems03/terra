package com.example.hemanth.terra_android.dagger.app


import com.example.hemanth.terra_android.dagger.controller.ControllerComponent
import com.example.hemanth.terra_android.dagger.controller.ControllerModule
import com.example.hemanth.terra_android.dagger.controller.ViewMvcModule
import com.example.hemanth.terra_android.dagger.service.ServiceModule
import com.example.hemanth.terra_android.dagger.viewmodel.ViewModelComponent
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