package com.cleveradssolutions.plugin.kmp

import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback
import com.cleveradssolutions.sdk.AdContentInfo as AndroidAdContentInfo
import com.cleveradssolutions.sdk.AdFormat as AndroidAdFormat
import com.cleveradssolutions.sdk.screen.CASAppOpen as AndroidCASAppOpen
import com.cleversolutions.ads.AdError as AndroidAdError

public actual class CASAppOpenAd actual constructor(
    casId: String,
    autoLoad: Boolean,
    autoShow: Boolean,
    private var callback: CASScreenAdContentCallback?,
) : ScreenAdContentCallback(), OnAdImpressionListener {

    private val android: AndroidCASAppOpen =
        AndroidCASAppOpen(CASMobileAds.appContext, casId).also {
            it.contentCallback = this
            it.onImpressionListener = this
            it.isAutoloadEnabled = autoLoad
            it.isAutoshowEnabled = autoShow
        }

    private var loadedInfo: CASAdContentInfo? = null

    public actual var impressionCallback: CASAdImpressionCallback? = null

    public actual var isAutoLoadEnabled: Boolean
        get() = android.isAutoloadEnabled
        set(value) {
            android.isAutoloadEnabled = value
        }

    public actual var isAutoShowEnabled: Boolean
        get() = android.isAutoshowEnabled
        set(value) {
            android.isAutoshowEnabled = value
        }

    public actual var placement: String?
        get() = android.placement
        set(value) {
            android.placement = value
        }

    public actual val isLoaded: Boolean
        get() = android.isLoaded

    public actual val contentInfo: CASAdContentInfo?
        get() = loadedInfo

    public actual fun load() {
        android.load(CASMobileAds.appContext)
    }

    public actual fun show() {
        android.show(null)
    }

    public actual fun destroy() {
        android.onImpressionListener = null
        android.contentCallback = null
        callback = null
        impressionCallback = null
        android.destroy()
    }

    override fun onAdLoaded(ad: AndroidAdContentInfo) {
        val info = AndroidContentInfoImpl(ad)
        loadedInfo = info
        callback?.onAdLoaded(info)
    }

    override fun onAdFailedToLoad(
        format: AndroidAdFormat,
        error: AndroidAdError,
    ) {
        callback?.onAdFailedToLoad(format.toKmp(), error.toKmp())
    }

    override fun onAdFailedToShow(
        format: AndroidAdFormat,
        error: AndroidAdError,
    ) {
        callback?.onAdFailedToShow(format.toKmp(), error.toKmp())
    }

    override fun onAdShowed(ad: AndroidAdContentInfo) {
        callback?.onAdShowed(loadedInfo!!)
    }

    override fun onAdClicked(ad: AndroidAdContentInfo) {
        callback?.onAdClicked(loadedInfo!!)
    }

    override fun onAdDismissed(ad: AndroidAdContentInfo) {
        callback?.onAdDismissed(loadedInfo!!)
    }

    override fun onAdImpression(ad: AndroidAdContentInfo) {
        impressionCallback?.onAdImpression(loadedInfo!!)
    }
}