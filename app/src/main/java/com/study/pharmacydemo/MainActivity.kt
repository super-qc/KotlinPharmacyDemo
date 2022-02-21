package com.study.pharmacydemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPharmaciesData()
    }
    private fun getPharmaciesData(){
        val pharmaciesDataUrl=""
        val okhttpClient = OkHttpClient().newBuilder().build()
        val request = Request.Builder().url(pharmaciesDataUrl).get().build()
        val call=okhttpClient.newCall(request)
        call.enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("MainActivity","onResponse${response.body.toString()}")
            }

        }

        )





    }
}