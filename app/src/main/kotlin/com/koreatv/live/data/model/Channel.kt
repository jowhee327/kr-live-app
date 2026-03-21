package com.koreatv.live.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChannelList(
    val version: Int = 1,
    val updated: String = "",
    val channels: List<Channel> = emptyList()
)

@Serializable
data class Channel(
    val id: String,
    val name: String,
    val category: String = "",
    val logo: String = "",
    val streams: List<Stream> = emptyList()
)

@Serializable
data class Stream(
    val url: String,
    val label: String = "",
    val priority: Int = 1
)
