package com.example.hemanth.terra_android.dagger.controller

import android.app.Activity
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import dagger.Module
import dagger.Provides


/**
 * Created by hemanth on 8/28/17.
 */
@Module
class ControllerModule(private val mActivity: FragmentActivity) {

    @Provides
    internal fun context(): Context {
        return mActivity
    }

    @Provides
    internal fun activity(): Activity {
        return mActivity
    }

    @Provides
    internal fun fragmentManager(): FragmentManager {
        return mActivity.supportFragmentManager
    }
}