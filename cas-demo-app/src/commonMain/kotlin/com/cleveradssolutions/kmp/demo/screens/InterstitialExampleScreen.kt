package com.cleveradssolutions.kmp.demo.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.cleveradssolutions.plugin.kmp.CASAdContentInfo
import com.cleveradssolutions.plugin.kmp.CASAdFormat
import com.cleveradssolutions.plugin.kmp.CASError
import com.cleveradssolutions.plugin.kmp.CASInterstitialAd
import com.cleveradssolutions.plugin.kmp.CASMobileAds
import com.cleveradssolutions.plugin.kmp.CASScreenAdContentCallback
import com.cleveradssolutions.kmp.demo.TAG
import com.cleveradssolutions.kmp.demo.components.FullscreenControls
import com.cleveradssolutions.kmp.demo.components.RETRY_DELAY_MS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InterstitialExampleScreen() {
    val scope = rememberCoroutineScope()
    val retryJob = remember { arrayOf<Job?>(null) }
    var loaded by remember { mutableStateOf(false) }

    val interstitial = remember {
        lateinit var adRef: CASInterstitialAd

        adRef = CASInterstitialAd(
            casId = CASMobileAds.TEST_ID,
            autoLoad = false,
            callback = object : CASScreenAdContentCallback {
                override fun onAdLoaded(ad: CASAdContentInfo) {
                    println("$TAG: Interstitial Ad loaded")
                    loaded = true
                    retryJob[0]?.cancel()
                    retryJob[0] = null
                }

                override fun onAdFailedToLoad(format: CASAdFormat, error: CASError) {
                    println("$TAG: Interstitial Ad failed to load: $error")
                    loaded = false
                    retryJob[0]?.cancel()
                    retryJob[0] = scope.launch {
                        delay(RETRY_DELAY_MS)
                        adRef.load()
                    }
                }

                override fun onAdClicked(ad: CASAdContentInfo) {
                    println("$TAG: Interstitial Ad clicked")
                }

                override fun onAdShowed(ad: CASAdContentInfo) {
                    println("$TAG: Interstitial Ad showed")
                }

                override fun onAdFailedToShow(format: CASAdFormat, error: CASError) {
                    println("$TAG: Interstitial Ad failed to show: $error")
                    loaded = false
                    adRef.load()
                }

                override fun onAdDismissed(ad: CASAdContentInfo) {
                    println("$TAG: Interstitial Ad dismissed")
                    loaded = false
                    adRef.load()
                }
            },
        ).apply {
            impressionCallback = { ad ->
                println("$TAG: Interstitial Ad impression: $ad")
            }
        }

        adRef
    }

    LaunchedEffect(Unit) {
        interstitial.load()
    }

    DisposableEffect(Unit) {
        onDispose {
            retryJob[0]?.cancel()
            interstitial.destroy()
        }
    }

    FullscreenControls(
        title = "Interstitial Ad",
        loaded = loaded && interstitial.isLoaded,
        onLoad = { interstitial.load() },
        onShow = {
            loaded = false
            interstitial.show()
        },
    )
}