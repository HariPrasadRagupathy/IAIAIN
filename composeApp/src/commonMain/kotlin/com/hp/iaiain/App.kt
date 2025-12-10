package com.hp.iaiain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
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

            // Observe state changes
            LaunchedEffect(viewModel) {
                viewModel.state.collect { newState ->
                    state = newState
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
        }
    }
}