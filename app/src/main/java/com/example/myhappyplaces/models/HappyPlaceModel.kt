package com.example.myhappyplaces.models

import java.io.Serializable

data class HappyPlaceModel(
    val id: Int,
    val title: String,
    val image: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
): Serializable //for passing from one class to another parcelable < serializable (in time)

