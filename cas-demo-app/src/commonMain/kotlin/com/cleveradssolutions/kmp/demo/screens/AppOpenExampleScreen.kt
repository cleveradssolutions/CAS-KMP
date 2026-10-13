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
import com.cleveradssolutions.plugin.kmp.CASAppOpenAd
import com.cleveradssolutions.plugin.kmp.CASError
import com.cleveradssolutions.plugin.kmp.CASMobileAds
import com.cleveradssolutions.plugin.kmp.CASScreenAdContentCallback
import com.cleveradssolutions.kmp.demo.TAG
import com.cleveradssolutions.kmp.demo.components.FullscreenControls
import com.cleveradssolutions.kmp.demo.components.RETRY_DELAY_MS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppOpenExampleScreen() {
    val scope = rememberCoroutineScope()
    val retryJob = remember { arrayOf<Job?>(null) }
    var loaded by remember { mutableStateOf(false) }

    val appOpen = remember {
        lateinit var adRef: CASAppOpenAd

        adRef = CASAppOpenAd(
            casId = CASMobileAds.TEST_ID,
            autoLoad = false,
            autoShow = false,
            callback = object : CASScreenAdContentCallback {
                override fun onAdLoaded(ad: CASAdContentInfo) {
                    println("$TAG: AppOpen Ad loaded")
                    loaded = true
                    retryJob[0]?.cancel()
                    retryJob[0] = null
                }

                override fun onAdFailedToLoad(format: CASAdFormat, error: CASError) {
                    println("$TAG: AppOpen Ad failed to load: $error")
                    loaded = false
                    retryJob[0]?.cancel()
                    retryJob[0] = scope.launch {
                        delay(RETRY_DELAY_MS)
                        adRef.load()
                    }
                }

                override fun onAdClicked(ad: CASAdContentInfo) {
                    println("$TAG: AppOpen Ad clicked")
                }

                override fun onAdShowed(ad: CASAdContentInfo) {
                    println("$TAG: AppOpen Ad showed")
                }

                override fun onAdFailedToShow(format: CASAdFormat, error: CASError) {
                    println("$TAG: AppOpen Ad failed to show: $error")
                    loaded = false
                    adRef.load()
                }

                override fun onAdDismissed(ad: CASAdContentInfo) {
                    println("$TAG: AppOpen Ad dismissed")
                    loaded = false
                    adRef.load()
                }
            },
        ).apply {
            impressionCallback = { ad ->
                println("$TAG: AppOpen Ad impression: $ad")
            }
        }

        adRef
    }

    LaunchedEffect(Unit) {
        appOpen.load()
    }

    DisposableEffect(Unit) {
        onDispose {
            retryJob[0]?.cancel()
            appOpen.destroy()
        }
    }

    FullscreenControls(
        title = "App Open Ad",
        loaded = loaded && appOpen.isLoaded,
        onLoad = { appOpen.load() },
        onShow = {
            loaded = false
            appOpen.show()
        },
    )
}