package com.koreatv.live.ui.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koreatv.live.ui.player.PlayerViewModel
import com.koreatv.live.ui.settings.SettingsScreen
import com.koreatv.live.ui.theme.KoreaTvTheme

class TvActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoreaTvTheme {
                val viewModel: PlayerViewModel = viewModel()
                viewModel.initPlayer(this)

                val isPlayerVisible by viewModel.isPlayerVisible.collectAsState()
                var showSettings by remember { mutableStateOf(false) }

                when {
                    showSettings -> {
                        BackHandler { showSettings = false }
                        SettingsScreen(onBack = { showSettings = false })
                    }
                    isPlayerVisible -> {
                        BackHandler { viewModel.closePlayer() }
                        TvPlayerScreen(viewModel = viewModel)
                    }
                    else -> {
                        TvChannelGridScreen(
                            viewModel = viewModel,
                            onOpenSettings = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}
