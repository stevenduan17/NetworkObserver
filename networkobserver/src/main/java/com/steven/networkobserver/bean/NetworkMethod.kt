package com.steven.networkobserver.bean

import java.lang.reflect.Method

/**
 * @author Steven Duan
 * @since 2019/2/20
 * @version 1.0
 */
data class NetworkMethod(
    val type: Class<*>,
    val method: Method
)