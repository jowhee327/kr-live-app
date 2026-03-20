package com.koreatv.live.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.koreatv.live.data.StreamSource

class StreamPlayer(context: Context) {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private var currentStreams: List<StreamSource> = emptyList()
    private var currentStreamIndex: Int = 0
    private var onAllStreamsFailed: (() -> Unit)? = null

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                // Try next stream source
                tryNextStream()
            }
        })
    }

    fun play(streams: List<StreamSource>, onAllFailed: (() -> Unit)? = null) {
        currentStreams = streams.sortedBy { it.priority }
        currentStreamIndex = 0
        onAllStreamsFailed = onAllFailed
        playCurrentStream()
    }

    private fun playCurrentStream() {
        if (currentStreamIndex >= currentStreams.size) {
            onAllStreamsFailed?.invoke()
            return
        }
        val stream = currentStreams[currentStreamIndex]
        val mediaItem = MediaItem.fromUri(stream.url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    private fun tryNextStream() {
        currentStreamIndex++
        playCurrentStream()
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun isPlaying(): Boolean = exoPlayer.isPlaying

    fun release() {
        exoPlayer.release()
    }
}
