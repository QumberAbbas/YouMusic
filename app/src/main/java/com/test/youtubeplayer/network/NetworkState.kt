package com.test.youtubeplayer.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest

interface NetworkState {
    val isConnected: Boolean
}

internal class NetworkStateImp : NetworkState {
    override var isConnected: Boolean = false
        set(value) {
            field = value
            NetworkEvents.notify(if (value) Event.ConnectivityAvailable else Event.ConnectivityLost)
        }
}

internal class NetworkCallbackImp(private val holder: NetworkStateImp) : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
        holder.isConnected = true
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
        holder.isConnected = false
    }

    override fun onLost(network: Network) {
        holder.isConnected = false
    }

    override fun onUnavailable() {
        super.onUnavailable()
        holder.isConnected = false
    }

    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
        super.onBlockedStatusChanged(network, blocked)
        holder.isConnected = !blocked
    }

}
object NetworkStateHolder : NetworkState {

    private lateinit var holder: NetworkStateImp

    override val isConnected: Boolean
        get() = holder.isConnected

    fun Application.registerConnectivityMonitor() {
        holder = NetworkStateImp()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(),
            NetworkCallbackImp(holder)
        )
    }
}