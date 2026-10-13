package com.cleveradssolutions.kmp.demo.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cleveradssolutions.plugin.kmp.CASAdContentInfo
import com.cleveradssolutions.plugin.kmp.CASAdFormat
import com.cleveradssolutions.plugin.kmp.CASError
import com.cleveradssolutions.plugin.kmp.CASMobileAds
import com.cleveradssolutions.plugin.kmp.CASRewardedAd
import com.cleveradssolutions.plugin.kmp.CASScreenAdContentCallback
import com.cleveradssolutions.kmp.demo.TAG
import com.cleveradssolutions.kmp.demo.components.FullscreenControls

@Composable
fun RewardedExampleScreen() {
    var loaded by remember { mutableStateOf(false) }

    val rewarded = remember {
        CASRewardedAd(
            casId = CASMobileAds.TEST_ID,
            autoLoad = true,
            callback = object : CASScreenAdContentCallback {
                override fun onAdLoaded(ad: CASAdContentInfo) {
                    println("$TAG: Rewarded Ad loaded")
                    loaded = true
                }

                override fun onAdFailedToLoad(format: CASAdFormat, error: CASError) {
                    println("$TAG: Rewarded Ad failed to load: $error")
                    loaded = false
                }

                override fun onAdClicked(ad: CASAdContentInfo) {
                    println("$TAG: Rewarded Ad clicked")
                }

                override fun onAdShowed(ad: CASAdContentInfo) {
                    println("$TAG: Rewarded Ad showed")
                }

                override fun onAdFailedToShow(format: CASAdFormat, error: CASError) {
                    println("$TAG: Rewarded Ad failed to show: $error")
                    loaded = false
                }

                override fun onAdDismissed(ad: CASAdContentInfo) {
                    println("$TAG: Rewarded Ad dismissed")
                    loaded = false
                }
            },
        ).apply {
            impressionCallback = { ad ->
                println("$TAG: Rewarded Ad impression: $ad")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { rewarded.destroy() }
    }

    FullscreenControls(
        title = "Rewarded Ad",
        loaded = loaded && rewarded.isLoaded,
        onLoad = { rewarded.load() },
        onShow = {
            loaded = false
            rewarded.show(
                rewardEarnedListener = { ad ->
                    println("$TAG: Rewarded Ad gives a reward to the user: $ad")
                },
            )
        },
    )
}