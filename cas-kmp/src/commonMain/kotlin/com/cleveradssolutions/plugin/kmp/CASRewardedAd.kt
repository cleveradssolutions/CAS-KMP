package com.cleveradssolutions.plugin.kmp

/**
 * A functional interface for listening to reward events.
 *
 * Implement this interface to handle events where a user earns a reward from interacting with an ad.
 */
public fun interface CASRewardEarnedListener {
    /**
     * Called when a user earns a reward from the ad.
     *
     * This method provides an opportunity to handle the reward event,
     * such as updating the user's rewards balance,
     * or displaying a confirmation message to the user.
     *
     * @param ad The ad content associated with the reward.
     */
    public fun onUserEarnedReward(ad: CASAdContentInfo): Unit
}

/**
 * Manages a rewarded ad, allowing for loading, showing, and destroying the ad content.
 *
 * Rewarded ads are ads where users can earn rewards for interacting with them.
 *
 * @param casId The unique identifier for the CAS content, typically an application bundle name.
 * @param autoLoad Whether autoloading of ads is enabled. Disabled by default.
 * @param callback Callback for handling ad content events.
 */
public expect class CASRewardedAd(
    casId: String,
    autoLoad: Boolean = false,
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
     * An optional placement name for the ad instance that helps categorize
     * and track statistics across different ad placements.
     *
     * Maximum 100 characters allowed for the placement name.
     */
    public var placement: String?

    /**
     * Sets custom data to be included in server-side verification callbacks.
     *
     * Maximum 8192 characters allowed for the custom data.
     *
     * The callbacks contain query parameters that describe the rewarded ad interaction,
     * including the placement and user ID alongside any custom data provided here.
     *
     * This feature is currently in closed beta.
     */
    public var serverSideVerificationData: String?

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
     * Loads the rewarded ad content.
     *
     * Initiates the process of loading ad content.
     * This method should be called before attempting to show the ad.
     */
    public fun load(): Unit

    /**
     * Shows the rewarded ad to the user.
     *
     * You must provide a [CASRewardEarnedListener] to handle reward events
     * when the user earns a reward from interacting with the ad.
     *
     * @param rewardEarnedListener The listener that handles the reward event.
     */
    public fun show(rewardEarnedListener: CASRewardEarnedListener): Unit

    /**
     * Destroys the ad content and releases any associated resources.
     *
     * Call this method when the ad is no longer needed to clean up resources and prevent memory leaks.
     */
    public fun destroy(): Unit
}