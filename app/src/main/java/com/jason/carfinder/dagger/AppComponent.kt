package com.jason.carfinder.dagger

import com.jason.carfinder.activities.MainActivity
import com.jason.carfinder.fragments.CarSearchFragment
import com.jason.carfinder.models.MainActivityViewModel
import dagger.Component

@AppScope
@Component(modules = [(AppModule::class), (NetworkModule::class), (ServiceMod::class)])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: MainActivityViewModel)

    fun inject(carSearchFragment: CarSearchFragment)
}