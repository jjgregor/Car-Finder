package com.jason.carfinder.dagger

import com.jason.carfinder.services.AmadeusService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class ServiceMod {

    @Provides
    internal fun provideStackOverflowService(restAdapter: Retrofit) : AmadeusService {
        return restAdapter.create(AmadeusService::class.java)
    }

}