package com.nishantpardamwar.unnamed

import android.app.Application
import com.nishantpardamwar.unnamed.viewmodels.VMFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkClient.init(this)
        VMFactory.init(this)
    }
}