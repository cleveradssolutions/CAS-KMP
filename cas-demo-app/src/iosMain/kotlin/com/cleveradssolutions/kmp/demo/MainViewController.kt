package com.cleveradssolutions.kmp.demo

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.ui.uikit.LocalUIViewController

fun MainViewController() = ComposeUIViewController {
    // optional UIView Controller
    val uiViewController = LocalUIViewController.current
    App(context = uiViewController)
}