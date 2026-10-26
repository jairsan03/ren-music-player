package com.dn0ne.player.app.data

import android.content.Context
import com.dn0ne.player.app.domain.lyrics.Lyrics
import com.dn0ne.player.app.domain.lyrics.toSyncedLyrics
import com.dn0ne.player.app.domain.result.DataError
import com.dn0ne.player.app.domain.result.Result
import com.dn0ne.player.app.domain.track.Track
import com.dn0ne.player.app.domain.track.format
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

class LyricsReaderImpl(private val context: Context) : LyricsReader {
    override fun readFromTag(track: Track): Result<Lyrics?, DataError.Local> {
        val tagResult = readAudioTag(track)
        if (!track.format.equals("opus", ignoreCase = true) &&
            !track.format.equals("ogg", ignoreCase = true)
        ) {
            return tagResult
        }

        if (tagResult is Result.Success && tagResult.data != null) return tagResult
        return readSiblingLrc(track) ?: tagResult
    }

    private fun readAudioTag(track: Track): Result<Lyrics?, DataError.Local> {
        var tempFile: File? = null

        return try {
            val input = context.contentResolver.openInputStream(track.uri)
                ?: return Result.Error(DataError.Local.NoReadPermission)
            input.use {
                val temp = File.createTempFile("temp_audio", ".${track.format}", context.cacheDir)
                tempFile = temp
                FileOutputStream(temp).use { output ->
                    it.copyTo(output)
                }
            }

            val audioFile = AudioFileIO.read(
                tempFile ?: return Result.Error(DataError.Local.FailedToRead)
            )
            val tag = audioFile.tagAndConvertOrCreateAndSetDefault
                ?: return Result.Error(DataError.Local.FailedToRead)

            val lyricsText = tag.getFirst(FieldKey.LYRICS)
            val lyrics = lyricsText
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    Lyrics(
                        uri = track.uri.toString(),
                        plain = it.split('\n'),
                        synced = null,
                        areFromRemote = false
                    )
                }

            Result.Success(lyrics)
        } catch (_: Exception) {
            Result.Error(DataError.Local.FailedToRead)
        } finally {
            try {
                tempFile?.delete()
            } catch (_: Exception) {
                // A failed cleanup must not crash lyric loading.
            }
        }
    }

    private fun readSiblingLrc(track: Track): Result<Lyrics?, DataError.Local>? {
        val audioFile = File(track.data)
        val parentDirectory = audioFile.parentFile ?: return null
        val lrcFile = File(parentDirectory, "${audioFile.nameWithoutExtension}.lrc")
        if (!lrcFile.isFile) return null

        return try {
            val text = lrcFile.readText(StandardCharsets.UTF_8).removePrefix("\uFEFF")
            if (text.isBlank()) return Result.Success(null)

            val syncedLyrics = try {
                text.toSyncedLyrics()
            } catch (_: Exception) {
                null
            }
            Result.Success(
                Lyrics(
                    uri = track.uri.toString(),
                    plain = syncedLyrics?.map { it.second } ?: text.lines(),
                    synced = syncedLyrics,
                    areFromRemote = false
                )
            )
        } catch (_: Exception) {
            Result.Error(DataError.Local.FailedToRead)
        }
    }
}
