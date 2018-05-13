package com.jason.carfinder

import android.app.Application
import com.jason.carfinder.dagger.AppComponent
import com.jason.carfinder.dagger.AppModule
import com.jason.carfinder.dagger.DaggerAppComponent
import com.jason.carfinder.dagger.NetworkModule

class CarFinderApp : Application() {

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .networkModule(NetworkModule(applicationContext))
                .build()
    }

}