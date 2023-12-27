package com.example.athanapp

import android.app.Application
import com.example.athanapp.data.AppContainer
import com.example.athanapp.data.DefaultAppContainer

class AthanApplication : Application(){
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()

    }
}