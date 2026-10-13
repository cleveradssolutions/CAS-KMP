package com.cleveradssolutions.plugin.kmp.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cleveradssolutions.plugin.kmp.CASBannerAdContent

/**
 * Shows already created [CASBannerAdContent].
 *
 * Loading, reloading and destroying should be controlled by the application/example code.
 * If [adContent] is not loaded yet, this composable does not show visible banner content.
 */
@Composable
public expect fun CASBannerAd(
    adContent: CASBannerAdContent,
    modifier: Modifier = Modifier,
): Unit