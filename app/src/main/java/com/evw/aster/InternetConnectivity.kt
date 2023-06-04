package com.evw.aster

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class InternetConnectivity {


    fun checkInternetConnection(callback: ConnectivityCallback,context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            if (isNetworkAvailable(context)) {
                var connection: HttpsURLConnection? = null
                try {
                    connection = withContext(Dispatchers.IO) {
                        URL("https://clients3.google.com/generate_204").openConnection()
                    } as HttpsURLConnection
                    connection.setRequestProperty("User-Agent", "Android")
                    connection.setRequestProperty("Connection", "close")
                    connection.connectTimeout = 1500
                    withContext(Dispatchers.IO) {
                        connection.connect()
                    }
                    val isConnected = connection.responseCode == 204 && connection.contentLength == 0
                    postCallback(callback, isConnected)
                    connection.disconnect()
                } catch (e: Exception) {
                    postCallback(callback, false)
                    connection?.disconnect()
                }
            } else {
                postCallback(callback, false)
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    private suspend fun postCallback(callBack: ConnectivityCallback, isConnected: Boolean) {
        withContext(Dispatchers.Main){
            callBack.onDetected(isConnected)
        }
    }

    interface ConnectivityCallback {
        fun onDetected(isConnected: Boolean)
    }




}




