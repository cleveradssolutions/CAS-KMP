@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.cleveradssolutions.plugin.kmp

import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.CleverAdsSolutions_Base.CASBannerView
import cocoapods.CleverAdsSolutions_Base.CASSize
import platform.CoreGraphics.CGPointMake

public actual class CASBannerSize internal constructor(
    internal val ios: CASSize,
) {
    public actual val width: Int
        get() = ios.width().toInt()

    public actual val height: Int
        get() = ios.height().toInt()

    public actual companion object {
        public actual val BANNER: CASBannerSize
            get() = CASBannerSize(CASSize.banner())

        public actual val LEADERBOARD: CASBannerSize
            get() = CASBannerSize(CASSize.leaderboard())

        public actual val MEDIUM_RECTANGLE: CASBannerSize
            get() = CASBannerSize(CASSize.mediumRectangle())

        public actual fun getInlineBanner(
            width: Int,
            maxHeight: Int,
        ): CASBannerSize {
            return CASBannerSize(
                CASSize.getInlineBannerWithWidth(
                    width = width.toDouble(),
                    maxHeight = maxHeight.toDouble(),
                ),
            )
        }

        public actual fun getAdaptiveBanner(
            maxWidthDP: Int,
        ): CASBannerSize {
            return CASBannerSize(
                CASSize.getAdaptiveBannerForMaxWidth(
                    maxWidthDP.toDouble(),
                ),
            )
        }

        public actual fun getSmartBanner(): CASBannerSize {
            return CASBannerSize(CASSize.getSmartBanner())
        }
    }
}

@MainThread
public actual class CASBannerAdContent actual constructor(
    casId: String,
    size: CASBannerSize,
    autoLoad: Boolean,
    refreshInterval: Int,
    placement: String?,
    callback: CASBannerAdContentCallback?,
) {
    internal val adSize: CASBannerSize = size

    private val delegate = IOSBannerDelegate(callback)

    internal val view: CASBannerView =
        CASBannerView(
            casID = casId,
            size = size.ios,
            origin = CGPointMake(0.0, 0.0),
        ).also { banner ->
            banner.setDelegate(delegate)
            banner.setImpressionDelegate(delegate)
            banner.setRefreshInterval(refreshInterval.toLong())
            banner.setPlacement(placement)
            banner.setIsAutoloadEnabled(autoLoad)
        }

    public actual var impressionCallback: CASAdImpressionCallback?
        get() = delegate.impressionCallback
        set(value) {
            delegate.impressionCallback = value
        }

    public actual val isLoaded: Boolean
        get() = delegate.isLoadedState.value

    public actual val contentInfo: CASAdContentInfo?
        get() = delegate.loadedInfo

    public actual fun load() {
        delegate.isLoadedState.value = false
        view.loadAd()
    }

    public actual fun destroy() {
        view.setDelegate(null)
        view.setImpressionDelegate(null)
        delegate.isLoadedState.value = false
        delegate.loadedInfo = null
        delegate.impressionCallback = null
        delegate.callback = null
        view.removeFromSuperview()
        view.destroy()
    }
}
