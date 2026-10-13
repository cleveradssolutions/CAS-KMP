package com.cleveradssolutions.plugin.kmp

import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.AdFormat as AndroidAdFormat
import com.cleversolutions.ads.AdError as AndroidAdError

internal class AndroidContentInfoImpl(
    private val android: AdContentInfo
) : CASAdContentInfo {
    override val format: CASAdFormat
        get() = android.format.toKmp()
    override val sourceName: String
        get() = android.sourceName
    override val sourceUnitId: String
        get() = android.sourceUnitId
    override val creativeId: String?
        get() = android.creativeId
    override val revenue: Double
        get() = android.revenue
    override val revenuePrecision: CASAdRevenuePrecision
        get() = android.revenuePrecision.toAdRevenuePrecision()
    override val impressionDepth: Int
        get() = android.impressionDepth
    override val revenueTotal: Double
        get() = android.revenueTotal
}

private fun Int.toAdRevenuePrecision(): CASAdRevenuePrecision {
    return CASAdRevenuePrecision.entries.find { it.value == this }
        ?: CASAdRevenuePrecision.UNKNOWN
}

internal fun AndroidAdFormat.toKmp(): CASAdFormat {
    return CASAdFormat.entries.find { it.value == value }
        ?: CASAdFormat.BANNER
}

internal fun AndroidAdError.toKmp(): CASError {
    return CASError(
        code = code,
        message = message,
    )
}
