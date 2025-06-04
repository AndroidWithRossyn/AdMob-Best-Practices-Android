package com.admob.android.ads.withwireframe.bestpractices.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowMetrics
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.admob.android.ads.withwireframe.bestpractices.R
import com.admob.android.ads.withwireframe.bestpractices.adapters.LayoutItemAdapter
import com.admob.android.ads.withwireframe.bestpractices.ads.GoogleMobileAdsConsentManager
import com.admob.android.ads.withwireframe.bestpractices.ads.GoogleMobileAdsConsentManager.Companion.TEST_DEVICE_HASHED_ID
import com.admob.android.ads.withwireframe.bestpractices.databinding.ActivityHomeBinding
import com.admob.android.ads.withwireframe.bestpractices.utils.isNetworkConnected
import com.admob.android.ads.withwireframe.bestpractices.utils.isNetworkStable
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Main activity that demonstrates best practices for implementing AdMob banner ads in an Android app.
 * This activity serves as the primary interface for users to interact with banner advertisements
 * while following Google's recommended implementation guidelines.
 *
 * Key Features:
 * - Implements banner ad integration
 * - Handles ad lifecycle management
 * - Demonstrates proper ad loading and error handling
 *
 * @author Rohitraj Khorwal
 * Created on: May 31, 2025
 * @since v1.0.0
 */
class HomeActivity : AppCompatActivity(), DefaultLifecycleObserver {

    private lateinit var binding: ActivityHomeBinding
    private val TAG = HomeActivity::class.java.simpleName

    /**
     * Cleans up resources when the activity is being destroyed.
     * This method ensures proper cleanup by removing the lifecycle observer
     * to prevent any memory leaks.
     */
    override fun onDestroy() {
        super<AppCompatActivity>.onDestroy()
        lifecycle.removeObserver(this)
    }

    /**
     * Initializes the activity and sets up the user interface.
     * This method handles:
     * - Edge-to-edge display setup for a better visual experience
     * - View binding initialization
     * - Window insets configuration for proper layout padding
     * - Lifecycle observer registration
     *
     * @param savedInstanceState Contains data most recently supplied in onSaveInstanceState if the activity is being re-initialized
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lifecycle.addObserver(this)

        gdprMessage()

        binding.settingsFuture.setOnClickListener {
            Timber.tag(TAG).d("Settings button clicked.")
            startActivity(Intent(this@HomeActivity, AboutActivity::class.java))
        }

        binding.recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@HomeActivity)
            setHasFixedSize(true)
            adapter = LayoutItemAdapter(){
                startActivity(Intent(this@HomeActivity, ItemFullActivity::class.java))
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    isEnabled = true // Disable the callback to prevent recursive calls
                    moveTaskToBack(true)
                }
            }
        })
    }


    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    /**
     * Handles the GDPR consent message and initializes the Google Mobile Ads SDK.
     * This method:
     * - Checks and manages user consent for personalized ads
     * - Sets up privacy settings button visibility
     * - Configures ad inspector and privacy options form listeners
     */
    private fun gdprMessage() {
        Timber.tag(TAG).d("Google Mobile Ads SDK Version: %s", MobileAds.getVersion())
        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.Companion.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(this) { error ->
            if (error != null) {
                Timber.tag(TAG).d("${error.errorCode}: ${error.message}")
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }

            binding.privacySettings.isVisible =
                googleMobileAdsConsentManager.isPrivacyOptionsRequired

            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
                invalidateOptionsMenu()
            }
        }

        binding.privacySettings.isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired

        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk()
        }

        Timber.tag(TAG).d("Google Mobile Ads Consent Manager initialized.")

        binding.adInspector.setOnClickListener {
            Timber.tag(TAG).d("Ad Inspector clicked.")
            MobileAds.openAdInspector(this@HomeActivity) { error ->
                // Error will be non-null if ad inspector closed due to an error.
                Timber.tag(TAG)
                    .d("Ad Inspector closed with error: %s", error?.message ?: "No error")

            }
        }

        binding.privacySettings.setOnClickListener {
            googleMobileAdsConsentManager.showPrivacyOptionsForm(this@HomeActivity) { formError ->
                Timber.tag(TAG).d(
                    "Privacy options form closed with error: %s", formError?.message ?: "No error"
                )

            }
        }
    }

    /**
     * Initializes the Mobile Ads SDK if not already initialized.
     * This method ensures the SDK is initialized only once and sets up:
     * - Test device configuration
     * - SDK initialization
     * - Banner ad loading
     */
    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf(TEST_DEVICE_HASHED_ID)).build()
        )

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@HomeActivity) { initializationStatus ->
                Timber.tag(TAG)
                    .d("Mobile Ads SDK initialized: %s", initializationStatus.adapterStatusMap)
            }

            runOnUiThread {
                loadBanner()
            }

        }
    }

    private var adView: AdView? = null

    /**
     * Loads and displays a banner advertisement.
     * This method handles:
     * - Network connectivity check
     * - Banner ad creation and configuration
     * - Ad loading and event listening
     * - Error handling and user feedback
     */
    fun loadBanner() {
        Timber.tag(TAG).d("Banner loading logic should be implemented here.")
        if (!isNetworkConnected()) {
            Timber.tag(TAG).d("No network connection available. Cannot load banner.")
            binding.adViewContainer.root.setVisibility(ViewGroup.GONE)
            return
        }

        if (isNetworkStable().not()) {
            Timber.tag(TAG).d("Network is unstable. Banner may fail to load.")
            //  return
        }


        startSimmer()
        Timber.tag(TAG).d("Loading banner ad...")
        val adView = AdView(this)
        adView.adUnitId = resources.getString(R.string.admob_banner_id)
        adView.setAdSize(adSize())
        this.adView = adView
        binding.adViewContainer.bannerAd.removeAllViews()
        binding.adViewContainer.bannerAd.addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
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
                startSimmer()
            }

            /**Code to be executed when an impression is recorded
             * for an ad.*/
            override fun onAdImpression() {
                Timber.tag(TAG).d("Ad impression recorded.")
            }

            /**Code to be executed when an ad finishes loading.*/
            override fun onAdLoaded() {
                Timber.tag(TAG).d("Ad loaded successfully.")
                stopSimmer()
            }

            /**Code to be executed when an ad opens an overlay that
             * covers the screen.*/
            override fun onAdOpened() {
                Timber.tag(TAG).d("Ad opened.")
            }
        }
    }

    private fun adSize(): AdSize {
        val displayMetrics = resources.displayMetrics
        val adWidthPixels = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            windowMetrics.bounds.width()
        } else {
            displayMetrics.widthPixels
        }
        val density = displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            this, adWidth
        )
    }

    private fun startSimmer() {
        binding.adViewContainer.root.setVisibility(ViewGroup.VISIBLE)
        binding.adViewContainer.bannerLoader.setVisibility(ViewGroup.VISIBLE)
        binding.adViewContainer.adLabel.setVisibility(ViewGroup.VISIBLE)
        binding.adViewContainer.bannerLoader.startShimmer()

    }

    private fun stopSimmer() {
        binding.adViewContainer.bannerLoader.stopShimmer()
        binding.adViewContainer.bannerLoader.setVisibility(ViewGroup.INVISIBLE)
        binding.adViewContainer.adLabel.setVisibility(ViewGroup.INVISIBLE)
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




