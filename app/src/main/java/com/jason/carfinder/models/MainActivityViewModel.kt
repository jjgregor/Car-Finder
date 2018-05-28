package com.jason.carfinder.models

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.jason.carfinder.CarFinderApp
import com.jason.carfinder.services.AmadeusService
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainActivityViewModel(app: Application) : AndroidViewModel(app) {

    init {
        (app as CarFinderApp).component.inject(this)
    }

    @Inject
    lateinit var amadeusService: AmadeusService

    var latitude = 0.0
    var longitude = 0.0
    var startDate = Date()
    var endDate = Date()
    var radius = 50
    val carsObserver = MutableLiveData<ArrayList<Company>>()
    var results = ArrayList<Company>()
    var selectedSort = DIST_ASC

    private fun formatDate(date: Date) = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date).toString()

    fun getCarCompanies() {
        amadeusService.getSearchForCars(getQueryMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    response?.let {
                        setDistances(it.results)
                        carsObserver.value = results
                    } ?: Log.d(TAG, "Amadeus reponse is null $response")
                }, { t: Throwable? ->
                    Log.d(TAG, "Amadeus response is null $t")
                    carsObserver.value = null
                })
    }

    private fun setDistances(companies: ArrayList<Company>) {

        companies.forEach {
            val start = LatLng(latitude, longitude)
            val finish = LatLng(it.location.latitude.toDouble(), it.location.longitude.toDouble())

            val startLocation = Location("start")
            startLocation.latitude = start.latitude
            startLocation.longitude = start.longitude

            val finishLocation = Location("finish")
            finishLocation.latitude = finish.latitude
            finishLocation.longitude = finish.longitude

            it.distance = startLocation.distanceTo(finishLocation) * 0.000621371
        }

        results = companies
    }

    private fun getQueryMap(): Map<String, String> {
        val map = HashMap<String, String>()
        map[LATITUDE] = latitude.toString()
        map[LONGITUDE] = longitude.toString()
        map[PICK_UP] = formatDate(startDate)
        map[DROP_OFF] = formatDate(endDate)
        map[RADIUS] = radius.toString()
        return map
    }

    fun sortResults(sort: String) {
        selectedSort = sort

        when (sort) {
            COMP_ASC -> results.sortBy { it.provider.company_name }
            COMP_DSC -> results.sortBy { it.provider.company_name.reversed() }
            DIST_ASC -> results.sortBy { it.distance }
            DIST_DSC -> results.sortByDescending { it.distance }
            PRICE_ASC -> results.forEach { it.cars.sortBy { it.estimated_total.amount.toDouble() } }
            PRICE_DSC -> results.forEach { it.cars.sortByDescending { it.estimated_total.amount.toDouble() } }
            else -> results.sortBy { it.distance }
        }

        carsObserver.value = results
    }

    companion object {
        val TAG: String = MainActivityViewModel::class.java.name
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val PICK_UP = "pick_up"
        const val DROP_OFF = "drop_off"
        const val RADIUS = "radius"
    }
}