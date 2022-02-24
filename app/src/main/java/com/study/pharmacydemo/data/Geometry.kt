package com.study.pharmacydemo.data

import java.io.Serializable

data class Geometry(
    val coordinates: List<Double>,
    val type: String
): Serializable