package com.koreatv.live.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.koreatv.live.data.local.PreferencesManager
import com.koreatv.live.data.model.Channel
import com.koreatv.live.data.repository.ChannelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    private val repository = ChannelRepository(prefs)

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _currentChannel = MutableStateFlow<Channel?>(null)
    val currentChannel: StateFlow<Channel?> = _currentChannel.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isPlayerVisible = MutableStateFlow(false)
    val isPlayerVisible: StateFlow<Boolean> = _isPlayerVisible.asStateFlow()

    private var currentStreamIndex = 0

    var player: ExoPlayer? = null
        private set

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(playing: Boolean) {
            _isPlaying.value = playing
        }

        override fun onPlayerError(error: PlaybackException) {
            tryNextStream()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                tryNextStream()
            }
        }
    }

    init {
        loadChannels()
    }

    private fun loadChannels() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val loaded = repository.loadChannels()
            _channels.value = loaded
            _categories.value = loaded.map { it.category }.distinct().sorted()
            _favorites.value = prefs.getFavorites()
            _isLoading.value = false
        }
    }

    fun refreshChannels() {
        loadChannels()
    }

    fun filteredChannels(): List<Channel> {
        var list = _channels.value
        _selectedCategory.value?.let { cat ->
            list = list.filter { it.category == cat }
        }
        if (_showFavoritesOnly.value) {
            list = list.filter { _favorites.value.contains(it.id) }
        }
        return list
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun toggleFavoritesFilter() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun toggleFavorite(channelId: String) {
        prefs.toggleFavorite(channelId)
        _favorites.value = prefs.getFavorites()
    }

    fun initPlayer(context: android.content.Context) {
        if (player == null) {
            player = ExoPlayer.Builder(context).build().apply {
                addListener(playerListener)
            }
        }
    }

    fun playChannel(channel: Channel) {
        _currentChannel.value = channel
        _isPlayerVisible.value = true
        currentStreamIndex = 0
        playCurrentStream()
    }

    private fun playCurrentStream() {
        val channel = _currentChannel.value ?: return
        val streams = channel.streams.sortedBy { it.priority }
        if (currentStreamIndex >= streams.size) {
            currentStreamIndex = 0
            return
        }
        val streamUrl = streams[currentStreamIndex].url
        player?.let {
            it.setMediaItem(MediaItem.fromUri(streamUrl))
            it.prepare()
            it.play()
        }
    }

    private fun tryNextStream() {
        val channel = _currentChannel.value ?: return
        currentStreamIndex++
        if (currentStreamIndex < channel.streams.size) {
            playCurrentStream()
        }
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun playNextChannel() {
        val filtered = filteredChannels()
        val current = _currentChannel.value ?: return
        val idx = filtered.indexOfFirst { it.id == current.id }
        if (idx >= 0 && idx < filtered.size - 1) {
            playChannel(filtered[idx + 1])
        } else if (filtered.isNotEmpty()) {
            playChannel(filtered[0])
        }
    }

    fun playPreviousChannel() {
        val filtered = filteredChannels()
        val current = _currentChannel.value ?: return
        val idx = filtered.indexOfFirst { it.id == current.id }
        if (idx > 0) {
            playChannel(filtered[idx - 1])
        } else if (filtered.isNotEmpty()) {
            playChannel(filtered[filtered.size - 1])
        }
    }

    fun closePlayer() {
        player?.stop()
        _isPlayerVisible.value = false
        _currentChannel.value = null
    }

    override fun onCleared() {
        super.onCleared()
        player?.removeListener(playerListener)
        player?.release()
        player = null
    }
}
