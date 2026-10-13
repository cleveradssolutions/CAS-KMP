@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
@file:Suppress("UNUSED_PARAMETER")

package com.cleveradssolutions.plugin.kmp

import cocoapods.CleverAdsSolutions_Base.CASImpressionDelegateProtocol
import cocoapods.CleverAdsSolutions_Base.CASNativeAdContentDelegateProtocol
import cocoapods.CleverAdsSolutions_Base.CASNativeLoaderDelegateProtocol
import platform.darwin.NSObject
import cocoapods.CleverAdsSolutions_Base.CASContentInfo as IosCASContentInfo
import cocoapods.CleverAdsSolutions_Base.CASError as IosCASError
import cocoapods.CleverAdsSolutions_Base.CASNativeAdContent as IosCASNativeAdContent
import cocoapods.CleverAdsSolutions_Base.CASNativeLoader as IosCASNativeLoader

public actual class CASNativeAdLoader actual constructor(
    casId: String,
    startVideoMuted: Boolean,
    adChoicesPlacement: CASNativeAdChoicesPlacement,
    placement: String?,
    callback: CASNativeAdContentCallback,
) {
    private val delegate = IosNativeLoaderDelegate(callback)

    private val ios: IosCASNativeLoader =
        IosCASNativeLoader(casID = casId).also { loader ->
            loader.setDelegate(delegate)
            loader.setIsStartVideoMuted(startVideoMuted)
            loader.setAdChoicesPlacement(adChoicesPlacement.toIos())
            loader.setPlacement(placement)
        }

    public actual fun load(maxNumberOfAds: Int) {
        if (maxNumberOfAds <= 1) {
            ios.loadAd()
        } else {
            ios.loadWithMaxNumberOfAds(maxNumberOfAds.toLong())
        }
    }

    private class IosNativeLoaderDelegate(
        private val callback: CASNativeAdContentCallback,
    ) : NSObject(), CASNativeLoaderDelegateProtocol {

        override fun nativeAdDidLoadContent(ad: IosCASNativeAdContent) {
            callback.onNativeAdLoaded(
                CASNativeAdContent(ios = ad, callback = callback),
            )
        }

        override fun nativeAdDidFailToLoadWithError(error: IosCASError) {
            callback.onNativeAdFailedToLoad(error.toKmp())
        }
    }
}

public actual class CASNativeAdContent internal constructor(
    internal val ios: IosCASNativeAdContent,
    private var callback: CASNativeAdContentCallback? = null,
) : NSObject(), CASNativeAdContentDelegateProtocol, CASImpressionDelegateProtocol {

    public actual val headline: String?
        get() = ios.headline()

    public actual val advertiser: String?
        get() = ios.advertiser()

    public actual val body: String?
        get() = ios.body()

    public actual val callToAction: String?
        get() = ios.callToAction()

    public actual val price: String?
        get() = ios.price()

    public actual val reviewCount: String?
        get() = ios.reviewCount()

    public actual val starRating: Double?
        get() = ios.starRating()?.doubleValue

    public actual val store: String?
        get() = ios.store()

    public actual val adLabel: String?
        get() = ios.adLabel()

    public actual val isExpired: Boolean
        get() = ios.isExpired()

    public actual val contentInfo: CASAdContentInfo = IOSContentInfo(ios.contentInfo())

    public actual var impressionCallback: CASAdImpressionCallback? = null

    init {
        ios.setDelegate(this)
        ios.setImpressionDelegate(this)
    }

    public actual fun destroy() {
        callback = null
        impressionCallback = null
        ios.setDelegate(null)
        ios.setImpressionDelegate(null)
        ios.destroy()
    }

    override fun nativeAd(
        ad: IosCASNativeAdContent,
        didFailToPresentWithError: IosCASError,
    ) {
        callback?.onNativeAdFailedToShow(this, didFailToPresentWithError.toKmp())
    }

    override fun nativeAdDidClickContent(ad: IosCASNativeAdContent) {
        callback?.onNativeAdClicked(this)
    }

    override fun adDidRecordImpressionWithInfo(info: IosCASContentInfo) {
        impressionCallback?.onAdImpression(contentInfo)
    }
}
