package com.cleveradssolutions.plugin.kmp

import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Banner ad size.
 */
public expect class CASBannerSize {
    /**
     * Width in dp.
     */
    public val width: Int

    /**
     * Height in dp.
     */
    public val height: Int

    public companion object {
        /**
         * Standard banner size 320dp width and 50dp height.
         */
        public val BANNER: CASBannerSize

        /**
         * Leaderboard banner size 728dp width and 90dp height.
         */
        public val LEADERBOARD: CASBannerSize

        /**
         * Medium Rectangle size 300dp width and 250dp height.
         */
        public val MEDIUM_RECTANGLE: CASBannerSize

        /**
         * Inline adaptive banners are larger, taller banners compared to anchored adaptive banners.
         * They are of variable height, and can be as tall as the device screen.
         *
         * - The height of adaptive banners cannot be less than 32dp.
         * - You must know the width of the view that the ad will be placed in,
         * and this should take into account the device width and any safe areas that are applicable.
         * - The inline adaptive banner sizes are designed to work best when using the full available width.
         * In most cases, this will be the full width of the screen of the device in use.
         * Be sure to take into account applicable safe areas.
         *
         * @param width Width in dp.
         * @param maxHeight Maximum height in dp.
         */
        public fun getInlineBanner(
            width: Int,
            maxHeight: Int,
        ): CASBannerSize

        /**
         * Creates adaptive banner size with max width in dp for current screen orientation.
         *
         * - The height of adaptive banners cannot be less than 50dp and more than 250dp.
         * - The width of adaptive banners cannot be less than 300dp.
         * - The adaptive banners use fixed aspect ratios instead of fixed heights.
         *
         * @param maxWidthDP Maximum width in dp.
         */
        public fun getAdaptiveBanner(
            maxWidthDP: Int,
        ): CASBannerSize

        /**
         * Typically, Smart Banners on phones have a [BANNER] size.
         * On tablets they usually have a [LEADERBOARD] size.
         */
        public fun getSmartBanner(): CASBannerSize
    }
}

/**
 * Banner ad content.
 *
 * Manages banner loading and destroying.
 *
 * @param casId The unique identifier for the CAS content, typically an application bundle name.
 * @param size Banner ad size.
 * @param autoLoad Whether autoloading of ads is enabled.
 * @param refreshInterval Number of seconds an ad is displayed before a new ad is shown.
 * Set 0 to disable auto refresh ads.
 * @param placement Optional placement name for the ad instance that helps categorize
 * and track statistics across different ad placements. Maximum 100 characters.
 * @param callback Callback used to handle banner ad events.
 */
@MainThread
public expect class CASBannerAdContent(
    casId: String,
    size: CASBannerSize,
    autoLoad: Boolean = true,
    refreshInterval: Int = 30,
    placement: String? = null,
    callback: CASBannerAdContentCallback? = null,
) {
    /**
     * Gets or sets the listener for ad impression events.
     *
     * This listener is notified when an ad impression is recorded and will be paid.
     * It allows you to track when the ad has been successfully shown and the impression has been accounted for.
     */
    public var impressionCallback: CASAdImpressionCallback?

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
     * Manual load Banner Ad or reload current loaded Ad to skip impression.
     *
     * If autoload is disabled, you should use this method before presenting the ad.
     */
    public fun load(): Unit

    /**
     * Destroy ad in the Banner View.
     *
     * Call when banner ad content is no longer needed.
     */
    public fun destroy(): Unit
}

/**
 * Callback interface for banner ad events.
 */
public interface CASBannerAdContentCallback {
    /**
     * Invokes this callback when ad loaded and ready to present.
     */
    @MainThread
    public fun onBannerAdLoaded(info: CASAdContentInfo): Unit {}

    /**
     * Invokes this callback when an error occurred with the ad.
     */
    @MainThread
    public fun onBannerAdFailed(error: CASError, ): Unit {}

    /**
     * Invokes this callback when a user clicks the ad.
     */
    @MainThread
    public fun onBannerAdClicked(info: CASAdContentInfo): Unit {}

    /**
     * Invokes this callback when an ad impression is recorded.
     */
    @MainThread
    public fun onBannerAdImpression(info: CASAdContentInfo): Unit {}
}
