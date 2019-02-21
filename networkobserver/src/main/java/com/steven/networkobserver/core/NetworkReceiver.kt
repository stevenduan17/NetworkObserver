package com.steven.networkobserver.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.steven.networkobserver.bean.NetworkType
import com.steven.networkobserver.constant.ACTION_NETWORK_CHANGE
import com.steven.networkobserver.util.getNetworkType

/**
 * BroadcastReceiver must have a public zero argument constructor
 *
 * @author Steven Duan
 * @since 2019/2/20
 * @version 1.0
 */
class NetworkReceiver() : BroadcastReceiver() {

    private var onNetworkChange: (type: NetworkType) -> Unit = {}
    private var cache: NetworkType? = null

    constructor(f: (type: NetworkType) -> Unit) : this() {
        this.onNetworkChange = f
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_NETWORK_CHANGE) {
            val networkType = getNetworkType(context)
            if (cache != networkType) {
                onNetworkChange(networkType)
                cache = networkType
            }
        }
    }
}