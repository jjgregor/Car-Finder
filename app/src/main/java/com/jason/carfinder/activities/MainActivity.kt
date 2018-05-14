package com.jason.carfinder.activities

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jason.carfinder.R
import com.jason.carfinder.adapters.CarCompanyAdapter
import com.jason.carfinder.models.AmadeusResponse
import com.jason.carfinder.models.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var carCompanyAdapter: CarCompanyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViewVisibilities(false, false)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        carCompanyAdapter = CarCompanyAdapter(viewModel.results)
        initObservers()
        setupRecyclerView()

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            mapFragment.getMapAsync(this)
        } else {
            getLocationData()
        }
    }

    private fun initObservers() {
        viewModel.carsObserver.observe(this, Observer<AmadeusResponse> { response ->
            response?.let {
                if(it.results.isNotEmpty() == true) {
                    setViewVisibilities(true, false)
                } else {
                    setViewVisibilities(false, true)
                }
                car_company_recycler_view.adapter.notifyDataSetChanged()
            } ?: Log.d(TAG, "Call failed $response")
        })
    }

    private fun setupRecyclerView() {
        car_company_recycler_view.layoutManager = LinearLayoutManager(this)
        car_company_recycler_view.adapter = carCompanyAdapter
    }

    private fun setViewVisibilities(shown: Boolean, empty: Boolean) {
        when {
            empty -> {
                empty_view.visibility = View.VISIBLE
                progress_bar.visibility = View.GONE
                car_company_recycler_view.visibility = View.GONE
            }
            shown -> {
                empty_view.visibility = View.GONE
                progress_bar.visibility = View.GONE
                car_company_recycler_view.visibility = View.VISIBLE
            }
            else -> {
                empty_view.visibility = View.GONE
                progress_bar.visibility = View.VISIBLE
                car_company_recycler_view.visibility = View.GONE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            getLocationData()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationData() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        viewModel.longitude = location.longitude
        viewModel.latitude = location.latitude
        mapFragment.getMapAsync(this)
        viewModel.getCarCompanies()
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {

            val curr = if (viewModel.latitude != 0.0 && viewModel.longitude != 0.0) {
                LatLng(viewModel.latitude, viewModel.longitude)
            } else {
                LatLng(34.0109741, -118.4908714)
            }
            it.addMarker(MarkerOptions().position(curr)
                    .title("Your search")
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_person_pin_circle)))
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 10.0f))
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        ContextCompat.getDrawable(context, vectorResId)?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
        return null
    }

    companion object {
        val TAG: String = MainActivity::class.java.name
        const val REQUEST_CODE = 88
    }

}
