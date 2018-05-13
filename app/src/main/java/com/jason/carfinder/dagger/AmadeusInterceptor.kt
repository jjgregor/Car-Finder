package com.jason.carfinder.dagger

import android.content.Context
import com.jason.carfinder.R
import okhttp3.Interceptor
import okhttp3.Response

class AmadeusInterceptor(private val context: Context) : Interceptor {

    val API_KEY = "apikey"

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val urlBuilder = request.url()
                .newBuilder()
                .addQueryParameter(API_KEY, context.getString(R.string.amadeus_key))
        request = request.newBuilder().url(urlBuilder.build()).build()
        return chain.proceed(request)
    }
}