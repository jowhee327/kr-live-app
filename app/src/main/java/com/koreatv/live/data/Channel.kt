package com.koreatv.live.data

import kotlinx.serialization.Serializable

@Serializable
data class ChannelConfig(
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
    val streams: List<StreamSource> = emptyList()
)

@Serializable
data class StreamSource(
    val url: String,
    val label: String = "",
    val priority: Int = 1
)
