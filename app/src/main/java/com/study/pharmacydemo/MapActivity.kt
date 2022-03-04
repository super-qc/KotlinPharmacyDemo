package com.study.pharmacydemo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.study.pharmacydemo.Constants.Companion.REQUEST_ENABLE_GPS
import com.study.pharmacydemo.Constants.Companion.REQUEST_LOCATION_PERMISSION
import com.study.pharmacydemo.adapter.MyInfoWindowAdapter
import com.study.pharmacydemo.data.PharmacyInFo
import com.study.pharmacydemo.databinding.ActivityMainBinding
import com.study.pharmacydemo.databinding.ActivityMapBinding
import com.study.pharmacydemo.util.ImgUtil
import com.study.pharmacydemo.util.ImgUtil.px
import com.study.pharmacydemo.util.OkHttpUtil
import com.study.pharmacydemo.util.ToastUtil
import okhttp3.Response
import java.io.IOException

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private var locationPermissionGranted = false
    private lateinit var mContext: Context
    private lateinit var binding: ActivityMapBinding
    private lateinit var mLocationProviderClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null
    private var mCurrentLocationMarker: Marker? = null
    private lateinit var pharmacInfo: PharmacyInFo
    private val defaultLocation = LatLng(25.0338483, 121.5645283)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_map)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLocationPermission()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        binding.ivList.setOnClickListener {
            toListActivity()
        }
    }

    private fun toListActivity() {
        startActivity(Intent(this,MainActivity::class.java))
    }

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 已经获取到权限
            ToastUtil.showMessage(this, "已经授权位置权限,准备获取经纬度权限")
            locationPermissionGranted = true
            checkGPSState()
        } else {
            // 询问要求获取权限
            requestLocationPermission();
        }
    }

    private fun checkGPSState() {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS没有开启
            AlertDialog.Builder(mContext)
                .setTitle("GPS 尚未开启")
                .setMessage("使用此功能需要开启GPS定位功能")
                .setPositiveButton("前往开启") { _, _ ->
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        REQUEST_ENABLE_GPS
                    )
                }.setNegativeButton("取消", null)
                .show()
        } else {
            // GPS已开启
            ToastUtil.showMessage(this, "GPS已开启")
            //getDeviceLocation()
            checkLocationPermission()
            mMap?.isMyLocationEnabled = true
            mMap?.setInfoWindowAdapter(MyInfoWindowAdapter(mContext))
            mMap?.setOnInfoWindowClickListener(this)
            getPharmaciesData()
        }
    }

    private fun getDeviceLocation() {
        if (locationPermissionGranted) {
            val locationRequest = LocationRequest()
            locationRequest.apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                // 更新频率
                interval = 1000
                // 更新次数，不设定的话会持续更新
                // numUpdates=1
            }
            checkLocationPermission()


            mLocationProviderClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)
                        locationResult ?: return
                        Log.d(
                            "PHA",
                            "纬度：${locationResult.lastLocation.latitude},经度：${locationResult.lastLocation.longitude}"
                        )

                        val currentLocation =
                            LatLng(
                                locationResult.lastLocation.latitude,
                                locationResult.lastLocation.longitude
                            )
                        // 清除所有标记
                        //mMap?.clear()

                        // 清除上一次位置标记
                        //mCurrentLocationMarker?.remove()

                        // 1. 当下位置Marker存起来，下次清除使用
                        /*
                        mCurrentLocationMarker = mMap?.addMarker(
                            MarkerOptions()
                                .position(currentLocation).title("当前位置")
                        )*/


                        // 2. 设置Icon图片来源为向量图
                        /*
                        mCurrentLocationMarker = mMap?.addMarker(
                            MarkerOptions()
                                .position(currentLocation).title("当前位置")
                                .snippet("显示资讯,内容太长会被截掉").icon(
                                    ImgUtil.getBitmapDescriptor(
                                        this@MapActivity,
                                        R.drawable.ic_baseline_masks_24,
                                        60.px, 60.px
                                    )
                                )
                        )
                         */
                        /*
                        // 自定义marker样式
                        mMap?.setInfoWindowAdapter(MyInfoWindowAdapter(mContext))
                        mCurrentLocationMarker = mMap?.addMarker(
                            MarkerOptions()
                                .position(currentLocation).title("当前位置")
                                .snippet(
                                    "100,66"
                                )
                        )
                        */

                        // 3.默认显示Marker 浮窗信息
                        //mCurrentLocationMarker?.showInfoWindow()


                        // 4. 显示当前位置，使用预设的蓝色圆点
                        checkLocationPermission()
                        mMap?.isMyLocationEnabled = true

                        // 地图中心移动到固定位置
                        /*
                       mMap?.moveCamera(
                           CameraUpdateFactory.newLatLngZoom(
                               LatLng(defaultLocation.longitude,defaultLocation.latitude), 15f
                           )
                       )
  */


                    }
                }, null
            )

        } else {
            getLocationPermission()
        }
    }


    private fun getPharmaciesData() {
        binding.progressBar.visibility = View.VISIBLE
        OkHttpUtil.mOkHttpUtil.getAsync(
            Constants.PHARMACIES_DATA_URL,
            object : OkHttpUtil.ICallBack {
                override fun onResponse(response: Response) {
                    pharmacInfo = Gson().fromJson(response.body?.string(), PharmacyInFo::class.java)
                    Log.d("MainActivity", "pharmacInfo.type${pharmacInfo.type}")

                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        addAllMarker()
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

    private fun addAllMarker() {
        for (index in 1..200) {
            var feature = pharmacInfo?.features[index]

            if (index == 1) {
                mMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            feature.geometry.coordinates[1],
                            feature.geometry.coordinates[0],
                        ), 15f
                    )
                )
            }

            mMap?.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            feature.geometry.coordinates[1],
                            feature.geometry.coordinates[0],
                        )
                    )
                    .title(feature.property.name)
                    .snippet("${feature.property.mask_adult},${feature.property.mask_child}")
            )
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            getLocationPermission()
            return
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setMessage("此程序需要位置权限才能正常使用")
                .setPositiveButton("确定") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_PERMISSION
                    )
                }.setNegativeButton("取消") { _, _ ->
                    requestLocationPermission()
                }.show()
        } else {
            // 可以询问使用者获取权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // 已获取到权限
                        ToastUtil.showMessage(this, "已经授权位置权限,准备获取经纬度权限")
                        locationPermissionGranted = true
                        checkGPSState()
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            // 权限被永久拒绝
                            ToastUtil.showMessage(this, "位置权限已经被关闭，功能将会无法正常使用")
                            AlertDialog.Builder(this)
                                .setTitle("开启位置权限")
                                .setMessage("此应用程序，位置权限已被关闭，需开启才能正常使用")
                                .setPositiveButton("确定") { _, _ ->
                                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    startActivityForResult(intent, REQUEST_LOCATION_PERMISSION)
                                }
                                .setNegativeButton("取消") { _, _ -> requestLocationPermission() }
                                .show()
                        } else {
                            // 权限被拒绝
                            ToastUtil.showMessage(this, "位置权限被拒绝，功能将无法正常使用")
                            requestLocationPermission()
                        }
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                getLocationPermission()
            }
            REQUEST_ENABLE_GPS -> {
                checkGPSState()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocationPermission()
    }

    /**
     * 点击进入详情页
     */
    override fun onInfoWindowClick(marker: Marker) {
        marker.title.let { title ->
            var feature = pharmacInfo.features.filter {
                it.property.name == title
            }
            if (feature != null && feature.size > 0) {
                val intent = Intent(this, PharmacyDetailActivity::class.java);
                intent.putExtra("data", feature[0])
                startActivity(intent)
            } else {
                ToastUtil.showMessage(this,"不存在的药局")
            }

        }

    }
}