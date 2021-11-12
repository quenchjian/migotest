package me.quenchjian.migotest.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(@ApplicationContext context: Context) {

  private val connectManager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)!!
  private val listeners = CopyOnWriteArraySet<CapabilitiesChangeListener>()
  private val callback = object : ConnectivityManager.NetworkCallback() {
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
      listeners.forEach { it.onChange(networkCapabilities) }
    }
  }

  init {
    val request = NetworkRequest.Builder()
      .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
      .build()
    connectManager.registerNetworkCallback(request, callback)
  }

  fun checkPrivate(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      checkConnectivity(connectManager.getNetworkCapabilities(connectManager.activeNetwork))
    } else {
      checkConnectivityCompat()
    }
  }

  fun addCapabilitiesChangeListener(listener: CapabilitiesChangeListener) {
    listeners.add(listener)
  }

  fun removeCapabilitiesChangeListener(listener: CapabilitiesChangeListener) {
    listeners.remove(listener)
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun checkConnectivity(capabilities: NetworkCapabilities?): Boolean {
    val vpn = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
    val wifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
    return vpn && wifi
  }

  @Suppress("Deprecation")
  private fun checkConnectivityCompat(): Boolean {
    val info = connectManager.activeNetworkInfo
    val vpn = info?.type == ConnectivityManager.TYPE_VPN
    val wifi = info?.type == ConnectivityManager.TYPE_WIFI
    return vpn && wifi
  }

  fun interface CapabilitiesChangeListener {
    fun onChange(capabilities: NetworkCapabilities)
  }
}