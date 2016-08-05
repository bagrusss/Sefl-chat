package ru.bagrusss.selfchat.app

import android.app.Application
import cat.ereza.customactivityoncrash.CustomActivityOnCrash

/**
 * Created by bagrusss.
 */

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        CustomActivityOnCrash.install(this)
        CustomActivityOnCrash.setShowErrorDetails(true)
    }
}