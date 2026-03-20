package com.koreatv.live.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class ChannelRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("koreatv_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val REMOTE_URL_KEY = "remote_channel_url"
        private const val FAVORITES_KEY = "favorite_channels"
        private const val DEFAULT_REMOTE_URL = ""
    }

    fun getChannels(): List<Channel> {
        // Try remote first, fallback to built-in
        val remoteUrl = prefs.getString(REMOTE_URL_KEY, DEFAULT_REMOTE_URL) ?: ""
        if (remoteUrl.isNotBlank()) {
            try {
                val channels = loadFromRemote(remoteUrl)
                if (channels.isNotEmpty()) return channels
            } catch (_: Exception) { }
        }
        return getDefaultChannels()
    }

    private fun loadFromRemote(url: String): List<Channel> {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return emptyList()
        val config = json.decodeFromString<ChannelConfig>(body)
        return config.channels
    }

    fun setRemoteUrl(url: String) {
        prefs.edit().putString(REMOTE_URL_KEY, url).apply()
    }

    fun getRemoteUrl(): String =
        prefs.getString(REMOTE_URL_KEY, DEFAULT_REMOTE_URL) ?: ""

    // Favorites
    fun getFavoriteIds(): Set<String> =
        prefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()

    fun toggleFavorite(channelId: String) {
        val favorites = getFavoriteIds().toMutableSet()
        if (favorites.contains(channelId)) {
            favorites.remove(channelId)
        } else {
            favorites.add(channelId)
        }
        prefs.edit().putStringSet(FAVORITES_KEY, favorites).apply()
    }

    fun isFavorite(channelId: String): Boolean =
        getFavoriteIds().contains(channelId)

    fun getDefaultChannels(): List<Channel> = listOf(
        Channel(
            id = "arirang_tv",
            name = "Arirang TV",
            category = "综合",
            logo = "https://i.imgur.com/Asu5pE9.png",
            streams = listOf(
                StreamSource("http://amdlive-ch01.ctnd.com.edgesuite.net/arirang_1ch/smil:arirang_1ch.smil/playlist.m3u8", "1080p", 1)
            )
        ),
        Channel(
            id = "arirang_radio",
            name = "Arirang Radio",
            category = "综合",
            logo = "https://i.imgur.com/dtfiG9k.png",
            streams = listOf(
                StreamSource("http://amdlive-ch01.ctnd.com.edgesuite.net/arirang_3ch/smil:arirang_3ch.smil/playlist.m3u8", "720p", 1)
            )
        ),
        Channel(
            id = "kbs_world",
            name = "KBS World",
            category = "综合",
            logo = "https://i.imgur.com/5VF8mLq.png",
            streams = listOf(
                StreamSource("http://worldlive.kbs.co.kr/worldtvlive_h.m3u8", "720p", 1)
            )
        ),
        Channel(
            id = "ktv",
            name = "Korea TV (KTV)",
            category = "新闻",
            logo = "https://i.ibb.co/WkrLDJW/Screenshot-20230714-135036-removebg-preview.png",
            streams = listOf(
                StreamSource("https://hlive.ktv.go.kr/live/klive_h.stream/playlist.m3u8", "1080p", 1)
            )
        ),
        Channel(
            id = "ebs1",
            name = "EBS 1",
            category = "教育",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e2/EBS_1TV_Logo.svg/960px-EBS_1TV_Logo.svg.png",
            streams = listOf(
                StreamSource("https://ebsonair.ebs.co.kr/ebs1familypc/familypc1m/playlist.m3u8", "400p", 1)
            )
        ),
        Channel(
            id = "ebs2",
            name = "EBS 2",
            category = "教育",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/db/EBS_2TV_Logo.svg/960px-EBS_2TV_Logo.svg.png",
            streams = listOf(
                StreamSource("https://ebsonair.ebs.co.kr/ebs2familypc/familypc1m/playlist.m3u8", "400p", 1)
            )
        ),
        Channel(
            id = "mbc_chuncheon",
            name = "MBC 春川",
            category = "综合",
            logo = "https://i.imgur.com/lthyx2P.png",
            streams = listOf(
                StreamSource("https://stream.chmbc.co.kr/TV/myStream/playlist.m3u8", "480p", 1)
            )
        ),
        Channel(
            id = "mbc_daejeon",
            name = "MBC 大田",
            category = "综合",
            logo = "https://i.imgur.com/HCbvAiM.png",
            streams = listOf(
                StreamSource("https://ns1.tjmbc.co.kr/live/myStream.sdp/playlist.m3u8", "720p", 1)
            )
        ),
        Channel(
            id = "abn",
            name = "ABN TV",
            category = "综合",
            logo = "https://www.abn.co.kr/resources/images/logo.png",
            streams = listOf(
                StreamSource("https://vod2.abn.co.kr/IPHONE/abn.m3u8", "720p", 1)
            )
        ),
        Channel(
            id = "bbs",
            name = "BBS 佛教放送",
            category = "宗教",
            logo = "https://i.imgur.com/01UmXRe.png",
            streams = listOf(
                StreamSource("http://bbstv.clouducs.com:1935/bbstv-live/livestream/playlist.m3u8", "1080p", 1)
            )
        ),
        Channel(
            id = "gugak",
            name = "国乐放送",
            category = "音乐",
            logo = "https://i.imgur.com/Ey7Htm8.png",
            streams = listOf(
                StreamSource("https://mgugaklive.nowcdn.co.kr/gugakvideo/gugakvideo.stream/playlist.m3u8", "1080p", 1)
            )
        ),
        Channel(
            id = "job_plus",
            name = "Job Plus TV",
            category = "综合",
            logo = "https://i.imgur.com/Y5Zg5SR.png",
            streams = listOf(
                StreamSource("https://live.jobplustv.or.kr/live/wowtvlive1.sdp/playlist.m3u8", "480p", 1)
            )
        )
    )
}
