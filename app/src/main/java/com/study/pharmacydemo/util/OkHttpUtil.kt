package com.study.pharmacydemo.util

import okhttp3.*
import java.io.IOException

class OkHttpUtil {
    private var mOkHttpClient:OkHttpClient?=null
    // 单例
    companion object{
        val mOkHttpUtil: OkHttpUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            OkHttpUtil()
        }
    }

    init {
        // 1. 初始化 OkHttpClient
        mOkHttpClient = OkHttpClient().newBuilder().build()
    }

    fun getAsync(url:String,callback:ICallBack ){
        // 2. 创建Request，连接到指定网址
        val request=with(Request.Builder()){
            url(url)
            get()
            build()

        }

        // 3. 创建Call
        val call= mOkHttpClient?.newCall(request)

        // 执行Call连接后，采用enqueue非同步方式，获取数据
        call?.enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                callback.onResponse(response)

            }
        })
    }

    interface ICallBack{
        fun onResponse(response:Response)
        fun onFailure(e:IOException)
    }

}