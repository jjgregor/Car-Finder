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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jason.carfinder.R
import com.jason.carfinder.adapters.CarExpandableListAdapter
import com.jason.carfinder.fragments.SortDialogFragment
import com.jason.carfinder.models.Company
import com.jason.carfinder.models.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
        ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener, SortDialogFragment.OnSortSelectedListener {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var adapter: CarExpandableListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViewVisibilities(false, false)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        adapter = CarExpandableListAdapter(this, viewModel.results)

        setupExpandableListView()
        initObservers()

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE)
            mapFragment.getMapAsync(this)
        } else {
            getLocationData()
        }

        fab.setOnClickListener {  }
    }

    private fun setupExpandableListView() {
        company_expandable_list_view.setAdapter(adapter)
        company_expandable_list_view.setOnGroupClickListener(this)
        company_expandable_list_view.setOnChildClickListener(this)
    }

    private fun initObservers() {
        viewModel.carsObserver.observe(this, Observer<ArrayList<Company>> { response ->
            response?.let {
                if (it.isNotEmpty() == true) {
                    setViewVisibilities(true, false)
                } else {
                    setViewVisibilities(false, true)
                }
                adapter.companies.clear()
                adapter.companies.addAll(it)
                adapter.notifyDataSetChanged()
                mapFragment.getMapAsync(this)
            } ?: setViewVisibilities(false, true)
        })
    }

    private fun setViewVisibilities(shown: Boolean, empty: Boolean) {
        when {
            empty -> {
                empty_view.visibility = View.VISIBLE
                progress_bar.visibility = View.GONE
                company_expandable_list_view.visibility = View.GONE
            }
            shown -> {
                empty_view.visibility = View.GONE
                progress_bar.visibility = View.GONE
                company_expandable_list_view.visibility = View.VISIBLE
            }
            else -> {
                empty_view.visibility = View.GONE
                progress_bar.visibility = View.VISIBLE
                company_expandable_list_view.visibility = View.GONE
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            it.isMyLocationEnabled = true
            val curr = if (viewModel.latitude != 0.0 && viewModel.longitude != 0.0) {
                LatLng(viewModel.latitude, viewModel.longitude)
            } else {
                LatLng(34.0109741, -118.4908714)
            }
            it.addMarker(MarkerOptions().position(curr).title("Your search").icon(bitmapDescriptorFromVector(this, R.drawable.ic_person_pin_circle)))

            viewModel.results.forEach {
                val latLng = LatLng(it.location.latitude.toDouble(), it.location.longitude.toDouble())
                map.addMarker(MarkerOptions().position(latLng).title(it.provider.company_name))
            }

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

    override fun onGroupClick(parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long): Boolean {
        parent?.smoothScrollToPosition(groupPosition)
        if (parent?.isGroupExpanded(groupPosition) == true) {
            parent.collapseGroup(groupPosition)
        } else {
            parent?.expandGroup(groupPosition, true)
        }

        return true
    }

    override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        return false
    }

    override fun onSortSelected(sort: String) {
        viewModel.sortResults(sort)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort -> {
                showSortDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSortDialog() {
        SortDialogFragment.newInstance(viewModel.selectedSort).show(fragmentManager, SortDialogFragment.TAG)
    }

    companion object {
        val TAG: String = MainActivity::class.java.name
        const val REQUEST_CODE = 88
    }

}
