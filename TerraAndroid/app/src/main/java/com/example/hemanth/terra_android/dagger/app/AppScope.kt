package com.example.hemanth.DiasporaAndroid.Dagger.app

/**
 * Created by hemanth on 8/28/17.
 */
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

import javax.inject.Scope

/**
 * Custom scope for global application singletons
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
annotation class ApplicationScope