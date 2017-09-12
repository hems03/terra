package com.example.hemanth.DiasporaAndroid.Dagger.controller

import com.example.hemanth.DiasporaAndroid.*
import com.example.hemanth.terra_android.NodeActivity
import dagger.Subcomponent

/**
 * Created by hemanth on 8/28/17.
 */


@Subcomponent(modules = arrayOf(ControllerModule::class, ViewMvcModule::class))
interface ControllerComponent {

    fun inject(exampleActivity: NodeActivity)

}