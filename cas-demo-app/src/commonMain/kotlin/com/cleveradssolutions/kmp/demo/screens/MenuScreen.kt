package com.cleveradssolutions.kmp.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cleveradssolutions.kmp.demo.DemoRoute
import com.cleveradssolutions.kmp.demo.components.AppButton
import com.cleveradssolutions.kmp.demo.components.DemoCard
import com.cleveradssolutions.kmp.demo.components.DemoScreenContainer
import com.cleveradssolutions.kmp.demo.components.DemoSubtitle
import com.cleveradssolutions.kmp.demo.components.DemoTitle

@Composable
fun MenuScreen(onOpenRoute: (DemoRoute) -> Unit) {
    DemoScreenContainer {
        DemoCard {
            DemoTitle(text = "Examples")
            DemoSubtitle(text = "Choose an ad format to test")

            AppButton(DemoRoute.Interstitial.title, onClick = { onOpenRoute(DemoRoute.Interstitial) })
            AppButton(DemoRoute.Rewarded.title, onClick = { onOpenRoute(DemoRoute.Rewarded) })
            AppButton(DemoRoute.AppOpen.title, onClick = { onOpenRoute(DemoRoute.AppOpen) })

            MenuDivider()

            AppButton(DemoRoute.Native.title, onClick = { onOpenRoute(DemoRoute.Native) })
            AppButton(DemoRoute.NativeSizeExample.title, onClick = { onOpenRoute(DemoRoute.NativeSizeExample) })

            MenuDivider()

            AppButton(DemoRoute.Banner.title, onClick = { onOpenRoute(DemoRoute.Banner) })
            AppButton(DemoRoute.MREC.title, onClick = { onOpenRoute(DemoRoute.MREC) })
            AppButton(DemoRoute.Adaptive.title, onClick = { onOpenRoute(DemoRoute.Adaptive) })
        }
    }
}

@Composable
private fun MenuDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.06f)),
    )
}