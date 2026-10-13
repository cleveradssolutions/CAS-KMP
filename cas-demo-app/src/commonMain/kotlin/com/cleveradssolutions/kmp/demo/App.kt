package com.cleveradssolutions.kmp.demo

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.cleveradssolutions.plugin.kmp.CASMobileAds
import com.cleveradssolutions.kmp.demo.screens.AdaptiveBannerExampleScreen
import com.cleveradssolutions.kmp.demo.screens.AppOpenExampleScreen
import com.cleveradssolutions.kmp.demo.screens.BannerExampleScreen
import com.cleveradssolutions.kmp.demo.screens.InterstitialExampleScreen
import com.cleveradssolutions.kmp.demo.screens.MRecExampleScreen
import com.cleveradssolutions.kmp.demo.screens.MenuScreen
import com.cleveradssolutions.kmp.demo.screens.NativeExampleScreen
import com.cleveradssolutions.kmp.demo.screens.NativeTemplateSizeExampleScreen
import com.cleveradssolutions.kmp.demo.screens.RewardedExampleScreen
import com.cleveradssolutions.kmp.demo.screens.SetupScreen
import com.cleveradssolutions.kmp.demo.ui.theme.CasKmpDemoTheme

/**
 * Stores the route by name. A configuration change, such as a rotation, recreates the activity on
 * Android, and `remember` does not survive that, which would drop the user back on the setup screen.
 */
private val DemoRouteSaver = Saver<DemoRoute, String>(
    save = { it.name },
    restore = { DemoRoute.valueOf(it) },
)

@Composable
fun App(context: Any?) {
    var route by rememberSaveable(stateSaver = DemoRouteSaver) {
        // Skip the setup screen when the SDK has already been initialized, so that a restored
        // session continues where it was instead of asking to initialize twice.
        mutableStateOf(if (CASMobileAds.isInitialized) DemoRoute.Menu else DemoRoute.Setup)
    }

    PlatformBackHandler(enabled = route != DemoRoute.Setup) {
        route = if (route == DemoRoute.Menu) DemoRoute.Setup else DemoRoute.Menu
    }

    CasKmpDemoTheme(darkTheme = true) {
        when (route) {
            DemoRoute.Setup -> SetupScreen(context, onInitialized = { route = DemoRoute.Menu })
            DemoRoute.Menu -> MenuScreen(onOpenRoute = { route = it })
            DemoRoute.Interstitial -> InterstitialExampleScreen()
            DemoRoute.Rewarded -> RewardedExampleScreen()
            DemoRoute.AppOpen -> AppOpenExampleScreen()
            DemoRoute.Banner -> BannerExampleScreen()
            DemoRoute.MREC -> MRecExampleScreen()
            DemoRoute.Adaptive -> AdaptiveBannerExampleScreen()
            DemoRoute.Native -> NativeExampleScreen()
            DemoRoute.NativeSizeExample -> NativeTemplateSizeExampleScreen()
        }
    }
}

@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)