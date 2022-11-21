package com.evw.aster

import android.app.Application
import io.realm.Realm

class ApplicationsubClass:Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }

}