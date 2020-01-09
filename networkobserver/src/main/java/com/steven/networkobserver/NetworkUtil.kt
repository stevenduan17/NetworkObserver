package com.steven.networkobserver

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.support.annotation.RequiresPermission
import android.telephony.TelephonyManager
import com.steven.networkobserver.bean.MobileNetworkSubType
import com.steven.networkobserver.bean.NetworkType

/**
 * @author Steven Duan
 * @since 2019/2/20
 * @version 1.0
 */

/**
 * Check network is available.
 */
@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
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
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        val activeNetworkInfo = manager.activeNetworkInfo ?: return NetworkType.NONE
        return when (activeNetworkInfo.type) {
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

@TargetApi(Build.VERSION_CODES.P)
fun getMobileNetworkSubType(context: Context): MobileNetworkSubType? {
    return getSubTypeForP(getTelephonyManager(context))
}

private fun getTelephonyManager(context: Context) =
    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

/**
 * API above has TelephonyManager.NETWORK_TYPE_NR for 5G.
 */
private fun getSubTypeForP(manager: TelephonyManager): MobileNetworkSubType? {
    return when (manager.networkType) {
        TelephonyManager.NETWORK_TYPE_GPRS,
        TelephonyManager.NETWORK_TYPE_EDGE,
        TelephonyManager.NETWORK_TYPE_CDMA,
        TelephonyManager.NETWORK_TYPE_1xRTT,
        TelephonyManager.NETWORK_TYPE_IDEN -> MobileNetworkSubType.MOBILE_2_G
        TelephonyManager.NETWORK_TYPE_UMTS,
        TelephonyManager.NETWORK_TYPE_EVDO_0,
        TelephonyManager.NETWORK_TYPE_EVDO_A,
        TelephonyManager.NETWORK_TYPE_HSDPA,
        TelephonyManager.NETWORK_TYPE_HSUPA,
        TelephonyManager.NETWORK_TYPE_HSPA,
        TelephonyManager.NETWORK_TYPE_EVDO_B,
        TelephonyManager.NETWORK_TYPE_EHRPD,
        TelephonyManager.NETWORK_TYPE_HSPAP -> MobileNetworkSubType.MOBILE_3_G
        TelephonyManager.NETWORK_TYPE_LTE -> MobileNetworkSubType.MOBILE_4_G
        else -> null
    }
}

