package com.koreatv.live.ui.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koreatv.live.ui.player.PlayerViewModel
import com.koreatv.live.ui.settings.SettingsScreen
import com.koreatv.live.ui.theme.KoreaTvTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoreaTvTheme {
                val viewModel: PlayerViewModel = viewModel()
                viewModel.initPlayer(this)

                val isPlayerVisible by viewModel.isPlayerVisible.collectAsState()
                var showSettings by remember { mutableStateOf(false) }

                when {
                    showSettings -> {
                        BackHandler { showSettings = false }
                        SettingsScreen(
                            onBack = { showSettings = false }
                        )
                    }
                    isPlayerVisible -> {
                        BackHandler { viewModel.closePlayer() }
                        MobilePlayerScreen(viewModel = viewModel)
                    }
                    else -> {
                        MobileChannelListScreen(
                            viewModel = viewModel,
                            onOpenSettings = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}
