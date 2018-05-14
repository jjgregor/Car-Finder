package com.jason.carfinder.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import com.jason.carfinder.CarFinderApp

class CarSearchFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CarFinderApp().component.inject(this)
    }
}