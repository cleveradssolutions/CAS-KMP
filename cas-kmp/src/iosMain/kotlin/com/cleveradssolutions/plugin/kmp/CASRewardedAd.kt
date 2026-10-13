@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.cleveradssolutions.plugin.kmp

import cocoapods.CleverAdsSolutions_Base.CASRewarded as IosCASRewarded

public actual class CASRewardedAd actual constructor(
    casId: String,
    autoLoad: Boolean,
    callback: CASScreenAdContentCallback?,
) {
    private val delegate = IOSScreenDelegate(
        format = CASAdFormat.REWARDED,
        callback = callback,
    )

    private val ios: IosCASRewarded =
        IosCASRewarded(casID = casId).also {
            it.setDelegate(delegate)
            it.setImpressionDelegate(delegate)
            it.setIsAutoloadEnabled(autoLoad)
        }

    public actual var impressionCallback: CASAdImpressionCallback?
        get() = delegate.impressionCallback
        set(value) {
            delegate.impressionCallback = value
        }

    public actual var isAutoLoadEnabled: Boolean
        get() = ios.isAutoloadEnabled()
        set(value) {
            ios.setIsAutoloadEnabled(value)
        }

    public actual var placement: String?
        get() = ios.placement()
        set(value) {
            ios.setPlacement(value)
        }

    public actual var serverSideVerificationData: String?
        get() = ios.serverSideVerificationData()
        set(value) {
            ios.setServerSideVerificationData(value)
        }

    public actual val isLoaded: Boolean
        get() = ios.isAdLoaded()

    public actual val contentInfo: CASAdContentInfo?
        get() = delegate.loadedInfo

    public actual fun load() {
        ios.loadAd()
    }

    public actual fun show(
        rewardEarnedListener: CASRewardEarnedListener,
    ) {
        ios.presentFromViewController(
            viewController = null,
            userDidEarnRewardHandler = { _ ->
                rewardEarnedListener.onUserEarnedReward(delegate.loadedInfo!!)
            },
        )
    }

    public actual fun destroy() {
        ios.setDelegate(null)
        ios.setImpressionDelegate(null)
        delegate.loadedInfo = null
        delegate.callback = null
        delegate.impressionCallback = null
        ios.destroy()
    }
}
