package com.jason.carfinder.dagger

import com.jason.carfinder.activites.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (NetworkModule::class), (ServiceMod::class)])
interface AppComponent {

    fun inject(activity: MainActivity)

}