package com.jason.carfinder.dagger

import com.jason.carfinder.services.AmadeusService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module
class ServiceMod {

    @Provides
    @AppScope
    fun provideStackOverflowService(@Named("Amadeus") restAdapter: Retrofit) : AmadeusService {
        return restAdapter.create(AmadeusService::class.java)
    }

}