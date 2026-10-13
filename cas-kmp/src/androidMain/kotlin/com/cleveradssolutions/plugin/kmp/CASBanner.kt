package com.cleveradssolutions.plugin.kmp

import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.compose.runtime.mutableStateOf
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdSize
import com.cleversolutions.ads.AdViewListener
import com.cleversolutions.ads.android.CASBannerView as AndroidCASBannerView

public actual class CASBannerSize private constructor(
    internal val android: AdSize,
) {
    public actual val width: Int
        get() = android.width

    public actual val height: Int
        get() = android.height

    public actual companion object {
        public actual val BANNER: CASBannerSize
            get() = CASBannerSize(AdSize.BANNER)

        public actual val LEADERBOARD: CASBannerSize
            get() = CASBannerSize(AdSize.LEADERBOARD)

        public actual val MEDIUM_RECTANGLE: CASBannerSize
            get() = CASBannerSize(AdSize.MEDIUM_RECTANGLE)

        public actual fun getInlineBanner(
            width: Int,
            maxHeight: Int,
        ): CASBannerSize {
            return CASBannerSize(
                AdSize.getInlineBanner(width, maxHeight),
            )
        }

        public actual fun getAdaptiveBanner(
            maxWidthDP: Int,
        ): CASBannerSize {
            return CASBannerSize(
                AdSize.getAdaptiveBanner(
                    CASMobileAds.requireAppContext(),
                    maxWidthDP,
                ),
            )
        }

        public actual fun getSmartBanner(): CASBannerSize {
            return CASBannerSize(
                AdSize.getSmartBanner(CASMobileAds.requireAppContext()),
            )
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
    private var callback: CASBannerAdContentCallback?,
) : AdViewListener, OnAdImpressionListener {
    private val isLoadedState = mutableStateOf(false)
    private var loadedInfo: CASAdContentInfo? = null

    public actual var impressionCallback: CASAdImpressionCallback? = null

    internal val view: AndroidCASBannerView =
        AndroidCASBannerView(CASMobileAds.requireAppContext(), casId).also {
            it.adListener = this
            it.onImpressionListener = this
            it.refreshInterval = refreshInterval
            it.placement = placement
            it.size = size.android
            it.isAutoloadEnabled = autoLoad // set autoload in last
        }

    public actual val isLoaded: Boolean
        get() = isLoadedState.value

    public actual val contentInfo: CASAdContentInfo?
        get() = loadedInfo

    public actual fun load(): Unit {
        isLoadedState.value = false
        view.load()
    }

    public actual fun destroy(): Unit {
        view.onImpressionListener = null
        view.adListener = null
        callback = null
        impressionCallback = null
        (view.parent as? ViewGroup)?.removeView(view)
        view.destroy()
        isLoadedState.value = false
    }

    override fun onAdViewLoaded(view: AndroidCASBannerView) {
        isLoadedState.value = true
        val info = AndroidContentInfoImpl(view.contentInfo!!)
        loadedInfo = info
        callback?.onBannerAdLoaded(info)
    }

    override fun onAdViewFailed(
        view: AndroidCASBannerView,
        error: AdError,
    ) {
        isLoadedState.value = false
        callback?.onBannerAdFailed(error.toKmp())
    }

    override fun onAdViewClicked(view: AndroidCASBannerView) {
        callback?.onBannerAdClicked(loadedInfo!!)
    }

    override fun onAdImpression(ad: AdContentInfo) {
        val info = AndroidContentInfoImpl(ad)
        loadedInfo = info
        impressionCallback?.onAdImpression(info)
    }
}

