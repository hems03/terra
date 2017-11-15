package com.example.hemanth.terra_android.dagger.controller

import android.content.Context
import android.view.LayoutInflater
import dagger.Module
import dagger.Provides


/**
 * Created by hemanth on 8/28/17.
 */
@Module
class ViewMvcModule {

    @Provides
    internal fun layoutInflater(context: Context): LayoutInflater {
        return LayoutInflater.from(context)
    }

}