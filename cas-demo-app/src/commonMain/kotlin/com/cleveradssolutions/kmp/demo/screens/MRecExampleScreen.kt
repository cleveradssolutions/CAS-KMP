package com.cleveradssolutions.kmp.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.cleveradssolutions.kmp.demo.components.AppButton
import com.cleveradssolutions.kmp.demo.components.DemoCard
import com.cleveradssolutions.kmp.demo.components.DemoTitle
import com.cleveradssolutions.kmp.demo.components.ScreenBackground

@Composable
fun MRecExampleScreen() {
    var visible by rememberSaveable { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }
    var reloadKey by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        DemoCard {
            DemoTitle(text = "MREC Ad")

            AppButton(
                title = if (visible) "Hide" else "Show",
                onClick = { visible = !visible },
            )
            AppButton(
                title = "Reload",
                onClick = {
                    loaded = false
                    reloadKey++
                },
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = if (visible && loaded) 0.dp else 320.dp)
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            key(reloadKey) {
                val bannerSize = remember { CASBannerSize.MEDIUM_RECTANGLE }

                val bannerAd = remember(bannerSize) {
                    CASBannerAdContent(
                        casId = CASMobileAds.TEST_ID,
                        size = bannerSize,
                        autoLoad = false,
                        refreshInterval = 30,
                        callback = object : CASBannerAdContentCallback {
                            override fun onBannerAdLoaded(info: CASAdContentInfo) {
                                println("$TAG: MREC Banner Ad loaded")
                                loaded = true
                            }

                            override fun onBannerAdFailed(error: CASError) {
                                println("$TAG: MREC Banner Ad failed: $error")
                                loaded = false
                            }

                            override fun onBannerAdClicked(info: CASAdContentInfo) {
                                println("$TAG: MREC Banner Ad clicked")
                            }

                            override fun onBannerAdImpression(info: CASAdContentInfo) {
                                println("$TAG: MREC Banner Ad impression: $info")
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
                        .height(bannerSize.height.dp),
                )
            }
        }
    }
}