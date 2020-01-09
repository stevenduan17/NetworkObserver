package com.steven.networkobserver

import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import com.steven.networkobserver.bean.NetworkMethod
import com.steven.networkobserver.bean.NetworkType
import com.steven.networkobserver.core.NetworkCallbackImpl
import com.steven.networkobserver.core.NetworkReceiver
import java.lang.IllegalArgumentException

/**
 * @author Steven
 * @since 2020/1/8
 * @version 0.1.0
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

    @Suppress("DEPRECATION")
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun subscribe(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            context.registerReceiver(
                mReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        } else {
            getConnectivityManager(context).registerNetworkCallback(
                NetworkRequest.Builder().build(),
                mNetworkCallbackImpl
            )
        }
    }

    fun unsubscribe(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            context.unregisterReceiver(mReceiver)
        } else {
            getConnectivityManager(context).unregisterNetworkCallback(mNetworkCallbackImpl)
        }
    }

    fun register(observer: Any) {
        checkObserver(observer)
        var methods = mMap[observer]
        if (methods == null) {
            methods = findAnnotationMethods(observer)
            mMap[observer] = methods
        }
    }

    fun unregister(observer: Any) {
        checkObserver(observer)
        if (mMap.isNotEmpty()) {
            mMap.remove(observer)
        }
    }

    private fun checkObserver(observer: Any) {
        if (observer !is Activity && observer !is Fragment) {
            throw  IllegalArgumentException("Observer must be one of Activity or Fragment.")
        }
    }

    private fun getConnectivityManager(context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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