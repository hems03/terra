package com.example.hemanth.DiasporaAndroid.Dagger.service

import android.content.Context
import com.example.hemanth.terra_android.retrofit.TerraService
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by hemanth on 8/28/17.
 */
@Module
class ServiceModule() {
    val BASE_URL = ""


    @Provides
    @Singleton
    fun terraService(retrofit: Retrofit): TerraService {
        val diasporaService = retrofit.create(TerraService::class.java)
        return diasporaService
    }


    @Provides
    @Singleton
    fun retrofit(): Retrofit {
        var retrofit: Retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit
    }

    @Provides
    @Singleton
    fun gson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun googleApiClient(context: Context, gso: GoogleSignInOptions): GoogleApiClient {
        return GoogleApiClient.Builder(context)
                .addApi(Nearby.CONNECTIONS_API)
                .build()
    }
}