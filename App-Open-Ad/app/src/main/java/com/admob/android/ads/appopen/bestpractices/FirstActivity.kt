package com.admob.android.ads.appopen.bestpractices

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.admob.android.ads.appopen.bestpractices.databinding.ActivityFirstBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

class FirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstBinding

    // Number of milliseconds to count down before showing the app open ad.
    private var COUNTER_TIME_MILLISECONDS =
        5000L * 2 // sometimes 5 seconds is too Short, so we set it to 10 seconds by default


    private lateinit var adManager: AppOpenAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adManager = AppOpenAdManager(this)

        // Check status
        if (adManager.isMobileAdsInitialized()) {
            adManager.loadAd(this)
        } else {
            COUNTER_TIME_MILLISECONDS =
                5000L // If Mobile Ads SDK is not initialized, set a default countdown time (Only on first run)
        }

        createTimer()
    }

    var countDownTimer: CountDownTimer? = null

    /**
     * Create the countdown timer, which counts down to zero and show the app open ad.
     *
     * @param time the number of milliseconds that the timer counts down from
     */
    private fun createTimer() {
        binding.progressSplash.max = COUNTER_TIME_MILLISECONDS.toInt()

        countDownTimer =
            object : CountDownTimer(COUNTER_TIME_MILLISECONDS, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    Timber.d("onTick millisUntilFinished: $millisUntilFinished")

                    if (adManager.isAdLoaded) {
                        countDownTimer?.cancel()
                        countDownTimer = null
                        showAd() // Show the ad immediately if it's loaded
                        return
                    }

                    runOnUiThread {
                        binding.progressSplash.setProgress(
                            (COUNTER_TIME_MILLISECONDS - millisUntilFinished).toInt(), true
                        )
                    }
                }

                override fun onFinish() {
                    showAd()
                }
            }
        countDownTimer!!.start()
    }

    private fun showAd() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                binding.progressSplash.setProgress(COUNTER_TIME_MILLISECONDS.toInt(), true)
            }

            delay(2000L)

            withContext(Dispatchers.Main) {
                adManager.showAdIfAvailable(
                    this@FirstActivity, object : OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            startMainActivity()
                        }
                    })
            }
        }

    }

    /** Start the MainActivity. */
    fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}