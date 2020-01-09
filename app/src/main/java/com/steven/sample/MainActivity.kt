package com.steven.sample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.steven.networkobserver.NetworkObserver
import com.steven.networkobserver.OnNetworkChange
import com.steven.networkobserver.bean.NetworkType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NetworkObserver.getDefault().apply {
            subscribe(this@MainActivity)
            register(this@MainActivity)
        }
    }

    override fun onDestroy() {
        NetworkObserver.getDefault().apply {
            unregister(this@MainActivity)
            unsubscribe(this@MainActivity)
        }
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    @Suppress("unused")
    @OnNetworkChange
    fun onNetworkChange(type: NetworkType) {
        when (type) {
            NetworkType.WIFI -> {
                textView.text = "WIFI网络"
            }
            NetworkType.MOBILE -> {
                textView.text = "移动网络"
            }
            NetworkType.NONE -> {
                textView.text = "暂无网络"
            }
        }
    }
}
