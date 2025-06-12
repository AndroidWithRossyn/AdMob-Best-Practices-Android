package com.admob.android.ads.interstital.bestpractices

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.admob.android.ads.interstital.bestpractices.GoogleMobileAdsConsentManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import timber.log.Timber

class AdManger(val context: Context) {

    val TAG = "AdManger"

    private val loadedSuccess: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoadedSuccess: LiveData<Boolean> = loadedSuccess

    private var adIsLoading: Boolean = false

    var mInterstitialAd: InterstitialAd? = null


    fun loadAd() {

        if (adIsLoading || mInterstitialAd != null) {
            return
        }
        adIsLoading = true
        loadedSuccess.value = false
        InterstitialAd.load(
            context,
            context.getString(R.string.admob_interstitial),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Timber.tag(TAG).d("Ad was loaded.")
                    mInterstitialAd = ad
                    loadedSuccess.value = true
                    adIsLoading = false
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    loadedSuccess.value = false
                    adIsLoading = false
                    Timber.tag(TAG).d(adError.message)
                    Timber.tag(TAG)
                        .e("onAdFailedToLoad() with domain: ${adError.domain}, code: ${adError.code}, \" + \"message: ${adError.message}")
                }
            })
    }

    fun showAd(activity: Activity) {
        mInterstitialAd?.let { interstitialAd ->
            interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Timber.tag(TAG).d("Ad was dismissed.")
                    mInterstitialAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Timber.tag(TAG).d("Ad failed to show.")
                    mInterstitialAd = null
                    loadAd()
                }

                override fun onAdShowedFullScreenContent() {
                    Timber.tag(TAG).d("Ad showed fullscreen content.")
                }

                override fun onAdImpression() {
                    Timber.tag(TAG).d("Ad recorded an impression.")
                }

                override fun onAdClicked() {
                    Timber.tag(TAG).d("Ad was clicked.")
                }
            }

            interstitialAd.show(activity)
        }
    }
}