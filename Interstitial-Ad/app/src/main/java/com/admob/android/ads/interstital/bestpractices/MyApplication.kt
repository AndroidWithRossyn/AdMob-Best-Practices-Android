package com.admob.android.ads.interstital.bestpractices

import androidx.multidex.MultiDexApplication
import timber.log.Timber

class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super<MultiDexApplication>.onCreate()
        // Initialize any libraries or SDKs here if needed

        if (BuildConfig.DEBUG) {
            // Plant a custom Timber DebugTree for logging
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "(${element.fileName}:${element.lineNumber})"
                }

                override fun log(
                    priority: Int, tag: String?, message: String, t: Throwable?
                ) {
                    val enhancedMessage = "[Admob Ad]--->  $message"
                    super.log(priority, tag, enhancedMessage, t)
                }
            })

        }
    }
}