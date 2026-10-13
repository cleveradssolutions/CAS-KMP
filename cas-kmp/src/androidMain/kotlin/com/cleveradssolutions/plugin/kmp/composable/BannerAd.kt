package com.cleveradssolutions.plugin.kmp.composable

import android.view.ViewGroup
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cleveradssolutions.plugin.kmp.CASBannerAdContent

@Composable
public actual fun CASBannerAd(
    adContent: CASBannerAdContent,
    modifier: Modifier,
): Unit {
    val bannerModifier = if (adContent.isLoaded) {
        modifier
    } else {
        Modifier.size(0.dp)
    }

    // `factory` runs once per call site, so a new [adContent] would never reach the hierarchy: the
    // previous banner view would stay attached and the freshly loaded one would never be shown.
    // An adaptive banner hits this on every rotation, because a new width means a new ad instance.
    key(adContent) {
        AndroidView(
            modifier = bannerModifier,
            factory = {
                adContent.view.also {
                    (it.parent as? ViewGroup)?.removeView(it)

                    it.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                }
            },
            update = {
                // Do not detach here.
                // AndroidView already owns this view after factory.
            },
            onRelease = { view ->
                // Detach before the composable goes away. Without this the banner view can still be
                // laid out after the ad content was destroyed, which the SDK reports as a render
                // failure and answers with a penalty on the ad unit for the rest of the session.
                (view.parent as? ViewGroup)?.removeView(view)
            },
        )
    }
}
