package com.steven.networkobserver.core

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.steven.networkobserver.bean.NetworkType

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class NetworkCallbackImpl private constructor(
    private val onNetworkChange: (type: NetworkType) -> Unit
) : ConnectivityManager.NetworkCallback() {

    companion object {
        @Volatile
        private var instance: NetworkCallbackImpl? = null

        fun getDefault(f: (type: NetworkType) -> Unit) = instance ?: synchronized(this) {
            instance ?: NetworkCallbackImpl(f).also {
                instance = it
            }
        }
    }

    /**
     * Because of [onCapabilitiesChanged] will be invoked several times even on same network type,
     * add a cache to avoid method by @OnNetworkChange annotation invoked on the same time.
     */
    private var cache: NetworkType? = null

    override fun onLost(network: Network) {
        super.onLost(network)
        if (cache != NetworkType.NONE) {
            cache = NetworkType.NONE
            onNetworkChange(NetworkType.NONE)
        }
    }

    /**
     * Note： Build code M or lower will not trigger this callback when testing on many phones.
     */
    override fun onCapabilitiesChanged(
        network: Network,
        networkCapabilities: NetworkCapabilities
    ) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                if (cache != NetworkType.WIFI) {
                    cache = NetworkType.WIFI
                    onNetworkChange(NetworkType.WIFI)
                }
            } else {
                if (cache != NetworkType.MOBILE) {
                    cache = NetworkType.MOBILE
                    onNetworkChange(NetworkType.MOBILE)
                }
            }
        }

    }
}
