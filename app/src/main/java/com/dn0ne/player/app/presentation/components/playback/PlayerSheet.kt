package com.dn0ne.player.app.presentation.components.playback

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.dn0ne.player.R
import com.dn0ne.player.app.domain.playback.PlaybackMode
import com.dn0ne.player.app.domain.track.Playlist
import com.dn0ne.player.app.domain.track.Track
import com.dn0ne.player.app.presentation.components.CoverArt
import com.dn0ne.player.app.presentation.components.TrackMenuButton
import com.dn0ne.player.app.presentation.components.isSystemInLandscapeOrientation
import com.dn0ne.player.app.presentation.components.settings.Theme
import com.dn0ne.player.core.data.MiniPlayerStyle
import com.dn0ne.player.core.data.ProgressStyle
import com.dn0ne.player.core.data.SeekBarStyle
import com.dn0ne.player.core.data.Settings
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerSheet(
    playbackStateFlow: StateFlow<PlaybackState>,
    onPlayerExpandedChange: (Boolean) -> Unit,
    onLyricsSheetExpandedChange: (Boolean) -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSeekToNextClick: () -> Unit,
    onSeekToPreviousClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onReset: () -> Unit,
    onPlaybackModeClick: () -> Unit,
    onCoverArtLoaded: (ImageBitmap?) -> Unit,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onViewTrackInfoClick: () -> Unit,
    onGoToAlbumClick: () -> Unit,
    onGoToArtistClick: () -> Unit,
    onLyricsClick: () -> Unit,
    onRemoveFromQueueClick: (Int) -> Unit,
    onReorderingQueue: (Int, Int) -> Unit,
    onTrackClick: (Track, Playlist) -> Unit,
    settings: Settings,
    modifier: Modifier = Modifier
) {
    val playbackState by playbackStateFlow.collectAsState()
    val miniPlayerStyle by settings.miniPlayerStyle.collectAsState()
    val isExpanded by remember {
        derivedStateOf {
            playbackState.isPlayerExpanded
        }
    }
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val thresholdY = with(density) { 200.dp.toPx() }
    val decay = rememberSplineBasedDecay<Float>()
    val translationY = remember {
        Animatable(0f).apply {
            if (isExpanded) {
                updateBounds(
                    lowerBound = 0f,
                    upperBound = thresholdY
                )
            } else {
                updateBounds(
                    lowerBound = -thresholdY,
                    upperBound = thresholdY
                )
            }
        }
    }
    val draggableState = rememberDraggableState { dragAmount ->
        coroutineScope.launch {
            translationY.snapTo(
                translationY.value + (dragAmount * (1 - (translationY.value / thresholdY).absoluteValue))
            )
        }
    }

    AnimatedContent(
        targetState = isExpanded,
        label = "player-content",
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.translationY = translationY.value

                val cornerRadius =
                    lerp(0.dp, 36.dp, this.translationY.absoluteValue / (thresholdY * 0.5f))
                this.clip = true
                this.shape = when {
                    isExpanded -> RoundedCornerShape(cornerRadius)
                    miniPlayerStyle == MiniPlayerStyle.CLASSIC_ROUNDED -> RoundedCornerShape(
                        topStart = 36.dp,
                        topEnd = 36.dp,
                        bottomStart = cornerRadius,
                        bottomEnd = cornerRadius
                    )
                    else -> RectangleShape
                }
            }
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    val decayY = decay.calculateTargetValue(
                        initialValue = translationY.value,
                        initialVelocity = velocity
                    )

                    coroutineScope.launch {
                        if (!isExpanded) {
                            val shouldStopPlayback = decayY > thresholdY * .5f
                            if (shouldStopPlayback) {
                                onReset()
                                return@launch
                            }
                        }

                        val shouldChangeExpandedState = decayY.absoluteValue > (thresholdY * 0.5f)
                        if (shouldChangeExpandedState) {
                            onPlayerExpandedChange(!isExpanded)
                            translationY.apply {
                                animateTo(0f)
                                if (isExpanded) {
                                    updateBounds(
                                        lowerBound = 0f,
                                        upperBound = thresholdY
                                    )
                                } else {
                                    updateBounds(
                                        lowerBound = -thresholdY,
                                        upperBound = thresholdY
                                    )
                                }
                            }
                        } else {
                            translationY.animateTo(0f)
                        }
                    }
                }
            )
            .then(
                if (isExpanded) {
                    Modifier.background(color = MaterialTheme.colorScheme.surfaceContainer)
                } else {
                    Modifier
                }
            )

    ) { state ->
        when (state) {
            false -> {
                BottomPlayer(
                    playbackStateFlow = playbackStateFlow,
                    onClick = {
                        onPlayerExpandedChange(true)
                        translationY.updateBounds(
                            lowerBound = 0f,
                            upperBound = thresholdY
                        )
                    },
                    onPlayClick = onPlayClick,
                    onPauseClick = onPauseClick,
                    onSeekToPreviousClick = onSeekToPreviousClick,
                    onSeekToNextClick = onSeekToNextClick,
                    onCoverArtLoaded = onCoverArtLoaded,
                    settings = settings,
                    miniPlayerStyle = miniPlayerStyle
                )
            }

            true -> {
                val appearance by settings.appearance.collectAsState()
                val amoledDarkTheme by settings.amoledDarkTheme.collectAsState()
                val isDarkTheme = when (appearance) {
                    Theme.Appearance.System -> isSystemInDarkTheme()
                    Theme.Appearance.Light -> false
                    Theme.Appearance.Dark -> true
                }

                ExpandedPlayer(
                    playbackStateFlow = playbackStateFlow,
                    onSeekTo = onSeekTo,
                    onPauseClick = onPauseClick,
                    onPlayClick = onPlayClick,
                    onSeekToNextClick = onSeekToNextClick,
                    onSeekToPreviousClick = onSeekToPreviousClick,
                    onHideClick = {
                        onPlayerExpandedChange(false)
                        translationY.updateBounds(
                            lowerBound = -thresholdY,
                            upperBound = 0f
                        )
                    },
                    onPlaybackModeClick = onPlaybackModeClick,
                    settings = settings,
                    onClearQueueClick = onReset,
                    onCoverArtLoaded = onCoverArtLoaded,
                    onPlayNextClick = onPlayNextClick,
                    onAddToQueueClick = onAddToQueueClick,
                    onAddToPlaylistClick = onAddToPlaylistClick,
                    onViewTrackInfoClick = onViewTrackInfoClick,
                    onGoToAlbumClick = onGoToAlbumClick,
                    onGoToArtistClick = onGoToArtistClick,
                    onLyricsSheetExpandedChange = onLyricsSheetExpandedChange,
                    onLyricsClick = onLyricsClick,
                    lyricsTextStyle = MaterialTheme.typography.headlineMedium
                        .copy(
                            fontSize = settings.lyricsFontSize,
                            lineHeight = settings.lyricsLineHeight,
                            letterSpacing = settings.lyricsLetterSpacing,
                            textAlign = settings.lyricsAlignment,
                            fontWeight = FontWeight(settings.lyricsFontWeight),
                        ),
                    lyricsContainerColor = when {
                        settings.useDarkPaletteOnLyricsSheet && !isDarkTheme -> {
                            MaterialTheme.colorScheme.primary
                        }

                        else -> {
                            if (!amoledDarkTheme) {
                                MaterialTheme.colorScheme.inversePrimary
                            } else {
                                MaterialTheme.colorScheme.background
                            }
                        }
                    },
                    lyricsContentColor = when {
                        settings.useDarkPaletteOnLyricsSheet && !isDarkTheme -> {
                            Color.White
                        }
                        else -> MaterialTheme.colorScheme.inverseSurface
                    },
                    onRemoveFromQueueClick = onRemoveFromQueueClick,
                    onReorderingQueue = onReorderingQueue,
                    onTrackClick = onTrackClick,
                    modifier = Modifier.clickable(
                        onClick = {},
                        interactionSource = null,
                        indication = null
                    )
                )
            }
        }
    }
}

@Composable
fun BottomPlayer(
    playbackStateFlow: StateFlow<PlaybackState>,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSeekToPreviousClick: () -> Unit,
    onSeekToNextClick: () -> Unit,
    onCoverArtLoaded: (ImageBitmap?) -> Unit,
    settings: Settings,
    miniPlayerStyle: MiniPlayerStyle,
    modifier: Modifier = Modifier
) {
    val floatingStyle = miniPlayerStyle == MiniPlayerStyle.FLOATING_RECT ||
        miniPlayerStyle == MiniPlayerStyle.FLOATING_CAPSULE
    val miniPlayerShape = when (miniPlayerStyle) {
        MiniPlayerStyle.CLASSIC_ROUNDED -> RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
        MiniPlayerStyle.CLASSIC_FLAT -> RectangleShape
        MiniPlayerStyle.FLOATING_RECT -> ShapeDefaults.Large
        MiniPlayerStyle.FLOATING_CAPSULE -> CircleShape
    }
    val compactMiniPlayer by settings.compactMiniPlayer.collectAsState()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (floatingStyle) {
                    Modifier
                        .navigationBarsPadding()
                        .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
                } else {
                    Modifier
                }
            )
            .shadow(
                elevation = if (floatingStyle) 6.dp else 0.dp,
                shape = miniPlayerShape
            )
            .clip(miniPlayerShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(
                if (floatingStyle) {
                    Modifier.height(if (compactMiniPlayer) 64.dp else 104.dp)
                } else {
                    Modifier.height(IntrinsicSize.Min)
                }
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        val playbackState by playbackStateFlow.collectAsState()
        val currentTrack by remember {
            derivedStateOf {
                playbackState.currentTrack!!
            }
        }
        val isPlaying by remember {
            derivedStateOf {
                playbackState.isPlaying
            }
        }
        val position by remember {
            derivedStateOf {
                playbackState.position
            }
        }
        val showPreviousButton by settings.showMiniPlayerPreviousButton.collectAsState()
        val showMiniPlayerProgress by settings.showMiniPlayerProgress.collectAsState()
        val progressStyle by settings.progressStyle.collectAsState()
        val circularCover by settings.circularMiniPlayerCover.collectAsState()
        val rotateCover by settings.rotateMiniPlayerCover.collectAsState()
        val shouldRotateCover = circularCover && rotateCover && isPlaying
        val coverRotation = if (shouldRotateCover) {
            val rotationTransition = rememberInfiniteTransition(label = "mini-player-vinyl-rotation")
            val rotation by rotationTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 18_000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "mini-player-cover-rotation"
            )
            rotation
        } else 0f

        if (showMiniPlayerProgress) {
            val progressFraction = if (currentTrack.duration > 0L) {
                (position.toFloat() / currentTrack.duration.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }
            val animatedProgress by animateFloatAsState(
                targetValue = progressFraction,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                label = "bottom-player-progress"
            )
            val progressColor = MaterialTheme.colorScheme.surfaceContainerHigh
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (progressStyle) {
                    ProgressStyle.LEFT_TO_RIGHT -> Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(progressColor)
                    )
                    ProgressStyle.CENTER_OUTWARD -> Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(progressColor)
                    )
                    ProgressStyle.GRADIENT_SWEEP -> Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(
                                Brush.horizontalGradient(
                                    colorStops = arrayOf(
                                        0f to progressColor,
                                        0.75f to progressColor,
                                        1f to progressColor.copy(alpha = 0f)
                                    )
                                )
                            )
                    )
                    ProgressStyle.TOP_LINE -> Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth(animatedProgress)
                            .height(3.dp)
                            .background(progressColor)
                    )
                }
            }
        }

        Row(
            modifier = if (floatingStyle) {
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = if (compactMiniPlayer) 6.dp else 28.dp)
            } else {
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 28.dp, vertical = if (compactMiniPlayer) 6.dp else 28.dp)
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedContent(
                targetState = currentTrack,
                transitionSpec = {
                    fadeIn() + slideInHorizontally(
                        initialOffsetX = { it / 5 }
                    ) togetherWith fadeOut() + slideOutHorizontally(
                        targetOffsetX = { -it / 5 }
                    )
                },
                label = "bottom-player-track-change-animation",
                modifier = Modifier.weight(1f)
            ) { currentTrack ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CoverArt(
                        uri = currentTrack.coverArtUri,
                        onCoverArtLoaded = onCoverArtLoaded,
                        modifier = Modifier
                            .size(if (compactMiniPlayer) 40.dp else 48.dp)
                            .clip(if (circularCover) CircleShape else ShapeDefaults.Small)
                            .graphicsLayer {
                                rotationZ = coverRotation
                            }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        val context = LocalContext.current
                        Text(
                            text = currentTrack.title
                                ?: context.resources.getString(R.string.unknown_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.basicMarquee()
                        )
                        Text(
                            text = currentTrack.artist
                                ?: context.resources.getString(R.string.unknown_artist),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.basicMarquee()
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showPreviousButton) {
                    IconButton(onClick = onSeekToPreviousClick) {
                        val layoutDirection = LocalLayoutDirection.current
                        val isRtl = layoutDirection == LayoutDirection.Rtl
                        Icon(
                            imageVector = Icons.Rounded.SkipPrevious,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(36.dp)
                                .graphicsLayer {
                                    if (isRtl) rotationY = 180f
                                }
                        )
                    }
                }

                if (isPlaying) {
                    IconButton(
                        onClick = onPauseClick,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Pause,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = onPlayClick,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onSeekToNextClick
                ) {
                    val layoutDirection = LocalLayoutDirection.current
                    val isRtl = layoutDirection == LayoutDirection.Rtl
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(36.dp)
                            .graphicsLayer {
                                if (isRtl) {
                                    rotationY = 180f
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandedPlayer(
    playbackStateFlow: StateFlow<PlaybackState>,
    settings: Settings,
    onClearQueueClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSeekToNextClick: () -> Unit,
    onSeekToPreviousClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onHideClick: () -> Unit,
    onPlaybackModeClick: () -> Unit,
    onCoverArtLoaded: (ImageBitmap?) -> Unit,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onViewTrackInfoClick: () -> Unit,
    onGoToAlbumClick: () -> Unit,
    onGoToArtistClick: () -> Unit,
    onLyricsSheetExpandedChange: (Boolean) -> Unit,
    onLyricsClick: () -> Unit,
    lyricsTextStyle: TextStyle,
    lyricsContainerColor: Color,
    lyricsContentColor: Color,
    onRemoveFromQueueClick: (Int) -> Unit,
    onReorderingQueue: (Int, Int) -> Unit,
    onTrackClick: (Track, Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onHideClick()
    }

    val playbackState by playbackStateFlow.collectAsState()
    val showQueueTrackCount by settings.showQueueTrackCount.collectAsState()
    val showQueueDuration by settings.showQueueDuration.collectAsState()
    val showQueuePlaybackIndicator by settings.showQueuePlaybackIndicator.collectAsState()
    val showClearQueueButton by settings.showClearQueueButton.collectAsState()
    val playbackIndicatorStyle by settings.playbackIndicatorStyle.collectAsState()
    val seekBarStyle by settings.seekBarStyle.collectAsState()
    val playerCarouselPreview by settings.playerCarouselPreview.collectAsState()
    val carouselPreviewTint by settings.carouselPreviewTint.collectAsState()
    val carouselPreviewBlur by settings.carouselPreviewBlur.collectAsState()
    val queueHandlePosition by settings.queueHandlePosition.collectAsState()
    val queueStyle by settings.queueStyle.collectAsState()
    val showLyricsSheet by remember {
        derivedStateOf {
            playbackState.isLyricsSheetExpanded
        }
    }
    var showQueue by remember {
        mutableStateOf(false)
    }
    var showSleepTimerSheet by remember {
        mutableStateOf(false)
    }

    Box {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            val playbackMode by remember {
                derivedStateOf {
                    playbackState.playbackMode
                }
            }

            val context = LocalContext.current
            if (!isSystemInLandscapeOrientation()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onHideClick
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ExpandMore,
                            contentDescription = context.resources.getString(R.string.close_player_sheet),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }


                    Row {
                        IconButton(
                            onClick = {
                                showSleepTimerSheet = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Timer,
                                contentDescription = context.resources.getString(R.string.set_sleep_timer),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AnimatedVisibility(
                            visible = playbackState.playbackMode != PlaybackMode.Shuffle,
                            enter = expandHorizontally(),
                            exit = shrinkHorizontally()
                        ) {
                            IconButton(
                                onClick = {
                                    if (playbackState.playbackMode != PlaybackMode.Shuffle) {
                                        showQueue = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                    contentDescription = context.resources.getString(R.string.show_queue),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                onLyricsSheetExpandedChange(true)
                                onLyricsClick()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lyrics,
                                contentDescription = context.resources.getString(R.string.show_lyrics),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onPlaybackModeClick
                        ) {
                            Icon(
                                imageVector = when (playbackMode) {
                                    PlaybackMode.Repeat -> Icons.Rounded.Repeat
                                    PlaybackMode.RepeatOne -> Icons.Rounded.RepeatOne
                                    PlaybackMode.Shuffle -> Icons.Rounded.Shuffle
                                },
                                contentDescription = context.resources.getString(R.string.playback_mode_toggle),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TrackMenuButton(
                            onPlayNextClick = onPlayNextClick,
                            onAddToQueueClick = onAddToQueueClick,
                            onAddToPlaylistClick = onAddToPlaylistClick,
                            onViewTrackInfoClick = onViewTrackInfoClick,
                            onGoToAlbumClick = onGoToAlbumClick,
                            onGoToArtistClick = onGoToArtistClick
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val currentTrack by remember {
                        derivedStateOf {
                            playbackState.currentTrack!!
                        }
                    }

                    PlayerCarousel(
                        currentTrack = currentTrack,
                        tracks = playbackState.playlist?.trackList.orEmpty(),
                        preview = playerCarouselPreview,
                        applyPreviewTint = carouselPreviewTint,
                        blurPreviews = carouselPreviewBlur,
                        onPreviousClick = onSeekToPreviousClick,
                        onNextClick = onSeekToNextClick,
                        onCoverArtLoaded = onCoverArtLoaded,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    AnimatedContent(
                        targetState = currentTrack,
                        label = "title-artist-text-animation",
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { track ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val context = LocalContext.current
                            Text(
                                text = track.title
                                    ?: context.resources.getString(R.string.unknown_title),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.basicMarquee()
                            )

                            Text(
                                text = track.artist
                                    ?: context.resources.getString(R.string.unknown_artist),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.basicMarquee()
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    PlaybackControl(
                        playbackStateFlow = playbackStateFlow,
                        onPlayClick = onPlayClick,
                        onPauseClick = onPauseClick,
                        onSeekTo = onSeekTo,
                        onSeekToNextClick = onSeekToNextClick,
                        onSeekToPreviousClick = onSeekToPreviousClick,
                        seekBarStyle = seekBarStyle,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    val currentTrack by remember {
                        derivedStateOf {
                            playbackState.currentTrack!!
                        }
                    }
                    PlayerCarousel(
                        currentTrack = currentTrack,
                        tracks = playbackState.playlist?.trackList.orEmpty(),
                        preview = playerCarouselPreview,
                        applyPreviewTint = carouselPreviewTint,
                        blurPreviews = carouselPreviewBlur,
                        onPreviousClick = onSeekToPreviousClick,
                        onNextClick = onSeekToNextClick,
                        onCoverArtLoaded = onCoverArtLoaded,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 28.dp, top = 28.dp, bottom = 28.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .padding(horizontal = 16.dp)
                                .align(Alignment.TopCenter),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onHideClick
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ExpandMore,
                                    contentDescription = context.resources.getString(R.string.close_player_sheet),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }


                            Row {
                                IconButton(
                                    onClick = {
                                        showSleepTimerSheet = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Timer,
                                        contentDescription = context.resources.getString(R.string.set_sleep_timer),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                AnimatedVisibility(
                                    visible = playbackState.playbackMode != PlaybackMode.Shuffle,
                                    enter = expandHorizontally(),
                                    exit = shrinkHorizontally()
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (playbackState.playbackMode != PlaybackMode.Shuffle) {
                                                showQueue = true
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                            contentDescription = context.resources.getString(R.string.show_queue),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        onLyricsSheetExpandedChange(true)
                                        onLyricsClick()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Lyrics,
                                        contentDescription = context.resources.getString(R.string.show_lyrics),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = onPlaybackModeClick
                                ) {
                                    Icon(
                                        imageVector = when (playbackMode) {
                                            PlaybackMode.Repeat -> Icons.Rounded.Repeat
                                            PlaybackMode.RepeatOne -> Icons.Rounded.RepeatOne
                                            PlaybackMode.Shuffle -> Icons.Rounded.Shuffle
                                        },
                                        contentDescription = context.resources.getString(R.string.playback_mode_toggle),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                TrackMenuButton(
                                    onPlayNextClick = onPlayNextClick,
                                    onAddToQueueClick = onAddToQueueClick,
                                    onAddToPlaylistClick = onAddToPlaylistClick,
                                    onViewTrackInfoClick = onViewTrackInfoClick,
                                    onGoToAlbumClick = onGoToAlbumClick,
                                    onGoToArtistClick = onGoToArtistClick
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .offset(y = 28.dp)
                                .padding(horizontal = 28.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AnimatedContent(
                                targetState = currentTrack,
                                label = "title-artist-text-animation",
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { track ->
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val context = LocalContext.current
                                    Text(
                                        text = track.title
                                            ?: context.resources.getString(R.string.unknown_title),
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.basicMarquee()
                                    )

                                    Text(
                                        text = track.artist
                                            ?: context.resources.getString(R.string.unknown_artist),
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.basicMarquee()
                                    )
                                }

                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            PlaybackControl(
                                playbackStateFlow = playbackStateFlow,
                                onPlayClick = onPlayClick,
                                onPauseClick = onPauseClick,
                                onSeekTo = onSeekTo,
                                onSeekToNextClick = onSeekToNextClick,
                                onSeekToPreviousClick = onSeekToPreviousClick,
                                seekBarStyle = seekBarStyle,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showLyricsSheet,
            enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + slideInVertically(
                initialOffsetY = { it / 10 }),
            exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + slideOutVertically(
                targetOffsetY = { it / 10 }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            LyricsSheet(
                playbackStateFlow = playbackStateFlow,
                lyricsTextStyle = lyricsTextStyle,
                onBackClick = {
                    onLyricsSheetExpandedChange(false)
                },
                containerColor = lyricsContainerColor,
                contentColor = lyricsContentColor,
                onSeekTo = onSeekTo,
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = showQueue,
            enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + slideInVertically(
                initialOffsetY = { it / 10 }),
            exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + slideOutVertically(
                targetOffsetY = { it / 10 }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val playlist by remember {
                derivedStateOf {
                    playbackState.playlist
                }
            }
            val currentTrack by remember {
                derivedStateOf {
                    playbackState.currentTrack
                }
            }

            val listState = rememberLazyListState()
            Queue(
                listState = listState,
                playlist = playlist,
                currentTrack = currentTrack,
                isPlaying = playbackState.isPlaying,
                showTrackCount = showQueueTrackCount,
                showTotalDuration = showQueueDuration,
                showPlaybackIndicator = showQueuePlaybackIndicator,
                playbackIndicatorStyle = playbackIndicatorStyle,
                queueHandlePosition = queueHandlePosition,
                queueStyle = queueStyle,
                showClearQueueButton = showClearQueueButton,
                onClearQueueClick = onClearQueueClick,
                onRemoveFromQueueClick = onRemoveFromQueueClick,
                onReorderingQueue = onReorderingQueue,
                onTrackClick = onTrackClick,
                onBackClick = {
                    showQueue = false
                }
            )
        }

        if (showSleepTimerSheet) {
            SleepTimerBottomSheet(
                onDismissRequest = {
                    showSleepTimerSheet = false
                }
            )
        }
    }
}

@Composable
fun PlaybackControl(
    playbackStateFlow: StateFlow<PlaybackState>,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSeekToNextClick: () -> Unit,
    onSeekToPreviousClick: () -> Unit,
    seekBarStyle: SeekBarStyle = SeekBarStyle.DEFAULT_WAVE,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val playbackState by playbackStateFlow.collectAsState()
        val position by remember {
            derivedStateOf {
                playbackState.position
            }
        }
        val currentTrack by remember {
            derivedStateOf {
                playbackState.currentTrack!!
            }
        }
        val isPlaying by remember {
            derivedStateOf {
                playbackState.isPlaying
            }
        }

        WavingSeekBar(
            position = position,
            duration = currentTrack.duration.toLong(),
            onPositionChange = {
                onSeekTo(it)
            },
            isPlaying = isPlaying,
            style = seekBarStyle,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val layoutDirection = LocalLayoutDirection.current
            val isRtl = layoutDirection == LayoutDirection.Rtl
            IconButton(
                onClick = onSeekToPreviousClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(36.dp)
                        .graphicsLayer {
                            if (isRtl) {
                                rotationY = 180f
                            }
                        }
                )
            }

            if (isPlaying) {
                IconButton(
                    onClick = onPauseClick,
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onPlayClick,
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)

                    )
                }
            }

            IconButton(
                onClick = onSeekToNextClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(36.dp)
                        .graphicsLayer {
                            if (isRtl) {
                                rotationY = 180f
                            }
                        }
                )
            }
        }
    }
}
