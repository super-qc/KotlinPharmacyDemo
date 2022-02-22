package com.study.pharmacydemo

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import com.study.pharmacydemo.data.PharmacyInFo
import com.study.pharmacydemo.databinding.ActivityMainBinding
import com.study.pharmacydemo.util.OkHttpUtil
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    //lateinit var tv_content: TextView;

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        setContentView(R.layout.activity_main)
         tv_content = findViewById(R.id.tv_content)
        */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvContent.text = "tvContent"
        getPharmaciesData()
    }


    private fun getPharmaciesData() {
        binding.progressBar.visibility = View.VISIBLE
        val pharmaciesDataUrl =
            "https://raw.githubusercontent.com/thishkt/pharmacies/master/data/info.json"

        OkHttpUtil.mOkHttpUtil.getAsync(pharmaciesDataUrl,object:OkHttpUtil.ICallBack{
            override fun onResponse(response: Response) {
                val pharmacInfo = Gson().fromJson(response.body?.string(), PharmacyInFo::class.java)
                Log.d("MainActivity", "pharmacInfo.type${pharmacInfo.type}")

                var propertiesNames = StringBuilder()
                for (i in pharmacInfo.features) {
                    Log.d("MainActivity", "药局名称:${i.property.name},${i.property.phone}")
                    propertiesNames.append(i.property.name + "\n")
                }
                runOnUiThread {
                    binding.tvContent.text = propertiesNames
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(e: IOException) {
                Log.e("MainActivity onFailure", e.toString())
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

    }
    private fun getPharmaciesData2() {
        binding.progressBar.visibility = View.VISIBLE
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
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val pharmacInfo = Gson().fromJson(response.body?.string(), PharmacyInFo::class.java)
                Log.d("MainActivity", "pharmacInfo.type${pharmacInfo.type}")

                var propertiesNames = StringBuilder()
                for (i in pharmacInfo.features) {
                    Log.d("MainActivity", "药局名称:${i.property.name},${i.property.phone}")
                    propertiesNames.append(i.property.name + "\n")
                }
                runOnUiThread {
                    binding.tvContent.text = propertiesNames
                    binding.progressBar.visibility = View.GONE
                }
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
                    binding.tv_content.text=response.body?.string()
                }
            */
            }

        }
        )
    }
}