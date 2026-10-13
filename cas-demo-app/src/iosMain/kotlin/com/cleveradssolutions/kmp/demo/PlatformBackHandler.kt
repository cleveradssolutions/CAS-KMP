package com.cleveradssolutions.kmp.demo

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS handles back navigation via swipe gestures; no-op here
}