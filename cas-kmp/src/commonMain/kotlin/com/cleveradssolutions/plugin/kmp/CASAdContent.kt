package com.cleveradssolutions.plugin.kmp

/**
 * Represents the ad format.
 */
public enum class CASAdFormat(public val value: Int) {
    BANNER(0),
    INTERSTITIAL(1),
    REWARDED(2),
    APP_OPEN(3),
    NATIVE(4),
    MEDIUM_RECTANGLE(5),
    INLINE_BANNER(6),
}

/**
 * Represents an ad error.
 *
 * To check the error code, use [code].
 * To get a description of the error, use [message].
 */
public data class CASError(
    public val code: Int,
    public val message: String,
)

/**
 * Information about the currently loaded or displayed ad.
 */
public interface CASAdContentInfo {
    /**
     * Gets the format of the ad that is shown.
     */
    public val format: CASAdFormat

    /**
     * Gets the display name of the mediated network that purchased the impression.
     */
    public val sourceName: String

    /**
     * Gets the Ad Unit ID from the mediated network that purchased the impression.
     */
    public val sourceUnitId: String

    /**
     * Gets the Creative ID associated with the ad, if available. May be `null`.
     *
     * You can use this ID to report creative issues to the Ad review team.
     */
    public val creativeId: String?

    /**
     * Gets the revenue generated from the impression, in USD.
     *
     * The revenue value may be either estimated or exact, depending on the precision specified by [revenuePrecision].
     */
    public val revenue: Double

    /**
     * Gets the precision type of the revenue field.
     */
    public val revenuePrecision: CASAdRevenuePrecision

    /**
     * Gets the total number of impressions across all ad formats for the current user, across all sessions.
     */
    public val impressionDepth: Int

    /**
     * Gets the accumulated value of user ad revenue in USD from all ad format impressions.
     */
    public val revenueTotal: Double
}

/**
 * Defines the precision levels for ad revenue calculations.
 * These levels indicate the accuracy or source of the revenue data provided by the system.
 */
public enum class CASAdRevenuePrecision(public val value: Int) {
    /**
     * Indicates that the revenue precision is unknown.
     * This occurs when there is insufficient data available for CAS to calculate a revenue value.
     * In such cases, CAS returns $0 in revenue.
     */
    UNKNOWN(0),

    /**
     * Indicates that the revenue value is provided as part of a real-time auction.
     * This value represents the most accurate revenue figure available at the time.
     */
    PRECISE(1),

    /**
     * Indicates that the revenue is based on the manual CPM (Cost Per Mille) value entered
     * for the waterfall ad network instance in mediation.
     * Note: Actual ad revenue is expected to be 10-20% higher than this minimum (floor) value.
     */
    FLOOR(2),

    /**
     * Indicates that the revenue calculation is based on historical performance data
     * as analyzed by the CAS platform.
     * Note: Estimated ad revenue may have discrepancies of up to 10% compared to actual values.
     */
    ESTIMATED(3)
}

/**
 * A callback interface for handling events related to screen ad content.
 *
 * Implement this interface to respond to various states and actions associated with ad content,
 * such as loading success, showing success, and user interactions.
 *
 * Methods in this interface are called on the main thread.
 */
public interface CASScreenAdContentCallback {
    /**
     * Called when the ad content has been successfully loaded.
     *
     * This method provides an opportunity to handle successful ad loading,
     * such as preparing the ad for display.
     *
     * @param ad The ad content that has been successfully loaded.
     */
    public fun onAdLoaded(ad: CASAdContentInfo): Unit {}

    /**
     * Called when the ad content fails to load.
     *
     * This method provides an opportunity to handle loading failures,
     * such as displaying an error message to the user or attempting to load a different ad.
     *
     * @param format The format of ad content.
     * @param error The error that occurred while attempting to load the ad content.
     */
    public fun onAdFailedToLoad(format: CASAdFormat, error: CASError): Unit {}

    /**
     * Called when the ad content fails to show.
     *
     * This method is triggered if there is an issue displaying the ad content.
     * Implement this method to handle errors related to ad presentation,
     * such as retrying the display or notifying the user.
     *
     * @param format The format of ad content.
     * @param error The error that occurred while attempting to show the ad content.
     */
    public fun onAdFailedToShow(format: CASAdFormat, error: CASError): Unit {}

    /**
     * Called when the ad content is successfully shown.
     *
     * This method is triggered when the ad content has been successfully displayed.
     * Use this method to perform any actions required after the ad is shown,
     * such as logging or UI updates.
     *
     * @param ad The ad content that has been successfully shown.
     */
    public fun onAdShowed(ad: CASAdContentInfo): Unit {}

    /**
     * Called when the ad content is clicked by the user.
     *
     * This method provides an opportunity to handle user clicks on the ad,
     * such as tracking click events or updating the UI.
     *
     * @param ad The ad content that was clicked by the user.
     */
    public fun onAdClicked(ad: CASAdContentInfo): Unit {}

    /**
     * Called when the ad content is dismissed.
     *
     * This method is triggered when the ad content is closed or dismissed by the user.
     * Use this method to perform any necessary actions after the ad has been dismissed,
     * such as cleaning up resources or updating the UI.
     *
     * @param ad The ad content that was dismissed.
     */
    public fun onAdDismissed(ad: CASAdContentInfo): Unit {}
}

/**
 * A functional interface for listening to ad impression events.
 *
 * Implement this interface to handle actions when an ad impression is recorded.
 */
public fun interface CASAdImpressionCallback {
    /**
     * Called when an ad impression occurs.
     *
     * @param ad The ad content associated with the impression. This object contains details
     * about the ad, including format, source, and revenue information.
     */
    public fun onAdImpression(ad: CASAdContentInfo): Unit
}