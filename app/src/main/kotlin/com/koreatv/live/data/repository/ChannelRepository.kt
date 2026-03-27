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
                id = "kbs1",
                name = "KBS1",
                category = "地面波",
                logo = "https://d1g8wyuo4sonlw.cloudfront.net/logos/kbs1.png",
                streams = listOf(
                    Stream("http://mytv.dothome.co.kr/ch/public/1.php", "主线路", 1),
                    Stream("http://ye23.vip/z7z8/2021/kbs2020.php?id=1", "备用线路", 2)
                )
            ),
            Channel(
                id = "kbs2",
                name = "KBS2",
                category = "地面波",
                logo = "https://d1g8wyuo4sonlw.cloudfront.net/logos/kbs2.png",
                streams = listOf(
                    Stream("http://ye23.vip/z7z8/2021/kbs2020.php?id=2", "主线路", 1),
                    Stream("http://koreatv.dothome.co.kr/kbs2.php", "备用线路", 2)
                )
            ),
            Channel(
                id = "kbs_world",
                name = "KBS World",
                category = "地面波",
                logo = "https://d1g8wyuo4sonlw.cloudfront.net/logos/kbs_world.png",
                streams = listOf(
                    Stream("http://ye23.vip/z7z8/2021/kbs2020.php?id=3", "主线路", 1),
                    Stream("http://worldlive.kbs.co.kr/worldtvlive_h.m3u8", "备用线路", 2)
                )
            ),
            Channel(
                id = "mbc",
                name = "MBC",
                category = "地面波",
                logo = "https://d1g8wyuo4sonlw.cloudfront.net/logos/mbc.png",
                streams = listOf(
                    Stream("http://211.33.246.4:32954/cj_live/myStream.sdp/playlist.m3u8", "主线路", 1),
                    Stream("http://123.254.93.7:1935/tvlive/livestream2/playlist.m3u8", "备用线路", 2)
                )
            ),
            Channel(
                id = "sbs",
                name = "SBS",
                category = "地面波",
                logo = "https://d1g8wyuo4sonlw.cloudfront.net/logos/sbs.png",
                streams = listOf(
                    Stream("https://allanf181.github.io/adaptive-streams/streams/kr/SBSTV.m3u8", "主线路", 1),
                    Stream("https://streaming-a-802.cdn.nextologies.com/SBS_Live_HD/live/SBS_Live_HD_1500k/chunks.m3u8", "备用线路", 2)
                )
            ),
            Channel(
                id = "jtbc",
                name = "JTBC",
                category = "综编",
                logo = "https://d1g8wyuo4sonlw.cloudfront.net/logos/jtbc.png",
                streams = listOf(
                    Stream("https://allanf181.github.io/adaptive-streams/streams/kr/JTBC.m3u8", "主线路", 1),
                    Stream("http://channelalive.ktcdn.co.kr/chalivepc/_definst_/atv2/playlist.m3u8", "备用线路", 2)
                )
            ),
            Channel(
                id = "tvn",
                name = "tvN",
                category = "综编",
                logo = "https://d1g8wyuo4sonlw.cloudfront.net/logos/tvn.png",
                streams = listOf(
                    Stream("https://allanf181.github.io/adaptive-streams/streams/kr/tvN.m3u8", "主线路", 1),
                    Stream("https://allanf181.github.io/adaptive-streams/streams/kr/tvNDrama.m3u8", "备用线路", 2)
                )
            ),
            Channel(
                id = "tv_chosun",
                name = "TV Chosun",
                category = "综编",
                logo = "https://d1g8wyuo4sonlw.cloudfront.net/logos/tv_chosun.png",
                streams = listOf(
                    Stream("http://onair.cdn.tvchosun.com/origin1/_definst_/tvchosun_s1/playlist.m3u8", "主线路", 1),
                    Stream("https://allanf181.github.io/adaptive-streams/streams/kr/TVChosun.m3u8", "备用线路", 2)
                )
            )
        )
    }
}
