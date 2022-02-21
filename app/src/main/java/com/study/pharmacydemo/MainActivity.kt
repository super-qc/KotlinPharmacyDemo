package com.study.pharmacydemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.gson.Gson
import com.study.pharmacydemo.data.PharmacyInFo
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var tv_content: TextView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_content = findViewById(R.id.tv_content)
        getPharmaciesData()
    }

    private fun getPharmaciesData() {
        //val pharmaciesDataUrl="http://192.168.50.113/kouzhao/points.php"
        // 6000条数据 https://raw.githubusercontent.com/thishkt/pharmacies/master/data/info.json
        val pharmaciesDataUrl =
            "https://raw.githubusercontent.com/thishkt/pharmacies/master/data/info.json"
        val okhttpClient = OkHttpClient().newBuilder().build()
        val request = Request.Builder().url(pharmaciesDataUrl).get().build()
        val call = okhttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity onFailure", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {

                val pharmacInfo = Gson().fromJson(response.body?.string(), PharmacyInFo::class.java)
                Log.d("MainActivity", "pharmacInfo.type${pharmacInfo.type}")
                /*
                    val obj = JSONObject(response.body?.string())
                    val featuresArray = obj.getJSONArray("features")
                    for (i in 0 until featuresArray.length()) {
                        val propertyObj = featuresArray.getJSONObject(i).getJSONObject("properties")
                        Log.d("MainActivity", "name:${propertyObj.getString("name")}")
                        //Log.d("MainActivity", "name:${propertyObj.toString()}")

                    }*/

                //Log.d("MainActivity","onResponse${response.body?.string()}")
                /*
                runOnUiThread {
                    tv_content.text=response.body?.string()
                }
            */
            }

        }

        )


    }
}