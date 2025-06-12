package com.admob.android.ads.withwireframe.bestpractices.ads

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import com.admob.android.ads.withwireframe.bestpractices.R
import com.admob.android.ads.withwireframe.bestpractices.interfaces.OnShowAdCompleteListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import timber.log.Timber
import java.util.Date

class AppOpenAdManager(context: Context) {


    private val prefs = context.getSharedPreferences("ad_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_MOBILE_ADS_INIT = "is_mobile_ads_initialized"
    }

    fun setMobileAdsInitialized(value: Boolean) {
        prefs.edit { putBoolean(KEY_MOBILE_ADS_INIT, value) }
    }

    fun isMobileAdsInitialized(): Boolean {
        return prefs.getBoolean(KEY_MOBILE_ADS_INIT, false)
    }

    private val LOG_TAG = "MyApplication"

    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager =
        GoogleMobileAdsConsentManager.getInstance(context.applicationContext)

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isAdLoaded = false
    var isShowingAd = false

    private var loadTime: Long = 0

    /**
     * Load an ad.
     *
     * @param context the context of the activity that loads the ad
     */
    fun loadAd(context: Context, callback: (() -> Unit)? = null) {

        if (!isMobileAdsInitialized()) {
            Timber.tag(LOG_TAG).d("Mobile Ads SDK is not initialized.")
            return // Do not load ads if Mobile Ads SDK is not initialized.
        }

        if (isLoadingAd || isAdAvailable()) {
            Timber.tag(LOG_TAG).d("Ad is already loading or available.")
            return // Do not load ads if an ad is already loading or available.
        }

        isLoadingAd = true
        isAdLoaded = false
        Timber.tag(LOG_TAG).d("Loading app open ad.")
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            context.getString(R.string.admob_app_open), // Replace with your ad unit ID
            request,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    isAdLoaded = true
                    loadTime = Date().time
                    Timber.tag(LOG_TAG).d("onAdLoaded.")
                    callback?.invoke()
                }


                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    isAdLoaded = false
                    Timber.tag(LOG_TAG).d("onAdFailedToLoad: %s", loadAdError.message)
                }
            },
        )
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long = 4L): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }


    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }


    fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(
            activity,
            object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                    // Empty because the user will go back to the activity that shows the ad.
                }
            },
        )
    }


    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        if (isShowingAd) {
            Timber.tag(LOG_TAG).d("The app open ad is already showing.")
            return
        }

        if (!isAdAvailable()) {
            Timber.tag(LOG_TAG).d("The app open ad is not ready yet.")
            onShowAdCompleteListener.onShowAdComplete()
            if (googleMobileAdsConsentManager.canRequestAds) {
                loadAd(activity)
            }
            return
        }

        Timber.tag(LOG_TAG).d("Will show ad.")

        appOpenAd?.let {
            it.setOnPaidEventListener { adValue ->
                Timber.tag(LOG_TAG).d(
                    "App Open Ad: %s, %s, %s",
                    adValue.valueMicros,
                    adValue.currencyCode,
                    adValue.precisionType
                )
            }

            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onShowAdCompleteListener.onShowAdComplete()
                    appOpenAd = null
                    isShowingAd = false
                    isAdLoaded = false
                    Timber.tag(LOG_TAG).d("onAdDismissedFullScreenContent.")

                    if (googleMobileAdsConsentManager.canRequestAds) {
                        loadAd(activity)
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    isAdLoaded = false
                    Timber.tag(LOG_TAG).d("onAdFailedToShowFullScreenContent: %s", adError.message)

                    onShowAdCompleteListener.onShowAdComplete()
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        loadAd(activity)
                    }
                }


                override fun onAdShowedFullScreenContent() {
                    Timber.tag(LOG_TAG).d("onAdShowedFullScreenContent.")
                }
            }
            isShowingAd = true
            it.show(activity)
        }

    }

}