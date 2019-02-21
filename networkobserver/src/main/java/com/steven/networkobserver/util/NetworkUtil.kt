package com.steven.networkobserver.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.steven.networkobserver.bean.NetworkType

/**
 * @author Steven Duan
 * @since 2019/2/20
 * @version 1.0
 */

/**
 * Check network is available.
 */
fun isNetworkConnected(context: Context): Boolean {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = manager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

/**
 * Get current network type.
 */
fun getNetworkType(context: Context): NetworkType {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        val activeNetworkInfo = manager.activeNetworkInfo ?: return NetworkType.NONE
        val type = activeNetworkInfo.type
        return when (type) {
            ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
            ConnectivityManager.TYPE_MOBILE -> NetworkType.MOBILE
            else -> NetworkType.NONE
        }
    } else {
        val activeNetwork = manager.activeNetwork
        val capabilities = manager.getNetworkCapabilities(activeNetwork)
        if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            return if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                NetworkType.WIFI
            } else {
                NetworkType.MOBILE
            }
        }
        return NetworkType.NONE
    }
}