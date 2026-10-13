package com.cleveradssolutions.plugin.kmp

import androidx.annotation.AnyThread
import androidx.annotation.MainThread

/**
 * Main entry point for configuring and initializing CAS SDK.
 */
public expect object CASMobileAds {
    /**
     * Demo CAS ID used for test ads.
     */
    public val TEST_ID: String

    /**
     * You can now easily tailor the way you serve your ads to fit a specific audience.
     *
     * You’ll need to inform CAS servers of the users’ details
     * so the SDK will know to serve ads according to the segment the user belongs to.
     *
     * Important: must be set before initializing the SDK.
     */
    public val targetingOptions: CASTargetingOptions

    /**
     * Returns current CAS SDK version.
     */
    public val sdkVersion: String

    /**
     * Indicates if the application’s audio is muted.
     *
     * Affects initial mute state for all ads.
     * Use this property only if your application has its own volume controls,
     * for example custom music or sound effect muting.
     *
     * Disabled by default.
     */
    public var adSoundsMuted: Boolean

    /**
     * The enabled Debug Mode will display a lot of useful information for debugging
     * about the states of the SDK with tag CAS.
     *
     * Disabling Debug Mode may improve application performance.
     *
     * Disabled by default.
     */
    public var debugLoggingEnabled: Boolean

    /**
     * Defines the time interval, in seconds, starting from the moment of the initial app installation,
     * during which users can use the application without ads being displayed while still retaining
     * access to the Rewarded Ads format.
     *
     * Within this interval, users enjoy privileged access to the application's features
     * without intrusive advertisements.
     *
     * Default: 0 seconds.
     */
    public var trialAdFreeInterval: Int

    /**
     * Indicates whether CAS SDK was successfully initialized.
     */
    public val isInitialized: Boolean

    /**
     * Initializes CAS SDK.
     *
     * Sets the CAS ID used to identify ad requests.
     *
     * The CAS ID is a unique identifier assigned to each ad placement created in CAS.
     * Typically, the CAS ID matches your app's Bundle ID.
     *
     * If you haven’t set up a CAS account and registered your app,
     * visit cas.ai to do so.
     *
     * @param context Platform context. For Android it can be Activity, Application or another Context.
     * @param casId CAS ID used to identify ad requests.
     * @param targetAudience Ad filters by audience. Must be set before initializing the SDK.
     * @param showConsentFormIfRequired Whether the consent flow should be shown to users as required by law.
     * @param forceTestAds Enables demo ad mode, which always requests test ads.
     * Remember to disable this mode after testing is complete.
     * @param testDeviceIds Test device IDs which will always request test ads.
     * List of test devices should be defined before first SDK initialization.
     * @param debugGeography Debug geography for consent flow testing. Note that debug settings only work test ads enabled.
     * @param mediationExtras Additional mediation settings.
     * @param listener Listener to receive a callback when CAS initialization is complete.
     */
    @AnyThread
    public fun initialize(
        context: Any? = null,
        casId: String,
        targetAudience: CASTaggedAudience? = null,
        showConsentFormIfRequired: Boolean = true,
        forceTestAds: Boolean = false,
        testDeviceIds: Set<String>? = null,
        debugGeography: CASConsentDebugGeography = CASConsentDebugGeography.EEA,
        mediationExtras: Map<String, String>? = null,
        listener: CASInitializationListener? = null,
    ): Unit

    /**
     * Shows the consent form.
     *
     * Use this method when you need to manually show the consent flow UI.
     *
     * @param ifRequiredOnly Shows the consent form only if it is required and the user has not responded previously.
     * If the consent status is required, the SDK loads a form and immediately presents it.
     * @param debugGeography Force testing flow respect debug geography settings to enable easier testing.
     * Attention Don't forget to disable it after the tests are completed.
     * @param dismissListener Listener called when consent flow is dismissed.
     */
    @MainThread
    public fun showConsentForm(
        ifRequiredOnly: Boolean = true,
        debugGeography: CASConsentDebugGeography = CASConsentDebugGeography.DISABLED,
        dismissListener: CASConsentFormDismissListener? = null,
    ): Unit
}

/**
 * User targeting options.
 *
 * You can tailor the way you serve your ads to fit a specific audience.
 * Values should be set before SDK initialization when possible.
 */
public interface CASTargetingOptions {
    /**
     * The user ID is a unique identifier supplied by your application and must be static
     * for each user across sessions.
     *
     * Your user ID should not contain any personally identifiable information such as
     * an email address, screen name, Android ID, or Google Advertising ID.
     */
    public var userId: String?

    /**
     * Set targeting to user’s gender.
     */
    public var gender: CASGender

    /**
     * The user’s age.
     *
     * Limitation: 1-99 and 0 is unknown.
     */
    public var age: Int

    /**
     * Collect from the device the latitude and longitude coordinates truncated to the
     * hundredths decimal place.
     *
     * Collect only if your application already has the relevant end-user permissions.
     * Does not collect if the target audience is children.
     *
     * Disabled by default.
     */
    public var locationCollectionEnabled: Boolean

    /**
     * A list of keywords, interests, or intents related to your application.
     *
     * Words or phrase describing the current activity of the user for targeting purposes.
     */
    public var keywords: Set<String>?

    /**
     * Sets the content URL for a web site whose content matches the app's primary content.
     *
     * This web site content is used for targeting and brand safety purposes.
     * Limitation: max URL length 512.
     */
    public var contentUrl: String?
}

/**
 * Listener to receive a callback when CAS initialization is complete.
 */
public fun interface CASInitializationListener {
    /**
     * Called when CAS initialization is complete.
     *
     * @param config Initialization result.
     */
    public fun onCASInitialized(config: CASInitializationResult): Unit
}

/**
 * Listener called when consent form is dismissed.
 */
public fun interface CASConsentFormDismissListener {
    /**
     * Called when consent flow is dismissed.
     *
     * @param status Consent flow status.
     */
    public fun onConsentFormDismissed(status: CASConsentFlowStatus): Unit
}

/**
 * CAS SDK initialization result.
 */
public data class CASInitializationResult(
    /**
     * Error message if initialization failed, or `null` if initialization completed successfully.
     */
    public val error: String?,

    /**
     * Country code detected during initialization, if available.
     */
    public val countryCode: String?,

    /**
     * Indicates whether consent is required for the user.
     */
    public val isConsentRequired: Boolean,

    /**
     * Current consent flow status.
     */
    public val consentFlowStatus: CASConsentFlowStatus,
)

/**
 * Gender targeting value.
 */
public enum class CASGender(public val value: Int) {
    UNKNOWN(0),
    MALE(1),
    FEMALE(2),
}

/**
 * Ad filters by audience.
 */
public enum class CASTaggedAudience(public val value: Int) {
    UNDEFINED(0),
    CHILDREN(1),
    NOT_CHILDREN(2),
}

/**
 * Consent flow status.
 */
public enum class CASConsentFlowStatus(public val value: Int) {
    UNKNOWN(0),
    OBTAINED(3),
    NOT_REQUIRED(4),
    UNAVAILABLE(5),
    INTERNAL_ERROR(10),
    NETWORK_ERROR(11),
    CONTEXT_INVALID(12),
    FLOW_STILL_SHOWING(13),
}

/**
 * Debug geography for consent flow testing.
 */
public enum class CASConsentDebugGeography(public val value: Int) {
    DISABLED(0),
    EEA(1),
    REGULATED_US_STATE(3),
    OTHER(4),
}