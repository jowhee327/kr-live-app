package com.koreatv.live

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.koreatv.live.data.Channel
import com.koreatv.live.data.ChannelRepository
import com.koreatv.live.player.StreamPlayer
import com.koreatv.live.ui.common.PlayerScreen
import com.koreatv.live.ui.mobile.MobileChannelList
import com.koreatv.live.ui.mobile.SettingsDialog
import com.koreatv.live.ui.theme.KoreaTVTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var repository: ChannelRepository
    private lateinit var streamPlayer: StreamPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = ChannelRepository(this)
        streamPlayer = StreamPlayer(this)

        setContent {
            KoreaTVTheme {
                MobileApp(repository, streamPlayer)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        streamPlayer.release()
    }
}

@Composable
fun MobileApp(repository: ChannelRepository, streamPlayer: StreamPlayer) {
    var channels by remember { mutableStateOf(repository.getDefaultChannels()) }
    var favoriteIds by remember { mutableStateOf(repository.getFavoriteIds()) }
    var selectedChannel by remember { mutableStateOf<Channel?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    // Load channels (attempt remote)
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val loaded = repository.getChannels()
                if (loaded.isNotEmpty()) {
                    channels = loaded
                }
            } catch (_: Exception) { }
        }
    }

    if (selectedChannel != null) {
        PlayerScreen(
            channel = selectedChannel!!,
            channels = channels,
            streamPlayer = streamPlayer,
            onBack = { selectedChannel = null },
            onSwitchChannel = { selectedChannel = it },
            isTv = false
        )
    } else {
        MobileChannelList(
            channels = channels,
            favoriteIds = favoriteIds,
            onChannelClick = { selectedChannel = it },
            onToggleFavorite = { id ->
                repository.toggleFavorite(id)
                favoriteIds = repository.getFavoriteIds()
            },
            onOpenSettings = { showSettings = true }
        )
    }

    if (showSettings) {
        SettingsDialog(
            remoteUrl = repository.getRemoteUrl(),
            onDismiss = { showSettings = false },
            onSave = { url ->
                repository.setRemoteUrl(url)
                showSettings = false
                // Reload channels
                channels = repository.getChannels()
            }
        )
    }
}
