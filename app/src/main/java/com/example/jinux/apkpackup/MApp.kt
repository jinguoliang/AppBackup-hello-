package com.example.jinux.apkpackup

import android.app.Application

/**
 * Created by jinux on 17-6-22.
 */

class MApp : Application() {

    object a {
        var mInstance: MApp? = null
    }

    override fun onCreate() {
        super.onCreate()
        a.mInstance = this
    }

}