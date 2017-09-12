package com.example.hemanth.terra_android.common

import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import com.example.hemanth.DiasporaAndroid.Dagger.controller.ControllerComponent
import com.example.hemanth.DiasporaAndroid.Dagger.controller.ControllerModule
import com.example.hemanth.DiasporaAndroid.Dagger.controller.ViewMvcModule
import com.example.hemanth.terra_android.App
import com.example.hemanth.terra_android.retrofit.TerraService
import javax.inject.Inject

/**
 * Created by hemanth on 9/12/17.
 */
abstract class BaseActivity: AppCompatActivity(){
    private var mIsControllerComponentUsed = false

    @Inject
    lateinit var _service:TerraService
    /**
     * @return controller injector of type [ControllerComponent]
     */
    @UiThread
    protected fun getControllerComponent(): ControllerComponent {
        if (mIsControllerComponentUsed) {
            throw IllegalStateException("must not use ControllerComponent more than once")
        }
        mIsControllerComponentUsed = true
        return (getApplication() as App)
                .getApplicationComponent()
                .newControllerComponent(ControllerModule(this), ViewMvcModule())
    }
}