package com.cleveradssolutions.kmp.demo.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.cleveradssolutions.plugin.kmp.CASInitializationListener
import com.cleveradssolutions.plugin.kmp.CASMobileAds
import com.cleveradssolutions.plugin.kmp.CASTaggedAudience
import com.cleveradssolutions.kmp.demo.TAG
import com.cleveradssolutions.kmp.demo.components.AppButton
import com.cleveradssolutions.kmp.demo.components.DemoCard
import com.cleveradssolutions.kmp.demo.components.DemoScreenContainer
import com.cleveradssolutions.kmp.demo.components.DemoSubtitle
import com.cleveradssolutions.kmp.demo.components.DemoTitle
import com.cleveradssolutions.kmp.demo.components.ErrorText
import com.cleveradssolutions.kmp.demo.components.SecondaryText

@Composable
fun SetupScreen(context: Any?, onInitialized: () -> Unit) {
    var loading by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    DemoScreenContainer {
        DemoCard {
            DemoTitle(text = "Initialize CAS")
            DemoSubtitle(text = "One tap to set up SDK & continue")

            AppButton(
                title = if (loading) "Initializing…" else "Initialize",
                enabled = !loading,
                onClick = {
                    if (loading) return@AppButton

                    loading = true
                    errorText = null
                    statusText = "Initializing…"

                    CASMobileAds.debugLoggingEnabled = true

                    CASMobileAds.initialize(
                        context = context,
                        casId = CASMobileAds.TEST_ID,
                        targetAudience = CASTaggedAudience.UNDEFINED,
                        forceTestAds = true,
                        mediationExtras = mapOf(
                            "testKey" to "testValue",
                        ),
                        listener = CASInitializationListener { config ->
                            println("$TAG: CAS initialized: $config")
                            if (config.error != null) {
                                statusText = null
                                errorText = config.error
                                loading = false
                            } else {
                                statusText = "Initialization complete"
                                loading = false
                                onInitialized()
                            }
                        },
                    )
                },
            )

            if (loading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = statusText.orEmpty(),
                        color = SecondaryText,
                    )
                }
            }

            if (errorText != null) {
                Text(
                    text = errorText.orEmpty(),
                    color = ErrorText,
                    textAlign = TextAlign.Center,
                )
            }

            Text(
                text = "SDK version: ${CASMobileAds.sdkVersion}",
                color = Color(0xFF7C8797),
                textAlign = TextAlign.Center,
            )
        }
    }
}