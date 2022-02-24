package com.study.pharmacydemo.data

import java.io.Serializable

data class PharmacyInFo(
    val type:String,
    val features:List<Feature>

): Serializable