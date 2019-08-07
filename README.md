# NetworkObserver
#### NetworkObserver--极简可实时监控网络状态，兼容至Android 9.0。
在APP中涉及到网络请求，很多时候都需要监控网络状态，NetworkObserver借鉴EventBus的思想，实现随处可监听网络状态变化。

### 使用
1. 注册
```
NetworkObserver.getDefault().apply {
    subscribe(this@MainActivity)
    register(this@MainActivity)
}
```
2. 监听
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
3. 解除注册
```
 NetworkObserver.getDefault().apply {
    unsubscribe(this@MainActivity)
    unregister(this@MainActivity)
 }
```
### 说明
1. 注册和解除注册必定是成对出现的，否则会出现异常，例如在Activity的onCreate()注册，在onDestroy()解除注册
2. 传入的参数为（context,observer），observer为所要监听的主体，可以为Activity、Fragment等。
3. 如果去全局使用，请在应用Application的onCreate中subscribe,unsubscribe ，页面中仅需注册，无需订阅。