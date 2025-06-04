package com.admob.android.ads.appopen.bestpractices

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm.OnConsentFormDismissedListener
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

/**
 * The Google Mobile Ads SDK provides the User Messaging Platform (Google's IAB Certified consent
 * management platform) as one solution to capture consent for users in GDPR impacted countries.
 * This is an example and you can choose another consent management platform to capture consent.
 *
 * <href href="https://github.com/googleads/googleads-mobile-android-examples">Learn more</a>.
 */
class GoogleMobileAdsConsentManager private constructor(context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    /** Interface definition for a callback to be invoked when consent gathering is complete. */
    fun interface OnConsentGatheringCompleteListener {
        fun consentGatheringComplete(error: FormError?)
    }

    /** Helper variable to determine if the app can request ads. */
    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    // [START is_privacy_options_required]
    /** Helper variable to determine if the privacy options form is required. */
    val isPrivacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    // [END is_privacy_options_required]

    /**
     * Helper method to call the UMP SDK methods to request consent information and load/show a
     * consent form if necessary.
     */
    fun gatherConsent(
        activity: Activity,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener,
    ) {
        // For testing purposes, you can force a DebugGeography of EEA or NOT_EEA.
        val debugSettings = ConsentDebugSettings.Builder(activity)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId(TEST_DEVICE_HASHED_ID).build()

        val params = ConsentRequestParameters.Builder()
        if (BuildConfig.DEBUG) {
            params.setConsentDebugSettings(debugSettings)
        }
        val buildParams = params.build()
        // [START request_consent_info_update]
        // Requesting an update to consent information should be called on every app launch.
        consentInformation.requestConsentInfoUpdate(
            activity,
            buildParams,
            {
                // Called when consent information is successfully updated.
                // [START_EXCLUDE silent]
                loadAndShowConsentFormIfRequired(activity, onConsentGatheringCompleteListener)
                // [END_EXCLUDE]
            },
            { requestConsentError ->
                // Called when there's an error updating consent information.
                // [START_EXCLUDE silent]
                onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError)
                // [END_EXCLUDE]
            },
        )
        // [END request_consent_info_update]
    }

    private fun loadAndShowConsentFormIfRequired(
        activity: Activity,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener,
    ) {
        // [START load_and_show_consent_form]
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
            // Consent gathering process is complete.
            // [START_EXCLUDE silent]
            onConsentGatheringCompleteListener.consentGatheringComplete(formError)
            // [END_EXCLUDE]
        }
        // [END load_and_show_consent_form]
    }

    /** Helper method to call the UMP SDK method to show the privacy options form. */
    fun showPrivacyOptionsForm(
        activity: MainActivity,
        onConsentFormDismissedListener: OnConsentFormDismissedListener,
    ) {
        // [START present_privacy_options_form]
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
        // [END present_privacy_options_form]
    }

    companion object {
        @Volatile
        private var instance: GoogleMobileAdsConsentManager? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: GoogleMobileAdsConsentManager(context).also { instance = it }
        }

        // Check your logcat output for the test device hashed ID e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device" or
        // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
        // a debug device".
        const val TEST_DEVICE_HASHED_ID = "74ABBD12ABE4E546DEF890676942C4CD"
    }
}
