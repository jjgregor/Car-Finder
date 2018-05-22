package com.jason.carfinder.models

import java.io.Serializable

data class Sort(val field: String, val name: String, val value: String) : Serializable

fun getSortList() = arrayListOf(
        Sort(COMP_ASC, COMPANY, ASC),
        Sort(COMP_DSC, COMPANY, DSC),
        Sort(DIST_ASC, DISTANCE, ASC),
        Sort(DIST_DSC, DISTANCE, DSC),
        Sort(PRICE_ASC, PRICE, ASC),
        Sort(PRICE_DSC, PRICE, DSC))

fun getSortTitles() = arrayListOf(
        COMP_ASC,
        COMP_DSC,
        DIST_ASC,
        DIST_DSC,
        PRICE_ASC,
        PRICE_DSC)

const val COMP_ASC = "Company Asc"
const val COMP_DSC = "Company Dsc"
const val DIST_ASC = "Distance Asc"
const val DIST_DSC = "Distance Dsc"
const val PRICE_ASC = "Price Asc"
const val PRICE_DSC = "Price Dsc"
const val COMPANY = "COMPANY"
const val DISTANCE = "DISTANCE"
const val PRICE = "PRICE"
const val ASC = "ASC"
const val DSC = "DSC"