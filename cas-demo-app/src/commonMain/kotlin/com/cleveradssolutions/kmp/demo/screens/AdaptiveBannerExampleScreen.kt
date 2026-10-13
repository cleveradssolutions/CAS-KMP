package com.cleveradssolutions.kmp.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.cleveradssolutions.plugin.kmp.CASAdContentInfo
import com.cleveradssolutions.plugin.kmp.composable.CASBannerAd
import com.cleveradssolutions.plugin.kmp.CASBannerAdContent
import com.cleveradssolutions.plugin.kmp.CASBannerAdContentCallback
import com.cleveradssolutions.plugin.kmp.CASBannerSize
import com.cleveradssolutions.plugin.kmp.CASError
import com.cleveradssolutions.plugin.kmp.CASMobileAds
import com.cleveradssolutions.kmp.demo.TAG
import com.cleveradssolutions.kmp.demo.components.ScreenBackground

@Composable
fun AdaptiveBannerExampleScreen() {
    var loaded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        contentAlignment = Alignment.Center,
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
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
                            loaded = true
                        }

                        override fun onBannerAdFailed(error: CASError) {
                            println("$TAG: Adaptive banner ad failed: $error")
                            loaded = false
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
                loaded = false
                bannerAd.load()
            }

            DisposableEffect(bannerAd) {
                onDispose { bannerAd.destroy() }
            }

            CASBannerAd(
                adContent = bannerAd,
                modifier = Modifier
                    .width(bannerSize.width.dp)
                    .height(bannerSize.height.dp)
                    .alpha(if (loaded) 1f else 0.35f),
            )
        }
    }
}