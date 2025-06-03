package com.admob.android.ads.banner.bestpractices

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.admob.android.ads.banner.bestpractices.GoogleMobileAdsConsentManager.Companion.TEST_DEVICE_HASHED_ID
import com.admob.android.ads.banner.bestpractices.databinding.ActivityMainBinding
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import androidx.core.net.toUri

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
class MainActivity : AppCompatActivity(), DefaultLifecycleObserver {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lifecycle.addObserver(this)

        reportText("Welcome to AdMob Banner Best Practices!")
        reportText("Initializing Google Mobile Ads SDK...")
        gdprMessage()

        binding.githubLiner.setOnClickListener {
            openValidUrl(resources.getString(R.string.admob_best_practices_repo))

        }
        binding.admobLiner.setOnClickListener {
            openValidUrl(resources.getString(R.string.admob_google))
        }
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
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(this) { error ->
            if (error != null) {
                Timber.tag(TAG).d("${error.errorCode}: ${error.message}")
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            } else {
                reportText("Consent not obtained in current session. Cannot load ads.")
            }

            binding.privacySettings.isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired

            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
                invalidateOptionsMenu()
            }
        }

        binding.privacySettings.isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired

        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk()
        } else {
            reportText("Consent not obtained in current session. Cannot load ads.")
        }

        Timber.tag(TAG).d("Google Mobile Ads Consent Manager initialized.")

        binding.adInspector.setOnClickListener {
            Timber.tag(TAG).d("Ad Inspector clicked.")
            MobileAds.openAdInspector(this@MainActivity) { error ->
                // Error will be non-null if ad inspector closed due to an error.
                Timber.tag(TAG)
                    .d("Ad Inspector closed with error: %s", error?.message ?: "No error")
                reportText(
                    "Ad Inspector closed with error: ${error?.message ?: "No error"}"
                )
            }
        }

        binding.privacySettings.setOnClickListener {
            googleMobileAdsConsentManager.showPrivacyOptionsForm(this@MainActivity) { formError ->
                Timber.tag(TAG).d(
                    "Privacy options form closed with error: %s", formError?.message ?: "No error"
                )
                reportText(
                    "Privacy options form closed with error: ${formError?.message ?: "No error"}"
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
            MobileAds.initialize(this@MainActivity) { initializationStatus ->
                Timber.tag(TAG)
                    .d("Mobile Ads SDK initialized: %s", initializationStatus.adapterStatusMap)
            }

            runOnUiThread {
                loadBanner()
            }

        }
        reportText("Google Mobile Ads SDK initialized successfully.")

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
            reportText("No network connection available. Cannot load banner.")
            return
        }

        if (isNetworkStable().not()) {
            Timber.tag(TAG).d("Network is unstable. Banner may fail to load.")
            reportText("Network is unstable. Banner may fail to load.")
            //  return
        } else {
            reportText("Network is stable.")
        }

        reportText("Loading banner ad...")
        adView =  binding.adViewContainer
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
        adView?.adListener = object : AdListener() {
            /** Code to be executed when the user clicks on an ad.*/
            override fun onAdClicked() {
                Timber.tag(TAG).d("Ad clicked.")
                reportText("Ad clicked.")
            }

            /** Code to be executed when the user is about to return
             * to the app after tapping on an ad.
             * */
            override fun onAdClosed() {
                Timber.tag(TAG).d("Ad closed.")
                reportText("Ad closed.")
            }

            /** Code to be executed when an ad request fails.*/
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Timber.tag(TAG).d("Ad failed to load: ${adError.message} (Code: ${adError.code})")
                reportText("Ad failed to load: ${adError.message} (Code: ${adError.code})")
            }

            /**Code to be executed when an impression is recorded
             * for an ad.*/
            override fun onAdImpression() {
                Timber.tag(TAG).d("Ad impression recorded.")
                reportText("Ad impression recorded.")
            }

            /**Code to be executed when an ad finishes loading.*/
            override fun onAdLoaded() {
                Timber.tag(TAG).d("Ad loaded successfully.")
                reportText("Ad loaded successfully.")
            }

            /**Code to be executed when an ad opens an overlay that
             * covers the screen.*/
            override fun onAdOpened() {
                Timber.tag(TAG).d("Ad opened.")
                reportText("Ad opened.")
            }
        }
    }

    /**
     * Pauses the ad view when the activity is paused.
     * This helps conserve resources and follows AdMob best practices.
     */
    public override fun onPause() {
        adView?.pause()
        reportText("Activity paused.")
        super<AppCompatActivity>.onPause()
    }

    /**
     * Resumes the ad view when the activity returns to the foreground.
     * This ensures ads continue to be displayed properly after the app resumes.
     */
    public override fun onResume() {
        adView?.resume()
        reportText("Activity resumed.")
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
        reportText("Activity destroyed.")
        adView = null
        super<DefaultLifecycleObserver>.onDestroy(owner)
    }

    /**
     * Reports messages to the UI with timestamps.
     * This helper method:
     * - Adds timestamps to messages
     * - Updates the UI on the main thread
     * - Handles text view scrolling for long logs
     *
     * @param message The message to be displayed in the UI
     */
    private fun reportText(message: String) {
        runOnUiThread {
            val time = SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault()).format(Date())
            val logLine = "\n[$time] $message"

            binding.reportText.apply {
                append(logLine)
                visibility = if (text.isEmpty()) View.GONE else View.VISIBLE

                val scrollAmount = layout?.getLineTop(lineCount) ?: 0
                if (scrollAmount > height) {
                    scrollTo(0, scrollAmount - height)
                }
            }
        }
    }

    /**
     * Opens a URL in the device's default browser.
     * This method:
     * - Validates the URL format
     * - Checks if an app is available to handle the URL
     * - Opens the URL in the appropriate app
     */
    fun openValidUrl(url: String?) {
        if (!url.isNullOrBlank() && Patterns.WEB_URL.matcher(url).matches()) {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Timber.tag(TAG).d("No app found to open this link: $url")
            }
        } else {
            Timber.tag(TAG).d("Invalid URL: $url")
        }
    }
}




