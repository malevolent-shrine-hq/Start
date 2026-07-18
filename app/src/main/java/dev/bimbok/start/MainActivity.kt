package dev.bimbok.start

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.bimbok.start.ui.MainScreen
import dev.bimbok.start.ui.settings.viewmodel.SettingsViewModel
import dev.bimbok.start.ui.theme.StartTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val currentTheme by settingsViewModel.theme.collectAsState()
            
            StartTheme(theme = currentTheme) {
                MainScreen()
            }
        }
    }
}
