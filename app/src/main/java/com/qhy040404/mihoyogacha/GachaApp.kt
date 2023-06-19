package com.qhy040404.mihoyogacha

import android.app.Application
import com.absinthe.libraries.utils.utils.Utility

class GachaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this

        Utility.init(this)
    }

    companion object {
        lateinit var app: Application
    }
}