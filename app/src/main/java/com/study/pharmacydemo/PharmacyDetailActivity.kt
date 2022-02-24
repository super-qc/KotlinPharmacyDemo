package com.study.pharmacydemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.study.pharmacydemo.data.Feature
import com.study.pharmacydemo.databinding.ActivityPharmacyDetailBinding

class PharmacyDetailActivity : AppCompatActivity() {
    private val data by lazy {
        intent.getSerializableExtra("data") as Feature
    }
    private val name by lazy {
        data?.property?.name
    }
    private val maskAdult by lazy {
        data?.property?.mask_adult
    }
    private val maskChild by lazy {
        data?.property?.mask_child
    }
    private val phone by lazy {
        data?.property?.phone
    }
    private val address by lazy {
        data?.property?.address
    }
    private lateinit var binding: ActivityPharmacyDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_pharmacy_detail)
        Log.d("PharmacyDetailActivity", "detail: ${data.property.name}")

        binding = ActivityPharmacyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView();
    }

    private fun initView() {
        binding.apply {
            tvName.text = name?:"名称错误"
            tvAdultAmount.text = maskAdult.toString()
            tvChildAmount.text = maskChild.toString()
            tvPhone.text = phone
            tvAddress.text = address
            clPhone.setOnClickListener{
                Log.d("PharmacyDetailActivity", "clPhone click: ${phone}")
                Toast.makeText(this@PharmacyDetailActivity,phone.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }
}