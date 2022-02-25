package com.study.pharmacydemo

import com.study.pharmacydemo.util.CountyUtil

fun main() {
    /*
    CountyUtil.getAllCountiesName().forEach{
        println(it)
    }*/
    println("高雄市的区：")
    CountyUtil.getTownsByCountyName("高雄市").forEach{
        println("--${it}")
    }
}