package com.cleveradssolutions.plugin.kmp

import cocoapods.CleverAdsSolutions_Base.CASContentInfo
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal class IOSContentInfo(
    private val ios: CASContentInfo
) : CASAdContentInfo {
    override val format: CASAdFormat
        get() = ios.format().toKmp()
    override val sourceName: String
        get() = ios.sourceName()
    override val sourceUnitId: String
        get() = ios.sourceUnitID()
    override val creativeId: String?
        get() = ios.creativeID()
    override val revenue: Double
        get() = ios.revenue()
    override val revenuePrecision: CASAdRevenuePrecision
        get() = ios.revenuePrecision().toInt().toAdRevenuePrecision()
    override val impressionDepth: Int
        get() = ios.impressionDepth().toInt()
    override val revenueTotal: Double
        get() = ios.revenueTotal()
}