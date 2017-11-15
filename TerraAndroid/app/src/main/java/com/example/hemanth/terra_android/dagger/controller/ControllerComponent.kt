package com.example.hemanth.terra_android.dagger.controller

import com.example.hemanth.terra_android.NodeActivity
import com.example.hemanth.terra_android.ThingViewModel
import dagger.Subcomponent

/**
 * Created by hemanth on 8/28/17.
 */


@Subcomponent(modules = arrayOf(ControllerModule::class, ViewMvcModule::class))
interface ControllerComponent {

    fun inject(exampleActivity: NodeActivity)

    fun inject(viewModel:ThingViewModel)


}