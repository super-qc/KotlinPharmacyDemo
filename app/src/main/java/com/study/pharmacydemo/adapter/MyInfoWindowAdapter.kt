package com.study.pharmacydemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.study.pharmacydemo.databinding.InfoWindowBinding

/**
 * 自定义Google Map Marker样式
 */
class MyInfoWindowAdapter(_context: Context) : GoogleMap.InfoWindowAdapter {
    private val context = _context

    override fun getInfoContents(marker: Marker): View? {

        var infoWindowBinding =
            InfoWindowBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        render(marker, infoWindowBinding)
        return infoWindowBinding.root

    }

    private fun render(marker: Marker, infoWindowBinding: InfoWindowBinding) {
        val mask = marker.snippet.toString().split(",")
        infoWindowBinding.apply {
            tvName.text = marker.title
            infoWindowBinding.tvAdultAmount.text = mask[0]
            infoWindowBinding.tvChildAmount.text = mask[1]
        }
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }


}