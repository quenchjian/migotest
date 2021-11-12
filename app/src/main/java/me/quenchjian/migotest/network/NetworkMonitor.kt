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
    val request = NetworkRequest.Builder().build()
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
    val connected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false
    val wifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
    val cellular = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false
    return (!connected && wifi) || (wifi && cellular)
  }

  @Suppress("Deprecation")
  private fun checkConnectivityCompat(): Boolean {
    val info = connectManager.activeNetworkInfo
    val connected = info?.isConnected ?: false
    val wifi = info?.type == ConnectivityManager.TYPE_WIFI
    val cellular = info?.type == ConnectivityManager.TYPE_MOBILE
    return (!connected && wifi) || (wifi && cellular)
  }

  fun interface CapabilitiesChangeListener {
    fun onChange(capabilities: NetworkCapabilities)
  }
}