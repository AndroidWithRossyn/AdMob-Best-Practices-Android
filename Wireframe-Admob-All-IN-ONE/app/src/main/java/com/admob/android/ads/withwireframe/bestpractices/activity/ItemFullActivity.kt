package com.admob.android.ads.withwireframe.bestpractices.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.admob.android.ads.withwireframe.bestpractices.R
import com.admob.android.ads.withwireframe.bestpractices.ads.GoogleMobileAdsConsentManager
import com.admob.android.ads.withwireframe.bestpractices.databinding.ActivityItemFullBinding
import com.admob.android.ads.withwireframe.bestpractices.utils.isNetworkConnected
import com.admob.android.ads.withwireframe.bestpractices.utils.isNetworkStable
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import timber.log.Timber

class ItemFullActivity : AppCompatActivity(), DefaultLifecycleObserver {


    private val TAG = ItemFullActivity::class.java.simpleName
    private lateinit var binding: ActivityItemFullBinding

    override fun onDestroy() {
        super<AppCompatActivity>.onDestroy()
        lifecycle.removeObserver(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemFullBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lifecycle.addObserver(this)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    isEnabled = true // Disable the callback to prevent recursive calls
                    finish()
                }
            }
        })

        binding.toolbar.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        loadBanner()
    }


    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private var adView: AdView? = null
    private fun loadBanner() {
        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.Companion.getInstance(applicationContext)
        if (!googleMobileAdsConsentManager.canRequestAds) {
            return
        }
        Timber.tag(TAG).d("Banner loading logic should be implemented here.")
        if (!isNetworkConnected()) {
            Timber.tag(TAG).d("No network connection available. Cannot load banner.")
            return
        }

        if (isNetworkStable().not()) {
            Timber.tag(TAG).d("Network is unstable. Banner may fail to load.")
            return
        }

        adView = binding.adViewContainer
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
        adView?.adListener = object : AdListener() {
            /** Code to be executed when the user clicks on an ad.*/
            override fun onAdClicked() {
                Timber.tag(TAG).d("Ad clicked.")

            }

            /** Code to be executed when the user is about to return
             * to the app after tapping on an ad.
             * */
            override fun onAdClosed() {
                Timber.tag(TAG).d("Ad closed.")

            }

            /** Code to be executed when an ad request fails.*/
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Timber.tag(TAG).d("Ad failed to load: ${adError.message} (Code: ${adError.code})")

            }

            /**Code to be executed when an impression is recorded
             * for an ad.*/
            override fun onAdImpression() {
                Timber.tag(TAG).d("Ad impression recorded.")

            }

            /**Code to be executed when an ad finishes loading.*/
            override fun onAdLoaded() {
                Timber.tag(TAG).d("Ad loaded successfully.")

            }

            /**Code to be executed when an ad opens an overlay that
             * covers the screen.*/
            override fun onAdOpened() {
                Timber.tag(TAG).d("Ad opened.")

            }
        }
    }

    /**
     * Pauses the ad view when the activity is paused.
     * This helps conserve resources and follows AdMob best practices.
     */
    public override fun onPause() {
        adView?.pause()
        super<AppCompatActivity>.onPause()
    }

    /**
     * Resumes the ad view when the activity returns to the foreground.
     * This ensures ads continue to be displayed properly after the app resumes.
     */
    public override fun onResume() {
        adView?.resume()
        super<AppCompatActivity>.onResume()
    }

    /**
     * Performs cleanup when the activity is destroyed.
     * This method:
     * - Removes the ad view from its parent
     * - Properly destroys the ad to prevent memory leaks
     * - Cleans up resources
     *
     * @param owner The LifecycleOwner whose lifecycle is being destroyed
     */
    override fun onDestroy(owner: LifecycleOwner) {
        val parentView = adView?.parent
        if (parentView is ViewGroup) {
            parentView.removeView(adView)
        }
        adView?.destroy()
        adView = null
        super<DefaultLifecycleObserver>.onDestroy(owner)
    }
}