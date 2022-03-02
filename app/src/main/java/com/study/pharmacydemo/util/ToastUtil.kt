package com.study.pharmacydemo.util

import android.content.Context
import android.widget.Toast

object ToastUtil {
    fun showMessage(context: Context,message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }
}