package com.cleveradssolutions.kmp.demo

internal const val TAG = "CAS-KMP-DEMO"

enum class DemoRoute(val title: String) {
    Setup("Setup"),
    Menu("Menu"),
    Interstitial("Interstitial Ad"),
    Rewarded("Rewarded Ad"),
    AppOpen("App Open Ad"),
    Native("Native Ad"),
    NativeSizeExample("Native Ad (Template Size)"),
    Banner("Banner Ad"),
    MREC("MREC Ad"),
    Adaptive("Adaptive Banner Ad"),
}