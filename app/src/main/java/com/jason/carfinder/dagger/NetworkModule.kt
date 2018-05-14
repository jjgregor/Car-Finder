package com.jason.carfinder.dagger

import android.app.Application
import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jason.carfinder.BuildConfig
import com.jason.carfinder.R
import com.jason.carfinder.utils.ObjectMapperFactory
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Named

@Module
class NetworkModule(val context: Context) {

    @Provides
    @AppScope
    internal fun provideOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @AppScope
    internal fun provideOkHttpClient(cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
                .addNetworkInterceptor(if (BuildConfig.DEBUG) StethoInterceptor() else null)
                .cache(cache)
                .build()
    }

    @Provides
    @Named("Amadeus")
    @AppScope
    internal fun provideAmadeusRestAdapter(client: OkHttpClient): Retrofit {
        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val newClient = client.newBuilder()
                .addInterceptor(AmadeusInterceptor(context))
                .addInterceptor(interceptor)
                .build()
        return Retrofit.Builder().baseUrl(context.getString(R.string.amadeus_api))
                .addConverterFactory(JacksonConverterFactory.create(ObjectMapperFactory().getObjectMapper()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(newClient)
                .build()
    }
}