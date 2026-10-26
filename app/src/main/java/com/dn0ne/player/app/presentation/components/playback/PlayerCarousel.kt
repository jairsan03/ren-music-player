package com.dn0ne.player.app.presentation.components.playback

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.snapshotFlow
import coil3.compose.AsyncImage
import coil3.toBitmap
import com.dn0ne.player.app.domain.track.Track
import com.dn0ne.player.core.data.PlayerCarouselPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerCarousel(
    currentTrack: Track,
    tracks: List<Track>,
    preview: PlayerCarouselPreview,
    applyPreviewTint: Boolean,
    blurPreviews: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onCoverArtLoaded: (ImageBitmap?) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerTracks = remember(tracks, currentTrack) {
        if (tracks.any { it.uri == currentTrack.uri }) tracks else listOf(currentTrack)
    }
    val currentIndex = pagerTracks.indexOfFirst { it.uri == currentTrack.uri }.coerceAtLeast(0)
    val activeIndex by rememberUpdatedState(currentIndex)
    val pagerState = rememberPagerState(initialPage = currentIndex) { pagerTracks.size }
    var synchronizingWithPlayback by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                if (!synchronizingWithPlayback && page != activeIndex) {
                    if (page > activeIndex) onNextClick() else onPreviousClick()
                }
            }
    }
    LaunchedEffect(currentTrack.uri, currentIndex) {
        if (pagerState.currentPage != currentIndex) {
            synchronizingWithPlayback = true
            try {
                pagerState.animateScrollToPage(currentIndex)
            } finally {
                synchronizingWithPlayback = false
            }
        }
    }

    val previewOverlay = MaterialTheme.colorScheme.surface
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(
                when (preview) {
                    PlayerCarouselPreview.NONE -> 1f
                    PlayerCarouselPreview.SINGLE_SIDE -> 1.28f
                    PlayerCarouselPreview.BOTH_SIDES -> 1.5f
                }
            )
    ) {
        val width = maxWidth
        val pageWidth: Dp
        val pagerPadding: PaddingValues
        when (preview) {
            PlayerCarouselPreview.NONE -> {
                pageWidth = width
                pagerPadding = PaddingValues(0.dp)
            }
            PlayerCarouselPreview.SINGLE_SIDE -> {
                pageWidth = width * 0.76f
                pagerPadding = PaddingValues(start = width * 0.06f, end = width * 0.18f)
            }
            PlayerCarouselPreview.BOTH_SIDES -> {
                pageWidth = width * 0.69f
                pagerPadding = PaddingValues(horizontal = width * 0.155f)
            }
        }

        HorizontalPager(
            state = pagerState,
            pageSize = if (preview == PlayerCarouselPreview.NONE) PageSize.Fill else PageSize.Fixed(pageWidth),
            pageSpacing = 12.dp,
            contentPadding = pagerPadding,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val track = pagerTracks[page]
            val pageProgress = pagerState.getOffsetDistanceInPages(page).absoluteValue.coerceIn(0f, 1f)
            val isAdjacent = pageProgress > 0.01f
            val canBlur = blurPreviews && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            val coverShape = RoundedCornerShape(24.dp)
            val blurModifier = if (isAdjacent && canBlur) {
                Modifier
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .blur(16.dp)
            } else {
                Modifier
            }
            val overlayModifier = Modifier.drawWithContent {
                drawContent()
                if (applyPreviewTint && pageProgress > 0f) {
                    drawRect(previewOverlay.copy(alpha = 0.34f * pageProgress))
                }
            }
            val pageScale = 0.85f + 0.15f * (1f - pageProgress)

            AsyncImage(
                model = track.coverArtUri,
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                onSuccess = { result ->
                    if (track.uri == currentTrack.uri) {
                        onCoverArtLoaded(result.result.image.toBitmap().asImageBitmap())
                    }
                },
                onError = { result ->
                    if (track.uri == currentTrack.uri) {
                        onCoverArtLoaded(result.result.image?.toBitmap()?.asImageBitmap())
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { scaleX = pageScale; scaleY = pageScale }
                    .clip(coverShape)
                    .then(blurModifier)
                    .then(overlayModifier)
                    .clip(coverShape)
                    .clickable(enabled = isAdjacent) {
                        if (page < activeIndex) onPreviousClick() else onNextClick()
                    }
            )
        }
    }
}
