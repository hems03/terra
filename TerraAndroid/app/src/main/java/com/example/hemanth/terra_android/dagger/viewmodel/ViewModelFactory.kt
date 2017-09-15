package com.example.hemanth.terra_android.dagger.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.hemanth.terra_android.App
import com.example.hemanth.terra_android.ThingViewModel
import com.example.hemanth.terra_android.dagger.app.ApplicationComponent
import java.lang.reflect.Type
import kotlin.properties.Delegates

/**
 * Created by dev on 9/13/17.
 */
class MyViewModelFactory(app:App):ViewModelProvider.NewInstanceFactory(){
    private var _app:App by Delegates.notNull()
    init {
        _app=app
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
        val t=super.create(modelClass)
        if(t is ViewModelComponent.Injectable){
            t.inject(_app?.getViewModelComponent())
        }
        return t
    }
}