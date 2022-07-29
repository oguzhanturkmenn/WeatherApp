package com.oguzhanturkmen.kotlinweatherapp.model

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)