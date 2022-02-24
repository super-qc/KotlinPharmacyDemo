package com.study.pharmacydemo.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Feature(
    val geometry: Geometry,
    @SerializedName("properties")
    val property: Property,
    val type: String
):Serializable