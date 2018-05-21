package com.jason.carfinder.models

import java.io.Serializable

data class Sort(val name: String, val value: String) : Serializable

fun getSortList() = arrayListOf(
        Sort(COMPANY, ASC),
        Sort(COMPANY, DSC),
        Sort(DISTANCE, ASC),
        Sort(DISTANCE, DSC),
        Sort(PRICE, ASC),
        Sort(PRICE, DSC))

const val COMPANY = "COMPANY"
const val DISTANCE = "DISTANCE"
const val PRICE = "PRICE"
const val ASC = "ASC"
const val DSC = "DSC"