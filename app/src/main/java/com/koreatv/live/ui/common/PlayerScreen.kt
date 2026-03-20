package com.koreatv.live.ui.common

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.koreatv.live.data.Channel
import com.koreatv.live.player.StreamPlayer
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    channel: Channel,
    channels: List<Channel>,
    streamPlayer: StreamPlayer,
    onBack: () -> Unit,
    onSwitchChannel: (Channel) -> Unit,
    isTv: Boolean = false
) {
    var showControls by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Auto-hide controls
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(5000)
            showControls = false
        }
    }

    // Play channel
    LaunchedEffect(channel) {
        errorMessage = null
        streamPlayer.play(
            streams = channel.streams,
            onAllFailed = { errorMessage = "所有直播源均不可用" }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        // ExoPlayer view
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = streamPlayer.exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Error overlay
        errorMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(msg, color = Color.White, fontSize = 16.sp)
                }
            }
        }

        // Controls overlay
        if (showControls) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White,
                            modifier = Modifier.size(if (isTv) 36.dp else 28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = channel.name,
                        color = Color.White,
                        fontSize = if (isTv) 24.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = channel.category,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = if (isTv) 16.sp else 12.sp
                    )
                }

                // Center controls
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous channel
                    val currentIndex = channels.indexOf(channel)
                    val prevChannel = if (currentIndex > 0) channels[currentIndex - 1] else null

                    IconButton(
                        onClick = { prevChannel?.let { onSwitchChannel(it) } },
                        enabled = prevChannel != null,
                        modifier = Modifier.size(if (isTv) 64.dp else 48.dp)
                    ) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = "上一个频道",
                            tint = if (prevChannel != null) Color.White else Color.Gray,
                            modifier = Modifier.size(if (isTv) 48.dp else 36.dp)
                        )
                    }

                    // Play/Pause
                    IconButton(
                        onClick = {
                            streamPlayer.togglePlayPause()
                            isPlaying = streamPlayer.isPlaying()
                        },
                        modifier = Modifier.size(if (isTv) 80.dp else 64.dp)
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "暂停" else "播放",
                            tint = Color.White,
                            modifier = Modifier.size(if (isTv) 56.dp else 44.dp)
                        )
                    }

                    // Next channel
                    val nextChannel = if (currentIndex < channels.size - 1) channels[currentIndex + 1] else null

                    IconButton(
                        onClick = { nextChannel?.let { onSwitchChannel(it) } },
                        enabled = nextChannel != null,
                        modifier = Modifier.size(if (isTv) 64.dp else 48.dp)
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "下一个频道",
                            tint = if (nextChannel != null) Color.White else Color.Gray,
                            modifier = Modifier.size(if (isTv) 48.dp else 36.dp)
                        )
                    }
                }
            }
        }
    }
}
