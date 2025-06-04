package com.admob.android.ads.reword.bestpractices

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


@SuppressLint("ObsoleteSdkInt")
@Suppress("DEPRECATION")
fun Context.isNetworkConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        val networkInfo = cm.activeNetworkInfo
        networkInfo != null && networkInfo.isConnected
    }
}


fun isNetworkStable(): Boolean {
    return try {
        val process = Runtime.getRuntime().exec("ping -c 1 -W 2 www.google.com")
        val result = process.waitFor()
        result == 0
    } catch (e: Exception) {
        false
    }
}
