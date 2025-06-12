package com.admob.android.ads.interstital.bestpractices

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import com.admob.android.ads.interstital.bestpractices.GoogleMobileAdsConsentManager.Companion.TEST_DEVICE_HASHED_ID
import com.admob.android.ads.interstital.bestpractices.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity(), DefaultLifecycleObserver {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private lateinit var adManger: AdManger

    override fun onDestroy() {
        super<AppCompatActivity>.onDestroy()
        lifecycle.removeObserver(this)
        adManger.isLoadedSuccess.removeObservers(this)
    }

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
        adManger = AdManger(this)



        gdprMessage()

        binding.githubLiner.setOnClickListener {
            openValidUrl(resources.getString(R.string.admob_best_practices_repo))

        }
        binding.admobLiner.setOnClickListener {
            openValidUrl(resources.getString(R.string.admob_google))
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    isEnabled = true // Disable the callback to prevent recursive calls
                    moveTaskToBack(true)
                }
            }
        })

        adManger.isLoadedSuccess.observe(this) { isLoading ->
            binding.showAd.isEnabled = isLoading
            if (isLoading) {
                Timber.tag(TAG).d("Ad is loaded and ready to be shown.")
                binding.showAd.text = "Show Ad"
            } else {
                Timber.tag(TAG).d("Ad is not loaded yet.")
                binding.showAd.text = "Loading Ad..."
            }
        }



        binding.showAd.setOnClickListener {
            adManger.showAd(this@MainActivity)
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
            }

            binding.privacySettings.isVisible =
                googleMobileAdsConsentManager.isPrivacyOptionsRequired

        }

        binding.privacySettings.isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired

        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk()
        }
        binding.showAd.isVisible = googleMobileAdsConsentManager.canRequestAds

        Timber.tag(TAG).d("Google Mobile Ads Consent Manager initialized.")

        binding.adInspector.setOnClickListener {
            Timber.tag(TAG).d("Ad Inspector clicked.")
            MobileAds.openAdInspector(this@MainActivity) { error ->
                // Error will be non-null if ad inspector closed due to an error.
                Timber.tag(TAG)
                    .d("Ad Inspector closed with error: %s", error?.message ?: "No error")
            }
        }

        binding.privacySettings.setOnClickListener {
            googleMobileAdsConsentManager.showPrivacyOptionsForm(this@MainActivity) { formError ->
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
            MobileAds.initialize(this@MainActivity) { initializationStatus ->
                Timber.tag(TAG)
                    .d("Mobile Ads SDK initialized: %s", initializationStatus.adapterStatusMap)

            }
        }

        adManger.loadAd()

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