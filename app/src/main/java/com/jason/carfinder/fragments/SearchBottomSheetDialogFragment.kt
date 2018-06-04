package com.jason.carfinder.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.jason.carfinder.R
import com.jason.carfinder.adapters.PlacesArrayAdapter
import com.jason.carfinder.databinding.FragmentBottomSheetSearchBinding


class SearchBottomSheetDialogFragment : BottomSheetDialogFragment(),
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private lateinit var binding: FragmentBottomSheetSearchBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var adapter: PlacesArrayAdapter
    private lateinit var googleApiClient: GoogleApiClient

    private val bottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        override fun onStateChanged(bottomSheet: View, newState: Int) {}

    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        binding = DataBindingUtil.inflate(dialog.layoutInflater, R.layout.fragment_bottom_sheet_search, null, false)
        dialog.setContentView(binding.root)
        setBottomSheetBehavior(binding.root.parent as View)
        setupAutoCompleteFragment()
    }

    private fun setupAutoCompleteFragment() {
        activity?.let {
            googleApiClient = GoogleApiClient.Builder(it)
                    .addApi(Places.GEO_DATA_API)
                    .enableAutoManage(it, GOOGLE_API_CLIENT_ID, this)
                    .addConnectionCallbacks(this)
                    .build()
            googleApiClient.connect()

            adapter = PlacesArrayAdapter(it, android.R.layout.simple_list_item_1,
                    BOUNDS_MOUNTAIN_VIEW, null)
        } ?: dismiss()

        binding.autoCompleteTextView.threshold = 3
        binding.autoCompleteTextView.setAdapter(adapter)
        binding.autoCompleteTextView.onItemClickListener = autocompleteClickListener

    }

    private val autocompleteClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val item = adapter.getItem(position)
        val placeId = item.placeId
        Log.i(TAG, "Selected: " + item.description)
        val placeResult = Places.GeoDataApi
                .getPlaceById(googleApiClient, placeId)
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback)
        Log.i(TAG, "Fetching details for ID: " + item.placeId)
    }

    private val mUpdatePlaceDetailsCallback = ResultCallback<PlaceBuffer> { places ->
        if (!places.status.isSuccess) {
            Log.e(TAG, "Place query did not complete. Error: " + places.status.toString())
            return@ResultCallback
        }
        // Selecting the first object buffer.
        val place = places.get(0)
        val attributions = places.attributions

        binding.name.text = Html.fromHtml(place.address.toString() + "")
    }

    private fun setBottomSheetBehavior(view: View) {
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        val behavior: CoordinatorLayout.Behavior<*>? = params.behavior
        bottomSheetBehavior = behavior as BottomSheetBehavior<*>
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetBehaviorCallback)
        bottomSheetBehavior.isHideable = false
        //bottomSheetBehavior.peekHeight = (activity?.resources?.displayMetrics?.heightPixels?.times(0.88))?.toInt() ?: 0
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + p0.errorCode)
    }

    override fun onConnected(p0: Bundle?) {
        adapter.setGoogleApiClient(googleApiClient)
    }

    override fun onConnectionSuspended(p0: Int) {
        adapter.setGoogleApiClient(null)
    }

    companion object {
        val TAG: String = SearchBottomSheetDialogFragment::class.java.name
        private val BOUNDS_MOUNTAIN_VIEW = LatLngBounds(LatLng(37.398160, -122.180831), LatLng(37.430610, -121.972090))
        private val GOOGLE_API_CLIENT_ID: Int = 0
    }
}