package com.dn0ne.player.app.presentation.components.playback

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import com.dn0ne.player.R
import com.dn0ne.player.app.domain.track.Playlist
import com.dn0ne.player.app.domain.track.Track
import com.dn0ne.player.app.domain.track.filterTracks
import com.dn0ne.player.app.presentation.ScrollToTopAndLocateButtons
import com.dn0ne.player.app.presentation.components.PlayingAudioAnimation
import com.dn0ne.player.app.presentation.components.topbar.LazyColumnWithCollapsibleTopBar
import com.dn0ne.player.app.presentation.components.trackinfo.SearchField
import com.dn0ne.player.core.data.PlaybackIndicatorStyle
import com.dn0ne.player.core.data.QueueHandlePosition
import com.dn0ne.player.core.data.QueueStyle
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun Queue(
    listState: LazyListState ,
    playlist: Playlist?,
    currentTrack: Track?,
    isPlaying: Boolean,
    showTrackCount: Boolean,
    showTotalDuration: Boolean,
    showPlaybackIndicator: Boolean,
    playbackIndicatorStyle: PlaybackIndicatorStyle,
    queueHandlePosition: QueueHandlePosition,
    queueStyle: QueueStyle,
    showClearQueueButton: Boolean,
    onClearQueueClick: () -> Unit,
    onRemoveFromQueueClick: (index: Int) -> Unit,
    onReorderingQueue: (from: Int, to: Int) -> Unit,
    onTrackClick: (Track, Playlist) -> Unit,
    onBackClick: () -> Unit,
) {
    Box {
        val context = LocalContext.current
        val view = LocalView.current

        var collapseFraction by remember {
            mutableFloatStateOf(0f)
        }

        BackHandler {
            onBackClick()
        }

        var searchFieldValue by rememberSaveable {
            mutableStateOf("")
        }
        var showSearchField by rememberSaveable {
            mutableStateOf(false)
        }
        var showClearQueueConfirmation by rememberSaveable {
            mutableStateOf(false)
        }

        var trackList by remember(playlist) {
            mutableStateOf(playlist?.trackList ?: emptyList())
        }

        var lastMovedFrom: Int? by remember {
            mutableStateOf(null)
        }
        var lastMovedTo by remember {
            mutableIntStateOf(0)
        }
        val reorderableListState = rememberReorderableLazyListState(listState) { from, to ->
            trackList = trackList.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }

            if (lastMovedFrom == null) {
                lastMovedFrom = from.index
            }
            lastMovedTo = to.index
        }

        var wasTriggered by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(reorderableListState.isAnyItemDragging) {
            if (wasTriggered) {
                if (!reorderableListState.isAnyItemDragging) {
                    onReorderingQueue(lastMovedFrom ?: 0, lastMovedTo)
                    lastMovedFrom = null
                }
            } else wasTriggered = true
        }

        LazyColumnWithCollapsibleTopBar(
            listState = listState,
            minTopBarHeight = 60.dp,
            topBarContent = {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 92.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = context.resources.getString(R.string.queue),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = lerp(
                                start = MaterialTheme.typography.titleLarge.fontSize,
                                stop = MaterialTheme.typography.headlineLarge.fontSize,
                                fraction = collapseFraction
                            )
                        ),
                        softWrap = collapseFraction > .2f,
                        overflow = if (collapseFraction > .2f) TextOverflow.Clip else TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    if ((showTrackCount || showTotalDuration) && collapseFraction > .2f) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val totalSeconds = trackList.sumOf { it.duration.coerceAtLeast(0).toLong() } / 1000
                        val summaryParts = buildList {
                            if (showTrackCount) add(context.resources.getQuantityString(
                                R.plurals.queue_track_count_summary,
                                trackList.size,
                                trackList.size
                            ))
                            if (showTotalDuration) add(context.getString(
                                R.string.queue_duration_summary,
                                totalSeconds / 3600,
                                (totalSeconds % 3600) / 60,
                                totalSeconds % 60
                            ))
                        }
                        Text(
                            text = summaryParts.joinToString(" • "),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                AnimatedContent(
                    targetState = showSearchField,
                    label = "top-bar-search-bar-animation",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .align(Alignment.BottomCenter)
                ) { state ->
                    when (state) {
                        false -> {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = onBackClick
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBackIosNew,
                                        contentDescription = context.resources.getString(
                                            R.string.back
                                        )
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        showSearchField = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = context.resources.getString(
                                            R.string.track_search
                                        )
                                    )
                                }
                            }
                        }

                        true -> {
                            BackHandler {
                                showSearchField = false
                                searchFieldValue = ""
                            }
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val focusRequester = remember {
                                    FocusRequester()
                                }
                                SearchField(
                                    value = searchFieldValue,
                                    onValueChange = {
                                        searchFieldValue = it.trimStart()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 48.dp)
                                        .align(Alignment.Center)
                                        .focusRequester(focusRequester)
                                )

                                LaunchedEffect(Unit) {
                                    focusRequester.requestFocus()
                                }

                                IconButton(
                                    onClick = {
                                        showSearchField = false
                                        searchFieldValue = ""
                                    },
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = context.resources.getString(
                                            R.string.close_track_search
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            collapseFraction = {
                collapseFraction = it
            },
            contentHorizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .safeDrawingPadding()
        ) {
            itemsIndexed(
                items = trackList.filterTracks(searchFieldValue),
                key = { index, track -> "${track.uri}" }
            ) { _, track ->
                val queueIndex = trackList.indexOfFirst { it.uri == track.uri }
                ReorderableItem(
                    state = reorderableListState,
                    key = "${track.uri}",
                    animateItemModifier = Modifier.animateItem(fadeInSpec = null),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) { isDragging ->
                    val scale by animateFloatAsState(
                        targetValue = if (isDragging) 1.05f else 1f,
                        label = "dragged-track-scale-animation"
                    )
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isDragging) {
                            MaterialTheme.colorScheme.surfaceContainer
                        } else Color.Transparent,
                        label = "dragged-track-back-animation"
                    )
                    QueueItem(
                        track = track,
                        isCurrent = currentTrack == track,
                        isPlaying = isPlaying,
                        showPlaybackIndicator = showPlaybackIndicator,
                        playbackIndicatorStyle = playbackIndicatorStyle,
                        handlePosition = queueHandlePosition,
                        queueStyle = queueStyle,
                        onClick = {
                            playlist?.let {
                                onTrackClick(
                                    track,
                                    playlist
                                )
                            }
                        },
                        onRemoveFromQueueClick = {
                            if (queueIndex >= 0) {
                                trackList = trackList.toMutableList().apply {
                                    removeAt(queueIndex)
                                }
                                onRemoveFromQueueClick(queueIndex)
                            }
                        },
                        dragHandle = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier.draggableHandle(
                                    onDragStarted = {
                                        ViewCompat.performHapticFeedback(
                                            view,
                                            HapticFeedbackConstantsCompat.GESTURE_START
                                        )
                                    },
                                    onDragStopped = {
                                        ViewCompat.performHapticFeedback(
                                            view,
                                            HapticFeedbackConstantsCompat.GESTURE_END
                                        )
                                    }
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DragHandle,
                                    contentDescription = context.resources.getString(R.string.reorder_track) + " ${track.title}",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp).alpha(0.55f)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleY = scale
                                scaleX = scale
                            }
                            .background(
                                color = backgroundColor,
                                shape = if (queueStyle == QueueStyle.CAPSULE_ACTIVE && track == currentTrack && isPlaying) {
                                    CircleShape
                                } else {
                                    ShapeDefaults.Medium
                                }
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        val isScrolledEnough by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex >= 5
            }
        }
        val shouldShowLocateButton by remember(currentTrack, playlist) {
            derivedStateOf {
                val index = playlist?.trackList?.indexOf(currentTrack) ?: -1
                currentTrack != null &&
                        index >= 0 &&
                        listState.layoutInfo.visibleItemsInfo.fastFirstOrNull {
                            it.index == index
                        } == null
            }
        }

        ScrollToTopAndLocateButtons(
            showScrollToTopButton = isScrolledEnough,
            onScrollToTopClick = remember {
                {
                    listState.scrollToItem(5)
                    listState.animateScrollToItem(0)
                }
            },
            showLocateButton = shouldShowLocateButton,
            onLocateClick = remember(currentTrack, playlist) {
                {
                    val currentTrackIndex = playlist?.trackList?.indexOf(currentTrack)
                    currentTrackIndex?.let {
                        val preAnimateItemIndex = if (
                            listState.firstVisibleItemIndex < currentTrackIndex
                        ) {
                            (currentTrackIndex - 5).coerceAtLeast(0)
                        } else currentTrackIndex + 5
                        listState.scrollToItem(preAnimateItemIndex)
                        listState.animateScrollToItem(currentTrackIndex)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = if (showClearQueueButton && trackList.isNotEmpty()) 88.dp else 8.dp)
        )

        if (showClearQueueButton && trackList.isNotEmpty()) {
            ExtendedFloatingActionButton(
                onClick = { showClearQueueConfirmation = true },
                icon = {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                },
                text = { Text(context.getString(R.string.clear_queue)) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 16.dp)
            )
        }

        if (showClearQueueConfirmation) {
            AlertDialog(
                onDismissRequest = { showClearQueueConfirmation = false },
                title = { Text(context.getString(R.string.clear_queue_confirm_title)) },
                text = { Text(context.getString(R.string.clear_queue_confirm_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        showClearQueueConfirmation = false
                        trackList = emptyList()
                        onClearQueueClick()
                    }) {
                        Text(context.getString(R.string.clear_queue))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearQueueConfirmation = false }) {
                        Text(context.getString(R.string.cancel))
                    }
                }
            )
        }
    }
}
