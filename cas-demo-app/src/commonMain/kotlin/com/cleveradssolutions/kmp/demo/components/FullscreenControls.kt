package com.cleveradssolutions.kmp.demo.components

import androidx.compose.runtime.Composable

const val RETRY_DELAY_MS = 10_000L

@Composable
fun FullscreenControls(
    title: String,
    loaded: Boolean,
    onLoad: () -> Unit,
    onShow: () -> Unit,
) {
    DemoScreenContainer {
        DemoCard {
            DemoTitle(text = title)
            AppButton(title = "Load", onClick = onLoad)
            AppButton(title = "Show", enabled = loaded, onClick = onShow)
        }
    }
}