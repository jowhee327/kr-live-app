package com.koreatv.live.data.repository

import com.koreatv.live.data.local.PreferencesManager
import com.koreatv.live.data.model.Channel
import com.koreatv.live.data.model.ChannelList
import com.koreatv.live.data.model.Stream
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class ChannelRepository(private val prefs: PreferencesManager) {

    private val json = Json { ignoreUnknownKeys = true }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    fun loadChannels(): List<Channel> {
        // Try remote first
        val remote = fetchRemote()
        if (remote != null) {
            prefs.cachedChannelsJson = json.encodeToString(ChannelList.serializer(), remote)
            return remote.channels
        }

        // Try cached
        val cached = loadCached()
        if (cached != null) return cached.channels

        // Fallback to built-in
        return defaultChannels()
    }

    private fun fetchRemote(): ChannelList? {
        return try {
            val request = Request.Builder().url(prefs.channelSourceUrl).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()?.let { body ->
                    json.decodeFromString(ChannelList.serializer(), body)
                }
            } else null
        } catch (_: Exception) {
            null
        }
    }

    private fun loadCached(): ChannelList? {
        return try {
            prefs.cachedChannelsJson?.let {
                json.decodeFromString(ChannelList.serializer(), it)
            }
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        fun defaultChannels(): List<Channel> = listOf(
            Channel(
                id = "arirang_tv",
                name = "Arirang TV",
                category = "综合",
                logo = "https://i.imgur.com/Asu5pE9.png",
                streams = listOf(
                    Stream("http://amdlive-ch01.ctnd.com.edgesuite.net/arirang_1ch/smil:arirang_1ch.smil/playlist.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "arirang_radio",
                name = "Arirang Radio",
                category = "综合",
                logo = "https://i.imgur.com/Asu5pE9.png",
                streams = listOf(
                    Stream("http://amdlive-ch01.ctnd.com.edgesuite.net/arirang_3ch/smil:arirang_3ch.smil/playlist.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "kbs_world",
                name = "KBS World",
                category = "综合",
                logo = "",
                streams = listOf(
                    Stream("http://worldlive.kbs.co.kr/worldtvlive_h.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "ktv",
                name = "Korea TV (KTV)",
                category = "新闻",
                logo = "",
                streams = listOf(
                    Stream("https://hlive.ktv.go.kr/live/klive_h.stream/playlist.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "ebs1",
                name = "EBS 1",
                category = "教育",
                logo = "",
                streams = listOf(
                    Stream("https://ebsonair.ebs.co.kr/ebs1familypc/familypc1m/playlist.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "ebs2",
                name = "EBS 2",
                category = "教育",
                logo = "",
                streams = listOf(
                    Stream("https://ebsonair.ebs.co.kr/ebs2familypc/familypc1m/playlist.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "mbc_chuncheon",
                name = "MBC 春川",
                category = "综合",
                logo = "",
                streams = listOf(
                    Stream("https://stream.chmbc.co.kr/TV/myStream/playlist.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "mbc_daejeon",
                name = "MBC 大田",
                category = "综合",
                logo = "",
                streams = listOf(
                    Stream("https://ns1.tjmbc.co.kr/live/myStream.sdp/playlist.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "abn_tv",
                name = "ABN TV",
                category = "综合",
                logo = "",
                streams = listOf(
                    Stream("https://vod2.abn.co.kr/IPHONE/abn.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "bbs",
                name = "BBS 佛教放送",
                category = "宗教",
                logo = "",
                streams = listOf(
                    Stream("http://bbstv.clouducs.com:1935/bbstv-live/livestream/playlist.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "gugak",
                name = "国乐放送",
                category = "音乐",
                logo = "",
                streams = listOf(
                    Stream("https://mgugaklive.nowcdn.co.kr/gugakvideo/gugakvideo.stream/playlist.m3u8", "默认", 1)
                )
            ),
            Channel(
                id = "jobplus",
                name = "Job Plus TV",
                category = "综合",
                logo = "",
                streams = listOf(
                    Stream("https://live.jobplustv.or.kr/live/wowtvlive1.sdp/playlist.m3u8", "默认", 1)
                )
            )
        )
    }
}
