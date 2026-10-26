package com.dn0ne.player.app.presentation.components.playback

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dn0ne.player.R
import com.dn0ne.player.app.domain.track.Track
import com.dn0ne.player.app.presentation.components.CoverArt
import com.dn0ne.player.app.presentation.components.PlayingAudioAnimation
import com.dn0ne.player.core.data.PlaybackIndicatorStyle
import com.dn0ne.player.core.data.QueueHandlePosition
import com.dn0ne.player.core.data.QueueStyle

@Composable
fun QueueItem(
    track: Track,
    isCurrent: Boolean,
    isPlaying: Boolean,
    showPlaybackIndicator: Boolean,
    playbackIndicatorStyle: PlaybackIndicatorStyle,
    handlePosition: QueueHandlePosition,
    queueStyle: QueueStyle,
    onClick: () -> Unit,
    onRemoveFromQueueClick: () -> Unit,
    dragHandle: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCapsuleActive = queueStyle == QueueStyle.CAPSULE_ACTIVE && isCurrent && isPlaying
    val isCurrentPlaying = isCurrent && isPlaying
    Row(
        modifier = modifier
            .clip(if (isCapsuleActive) CircleShape else ShapeDefaults.Medium)
            .clickable {
                onClick()
            }
            .background(
                color = when {
                    isCapsuleActive -> MaterialTheme.colorScheme.surfaceContainerHigh
                    isCurrent -> MaterialTheme.colorScheme.surfaceContainerLow
                    else -> Color.Transparent
                }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val context = LocalContext.current
        if (!isCurrentPlaying && handlePosition == QueueHandlePosition.LEFT) dragHandle()
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {

            CoverArt(
                uri = track.coverArtUri,
                modifier = Modifier
                    .size(60.dp)
                    .clip(if (isCapsuleActive) CircleShape else ShapeDefaults.Small)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = track.title ?: context.resources.getString(R.string.unknown_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    text = track.artist ?: context.resources.getString(R.string.unknown_artist),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.basicMarquee()
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCurrent && isPlaying && showPlaybackIndicator) {
                PlayingAudioAnimation(
                    style = playbackIndicatorStyle,
                    modifier = Modifier.size(width = 24.dp, height = 20.dp)
                )
            }

            IconButton(
                onClick = onRemoveFromQueueClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = context.resources.getString(R.string.remove_from_queue) +
                            " ${track.title}"
                )
            }

            if (!isCurrentPlaying && handlePosition == QueueHandlePosition.RIGHT) dragHandle()
        }
    }
}
