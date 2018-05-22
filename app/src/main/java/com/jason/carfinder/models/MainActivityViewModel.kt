package com.jason.carfinder.models

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
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
    val carsObserver = MutableLiveData<AmadeusResponse>()
    var results = ArrayList<Company>()
    val selectedSort = Sort(DISTANCE + ASC, DISTANCE, ASC)

    private fun formatDate(date: Date) = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date).toString()

    fun getCarCompanies() {
        amadeusService.getSearchForCars(getQueryMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    response?.let {
                        results = it.results
                        carsObserver.value = it
                    } ?: Log.d(TAG, "Amadeus reponse is null $response")
                }, { t: Throwable? ->
                    Log.d(TAG, "Amadeus response is null $t")
                    carsObserver.value = null
                })
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

    companion object {
        val TAG: String = MainActivityViewModel::class.java.name
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val PICK_UP = "pick_up"
        const val DROP_OFF = "drop_off"
        const val RADIUS = "radius"
    }
}