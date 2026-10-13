package com.cleveradssolutions.plugin.kmp

import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import com.cleversolutions.ads.ConsentFlow
import com.cleversolutions.ads.InitialConfiguration as AndroidInitialConfiguration
import com.cleversolutions.ads.android.CAS as AndroidCAS

public actual object CASMobileAds {
    internal var appContext: Context? = null

    public actual val TEST_ID: String = AndroidCAS.TEST_ID

    public actual val targetingOptions: CASTargetingOptions =
        AndroidTargetingOptions()

    private var lastCasId: String? = null
    private var lastConfig: CASInitializationResult? = null

    public actual val sdkVersion: String
        get() = AndroidCAS.getSDKVersion()

    public actual var adSoundsMuted: Boolean
        get() = AndroidCAS.settings.mutedAdSounds
        set(value) {
            AndroidCAS.settings.mutedAdSounds = value
        }

    public actual var debugLoggingEnabled: Boolean
        get() = AndroidCAS.settings.debugMode
        set(value) {
            AndroidCAS.settings.debugMode = value
        }

    public actual var trialAdFreeInterval: Int
        get() = AndroidCAS.settings.trialAdFreeInterval
        set(value) {
            AndroidCAS.settings.trialAdFreeInterval = value
        }

    public actual val isInitialized: Boolean
        get() = lastConfig != null && lastConfig?.error == null

    @AnyThread
    public actual fun initialize(
        context: Any?,
        casId: String,
        targetAudience: CASTaggedAudience?,
        showConsentFormIfRequired: Boolean,
        forceTestAds: Boolean,
        testDeviceIds: Set<String>?,
        debugGeography: CASConsentDebugGeography,
        mediationExtras: Map<String, String>?,
        listener: CASInitializationListener?,
    ) {
        val cachedConfig = lastConfig

        if (cachedConfig != null && cachedConfig.error == null && lastCasId == casId) {
            listener?.onCASInitialized(cachedConfig)
            return
        }

        lastCasId = casId

        val androidContext = context as? Context
            ?: error("Android Context is required. Pass Activity, Application, or another Context instance.")
        appContext = androidContext.applicationContext

        targetAudience?.let { audience ->
            AndroidCAS.settings.taggedAudience = audience.value
        }

        testDeviceIds?.let { ids ->
            AndroidCAS.settings.testDeviceIDs = ids
        }

        val consentFlow = ConsentFlow(showConsentFormIfRequired)
            .withDebugGeography(debugGeography.value)
            .withForceTesting(forceTestAds)

        val builder = AndroidCAS.buildManager()
            .withCasId(casId)
            .withTestAdMode(forceTestAds)
            .withFramework("KMP", KotlinVersion.CURRENT.toString())
            .withConsentFlow(consentFlow)
            .withCompletionListener { config ->
                val result = CASInitializationResult(
                    error = config.error,
                    countryCode = config.countryCode,
                    isConsentRequired = config.isConsentRequired,
                    consentFlowStatus = config.consentFlowStatus.toConsentFlowStatus(),
                )
                lastConfig = result
                listener?.onCASInitialized(result)
            }

        mediationExtras.orEmpty().forEach { entry ->
            if (entry.key.isNotEmpty() && entry.value.isNotEmpty()) {
                builder.withMediationExtras(entry.key, entry.value)
            }
        }

        builder.build(androidContext)
    }

    @MainThread
    public actual fun showConsentForm(
        ifRequiredOnly: Boolean,
        debugGeography: CASConsentDebugGeography,
        dismissListener: CASConsentFormDismissListener?,
    ) {
        val flow = ConsentFlow()
        if (debugGeography != CASConsentDebugGeography.DISABLED) {
            flow.withDebugGeography(debugGeography.value)
            flow.withForceTesting(true)
        }
        if (dismissListener != null) {
            flow.withDismissListener { status ->
                dismissListener.onConsentFormDismissed(status.toConsentFlowStatus())
            }
        }
        if (ifRequiredOnly)
            flow.showIfRequired()
        else
            flow.show()
    }

    internal fun requireAppContext(): Context {
        return appContext
            ?: error("CAS is not initialized. Call CAS.initialize(...) before creating ads.")
    }
}

private fun Int.toConsentFlowStatus(): CASConsentFlowStatus {
    return CASConsentFlowStatus.entries.find { it.value == this }
        ?: CASConsentFlowStatus.UNKNOWN
}