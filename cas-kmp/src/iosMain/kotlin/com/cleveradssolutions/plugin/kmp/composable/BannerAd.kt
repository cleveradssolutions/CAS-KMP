package com.cleveradssolutions.plugin.kmp.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import com.cleveradssolutions.plugin.kmp.CASBannerAdContent
import kotlinx.cinterop.ExperimentalForeignApi


@OptIn(ExperimentalForeignApi::class)
@Composable
public actual fun CASBannerAd(
    adContent: CASBannerAdContent,
    modifier: Modifier,
) {
    val uiViewController = LocalUIViewController.current
    // `factory` runs once per call site, so a new [adContent] would never reach the hierarchy and
    // the previously shown banner would stay in place. An adaptive banner hits this on every
    // rotation, because a new width means a new ad instance.
    key(adContent) {
        UIKitView(
            modifier = modifier.bannerSize(adContent),
            factory = {
                adContent.view.apply {
                    setRootViewController(uiViewController)
                    hidden = !adContent.isLoaded
                }
            },
            update = { view ->
                view.hidden = !adContent.isLoaded
            },
        )
    }
}

private fun Modifier.bannerSize(
    adContent: CASBannerAdContent,
): Modifier {
    return this.layout { measurable, _ ->
        if (!adContent.isLoaded) {
            val placeable = measurable.measure(Constraints.fixed(0, 0))

            layout(0, 0) {
                placeable.place(0, 0)
            }
        } else {
            val widthPx = adContent.adSize.width.dp.roundToPx()
            val heightPx = adContent.adSize.height.dp.roundToPx()

            val placeable = measurable.measure(
                Constraints.fixed(
                    width = widthPx,
                    height = heightPx,
                ),
            )

            layout(widthPx, heightPx) {
                placeable.place(0, 0)
            }
        }
    }
}