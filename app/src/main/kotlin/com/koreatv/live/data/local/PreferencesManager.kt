package com.koreatv.live.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("korea_tv_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_CHANNEL_SOURCE_URL = "channel_source_url"
        private const val KEY_CACHED_CHANNELS = "cached_channels"
        const val DEFAULT_SOURCE_URL =
            "https://d1g8wyuo4sonlw.cloudfront.net/channels.json"
    }

    var channelSourceUrl: String
        get() = prefs.getString(KEY_CHANNEL_SOURCE_URL, DEFAULT_SOURCE_URL) ?: DEFAULT_SOURCE_URL
        set(value) = prefs.edit().putString(KEY_CHANNEL_SOURCE_URL, value).apply()

    var cachedChannelsJson: String?
        get() = prefs.getString(KEY_CACHED_CHANNELS, null)
        set(value) = prefs.edit().putString(KEY_CACHED_CHANNELS, value).apply()

    fun getFavorites(): Set<String> =
        prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()

    fun toggleFavorite(channelId: String) {
        val current = getFavorites().toMutableSet()
        if (current.contains(channelId)) {
            current.remove(channelId)
        } else {
            current.add(channelId)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    fun isFavorite(channelId: String): Boolean = getFavorites().contains(channelId)
}
