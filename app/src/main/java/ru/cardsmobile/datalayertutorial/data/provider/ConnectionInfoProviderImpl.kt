package ru.cardsmobile.datalayertutorial.data.provider

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ConnectionInfoProviderImpl(private val context: Context) : ConnectionInfoProvider {

    override fun isConnectedToNetwork(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        val networks = connectivityManager?.allNetworks ?: return false

        for (network in networks) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                ?: continue
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
            }
        }
        return false
    }
}