package com.dn0ne.player.app.domain.lyrics

import kotlinx.serialization.Serializable

@Serializable
data class Lyrics(
    val uri: String,
    val areFromRemote: Boolean = true,
    val plain: List<String>? = null,
    val synced: List<Pair<Int, String>>? = null
)

fun String.toSyncedLyrics(): List<Pair<Int, String>> {
    val regex = """^\[(\d+):(\d+)\.(\d+)](.*)$""".toRegex()
    return split('\n')
        .filter {
            it.matches(regex)
        }
        .ifEmpty { throw IllegalArgumentException("Synced lines not found.") }
        .map {
            val match = regex.matchEntire(it)
                ?: throw IllegalArgumentException("Failed to parse synced lyric line.")
            val minutes = match.groupValues[1].toLongOrNull()
                ?: throw IllegalArgumentException("Failed to parse lyric minutes.")
            val seconds = match.groupValues[2].toLongOrNull()
                ?.takeIf { value -> value in 0..59 }
                ?: throw IllegalArgumentException("Failed to parse lyric seconds.")
            val fractionText = match.groupValues[3]
            val fraction = fractionText.toLongOrNull()
                ?: throw IllegalArgumentException("Failed to parse lyric timestamp fraction.")
            val millis = when (fractionText.length) {
                1 -> fraction * 100
                2 -> fraction * 10
                else -> fractionText.take(3).toLongOrNull()
                    ?: throw IllegalArgumentException("Failed to parse lyric timestamp fraction.")
            }
            if (minutes > (Int.MAX_VALUE.toLong() - 59_999) / 60_000) {
                throw IllegalArgumentException("Lyric timestamp is out of range.")
            }
            val timestamp = (minutes * 60_000 + seconds * 1_000 + millis)
                .takeIf { value -> value <= Int.MAX_VALUE }
                ?.toInt()
                ?: throw IllegalArgumentException("Lyric timestamp is out of range.")

            timestamp to match.groupValues[4].trim()
        }
}
