package com.example.chapter3_8

import android.app.Application
import android.content.Context

class MYApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MYApplication.applicationContext = applicationContext
    }

    companion object{
        lateinit var applicationContext: Context
    }
}