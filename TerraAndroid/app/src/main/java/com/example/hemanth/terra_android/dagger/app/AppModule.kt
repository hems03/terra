package com.example.hemanth.DiasporaAndroid.Dagger.app

import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by hemanth on 8/28/17.
 */
@Module
class AppModule {

    var mApplication:Application

    constructor(application:Application ) {
        mApplication = application;
    }

    @Provides
    fun application():Application  {
        return mApplication;
    }

    @Provides
    fun context():Context{
        return mApplication.applicationContext
    }


    @Provides
    fun resources():Resources{
        return mApplication.resources
    }
}
