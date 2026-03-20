package com.koreatv.live

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.koreatv.live.data.Channel
import com.koreatv.live.data.ChannelRepository
import com.koreatv.live.player.StreamPlayer
import com.koreatv.live.ui.common.PlayerScreen
import com.koreatv.live.ui.tv.TvChannelGrid
import com.koreatv.live.ui.theme.KoreaTVTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TvActivity : ComponentActivity() {

    private lateinit var repository: ChannelRepository
    private lateinit var streamPlayer: StreamPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = ChannelRepository(this)
        streamPlayer = StreamPlayer(this)

        setContent {
            KoreaTVTheme {
                TvApp(repository, streamPlayer)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        streamPlayer.release()
    }
}

@Composable
fun TvApp(repository: ChannelRepository, streamPlayer: StreamPlayer) {
    var channels by remember { mutableStateOf(repository.getDefaultChannels()) }
    var favoriteIds by remember { mutableStateOf(repository.getFavoriteIds()) }
    var selectedChannel by remember { mutableStateOf<Channel?>(null) }

    // Load channels
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
            isTv = true
        )
    } else {
        TvChannelGrid(
            channels = channels,
            favoriteIds = favoriteIds,
            onChannelClick = { selectedChannel = it },
            onToggleFavorite = { id ->
                repository.toggleFavorite(id)
                favoriteIds = repository.getFavoriteIds()
            }
        )
    }
}
