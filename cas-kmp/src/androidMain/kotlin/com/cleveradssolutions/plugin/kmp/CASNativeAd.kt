package com.cleveradssolutions.plugin.kmp

import com.cleveradssolutions.sdk.OnAdImpressionListener
import java.util.IdentityHashMap
import com.cleveradssolutions.sdk.AdContentInfo as AndroidAdContentInfo
import com.cleveradssolutions.sdk.nativead.CASNativeLoader as AndroidCASNativeLoader
import com.cleveradssolutions.sdk.nativead.NativeAdContent as AndroidNativeAdContent
import com.cleveradssolutions.sdk.nativead.NativeAdContentCallback as AndroidNativeAdContentCallback
import com.cleversolutions.ads.AdError as AndroidAdError


public actual class CASNativeAdLoader actual constructor(
    casId: String,
    startVideoMuted: Boolean,
    adChoicesPlacement: CASNativeAdChoicesPlacement,
    placement: String?,
    callback: CASNativeAdContentCallback,
) {
    private val android: AndroidCASNativeLoader = AndroidCASNativeLoader(
        CASMobileAds.appContext,
        casId,
        object : AndroidNativeAdContentCallback() {
            private val loadedAds = IdentityHashMap<AndroidNativeAdContent, CASNativeAdContent>()

            override fun onNativeAdLoaded(
                nativeAd: AndroidNativeAdContent,
                ad: AndroidAdContentInfo,
            ) {
                val content = com.cleveradssolutions.plugin.kmp.CASNativeAdContent(nativeAd)
                loadedAds[nativeAd] = content
                callback.onNativeAdLoaded(content)
            }

            override fun onNativeAdFailedToLoad(error: AndroidAdError) {
                callback.onNativeAdFailedToLoad(error.toKmp())
            }

            override fun onNativeAdClicked(
                nativeAd: AndroidNativeAdContent,
                ad: AndroidAdContentInfo,
            ) {
                callback.onNativeAdClicked(loadedAds[nativeAd]!!)
            }

            override fun onNativeAdFailedToShow(
                nativeAd: AndroidNativeAdContent,
                error: AndroidAdError,
            ) {
                callback.onNativeAdFailedToShow(
                    loadedAds[nativeAd]!!,
                    error.toKmp(),
                )
            }
        },
    )

    init {
        android.isStartVideoMuted = startVideoMuted
        android.adChoicesPlacement = adChoicesPlacement.value
        android.placement = placement
    }

    public actual fun load(maxNumberOfAds: Int): Unit {
        android.load(maxNumberOfAds)
    }
}

public actual class CASNativeAdContent internal constructor(
    internal val android: AndroidNativeAdContent
) : OnAdImpressionListener {
    public actual val advertiser: String?
        get() = android.advertiser

    public actual val body: String?
        get() = android.body

    public actual val callToAction: String?
        get() = android.callToAction

    public actual val headline: String?
        get() = android.headline

    public actual val price: String?
        get() = android.price

    public actual val reviewCount: String?
        get() = android.reviewCount

    public actual val starRating: Double?
        get() = android.starRating

    public actual val store: String?
        get() = android.store

    public actual val adLabel: String?
        get() = android.adLabel

    public actual val isExpired: Boolean
        get() = android.isExpired

    public actual val contentInfo: CASAdContentInfo = AndroidContentInfoImpl(android.contentInfo)

    public actual var impressionCallback: CASAdImpressionCallback? = null

    init {
        android.onImpressionListener = this
    }

    public actual fun destroy(): Unit {
        impressionCallback = null
        android.onImpressionListener = null
        android.destroy()
    }

    override fun onAdImpression(ad: AndroidAdContentInfo) {
        impressionCallback?.onAdImpression(contentInfo)
    }
}

