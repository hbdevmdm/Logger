package com.hb.logger

import android.app.Application


class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.initializeSession(this)
    }
}