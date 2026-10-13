package com.cleveradssolutions.plugin.kmp

import androidx.compose.runtime.mutableStateOf
import cocoapods.CleverAdsSolutions_Base.CASBannerDelegateProtocol
import cocoapods.CleverAdsSolutions_Base.CASBannerView
import cocoapods.CleverAdsSolutions_Base.CASImpressionDelegateProtocol
import kotlinx.cinterop.ExperimentalForeignApi
import platform.darwin.NSObject
import cocoapods.CleverAdsSolutions_Base.CASContentInfo as IosCASContentInfo
import cocoapods.CleverAdsSolutions_Base.CASError as IosCASError

@OptIn(ExperimentalForeignApi::class)
internal class IOSBannerDelegate(
    var callback: CASBannerAdContentCallback?
) : NSObject(), CASBannerDelegateProtocol, CASImpressionDelegateProtocol {
    var loadedInfo: CASAdContentInfo? = null
    var impressionCallback: CASAdImpressionCallback? = null
    val isLoadedState = mutableStateOf(false)

    override fun bannerAdViewDidLoad(view: CASBannerView) {
        val newInfo = IOSContentInfo(view.contentInfo()!!)
        loadedInfo = newInfo
        callback?.onBannerAdLoaded(newInfo)
        isLoadedState.value = true
    }

    override fun bannerAdView(
        adView: CASBannerView,
        didFailWith: IosCASError,
    ) {
        isLoadedState.value = false
        callback?.onBannerAdFailed(didFailWith.toKmp())
    }

    override fun bannerAdViewDidRecordClick(adView: CASBannerView) {
        callback?.onBannerAdClicked(loadedInfo!!)
    }

    override fun adDidRecordImpressionWithInfo(info: IosCASContentInfo) {
        val newInfo = IOSContentInfo(info)
        loadedInfo = newInfo
        impressionCallback?.onAdImpression(newInfo)
    }
}
