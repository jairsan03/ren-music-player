package com.dn0ne.player.core.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.dn0ne.player.app.domain.sort.PlaylistSort
import com.dn0ne.player.app.domain.sort.SortOrder
import com.dn0ne.player.app.domain.sort.TrackSort
import com.dn0ne.player.app.presentation.components.settings.Theme
import com.dn0ne.player.app.presentation.components.topbar.Tab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class PlaybackIndicatorStyle {
    CenterBars,
    PixelBlocks,
    FlowingWave,
    ClassicBars,
    SpectrumBars,
    HarmonicPulse
}

enum class MiniPlayerStyle {
    CLASSIC_ROUNDED,
    CLASSIC_FLAT,
    FLOATING_RECT,
    FLOATING_CAPSULE
}

enum class ProgressStyle {
    LEFT_TO_RIGHT,
    CENTER_OUTWARD,
    GRADIENT_SWEEP,
    TOP_LINE
}

enum class QueueHandlePosition {
    RIGHT,
    LEFT
}

enum class QueueStyle {
    STANDARD,
    CAPSULE_ACTIVE
}

enum class SeekBarStyle {
    DEFAULT_WAVE,
    TRADITIONAL_LINE,
    SUBTLE_WAVE,
    MULTI_WAVE_SAMSUNG,
    DOTTED_GRANULAR
}

enum class PlayerCarouselPreview {
    NONE,
    SINGLE_SIDE,
    BOTH_SIDES
}

class Settings(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val miniPlayerDataStore = context.miniPlayerDataStore
    private val miniPlayerSettingsScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val circularMiniPlayerCoverKey = booleanPreferencesKey("circular-mini-player-cover")
    private val rotateMiniPlayerCoverKey = booleanPreferencesKey("rotate-mini-player-cover")
    private val compactMiniPlayerKey = booleanPreferencesKey("compact-mini-player")
    private val miniPlayerStyleKey = stringPreferencesKey("mini-player-style")
    private val progressStyleKey = stringPreferencesKey("mini-player-progress-style")
    private val showQueueDurationKey = booleanPreferencesKey("show-queue-duration")
    private val showQueueTrackCountKey = booleanPreferencesKey("show-queue-track-count")
    private val showQueuePlaybackIndicatorKey = booleanPreferencesKey("show-queue-playback-indicator")
    private val showClearQueueButtonKey = booleanPreferencesKey("show-clear-queue-button")
    private val queueHandlePositionKey = stringPreferencesKey("queue-handle-position")
    private val queueStyleKey = stringPreferencesKey("queue-style")
    private val seekBarStyleKey = stringPreferencesKey("seek-bar-style")
    private val playerCarouselPreviewKey = stringPreferencesKey("player-carousel-preview")
    private val carouselPreviewTintKey = booleanPreferencesKey("carousel-preview-tint")
    private val carouselPreviewBlurKey = booleanPreferencesKey("carousel-preview-blur")

    private val _circularMiniPlayerCover = MutableStateFlow(false)
    val circularMiniPlayerCover = _circularMiniPlayerCover.asStateFlow()

    private val _rotateMiniPlayerCover = MutableStateFlow(false)
    val rotateMiniPlayerCover = _rotateMiniPlayerCover.asStateFlow()

    private val _compactMiniPlayer = MutableStateFlow(false)
    val compactMiniPlayer = _compactMiniPlayer.asStateFlow()

    private val _miniPlayerStyle = MutableStateFlow(MiniPlayerStyle.CLASSIC_ROUNDED)
    val miniPlayerStyle = _miniPlayerStyle.asStateFlow()

    private val _progressStyle = MutableStateFlow(ProgressStyle.LEFT_TO_RIGHT)
    val progressStyle = _progressStyle.asStateFlow()

    private val _showQueueDuration = MutableStateFlow(true)
    val showQueueDuration = _showQueueDuration.asStateFlow()

    private val _showQueueTrackCount = MutableStateFlow(true)
    val showQueueTrackCount = _showQueueTrackCount.asStateFlow()

    private val _queueHandlePosition = MutableStateFlow(QueueHandlePosition.RIGHT)
    val queueHandlePosition = _queueHandlePosition.asStateFlow()

    private val _queueStyle = MutableStateFlow(QueueStyle.STANDARD)
    val queueStyle = _queueStyle.asStateFlow()

    private val _seekBarStyle = MutableStateFlow(SeekBarStyle.DEFAULT_WAVE)
    val seekBarStyle = _seekBarStyle.asStateFlow()

    private val _playerCarouselPreview = MutableStateFlow(PlayerCarouselPreview.NONE)
    val playerCarouselPreview = _playerCarouselPreview.asStateFlow()

    private val _carouselPreviewTint = MutableStateFlow(true)
    val carouselPreviewTint = _carouselPreviewTint.asStateFlow()

    private val _carouselPreviewBlur = MutableStateFlow(false)
    val carouselPreviewBlur = _carouselPreviewBlur.asStateFlow()

    private val _showQueuePlaybackIndicator = MutableStateFlow(true)
    val showQueuePlaybackIndicator = _showQueuePlaybackIndicator.asStateFlow()

    private val _showClearQueueButton = MutableStateFlow(true)
    val showClearQueueButton = _showClearQueueButton.asStateFlow()

    init {
        miniPlayerSettingsScope.launch {
            miniPlayerDataStore.data
                .catch { emit(emptyPreferences()) }
                .collect { preferences ->
                    _circularMiniPlayerCover.value = preferences[circularMiniPlayerCoverKey] ?: false
                    _rotateMiniPlayerCover.value = preferences[rotateMiniPlayerCoverKey] ?: false
                    _compactMiniPlayer.value = preferences[compactMiniPlayerKey] ?: false
                    _miniPlayerStyle.value = preferences[miniPlayerStyleKey]
                        ?.let { storedStyle -> MiniPlayerStyle.entries.firstOrNull { it.name == storedStyle } }
                        ?: MiniPlayerStyle.CLASSIC_ROUNDED
                    _progressStyle.value = preferences[progressStyleKey]
                        ?.let { storedStyle -> ProgressStyle.entries.firstOrNull { it.name == storedStyle } }
                        ?: ProgressStyle.LEFT_TO_RIGHT
                    _showQueueDuration.value = preferences[showQueueDurationKey] ?: true
                    _showQueueTrackCount.value = preferences[showQueueTrackCountKey] ?: true
                    _showQueuePlaybackIndicator.value = preferences[showQueuePlaybackIndicatorKey] ?: true
                    _showClearQueueButton.value = preferences[showClearQueueButtonKey] ?: true
                    _queueHandlePosition.value = preferences[queueHandlePositionKey]
                        ?.let { stored -> QueueHandlePosition.entries.firstOrNull { it.name == stored } }
                        ?: QueueHandlePosition.RIGHT
                    _queueStyle.value = preferences[queueStyleKey]
                        ?.let { stored -> QueueStyle.entries.firstOrNull { it.name == stored } }
                        ?: QueueStyle.STANDARD
                    _seekBarStyle.value = preferences[seekBarStyleKey]
                        ?.let { stored -> SeekBarStyle.entries.firstOrNull { it.name == stored } }
                        ?: SeekBarStyle.DEFAULT_WAVE
                    _playerCarouselPreview.value = preferences[playerCarouselPreviewKey]
                        ?.let { stored -> PlayerCarouselPreview.entries.firstOrNull { it.name == stored } }
                        ?: PlayerCarouselPreview.NONE
                    _carouselPreviewTint.value = preferences[carouselPreviewTintKey] ?: true
                    _carouselPreviewBlur.value = preferences[carouselPreviewBlurKey] ?: false
                }
        }
    }

    fun updateCircularMiniPlayerCover(value: Boolean) {
        _circularMiniPlayerCover.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[circularMiniPlayerCoverKey] = value }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateRotateMiniPlayerCover(value: Boolean) {
        _rotateMiniPlayerCover.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[rotateMiniPlayerCoverKey] = value }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateCompactMiniPlayer(value: Boolean) {
        _compactMiniPlayer.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[compactMiniPlayerKey] = value }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateMiniPlayerStyle(value: MiniPlayerStyle) {
        _miniPlayerStyle.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[miniPlayerStyleKey] = value.name }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateProgressStyle(value: ProgressStyle) {
        _progressStyle.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[progressStyleKey] = value.name }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateShowQueueDuration(value: Boolean) {
        _showQueueDuration.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[showQueueDurationKey] = value }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateShowQueueTrackCount(value: Boolean) {
        _showQueueTrackCount.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[showQueueTrackCountKey] = value }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateQueueHandlePosition(value: QueueHandlePosition) {
        _queueHandlePosition.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[queueHandlePositionKey] = value.name }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateQueueStyle(value: QueueStyle) {
        _queueStyle.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[queueStyleKey] = value.name }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateSeekBarStyle(value: SeekBarStyle) {
        _seekBarStyle.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[seekBarStyleKey] = value.name }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updatePlayerCarouselPreview(value: PlayerCarouselPreview) {
        _playerCarouselPreview.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[playerCarouselPreviewKey] = value.name }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateCarouselPreviewTint(value: Boolean) {
        _carouselPreviewTint.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[carouselPreviewTintKey] = value }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateCarouselPreviewBlur(value: Boolean) {
        _carouselPreviewBlur.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[carouselPreviewBlurKey] = value }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateShowQueuePlaybackIndicator(value: Boolean) {
        _showQueuePlaybackIndicator.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[showQueuePlaybackIndicatorKey] = value }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    fun updateShowClearQueueButton(value: Boolean) {
        _showClearQueueButton.value = value
        miniPlayerSettingsScope.launch {
            try {
                miniPlayerDataStore.edit { it[showClearQueueButtonKey] = value }
            } catch (_: Exception) {
                // Keep the in-memory preference available for the current session.
            }
        }
    }

    private val handleAudioFocusKey = "audio-focus"

    private val replaceSearchWithFilterKey = "replace-search-with-filter"

    private val appearanceKey = "appearance"
    private val useDynamicColorKey = "use-dynamic-color"
    private val useAlbumArtColorKey = "use-album-art-color"
    private val paletteStyleKey = "palette-style"
    private val paletteStyleOrderMigratedKey = "palette-style-ren-order-migrated"
    private val amoledDarkThemeKey = "amoled-dark-theme"
    private val showMiniPlayerPreviousButtonKey = "show-mini-player-previous-button"
    private val showAnimatedPlaybackIndicatorKey = "show-animated-playback-indicator"
    private val showMiniPlayerProgressKey = "show-mini-player-progress"
    private val playbackIndicatorStyleKey = "playback-indicator-style"

    private val lyricsFontSizeKey = "lyrics-font-size"
    private val lyricsFontWeightKey = "lyrics-font-weight"
    private val lyricsLineHeightKey = "lyrics-line-height"
    private val lyricsLetterSpacingKey = "lyrics-letter-spacing"
    private val lyricsAlignmentKey = "lyrics-alignment"
    private val useDarkPaletteOnLyricsSheetKey = "dark-palette-on-lyrics-sheet"

    private val areRisksOfMetadataEditingAcceptedKey = "metadata-editing-dialog"

    private val matchDurationWhenSearchMetadataKey = "match-duration"

    private val trackSortKey = "track-sort-key"
    private val trackSortOrderKey = "track-sort-order-key"
    private val playlistSortKey = "playlist-sort-key"
    private val playlistSortOrderKey = "playlist-sort-order-key"

    private val isScanModeInclusiveKey = "is-scan-mode-inclusive"
    private val ignoreShortTracksKey = "exclude-short-tracks"
    private val scanMusicFolderKey = "scan-music-in-music"
    private val extraScanFoldersKey = "extra-scan-folders"
    private val excludedScanFoldersKey = "excluded-scan-folders"
    private val scanOnAppLaunchKey = "scan-on-app-launch"

    private val tabOrderKey = "tab-order"
    private val defaultTabKey = "default-tab"

    private val jumpToBeginningKey = "jump-to-beginning"

    private val gridPlaylistsKey = "grid-playlists"

    var handleAudioFocus: Boolean
        get() = sharedPreferences.getBoolean(handleAudioFocusKey, true)
        set(value) {
            with(sharedPreferences.edit()) {
                putBoolean(handleAudioFocusKey, value)
                apply()
            }
        }

    private val _replaceSearchWithFilter = MutableStateFlow(
        sharedPreferences.getBoolean(replaceSearchWithFilterKey, false)
    )
    val replaceSearchWithFilter = _replaceSearchWithFilter.asStateFlow()
    fun updateReplaceSearchWithFilter(value: Boolean) {
        _replaceSearchWithFilter.update { value  }
        with(sharedPreferences.edit()) {
            putBoolean(replaceSearchWithFilterKey, value)
            apply()
        }
    }

    private val _appearance = MutableStateFlow(
        Theme.Appearance.entries[sharedPreferences.getInt(appearanceKey, 0)]
    )
    val appearance = _appearance.asStateFlow()
    fun updateAppearance(appearance: Theme.Appearance) {
        _appearance.update {
            appearance
        }
        with(sharedPreferences.edit()) {
            putInt(appearanceKey, appearance.ordinal)
            apply()
        }
    }

    private val _useDynamicColor = MutableStateFlow(
        sharedPreferences.getBoolean(useDynamicColorKey, true)
    )
    val useDynamicColor = _useDynamicColor.asStateFlow()
    fun updateUseDynamicColor(value: Boolean) {
        _useDynamicColor.update { value }
        with(sharedPreferences.edit()) {
            putBoolean(useDynamicColorKey, value)
            apply()
        }
    }

    private val _useAlbumArtColor = MutableStateFlow(
        sharedPreferences.getBoolean(useAlbumArtColorKey, true)
    )
    val useAlbumArtColor = _useAlbumArtColor.asStateFlow()
    fun updateUseAlbumArtColor(value: Boolean) {
        _useAlbumArtColor.update { value }
        with(sharedPreferences.edit()) {
            putBoolean(useAlbumArtColorKey, value)
            apply()
        }
    }

    private val _paletteStyle = MutableStateFlow(
        run {
            val savedValue = sharedPreferences.getInt(paletteStyleKey, -1)
            val alreadyMigrated = sharedPreferences.getBoolean(paletteStyleOrderMigratedKey, false)
            val style = if (alreadyMigrated) {
                Theme.PaletteStyle.entries.getOrNull(savedValue) ?: Theme.PaletteStyle.Ren
            } else {
                val legacyOrderStyle = when (savedValue) {
                    0 -> Theme.PaletteStyle.TonalSpot
                    1 -> Theme.PaletteStyle.Neutral
                    2 -> Theme.PaletteStyle.Vibrant
                    3 -> Theme.PaletteStyle.Expressive
                    4 -> Theme.PaletteStyle.Rainbow
                    5 -> Theme.PaletteStyle.FruitSalad
                    6 -> Theme.PaletteStyle.Monochrome
                    7 -> Theme.PaletteStyle.Fidelity
                    8 -> Theme.PaletteStyle.Content
                    9 -> Theme.PaletteStyle.Ren
                    else -> Theme.PaletteStyle.Ren
                }
                with(sharedPreferences.edit()) {
                    putInt(paletteStyleKey, legacyOrderStyle.ordinal)
                    putBoolean(paletteStyleOrderMigratedKey, true)
                    apply()
                }
                legacyOrderStyle
            }
            style
        }
    )
    val paletteStyle = _paletteStyle.asStateFlow()
    fun updatePaletteStyle(paletteStyle: Theme.PaletteStyle) {
        _paletteStyle.update {
            paletteStyle
        }
        with(sharedPreferences.edit()) {
            putInt(paletteStyleKey, paletteStyle.ordinal)
            apply()
        }
    }

    private val _amoledDarkTheme = MutableStateFlow(
        sharedPreferences.getBoolean(amoledDarkThemeKey, false)
    )
    val amoledDarkTheme = _amoledDarkTheme.asStateFlow()
    fun updateAmoledDarkTheme(value: Boolean) {
        _amoledDarkTheme.update {
            value
        }
        with(sharedPreferences.edit()) {
            putBoolean(amoledDarkThemeKey, value)
            apply()
        }
    }

    private val _showMiniPlayerPreviousButton = MutableStateFlow(
        sharedPreferences.getBoolean(showMiniPlayerPreviousButtonKey, true)
    )
    val showMiniPlayerPreviousButton = _showMiniPlayerPreviousButton.asStateFlow()
    fun updateShowMiniPlayerPreviousButton(value: Boolean) {
        _showMiniPlayerPreviousButton.update { value }
        with(sharedPreferences.edit()) {
            putBoolean(showMiniPlayerPreviousButtonKey, value)
            apply()
        }
    }

    private val _showAnimatedPlaybackIndicator = MutableStateFlow(
        sharedPreferences.getBoolean(showAnimatedPlaybackIndicatorKey, true)
    )
    val showAnimatedPlaybackIndicator = _showAnimatedPlaybackIndicator.asStateFlow()
    fun updateShowAnimatedPlaybackIndicator(value: Boolean) {
        _showAnimatedPlaybackIndicator.update { value }
        with(sharedPreferences.edit()) {
            putBoolean(showAnimatedPlaybackIndicatorKey, value)
            apply()
        }
    }

    private val _showMiniPlayerProgress = MutableStateFlow(
        sharedPreferences.getBoolean(showMiniPlayerProgressKey, true)
    )
    val showMiniPlayerProgress = _showMiniPlayerProgress.asStateFlow()
    fun updateShowMiniPlayerProgress(value: Boolean) {
        _showMiniPlayerProgress.update { value }
        with(sharedPreferences.edit()) {
            putBoolean(showMiniPlayerProgressKey, value)
            apply()
        }
    }

    private val _playbackIndicatorStyle = MutableStateFlow(
        PlaybackIndicatorStyle.entries.getOrElse(
            sharedPreferences.getInt(playbackIndicatorStyleKey, PlaybackIndicatorStyle.ClassicBars.ordinal)
        ) { PlaybackIndicatorStyle.ClassicBars }
    )
    val playbackIndicatorStyle = _playbackIndicatorStyle.asStateFlow()
    fun updatePlaybackIndicatorStyle(value: PlaybackIndicatorStyle) {
        _playbackIndicatorStyle.update { value }
        with(sharedPreferences.edit()) {
            putInt(playbackIndicatorStyleKey, value.ordinal)
            apply()
        }
    }

    var lyricsFontSize: TextUnit
        get() = sharedPreferences.getFloat(lyricsFontSizeKey, 28f).sp
        set(value) {
            with(sharedPreferences.edit()) {
                putFloat(lyricsFontSizeKey, value.value)
                apply()
            }
        }

    var lyricsFontWeight: Int
        get() = sharedPreferences.getInt(lyricsFontWeightKey, 600)
        set(value) {
            with(sharedPreferences.edit()) {
                putInt(lyricsFontWeightKey, value)
                apply()
            }
        }

    var lyricsLineHeight: TextUnit
        get() = sharedPreferences.getFloat(lyricsLineHeightKey, 32f).sp
        set(value) {
            with(sharedPreferences.edit()) {
                putFloat(lyricsLineHeightKey, value.value)
                apply()
            }
        }

    var lyricsLetterSpacing: TextUnit
        get() = sharedPreferences.getFloat(lyricsLetterSpacingKey, 0f).sp
        set(value) {
            with(sharedPreferences.edit()) {
                putFloat(lyricsLetterSpacingKey, value.value)
                apply()
            }
        }

    var lyricsAlignment: TextAlign
        get() = when (sharedPreferences.getInt(lyricsAlignmentKey, 0)) {
            1 -> TextAlign.Center
            2 -> TextAlign.End
            else -> TextAlign.Start
        }
        set(value) {
            with(sharedPreferences.edit()) {
                putInt(
                    lyricsAlignmentKey,
                    when (value) {
                        TextAlign.Center -> 1
                        TextAlign.End -> 2
                        else -> 0
                    }
                )
                apply()
            }
        }

    fun resetLyricsStyle() {
        with(sharedPreferences.edit()) {
            remove(lyricsFontSizeKey)
            remove(lyricsFontWeightKey)
            remove(lyricsLineHeightKey)
            remove(lyricsLetterSpacingKey)
            remove(lyricsAlignmentKey)
            apply()
        }
    }

    var useDarkPaletteOnLyricsSheet: Boolean
        get() = sharedPreferences.getBoolean(useDarkPaletteOnLyricsSheetKey, true)
        set(value) {
            with(sharedPreferences.edit()) {
                putBoolean(useDarkPaletteOnLyricsSheetKey, value)
                apply()
            }
        }

    var areRisksOfMetadataEditingAccepted: Boolean
        get() = sharedPreferences.getBoolean(areRisksOfMetadataEditingAcceptedKey, false)
        set(value) {
            with(sharedPreferences.edit()) {
                putBoolean(areRisksOfMetadataEditingAcceptedKey, value)
                apply()
            }
        }

    var matchDurationWhenSearchMetadata: Boolean
        get() = sharedPreferences.getBoolean(matchDurationWhenSearchMetadataKey, true)
        set(value) {
            with(sharedPreferences.edit()) {
                putBoolean(matchDurationWhenSearchMetadataKey, value)
                apply()
            }
        }

    var trackSort: TrackSort
        get() = TrackSort.entries[sharedPreferences.getInt(trackSortKey, 0)]
        set(value) {
            with(sharedPreferences.edit()) {
                putInt(trackSortKey, value.ordinal)
                apply()
            }
        }

    var trackSortOrder: SortOrder
        get() = SortOrder.entries[sharedPreferences.getInt(trackSortOrderKey, 0)]
        set(value) {
            with(sharedPreferences.edit()) {
                putInt(trackSortOrderKey, value.ordinal)
                apply()
            }
        }

    var playlistSort: PlaylistSort
        get() = PlaylistSort.entries[sharedPreferences.getInt(playlistSortKey, 0)]
        set(value) {
            with(sharedPreferences.edit()) {
                putInt(playlistSortKey, value.ordinal)
                apply()
            }
        }

    var playlistSortOrder: SortOrder
        get() = SortOrder.entries[sharedPreferences.getInt(playlistSortOrderKey, 0)]
        set(value) {
            with(sharedPreferences.edit()) {
                putInt(playlistSortOrderKey, value.ordinal)
                apply()
            }
        }

    private val _isScanModeInclusive = MutableStateFlow(
        sharedPreferences.getBoolean(isScanModeInclusiveKey, true)
    )
    val isScanModeInclusive = _isScanModeInclusive.asStateFlow()
    fun updateIsScanModeInclusive(value: Boolean) {
        _isScanModeInclusive.update { value }
        with(sharedPreferences.edit()) {
            putBoolean(isScanModeInclusiveKey, value)
            apply()
        }
    }

    var ignoreShortTracks: Boolean
        get() = sharedPreferences.getBoolean(ignoreShortTracksKey, true)
        set(value) {
            with(sharedPreferences.edit()) {
                putBoolean(ignoreShortTracksKey, value)
                apply()
            }
        }

    private val _scanMusicFolder = MutableStateFlow(
        sharedPreferences.getBoolean(scanMusicFolderKey, true)
    )
    val scanMusicFolder = _scanMusicFolder.asStateFlow()
    fun updateScanMusicFolder(value: Boolean) {
        _scanMusicFolder.update { value }
        with(sharedPreferences.edit()) {
            putBoolean(scanMusicFolderKey, value)
            apply()
        }
    }

    private val _extraScanFolders = MutableStateFlow(
        sharedPreferences.getStringSet(extraScanFoldersKey, setOf<String>()) ?: setOf<String>()
    )
    val extraScanFolders = _extraScanFolders.asStateFlow()
    fun updateExtraScanFolders(value: Set<String>) {
        _extraScanFolders.update { value }
        with(sharedPreferences.edit()) {
            putStringSet(extraScanFoldersKey, value)
            apply()
        }
    }

    private val _excludedScanFolders = MutableStateFlow(
        sharedPreferences.getStringSet(excludedScanFoldersKey, setOf<String>()) ?: setOf<String>()
    )
    val excludedScanFolders = _excludedScanFolders.asStateFlow()
    fun updateExcludedScanFolders(value: Set<String>) {
        _excludedScanFolders.update { value }
        with(sharedPreferences.edit()) {
            putStringSet(excludedScanFoldersKey, value)
            apply()
        }
    }

    private val _scanOnAppLaunch = MutableStateFlow(
        sharedPreferences.getBoolean(scanOnAppLaunchKey, true)
    )
    val scanOnAppLaunch = _scanOnAppLaunch.asStateFlow()
    fun updateScanOnAppLaunch(value: Boolean) {
        _scanOnAppLaunch.update { value }
        with(sharedPreferences.edit()) {
            putBoolean(scanOnAppLaunchKey, value)
            apply()
        }
    }

    private val _tabs = MutableStateFlow(
        sharedPreferences.getString(tabOrderKey, null)?.let {
            it.split(";").map {
                Tab.valueOf(it)
            }
        } ?: Tab.entries.toList()
    )
    val tabs = _tabs.asStateFlow()
    fun updateTabOrder(tabs: List<Tab>) {
        _tabs.update { tabs }
        with(sharedPreferences.edit()) {
            putString(tabOrderKey, tabs.map { it.name }.joinToString(";"))
            apply()
        }
    }

    var defaultTab: Tab
        get() = Tab.entries[sharedPreferences.getInt(defaultTabKey, 1)]
        set(value) {
            with(sharedPreferences.edit()) {
                putInt(defaultTabKey, value.ordinal)
                apply()
            }
        }

    var jumpToBeginning: Boolean
        get() = sharedPreferences.getBoolean(jumpToBeginningKey, true)
        set(value) {
            with(sharedPreferences.edit()) {
                putBoolean(jumpToBeginningKey, value)
                apply()
            }
        }

    private val _gridPlaylist = MutableStateFlow(
        sharedPreferences.getBoolean(gridPlaylistsKey, true)
    )
    val gridPlaylists = _gridPlaylist.asStateFlow()
    fun updateGridPlaylists(value: Boolean) {
        _gridPlaylist.update { value }
        with(sharedPreferences.edit()) {
            putBoolean(gridPlaylistsKey, value)
            apply()
        }
    }
}
