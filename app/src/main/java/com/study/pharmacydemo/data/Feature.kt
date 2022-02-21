package com.study.pharmacydemo.data

import com.google.gson.annotations.SerializedName

data class Feature(
    val geometry: Geometry,
    @SerializedName("properties")
    val property: Property,
    val type: String
)