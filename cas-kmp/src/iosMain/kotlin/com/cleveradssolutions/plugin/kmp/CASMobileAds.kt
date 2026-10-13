package com.cleveradssolutions.plugin.kmp

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import cocoapods.CleverAdsSolutions_Base.CASConsentFlow
import cocoapods.CleverAdsSolutions_Base.CASInitialConfig
import kotlinx.cinterop.ExperimentalForeignApi
import cocoapods.CleverAdsSolutions_Base.CAS as IosCAS

@OptIn(ExperimentalForeignApi::class)
public actual object CASMobileAds {
    public actual val TEST_ID: String = "demo"

    public actual val targetingOptions: CASTargetingOptions =
        IOSTargetingOptions()

    private var lastCasId: String? = null
    private var lastConfig: CASInitializationResult? = null

    public actual val sdkVersion: String
        get() = IosCAS.getSDKVersion()

    public actual var adSoundsMuted: Boolean
        get() = IosCAS.settings().mutedAdSounds()
        set(value) {
            IosCAS.settings().setMutedAdSounds(value)
        }

    public actual var debugLoggingEnabled: Boolean
        get() = IosCAS.settings().debugMode()
        set(value) {
            IosCAS.settings().setDebugMode(value)
        }

    public actual var trialAdFreeInterval: Int
        get() = IosCAS.settings().trialAdFreeInterval().toInt()
        set(value) {
            IosCAS.settings().setTrialAdFreeInterval(value.toULong())
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

        targetAudience?.let { audience ->
            IosCAS.settings().setTaggedAudience(audience.toIos())
        }

        testDeviceIds
            ?.takeIf { it.isNotEmpty() }
            ?.let { ids ->
                IosCAS.settings().setTestDeviceWithIds(ids.toList())
            }

        val consentFlow = CASConsentFlow(showConsentFormIfRequired).apply {
            setDebugGeography(debugGeography.toIos())
            setForceTesting(forceTestAds)
        }

        val builder = IosCAS.buildManager()

        builder.withTestAdMode(forceTestAds)
        builder.withConsentFlow(consentFlow)
        builder.withFramework("KMP", KotlinVersion.CURRENT.toString())

        mediationExtras.orEmpty().forEach { entry ->
            val key = entry.key
            val value = entry.value

            if (key.isNotEmpty() && value.isNotEmpty()) {
                builder.withMediationExtras(
                    value = value,
                    forKey = key,
                )
            }
        }

        builder.withCompletionHandler { config: CASInitialConfig? ->
            val forceConfig = config!!
            val result = CASInitializationResult(
                error = forceConfig.error(),
                countryCode = forceConfig.countryCode(),
                isConsentRequired = forceConfig.isConsentRequired(),
                consentFlowStatus = forceConfig.consentFlowStatus().toConsentFlowStatus(),
            )
            lastConfig = result
            listener?.onCASInitialized(result)
        }

        builder.createWithCasId(casId)
    }

    @MainThread
    public actual fun showConsentForm(
        ifRequiredOnly: Boolean,
        debugGeography: CASConsentDebugGeography,
        dismissListener: CASConsentFormDismissListener?,
    ) {
        val flow = CASConsentFlow()
        if (debugGeography != CASConsentDebugGeography.DISABLED) {
            flow.setDebugGeography(debugGeography.toIos())
            flow.setForceTesting(true)
        }
        if (dismissListener != null) {
            flow.setCompletionHandler { status ->
                dismissListener.onConsentFormDismissed(status.toConsentFlowStatus())
            }
        }

        if (ifRequiredOnly)
            flow.presentIfRequired()
        else
            flow.present()
    }
}