package com.cleveradssolutions.plugin.kmp

/**
 * Manages an interstitial ad, allowing for loading, showing, and destroying the ad content.
 *
 * Interstitial ads are full-screen ads that cover the entire screen
 * and are typically used at natural transition points within an app.
 *
 * @param casId The unique identifier for the CAS content, typically an application bundle name.
 * @param autoLoad Whether autoloading of ads is enabled. Disabled by default.
 * @param autoShow Controls whether the ad should be automatically displayed when the user returns to the app.
 * Note that the ad must be ready for display at the time of returning to the app.
 * Disabled by default.
 * @param callback Callback for handling ad content events.
 */
public expect class CASInterstitialAd(
    casId: String,
    autoLoad: Boolean = false,
    autoShow: Boolean = false,
    callback: CASScreenAdContentCallback? = null,
) {
    /**
     * Gets or sets the listener for ad impression events.
     *
     * This listener is notified when an ad impression is recorded and will be paid.
     */
    public var impressionCallback: CASAdImpressionCallback?

    /**
     * Gets or sets whether autoloading of ads is enabled.
     *
     * If enabled, the ad will automatically load new content when the current ad is dismissed or completed.
     * Additionally, it will automatically retry loading the ad if an error occurs during the loading process.
     *
     * Disabled by default.
     */
    public var isAutoLoadEnabled: Boolean

    /**
     * Controls whether the ad should be automatically displayed when the user returns to the app.
     *
     * Note that the ad must be ready for display at the time of returning to the app.
     *
     * Disabled by default.
     */
    public var isAutoShowEnabled: Boolean

    /**
     * An optional placement name for the ad instance that helps categorize
     * and track statistics across different ad placements.
     *
     * Maximum 100 characters allowed for the placement name.
     */
    public var placement: String?

    /**
     * The minimum interval between showing interstitial ads, in seconds.
     *
     * Attempting to show a new interstitial ad before the interval has passed after the previous one was closed
     * will trigger [CASScreenAdContentCallback.onAdFailedToShow].
     *
     * By default, this interval is set to 0 seconds.
     */
    public var minInterval: Int

    /**
     * Indicates whether the ad is currently loaded and ready to be shown.
     */
    public val isLoaded: Boolean

    /**
     * Information about the currently loaded ad.
     *
     * This property is `null` if the ad has not been loaded yet or has been destroyed.
     */
    public val contentInfo: CASAdContentInfo?

    /**
     * Loads the interstitial ad content.
     *
     * Initiates the process of loading ad content.
     * This method should be called before attempting to show the ad.
     */
    public fun load(): Unit

    /**
     * Shows the interstitial ad to the user.
     *
     * This method should be called after the ad has been loaded and is ready to be shown.
     */
    public fun show(): Unit

    /**
     * Restarts the interval countdown until the next interstitial ad display.
     *
     * By default, the interval before the first interstitial ad impression is ignored.
     * Use this method to delay displaying the ad by restarting the interval countdown.
     */
    public fun restartInterval(): Unit

    /**
     * Destroys the ad content and releases any associated resources.
     *
     * Call this method when the ad is no longer needed to clean up resources and prevent memory leaks.
     */
    public fun destroy(): Unit
}