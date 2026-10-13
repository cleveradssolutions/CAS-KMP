package com.cleveradssolutions.kmp.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleveradssolutions.plugin.kmp.composable.AdAdvertiserText
import com.cleveradssolutions.plugin.kmp.composable.AdBodyText
import com.cleveradssolutions.plugin.kmp.composable.AdCallToAction
import com.cleveradssolutions.plugin.kmp.composable.AdChoices
import com.cleveradssolutions.plugin.kmp.composable.AdHeadlineText
import com.cleveradssolutions.plugin.kmp.composable.AdIconImage
import com.cleveradssolutions.plugin.kmp.composable.AdLabelText
import com.cleveradssolutions.plugin.kmp.composable.AdMedia
import com.cleveradssolutions.plugin.kmp.composable.AdPriceText
import com.cleveradssolutions.plugin.kmp.composable.AdReviewCountText
import com.cleveradssolutions.plugin.kmp.composable.AdStarRating
import com.cleveradssolutions.plugin.kmp.composable.AdStoreText
import com.cleveradssolutions.plugin.kmp.CASAdContentInfo
import com.cleveradssolutions.plugin.kmp.CASAdImpressionCallback
import com.cleveradssolutions.plugin.kmp.CASError
import com.cleveradssolutions.plugin.kmp.CASMobileAds
import com.cleveradssolutions.plugin.kmp.composable.CASNativeAd
import com.cleveradssolutions.plugin.kmp.CASNativeAdChoicesPlacement
import com.cleveradssolutions.plugin.kmp.CASNativeAdContent
import com.cleveradssolutions.plugin.kmp.CASNativeAdContentCallback
import com.cleveradssolutions.plugin.kmp.CASNativeAdLoader
import com.cleveradssolutions.plugin.kmp.composable.CASNativeAdTemplate
import com.cleveradssolutions.plugin.kmp.CASNativeScaleType
import com.cleveradssolutions.plugin.kmp.composable.NativeAdScope
import com.cleveradssolutions.kmp.demo.TAG
import com.cleveradssolutions.kmp.demo.components.AppButton
import com.cleveradssolutions.kmp.demo.components.DemoCard
import com.cleveradssolutions.kmp.demo.components.DemoScreenContainer
import com.cleveradssolutions.kmp.demo.components.DemoSubtitle
import com.cleveradssolutions.kmp.demo.components.DemoTitle
import com.cleveradssolutions.kmp.demo.components.ErrorText
import com.cleveradssolutions.kmp.demo.components.SecondaryText

@Composable
fun NativeExampleScreen() {
    NativeExampleContent(
        title = "Native Ad",
        usesTemplate = false,
    )
}

@Composable
fun NativeTemplateSizeExampleScreen() {
    NativeExampleContent(
        title = "Native Ad (Template Size)",
        usesTemplate = true,
    )
}

@Composable
private fun NativeExampleContent(
    title: String,
    usesTemplate: Boolean,
) {
    var statusText by remember { mutableStateOf("Loading native ad…") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableStateOf(0) }
    var muted by rememberSaveable { mutableStateOf(false) }
    var adChoicesPlacement by remember {
        mutableStateOf(CASNativeAdChoicesPlacement.TOP_RIGHT)
    }

    var adContent by remember { mutableStateOf<CASNativeAdContent?>(null) }
    var loader by remember { mutableStateOf<CASNativeAdLoader?>(null) }

    val templateSizeHeightDp = 250

    LaunchedEffect(reloadKey, muted, adChoicesPlacement) {
        statusText = "Loading native ad…"
        errorText = null

        adContent?.destroy()
        adContent = null

        val newLoader = CASNativeAdLoader(
            casId = CASMobileAds.TEST_ID,
            startVideoMuted = muted,
            adChoicesPlacement = adChoicesPlacement,
            callback = object : CASNativeAdContentCallback {
                override fun onNativeAdLoaded(ad: CASNativeAdContent) {
                    println("$TAG: Native Ad loaded")

                    ad.impressionCallback = CASAdImpressionCallback { info: CASAdContentInfo ->
                        println("$TAG: Native Ad impression: $info")
                        statusText = "Native Ad impression: ${info.sourceName}"
                    }

                    adContent = ad
                    statusText = "Native Ad loaded"
                    errorText = null
                }

                override fun onNativeAdFailedToLoad(error: CASError) {
                    println("$TAG: Native Ad failed to load: $error")

                    adContent = null
                    statusText = "Native Ad failed"
                    errorText = error.message
                }

                override fun onNativeAdClicked(ad: CASNativeAdContent) {
                    println("$TAG: Native Ad clicked")
                    statusText = "Native Ad clicked"
                }

                override fun onNativeAdFailedToShow(
                    ad: CASNativeAdContent,
                    error: CASError,
                ) {
                    println("$TAG: Native Ad failed to show: $error")

                    statusText = "Native Ad failed to show"
                    errorText = error.message
                }
            },
        )

        loader = newLoader
        newLoader.load()
    }

    DisposableEffect(Unit) {
        onDispose {
            adContent?.destroy()
            adContent = null
            loader = null
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val screenWidthDp = maxWidth.value.toInt()
        val cardWidthDp = minOf(screenWidthDp - 40, 420)
        val nativeWidthDp = maxOf(cardWidthDp - 68, 300)

        DemoScreenContainer {
            DemoCard(
                modifier = Modifier.widthIn(max = 420.dp),
            ) {
                DemoTitle(text = title)
                DemoSubtitle(text = statusText)

                if (errorText != null) {
                    Text(
                        text = errorText.orEmpty(),
                        color = ErrorText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                AppButton(
                    title = "Reload",
                    onClick = { reloadKey++ },
                )

                AppButton(
                    title = if (muted) "Unmute" else "Mute",
                    onClick = { muted = !muted },
                )

                AppButton(
                    title = "Change AdChoices",
                    onClick = { adChoicesPlacement = adChoicesPlacement.next() },
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F141C))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    val loadedAd = adContent

                    if (loadedAd == null) {
                        Text(
                            text = "Loading native ad...",
                            color = SecondaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        if (usesTemplate) {
                            CASNativeAdTemplate(
                                adContent = loadedAd,
                                maxWidth = nativeWidthDp.dp,
                                maxHeight = templateSizeHeightDp.dp,
                                backgroundColor = Color.White,
                            )
                        } else {
                            CASNativeAd(
                                adContent = loadedAd,
                                modifier = Modifier
                                    .width(nativeWidthDp.dp)
                                    .height(templateSizeHeightDp.dp),
                            ) {
                                DemoCustomNativeAd()
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun CASNativeAdChoicesPlacement.next(): CASNativeAdChoicesPlacement {
    return when (this) {
        CASNativeAdChoicesPlacement.TOP_LEFT -> CASNativeAdChoicesPlacement.TOP_RIGHT
        CASNativeAdChoicesPlacement.TOP_RIGHT -> CASNativeAdChoicesPlacement.BOTTOM_RIGHT
        CASNativeAdChoicesPlacement.BOTTOM_RIGHT -> CASNativeAdChoicesPlacement.BOTTOM_LEFT
        CASNativeAdChoicesPlacement.BOTTOM_LEFT -> CASNativeAdChoicesPlacement.TOP_LEFT
    }
}

@Composable
private fun NativeAdScope.DemoCustomNativeAd() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AdHeadlineText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 30.dp, bottom = 8.dp),
                color = Color(0xFF0F172A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .width(52.dp)
                        .height(52.dp)
                        .background(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(14.dp),
                        )
                        .clip(RoundedCornerShape(14.dp)),
                ) {
                    AdIconImage(
                        modifier = Modifier
                            .width(52.dp)
                            .height(52.dp),
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 10.dp),
                ) {
                    AdAdvertiserText(
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color(0xFF2563EB),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AdStarRating(
                            modifier = Modifier
                                .width(88.dp)
                                .height(14.dp)
                                .padding(end = 8.dp),
                        )

                        AdReviewCountText(
                            color = Color(0xFF475569),
                            fontSize = 12.sp,
                            maxLines = 1,
                        )
                    }
                }
            }

            AdLabelText(
                modifier = Modifier.padding(top = 10.dp),
                color = Color(0xFF2563EB),
                fontSize = 12.sp,
                maxLines = 1,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(180.dp)
                    .background(Color(0xFFF1F5F9)),
            ) {
                AdMedia(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    scaleType = CASNativeScaleType.CENTER_CROP,
                )
            }

            AdBodyText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                color = Color(0xFF334155),
                fontSize = 14.sp,
                maxLines = 2,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AdPriceText(
                    modifier = Modifier.padding(end = 10.dp),
                    color = Color(0xFF0F172A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )

                AdStoreText(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF0F172A),
                    fontSize = 14.sp,
                    maxLines = 1,
                )
            }

            AdCallToAction(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(48.dp),
                textColor = Color.White,
                backgroundColor = Color(0xFF2563EB),
                shape = RoundedCornerShape(12.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }

        AdChoices(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(20.dp)
                .height(20.dp),
        )
    }
}