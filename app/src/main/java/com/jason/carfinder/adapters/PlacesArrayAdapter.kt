package com.jason.carfinder.adapters

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLngBounds
import java.util.concurrent.TimeUnit


class PlacesArrayAdapter(context: Context, resource: Int, private val mBounds: LatLngBounds,
                         private val mPlaceFilter: AutocompleteFilter?) : ArrayAdapter<PlacesArrayAdapter.PlaceAutocomplete>(context, resource), Filterable {
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mResultList: ArrayList<PlaceAutocomplete>

    fun setGoogleApiClient(googleApiClient: GoogleApiClient?) {
        mGoogleApiClient = googleApiClient
    }

    override fun getCount() = mResultList.size

    override fun getItem(position: Int) = mResultList[position]

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val results = FilterResults()
                if (constraint != null) {
                    mResultList = getPredictions(constraint) ?: arrayListOf()

                    results.values = mResultList
                    results.count = mResultList.size
                }

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }


    private fun getPredictions(constraint: CharSequence?): ArrayList<PlaceAutocomplete>? {

        Log.i(TAG, "Executing autocomplete query for: $constraint")

        mGoogleApiClient?.let {
            val results = Places.GeoDataApi
                    .getAutocompletePredictions(it, constraint.toString(),
                            mBounds, mPlaceFilter)
            val autocompletePredictions = results.await(60, TimeUnit.SECONDS)

            val status = autocompletePredictions.status
            if (!status.isSuccess) {
                Toast.makeText(context, "Error: " + status.toString(),
                        Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error getting place predictions: " + status
                        .toString())
                autocompletePredictions.release()
                return null
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.count
                    + " predictions.")
            val iterator = autocompletePredictions.iterator()
            val resultList = ArrayList<PlaceAutocomplete>(autocompletePredictions.count)
            while (iterator.hasNext()) {
                val prediction = iterator.next()
                resultList.add(PlaceAutocomplete(prediction.placeId,
                        prediction.getFullText(null)))
            }
            // Buffer release
            autocompletePredictions.release()
            return resultList
        } ?: return null
    }

    data class PlaceAutocomplete(var placeId: String? = "", var description: CharSequence)

    companion object {

        private val TAG: String = "PlaceArrayAdapter"
    }
}