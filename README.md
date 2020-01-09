## NetworkObserver--A simple and convenient library to monitor network status in real time ，compatible with Android 10.0.
![release](https://img.shields.io/badge/release-0.1.0-green.svg)  ![API](https://img.shields.io/badge/API-14+-green.svg)  ![Licenses](https://img.shields.io/badge/Licenses-Apache2.0-green.svg)

In many cases, network status needs to be monitored in our application,we want to know current network status: WLAN,mobile network or event 2G,3G,4G,5G. NetworkObserver uses the idea of EventBus to observe network status changes everywhere.

[中文文档](https://github.com/stevenduan17/NetworkObserver/blob/master/README-CN.md)

### Gradle
```groovy
dependencies {
    implementation 'com.steven:networkobserver:$latest_version'
}
```

### AndroidX
If your application build with androidX,please use version 0.2.0 and above.Use support library can continue to use 0.1.x version. If the target version is lower than 29 (Android Q), the 5G is a new field added to Android Q to determine the mobile network status and the related methods may fail, but the basic network type judgment is not affected.

### Usage
1. subscribe & register
```
NetworkObserver.getDefault().apply {
    subscribe(this@MainActivity)
    register(this@MainActivity)
}
```
2. observe on Activity or Fragment
```
 @OnNetworkChange
 fun onNetworkChange(type: NetworkType) {
     when (type) {
         NetworkType.WIFI -> {
             textView.text = "WLAN"
         }
         NetworkType.MOBILE -> {
             textView.text = "MOBILE NETWORK"
         }
         NetworkType.NONE -> {
             textView.text = "NONE"
         }
     }
 }
```
3. unsubscribe & unregister
```
 NetworkObserver.getDefault().apply {
    unsubscribe(this@MainActivity)
    unregister(this@MainActivity)
 }
```
4. Get subtypes of mobile network
Global util method #getMobileNetworkSubType(context) can judge 2G, 3G, 4G, but 5G requires AndroidX, and target version is 29 (Android Q).

### Notes
 - Register and unregister must occur in pairs, such as register in Activity's onCreate() and unregister in onDestroy().
 - The parameters passed in (context, observer), observer is the subject to be monitored and it can be Activity or Fragment.
 - If you want to use it globally, please subscribe and unsubscribe in Application. You only need to register/unregister on the page, no subscription is required.

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
