package com.jason.carfinder.services

import com.jason.carfinder.AmadeusResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap
import rx.Observable

interface AmadeusService {

    @GET("cars/search-circle")
    fun getSearchForCars(@QueryMap params: Map<String, String>) : Observable<AmadeusResponse>
}