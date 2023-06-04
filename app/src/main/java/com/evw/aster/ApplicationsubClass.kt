package com.evw.aster

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory


class ApplicationsubClass:Application() {
    override fun onCreate() {
        super.onCreate()
       FirebaseApp.initializeApp(this)

    }

}