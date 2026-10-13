package com.cleveradssolutions.plugin.kmp

import androidx.annotation.MainThread

/**
 * Manages the loading of native ads with specific configurations.
 *
 * This class handles the loading of native ad content and allows for configuration
 * of video mute state and AdChoices placement.
 *
 * When you invoke the [load] function, [CASNativeAdLoader] will continue running
 * until the ad finishes loading. This can lead to [CASNativeAdContentCallback] being
 * triggered after the screen has been destroyed or at unexpected times.
 * Therefore, you should implement checks to destroy the loaded ad
 * if you do not plan to display it to the user.
 *
 * @param casId The unique identifier for the CAS content, typically an application bundle name.
 * @param startVideoMuted Initial mute state of the video.
 * By default the video will start with the audio muted.
 * The mute state is applied only before ad load.
 * @param adChoicesPlacement Placement of the AdChoices icon.
 * This determines where the AdChoices icon is displayed within the ad content.
 * The icon placement is applied only before ad load.
 * @param placement Optional placement name for the ad instance that helps categorize
 * and track statistics across different ad placements. Maximum 100 characters.
 * @param callback Callback used to handle native ad content events.
 */
public expect class CASNativeAdLoader(
    casId: String,
    startVideoMuted: Boolean = true,
    adChoicesPlacement: CASNativeAdChoicesPlacement = CASNativeAdChoicesPlacement.TOP_RIGHT,
    placement: String? = null,
    callback: CASNativeAdContentCallback,
) {
    /**
     * Starts loading the native ads with a specified maximum number of ads.
     *
     * Initiates the process of loading native ads with the specified maximum number of ads.
     * It's not guaranteed that the SDK will return the exact number of ads requested.
     *
     * [CASNativeAdContentCallback.onNativeAdLoaded] will be called multiple times,
     * once for each ad that is successfully loaded, up to the specified maximum number of ads.
     * If the load operation fails, [CASNativeAdContentCallback.onNativeAdFailedToLoad]
     * will be called once with the error details.
     *
     * Apps requesting multiple ads should check loading state in their callback implementations
     * to determine whether the loading process has finished.
     *
     * @param maxNumberOfAds The maximum number of ads to load.
     */
    public fun load(maxNumberOfAds: Int = 1): Unit
}

/**
 * Represents the content of a native ad.
 *
 * This class contains various properties related to the content of a native ad,
 * including text, images, and metadata that describe the ad.
 *
 * You must pass an instance of this class to [com.cleveradssolutions.plugin.kmp.composable.CASNativeAd] or [com.cleveradssolutions.plugin.kmp.composable.CASNativeAdTemplate]
 * to populate and display the ad content within a view.
 */
public expect class CASNativeAdContent {
    /**
     * The headline text of the native ad.
     *
     * This is typically a short, attention-grabbing statement or title for the ad.
     */
    public val headline: String?

    /**
     * Text that identifies the advertiser.
     *
     * For example, advertiser name, brand name, or visible URL.
     * This text may truncate after 25 characters.
     */
    public val advertiser: String?

    /**
     * The secondary body text.
     *
     * For example, the article or app description.
     * This text may truncate after 90 characters.
     */
    public val body: String?

    /**
     * Button or text field that encourages users to take action.
     *
     * For example, "Visit site", "Install", "Learn More", or "Buy Now".
     * This text may truncate after 15 characters.
     */
    public val callToAction: String?

    /**
     * The price of the product or service advertised.
     */
    public val price: String?

    /**
     * The number of reviews the app has received.
     */
    public val reviewCount: String?

    /**
     * The rating from 0-5 that represents the average rating of the app in a store.
     *
     * For example, 4.5 stars.
     */
    public val starRating: Double?

    /**
     * The name of the store where the product or service is available.
     */
    public val store: String?

    /**
     * The ad label, such as "Sponsored" or "Ad".
     *
     * Indicates that the content is an advertisement.
     */
    public val adLabel: String?

    /**
     * Indicates whether the ad has expired.
     *
     * This property returns `true` if the ad is no longer valid or has surpassed its
     * intended display duration. An expired ad may not be shown to users,
     * and interactions with it may be restricted or disabled.
     */
    public val isExpired: Boolean

    /**
     * Information about the currently loaded ad.
     */
    public val contentInfo: CASAdContentInfo

    /**
     * Gets or sets the listener for ad impression events.
     *
     * This listener is notified when an ad impression is recorded and will be paid.
     * It allows you to track when the impression has been accounted for.
     */
    public var impressionCallback: CASAdImpressionCallback?

    /**
     * Cleans up and releases resources associated with the native ad content.
     *
     * This method should be called when the ad content is no longer needed
     * to free up resources.
     */
    public fun destroy(): Unit
}

/**
 * A callback interface for handling events related to native ad content.
 *
 * Implement this interface to respond to various states and actions associated with ad content,
 * such as loading success and user interactions.
 *
 * Methods in this interface are called on the main thread.
 */
public interface CASNativeAdContentCallback {
    /**
     * Called when the native ad content has been successfully loaded.
     *
     * Register the native ad object with the view that's responsible for displaying it [com.cleveradssolutions.plugin.kmp.composable.CASNativeAd].
     *
     * Be sure to use the [CASNativeAdContent.destroy] method on all loaded native ads,
     * even if they weren't used or referenced. This releases utilized resources and prevents memory leaks.
     *
     * A single [CASNativeAdContent] can be displayed only once.
     * Attempting to display the ad that has already been displayed will result in [onNativeAdFailedToShow].
     *
     * @param ad The native ad content that has been successfully loaded.
     */
    @MainThread
    public fun onNativeAdLoaded(ad: CASNativeAdContent): Unit

    /**
     * Called when the native ad fails to load.
     *
     * This method provides an opportunity to handle loading failures.
     *
     * @param error The error that occurred while attempting to load the native ad.
     */
    @MainThread
    public fun onNativeAdFailedToLoad(error: CASError): Unit {}

    /**
     * Called when the native ad fails to show.
     *
     * This method is triggered if there is an issue displaying the ad.
     *
     * @param ad The native ad content that failed to show.
     * @param error The error that occurred while attempting to show the native ad.
     */
    @MainThread
    public fun onNativeAdFailedToShow(ad: CASNativeAdContent, error: CASError): Unit {}

    /**
     * Called when the native ad is clicked by the user.
     *
     * This method provides an opportunity to handle ad clicks,
     * such as tracking click events or updating the UI.
     *
     * @param ad The native ad content that was clicked.
     */
    @MainThread
    public fun onNativeAdClicked(ad: CASNativeAdContent): Unit {}
}


/**
 * Native media image scale type.
 */
public enum class CASNativeScaleType(public val value: Int) {
    CENTER(0),
    CENTER_CROP(1),
    CENTER_INSIDE(2),
    FIT_CENTER(3),
    FIT_END(4),
    FIT_START(5),
    FIT_XY(6),
    MATRIX(7),
}

/**
 * Placement of the AdChoices icon.
 */
public enum class CASNativeAdChoicesPlacement(public val value: Int) {
    TOP_LEFT(0),
    TOP_RIGHT(1),
    BOTTOM_RIGHT(2),
    BOTTOM_LEFT(3),
}

/**
 * Font style for predefined native ad templates.
 */
public enum class CASNativeTemplateFontStyle(public val value: String) {
    NORMAL("normal"),
    BOLD("bold"),
    ITALIC("italic"),
    MONOSPACE("monospace"),
    MEDIUM("medium"),
}