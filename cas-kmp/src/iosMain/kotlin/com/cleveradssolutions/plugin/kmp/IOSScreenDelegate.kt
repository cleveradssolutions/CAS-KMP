package com.cleveradssolutions.plugin.kmp

import cocoapods.CleverAdsSolutions_Base.CASImpressionDelegateProtocol
import cocoapods.CleverAdsSolutions_Base.CASScreenContentDelegateProtocol
import cocoapods.CleverAdsSolutions_Base.CASScreenContentProtocol
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import platform.darwin.NSObject
import cocoapods.CleverAdsSolutions_Base.CASContentInfo as IosCASContentInfo
import cocoapods.CleverAdsSolutions_Base.CASError as IosCASError

@OptIn(ExperimentalForeignApi::class)
internal class IOSScreenDelegate(
    private val format: CASAdFormat,
    var callback: CASScreenAdContentCallback?,
) : NSObject(), CASScreenContentDelegateProtocol, CASImpressionDelegateProtocol {
    var loadedInfo: CASAdContentInfo? = null
    var impressionCallback: CASAdImpressionCallback? = null

    override fun screenAdDidLoadContent(ad: CASScreenContentProtocol) {
        val info = IOSContentInfo(ad.contentInfo()!!)
        loadedInfo = info
        callback?.onAdLoaded(info)
    }

    @ObjCSignatureOverride
    override fun screenAd(
        ad: CASScreenContentProtocol,
        didFailToLoadWithError: IosCASError,
    ) {
        loadedInfo = null
        callback?.onAdFailedToLoad(
            format = format,
            error = didFailToLoadWithError.toKmp(),
        )
    }

    override fun screenAdWillPresentContent(ad: CASScreenContentProtocol) {
        callback?.onAdShowed(loadedInfo!!)
    }

    @ObjCSignatureOverride
    override fun screenAd(
        ad: CASScreenContentProtocol,
        didFailToPresentWithError: IosCASError,
    ) {
        callback?.onAdFailedToShow(
            format = format,
            error = didFailToPresentWithError.toKmp(),
        )
    }

    override fun screenAdDidClickContent(ad: CASScreenContentProtocol) {
        callback?.onAdClicked(loadedInfo!!)
    }

    override fun screenAdDidDismissContent(ad: CASScreenContentProtocol) {
        callback?.onAdDismissed(loadedInfo!!)
    }

    override fun adDidRecordImpressionWithInfo(info: IosCASContentInfo) {
        val newInfo = IOSContentInfo(info)
        loadedInfo = newInfo
        impressionCallback?.onAdImpression(newInfo)
    }
}
