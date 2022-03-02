package com.study.pharmacydemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.study.pharmacydemo.Constants.Companion.REQUEST_LOCATION_PERMISSION
import com.study.pharmacydemo.util.ToastUtil

class MapActivity : AppCompatActivity() {
    private var locationPermissionGranted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        getLocationPermission()
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
            //TODO checkGPSState
        } else {
            // 询问要求获取权限
            requestLocationPermission();
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
        }
    }
}