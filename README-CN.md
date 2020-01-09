## NetworkObserver--极简可实时监控网络状态，兼容至Android 10.0。
![release](https://img.shields.io/badge/release-0.2.0-green.svg)  ![API](https://img.shields.io/badge/API-14+-green.svg)  ![Licenses](https://img.shields.io/badge/Licenses-Apache2.0-green.svg)

在APP中涉及到网络请求，很多时候都需要监控网络状态，NetworkObserver借鉴EventBus的思想，实现随处可监听网络状态变化。

### Gradle
```groovy
dependencies {
    implementation 'com.steven:networkobserver:$latest_version'
}
```

### AndroidX
使用androidX请使用0.2.0及以上版本，使用support库可以继续使用0.1.x版本。如果使用target version 低于29（Android Q）,由于判断手机网路状态5G为Android Q新加字段，相关方法可能失效，基本的网络类型判断不受影响。

### 使用
1. 注册与监听
```
NetworkObserver.getDefault().apply {
    subscribe(this@MainActivity)
    register(this@MainActivity)
}
```
2. Activity或Fragment中使用
```
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
```
3. 解除注册与监听
```
 NetworkObserver.getDefault().apply {
    unsubscribe(this@MainActivity)
    unregister(this@MainActivity)
 }
```
4. 手机网络子类型判断
工具类全局方法getMobileNetworkSubType(context)可判断2G、3G、4G, 判断5G需要使用AndroidX,target version为29（Android Q）.

### 说明
1. 注册和解除注册必定是成对出现的，否则会出现异常，例如在Activity的onCreate()注册，在onDestroy()解除注册
2. 传入的参数为（context,observer），observer为所要监听的主体，可以为Activity、Fragment。
3. 如果去全局使用，请在应用Application的中subscribe,unsubscribe ，页面中仅需注册，无需订阅。

### Licence
```
Copyright (c) 2020 stevenduan17

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
