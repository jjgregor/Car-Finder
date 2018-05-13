package com.jason.carfinder.models

data class AmadeusResponse(val results: MutableList<Company>? = ArrayList())

data class Company(val provider: Provider,
                   val location: Location? = Location(),
                   val branch_id: String? = "",
                   val address: Address? = Address(),
                   val cars: MutableList<CarsObject>)

data class Provider(val company_code: String? = "",
                    val company_name: String = "")

data class Location(val latitude: String? = "",
                    val longitude: String? = "")

data class Address(val line1: String? = "",
                   val city: String? = "",
                   val region: String = "",
                   val country: String? = "")

data class CarsObject(val vehicle_info: VehicleInfo? = VehicleInfo(),
                      val rates: MutableList<RatesObject>? = ArrayList(),
                      val estimated_total: Totals? = Totals())

data class VehicleInfo(val acriss_code: String? = "",
                       val transmission: String? = "",
                       val fuel: String? = "",
                       val air_conditioning: String? = "",
                       val category: String? = "",
                       val type: String? = "")

data class RatesObject(val type: String? = "",
                       val price: Price? = Price())

data class Price(val amount: String? = "",
                 val currency: String? = "")

data class Totals(val amount: String? = "",
                  val currency: String? = "")