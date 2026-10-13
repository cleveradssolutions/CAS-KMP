package com.cleveradssolutions.plugin.kmp

import kotlinx.cinterop.ExperimentalForeignApi
import cocoapods.CleverAdsSolutions_Base.CASError as IosCASError
import cocoapods.CleverAdsSolutions_Base.CASFormat as IosCASFormat

@OptIn(ExperimentalForeignApi::class)
internal fun IosCASError.toKmp(): CASError {
    return CASError(
        code = code().toInt(),
        message = description(),
    )
}

@OptIn(ExperimentalForeignApi::class)
internal fun IosCASFormat.toKmp(): CASAdFormat {
    return CASAdFormat.entries.find { it.value == value().toInt() }
        ?: CASAdFormat.BANNER
}

internal fun Int.toAdRevenuePrecision(): CASAdRevenuePrecision {
    return CASAdRevenuePrecision.entries.find { it.value == this }
        ?: CASAdRevenuePrecision.UNKNOWN
}

internal fun CASTaggedAudience.toIos(): Long {
    return value.toLong()
}

internal fun CASGender.toIos(): Long {
    return value.toLong()
}

internal fun CASConsentDebugGeography.toIos(): Long {
    return value.toLong()
}

internal fun CASNativeAdChoicesPlacement.toIos(): Long {
    return value.toLong()
}

internal fun Long.toConsentFlowStatus(): CASConsentFlowStatus {
    return CASConsentFlowStatus.entries.find { it.value == toInt() }
        ?: CASConsentFlowStatus.UNKNOWN
}
