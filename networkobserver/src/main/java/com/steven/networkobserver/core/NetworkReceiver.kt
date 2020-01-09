package com.steven.networkobserver.core

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import com.steven.networkobserver.bean.NetworkType
import com.steven.networkobserver.getNetworkType

/**
 * Connectivity for api 20 or lower.
 *
 * @author Steven
 * @since 2019/2/20
 * @version 1.0
 */
@Suppress("DEPRECATION")
@TargetApi(Build.VERSION_CODES.KITKAT)
class NetworkReceiver() : BroadcastReceiver() {

    private var onNetworkChange: (type: NetworkType) -> Unit = {}
    private var cache: NetworkType? = null

    constructor(f: (type: NetworkType) -> Unit) : this() {
        this.onNetworkChange = f
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val networkType = getNetworkType(context)
            if (cache != networkType) {
                cache = networkType
                onNetworkChange(networkType)
            }
        }
    }
}