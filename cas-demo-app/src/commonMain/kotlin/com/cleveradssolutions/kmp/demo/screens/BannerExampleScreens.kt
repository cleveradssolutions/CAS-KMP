package com.cleveradssolutions.kmp.demo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.cleveradssolutions.kmp.demo.components.DemoScreenContainer
import com.cleveradssolutions.kmp.demo.components.DemoTitle

@Composable
fun BannerExampleScreen() {
    var visible by rememberSaveable { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }
    var reloadKey by remember { mutableStateOf(0) }

    val bannerSize = remember { CASBannerSize.BANNER }

    DemoScreenContainer(
        anchoredAd = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 6.dp)
                    // The slot keeps the banner height even while the banner is hidden, so showing
                    // an ad never pushes the controls around and never covers them.
                    .height(bannerSize.height.dp),
                contentAlignment = Alignment.Center,
            ) {
                // A fixed size on purpose. An adaptive banner depends on the available width and has
                // to be reloaded whenever that changes, which this screen is not about; see the
                // adaptive banner screen for that. Keeping the size fixed also means a rotation does
                // not recreate the ad, so the loaded ad stays loaded.
                key(reloadKey) {
                    val bannerAd = remember(bannerSize) {
                        CASBannerAdContent(
                            casId = CASMobileAds.TEST_ID,
                            size = bannerSize,
                            autoLoad = false,
                            refreshInterval = 30,
                            callback = object : CASBannerAdContentCallback {
                                override fun onBannerAdLoaded(info: CASAdContentInfo) {
                                    println("$TAG: Banner Ad loaded")
                                    loaded = true
                                }

                                override fun onBannerAdFailed(error: CASError) {
                                    println("$TAG: Banner Ad failed: $error")
                                    loaded = false
                                }

                                override fun onBannerAdClicked(info: CASAdContentInfo) {
                                    println("$TAG: Banner Ad clicked")
                                }

                                override fun onBannerAdImpression(info: CASAdContentInfo) {
                                    println("$TAG: Banner Ad impression: $info")
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
                            .offset(
                                y = if (visible && loaded) {
                                    0.dp
                                } else {
                                    // Past the bottom of the screen, not just past the slot: a
                                    // banner that is merely clipped still counts as visible for
                                    // the SDK and would collect impressions nobody can see.
                                    (bannerSize.height + 70).dp
                                },
                            ),
                    )
                }
            }
        },
    ) {
        DemoCard {
            DemoTitle(text = "Banner Ad")

            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                AppButton(
                    title = if (visible) "Hide Banner" else "Show Banner",
                    onClick = { visible = !visible },
                )
                AppButton(
                    title = "Reload Ad",
                    onClick = {
                        loaded = false
                        reloadKey++
                    },
                )
            }
        }
    }
}
