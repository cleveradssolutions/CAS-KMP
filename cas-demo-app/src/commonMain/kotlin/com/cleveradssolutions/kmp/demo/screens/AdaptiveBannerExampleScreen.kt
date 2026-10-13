package com.cleveradssolutions.kmp.demo.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cleveradssolutions.plugin.kmp.CASAdContentInfo
import com.cleveradssolutions.plugin.kmp.composable.CASBannerAd
import com.cleveradssolutions.plugin.kmp.CASBannerAdContent
import com.cleveradssolutions.plugin.kmp.CASBannerAdContentCallback
import com.cleveradssolutions.plugin.kmp.CASBannerSize
import com.cleveradssolutions.plugin.kmp.CASError
import com.cleveradssolutions.plugin.kmp.CASMobileAds
import com.cleveradssolutions.kmp.demo.TAG
import com.cleveradssolutions.kmp.demo.components.DemoCard
import com.cleveradssolutions.kmp.demo.components.DemoScreenContainer
import com.cleveradssolutions.kmp.demo.components.DemoSubtitle
import com.cleveradssolutions.kmp.demo.components.DemoTitle

@Composable
fun AdaptiveBannerExampleScreen() {
    var statusText by remember { mutableStateOf("Loading adaptive banner…") }

    DemoScreenContainer(
        anchoredAd = {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                // An adaptive banner is sized from the width it is given, so every rotation asks
                // the SDK for a new size and loads a new ad for it.
                val bannerWidthDp = maxWidth.value.toInt().coerceAtLeast(300)

                val bannerSize = remember(bannerWidthDp) {
                    CASBannerSize.getAdaptiveBanner(bannerWidthDp)
                }

                val bannerAd = remember(bannerSize) {
                    CASBannerAdContent(
                        casId = CASMobileAds.TEST_ID,
                        size = bannerSize,
                        autoLoad = false,
                        refreshInterval = 30,
                        callback = object : CASBannerAdContentCallback {
                            override fun onBannerAdLoaded(info: CASAdContentInfo) {
                                println("$TAG: Adaptive banner ad loaded")
                                statusText =
                                    "Loaded ${bannerSize.width} x ${bannerSize.height} dp"
                            }

                            override fun onBannerAdFailed(error: CASError) {
                                println("$TAG: Adaptive banner ad failed: $error")
                                statusText = "Failed: ${error.message}"
                            }

                            override fun onBannerAdClicked(info: CASAdContentInfo) {
                                println("$TAG: Adaptive banner ad clicked")
                            }

                            override fun onBannerAdImpression(info: CASAdContentInfo) {
                                println("$TAG: Adaptive banner ad impression: $info")
                            }
                        },
                    )
                }

                LaunchedEffect(bannerAd) {
                    statusText = "Loading ${bannerSize.width} x ${bannerSize.height} dp…"
                    bannerAd.load()
                }

                DisposableEffect(bannerAd) {
                    onDispose { bannerAd.destroy() }
                }

                // The slot keeps the banner height while the ad loads, so the screen does not jump
                // once it arrives.
                Box(
                    modifier = Modifier.height(bannerSize.height.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CASBannerAd(
                        adContent = bannerAd,
                        modifier = Modifier
                            .width(bannerSize.width.dp)
                            .height(bannerSize.height.dp),
                    )
                }
            }
        },
    ) {
        DemoCard {
            DemoTitle(text = "Adaptive Banner Ad")
            DemoSubtitle(text = statusText)
        }
    }
}
