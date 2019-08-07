package com.steven.networkobserver

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.steven.networkobserver.bean.NetworkMethod
import com.steven.networkobserver.bean.NetworkType
import com.steven.networkobserver.constant.ACTION_NETWORK_CHANGE
import com.steven.networkobserver.core.NetworkCallbackImpl
import com.steven.networkobserver.core.NetworkReceiver

/**
 * @author Steven Duan
 * @since 2019/2/20
 * @version 1.0
 */
class NetworkObserver private constructor() {

    companion object {
        @Volatile
        private var instance: NetworkObserver? = null

        fun getDefault() = instance ?: synchronized(this) {
            instance ?: NetworkObserver().also {
                instance = it
            }
        }
    }

    private val mHandler = Handler(Looper.getMainLooper())
    private val mMap by lazy { hashMapOf<Any, MutableList<NetworkMethod>>() }
    private val mReceiver by lazy {
        NetworkReceiver {
            post(it)
        }
    }

    private val mNetworkCallbackImpl by lazy {
        NetworkCallbackImpl.getDefault {
            post(it)
        }
    }

    fun subscribe(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            context.registerReceiver(mReceiver, IntentFilter(ACTION_NETWORK_CHANGE))
        } else {
            getConnectivityManager(context).registerNetworkCallback(
                NetworkRequest.Builder().build(),
                mNetworkCallbackImpl
            )
        }
    }

    fun unsubscribe(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            context.unregisterReceiver(mReceiver)
        } else {
            getConnectivityManager(context).unregisterNetworkCallback(mNetworkCallbackImpl)
        }
    }

    fun register(observer: Any) {
        registerObserver(observer)
    }

    fun unregister(observer: Any) {
        unregisterObserver(observer)
    }

    private fun getConnectivityManager(context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private fun registerObserver(obj: Any) {
        var methods = mMap[obj]
        if (methods == null) {
            methods = findAnnotationMethods(obj)
            mMap[obj] = methods
        }
    }

    private fun unregisterObserver(obj: Any) {
        if (mMap.isNotEmpty()) {
            mMap.remove(obj)
        }
    }

    private fun findAnnotationMethods(obj: Any): MutableList<NetworkMethod> {
        val clazz = obj.javaClass
        val methods = clazz.methods
        val list = mutableListOf<NetworkMethod>()
        for (method in methods) {
            method.getAnnotation(OnNetworkChange::class.java) ?: continue
            val returnType = method.genericReturnType
            if (returnType.toString() != "void") throw RuntimeException("${method.name} shouldn't return a non void type.")
            val parameterTypes = method.parameterTypes
            if (parameterTypes.size != 1) throw RuntimeException("${method.name} must only have a parameter.")
            list.add(NetworkMethod(parameterTypes[0], method))
        }
        return list
    }

    private fun post(networkType: NetworkType) {
        for (key in mMap.keys) {
            val methods = mMap[key]
            methods?.forEach {
                if (it.type.isAssignableFrom(networkType.javaClass)) {
                    mHandler.post { it.method.invoke(key, networkType) }
                }
            }
        }
    }
}