package com.study.pharmacydemo

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.study.pharmacydemo.Constants.Companion.PHARMACIES_DATA_URL
import com.study.pharmacydemo.adapter.MainAdapter
import com.study.pharmacydemo.data.Feature
import com.study.pharmacydemo.data.PharmacyInFo
import com.study.pharmacydemo.databinding.ActivityMainBinding
import com.study.pharmacydemo.util.CountyUtil
import com.study.pharmacydemo.util.OkHttpUtil
import com.study.pharmacydemo.util.OkHttpUtil.Companion.mOkHttpUtil
import okhttp3.*
import java.io.IOException
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), MainAdapter.IItemClickListener {
    //lateinit var tv_content: TextView;

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: MainAdapter
    private lateinit var pharmacInfo: PharmacyInFo

    private var currentCounty: String = "臺北市"
    private var currentTown: String = "中山區"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        getPharmaciesData()
    }

    private fun initView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = MainAdapter(this)

        binding.recyclerViewPharmacy.apply {
            layoutManager = viewManager
            adapter = viewAdapter
            // 设置分割线
            /*
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            */
        }
        val countryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            CountyUtil.getAllCountiesName()
        )
        binding.spinnerCounty.adapter = countryAdapter

        binding.spinnerCounty.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentCounty = binding.spinnerCounty.selectedItem.toString()
                    Log.d("MainActivity", "选择的县：${binding.spinnerCounty.selectedItem}")
                    setSpinnerTown()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        binding.spinnerTown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentTown = binding.spinnerTown.selectedItem.toString()
                getPharmaciesData()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        setDefaultCountyWithTown()
        binding.ivMap.setOnClickListener{
            startActivity(Intent(this,MapActivity::class.java))
        }

    }

    private fun setDefaultCountyWithTown() {
        binding.spinnerCounty.setSelection(CountyUtil.getCountyIndexByName(currentCounty))
        setSpinnerTown()
    }

    private fun setSpinnerTown() {
        var list = CountyUtil.getTownsByCountyName(currentCounty)
        val adapterTown = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
        binding.spinnerTown.adapter = adapterTown
        binding.spinnerTown.setSelection(CountyUtil.getTownIndexByName(currentCounty, currentTown))

    }

    private fun getPharmaciesData() {
        binding.progressBar.visibility = View.VISIBLE
        mOkHttpUtil.getAsync(PHARMACIES_DATA_URL, object : OkHttpUtil.ICallBack {
            override fun onResponse(response: Response) {
                pharmacInfo = Gson().fromJson(response.body?.string(), PharmacyInFo::class.java)
                Log.d("MainActivity", "pharmacInfo.type${pharmacInfo.type}")

                /*

                //filter语言的使用 臺東縣 ,town:池上鄉
                val data = pharmacInfo.features
                    .filter {
                    it.property.county=="臺東縣"&&it.property.town=="池上鄉"
                }
                data.forEach{
                    Log.d("MainActivity", "data.forEach it.property.county : ${it.property.county} ,town:${it.property.town}")
                }*/

                /*
                // groupBy分组 分组取出每个城市中的每个乡镇的每个药局的成人口罩数量和儿童口罩数量
                val countryData = pharmacInfo.features.groupBy { it.property.county }
                for (country in countryData) {
                    Log.d("MainActivity", "--${country.key} ")
                    val townData = country.value.groupBy { it.property.town }
                    for (town in townData) {
                        Log.d("MainActivity", "---${town.key} ")
                        for (tv_val in town.value) {
                            Log.d(
                                "MainActivity",
                                "${tv_val.property.name} : 成人 :${tv_val.property.mask_adult},儿童：${tv_val.property.mask_child}"
                            )
                        }
                    }

                }*/

                runOnUiThread {
                    updateRecycleView()
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

    private fun updateRecycleView() {
        var filterData =
            pharmacInfo?.features?.filter { it.property.county == currentCounty && it.property.town == currentTown }
        if(filterData!=null){
            viewAdapter.pharmacyList=filterData
        }

    }

    override fun onItemClickListener(data: Feature) {
        val intent = Intent(this, PharmacyDetailActivity::class.java);
        intent.putExtra("data", data)
        startActivity(intent)
    }

    private fun getPharmaciesData3() {
        binding.progressBar.visibility = View.VISIBLE
        mOkHttpUtil.getAsync(PHARMACIES_DATA_URL, object : OkHttpUtil.ICallBack {
            override fun onResponse(response: Response) {
                val pharmacInfo = Gson().fromJson(response.body?.string(), PharmacyInFo::class.java)
                Log.d("MainActivity", "pharmacInfo.type${pharmacInfo.type}")

                var propertiesNames = StringBuilder()
                for (i in pharmacInfo.features) {
                    Log.d("MainActivity", "药局名称:${i.property.name},${i.property.phone}")
                    propertiesNames.append(i.property.name + "\n")
                }
                runOnUiThread {
                    //binding.tvContent.text = propertiesNames
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
                    //binding.tvContent.text = propertiesNames
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