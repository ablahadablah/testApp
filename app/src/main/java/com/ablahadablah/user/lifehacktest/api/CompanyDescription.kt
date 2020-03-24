package com.ablahadablah.user.lifehacktest.api

data class CompanyDescription(
    val id: Long = 0,
    val name: String = "",
    val img: String = "",
    val description: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val www: String = "",
    val phone: String = ""
) 