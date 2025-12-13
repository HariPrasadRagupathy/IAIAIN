package com.hp.iaiain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hp.iaiain.design.system.BackgroundDark
import com.hp.iaiain.design.system.IAIAINTheme
import com.hp.iaiain.di.ServiceLocator
import com.hp.iaiain.features.launching.presentation.mvi.LaunchingEffect
import com.hp.iaiain.features.launching.presentation.mvi.LaunchingIntent
import com.hp.iaiain.features.launching.presentation.mvi.LaunchingScreenState
import com.hp.iaiain.features.launching.ui.composable.LaunchingScreen

@Composable
fun App() {
    IAIAINTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            val viewModel = remember { ServiceLocator.createLaunchingViewModel() }
            var state: LaunchingScreenState by remember { mutableStateOf(viewModel.state.value) }
            var urlToOpen by remember { mutableStateOf<String?>(null) }

            // Observe state changes
            LaunchedEffect(viewModel) {
                viewModel.state.collect { newState ->
                    state = newState
                }
            }

            // Observe effects
            LaunchedEffect(viewModel) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is LaunchingEffect.OpenLink -> {
                            // Store URL to open
                            urlToOpen = effect.url
                        }
                        is LaunchingEffect.ShowSuccess -> {
                            // Already handled by state
                        }
                        is LaunchingEffect.ShowError -> {
                            // Already handled by state
                        }
                        is LaunchingEffect.NavigateToNextScreen -> {
                            // Handle navigation if needed
                        }
                    }
                }
            }

            // Initialize countdown
            LaunchedEffect(Unit) {
                viewModel.handleIntent(LaunchingIntent.InitializeCountdown)
            }

            LaunchingScreen(
                state = state,
                onIntent = { viewModel.handleIntent(it) }
            )

            // Show link dialog
            urlToOpen?.let { url ->
                LinkDialog(
                    url = url,
                    onDismiss = { urlToOpen = null },
                    onOpenLink = {
                        openUrl(url)
                        urlToOpen = null
                    }
                )
            }
        }
    }
}

@Composable
fun LinkDialog(
    url: String,
    onDismiss: () -> Unit,
    onOpenLink: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Open Link") },
        text = { Text("Open this link?\n\n$url") },
        confirmButton = {
            Button(onClick = onOpenLink) {
                Text("Open")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Platform-specific URL opener
expect fun openUrl(url: String)
