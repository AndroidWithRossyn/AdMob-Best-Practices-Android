package com.admob.android.ads.withwireframe.bestpractices

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.admob.android.ads.withwireframe.bestpractices.activity.AboutActivity
import com.admob.android.ads.withwireframe.bestpractices.activity.FirstActivity
import com.admob.android.ads.withwireframe.bestpractices.ads.AppOpenAdManager
import timber.log.Timber

class MyApplication : MultiDexApplication(),Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver  {

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

        registerActivityLifecycleCallbacks(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager(this)
    }

    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null

    /**
     * DefaultLifecycleObserver method that shows the app open ad when the app moves to foreground.
     */
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (!checkAdShouldBeShown()) {
            Timber.tag("MyApplication")
                .d("Current activity is null or not suitable for showing ad.")
            return
        }
        currentActivity?.let {
            appOpenAdManager.showAdIfAvailable(it)  // Show the ad (if available) when the app moves to foreground.
        }
    }

    private fun checkAdShouldBeShown(): Boolean {
        return currentActivity != null
                && currentActivity!!.isFinishing.not()
                && currentActivity!!.isDestroyed.not()
                && currentActivity!!.isChangingConfigurations.not()
                && currentActivity !is FirstActivity // Exclude FirstActivity from showing the ad (this activity have own setup)
                && currentActivity !is AboutActivity // contains  large banner ad
    }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}
}