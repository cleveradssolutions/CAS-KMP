package com.cleveradssolutions.plugin.kmp

import kotlinx.cinterop.ExperimentalForeignApi
import cocoapods.CleverAdsSolutions_Base.CASAppOpen as IosCASAppOpen

@OptIn(ExperimentalForeignApi::class)
public actual class CASAppOpenAd actual constructor(
    casId: String,
    autoLoad: Boolean,
    autoShow: Boolean,
    callback: CASScreenAdContentCallback?,
) {
    private val delegate = IOSScreenDelegate(
        format = CASAdFormat.APP_OPEN,
        callback = callback
    )

    private val ios: IosCASAppOpen =
        IosCASAppOpen(casID = casId).also {
            it.setDelegate(delegate)
            it.setImpressionDelegate(delegate)
            it.setIsAutoloadEnabled(autoLoad)
            it.setIsAutoshowEnabled(autoShow)
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

    public actual var isAutoShowEnabled: Boolean
        get() = ios.isAutoshowEnabled()
        set(value) {
            ios.setIsAutoshowEnabled(value)
        }

    public actual var placement: String?
        get() = ios.placement()
        set(value) {
            ios.setPlacement(value)
        }

    public actual val isLoaded: Boolean
        get() = ios.isAdLoaded()

    public actual val contentInfo: CASAdContentInfo?
        get() = delegate.loadedInfo

    public actual fun load() {
        ios.loadAd()
    }

    public actual fun show() {
        ios.presentFromViewController(null)
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
