package com.dn0ne.player.app.presentation.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.dn0ne.player.R
import com.dn0ne.player.app.presentation.components.topbar.ColumnWithCollapsibleTopBar
import com.dn0ne.player.core.data.QueueHandlePosition
import com.dn0ne.player.core.data.QueueStyle
import com.dn0ne.player.core.data.Settings

@Composable
fun QueueSettings(
    settings: Settings,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val showDuration by settings.showQueueDuration.collectAsState()
    val showTrackCount by settings.showQueueTrackCount.collectAsState()
    val showPlaybackIndicator by settings.showQueuePlaybackIndicator.collectAsState()
    val showClearQueueButton by settings.showClearQueueButton.collectAsState()
    val handlePosition by settings.queueHandlePosition.collectAsState()
    val queueStyle by settings.queueStyle.collectAsState()
    var collapseFraction by remember { mutableFloatStateOf(0f) }
    var showHandlePositionDialog by remember { mutableStateOf(false) }
    var showQueueStyleDialog by remember { mutableStateOf(false) }

    ColumnWithCollapsibleTopBar(
        topBarContent = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.BottomStart).padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = context.getString(R.string.back))
            }
            Text(
                text = context.getString(R.string.queue_settings),
                fontSize = lerp(MaterialTheme.typography.titleLarge.fontSize, MaterialTheme.typography.displaySmall.fontSize, collapseFraction),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 56.dp)
            )
        },
        collapseFraction = { collapseFraction = it },
        contentPadding = PaddingValues(horizontal = 28.dp),
        contentHorizontalAlignment = Alignment.CenterHorizontally,
        contentVerticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize().safeDrawingPadding()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(context.getString(R.string.queue_header_section), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                SettingSwitch(
                    title = context.getString(R.string.show_queue_track_count),
                    supportingText = context.getString(R.string.show_queue_track_count_explain),
                    icon = Icons.Outlined.QueueMusic,
                    isChecked = showTrackCount,
                    onCheckedChange = settings::updateShowQueueTrackCount,
                    modifier = Modifier.fillMaxWidth()
                )
                SettingSwitch(
                    title = context.getString(R.string.show_queue_duration),
                    supportingText = context.getString(R.string.show_queue_duration_explain),
                    icon = Icons.Outlined.Timer,
                    isChecked = showDuration,
                    onCheckedChange = settings::updateShowQueueDuration,
                    modifier = Modifier.fillMaxWidth()
                )
                SettingSwitch(
                    title = context.getString(R.string.show_queue_playback_indicator),
                    supportingText = context.getString(R.string.show_queue_playback_indicator_explain),
                    icon = Icons.Outlined.GraphicEq,
                    isChecked = showPlaybackIndicator,
                    onCheckedChange = settings::updateShowQueuePlaybackIndicator,
                    modifier = Modifier.fillMaxWidth()
                )
                SettingSwitch(
                    title = context.getString(R.string.show_clear_queue_button),
                    supportingText = context.getString(R.string.show_clear_queue_button_explain),
                    icon = Icons.Outlined.Delete,
                    isChecked = showClearQueueButton,
                    onCheckedChange = settings::updateShowClearQueueButton,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(context.getString(R.string.queue_appearance_section), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = context.getString(R.string.queue_handle_position),
                            supportingText = context.getString(if (handlePosition == QueueHandlePosition.RIGHT) R.string.queue_handle_right else R.string.queue_handle_left),
                            icon = Icons.Rounded.DragHandle,
                            onClick = { showHandlePositionDialog = true }
                        ),
                        SettingsItem(
                            title = context.getString(R.string.queue_style),
                            supportingText = context.getString(if (queueStyle == QueueStyle.STANDARD) R.string.queue_style_standard else R.string.queue_style_capsule_active),
                            icon = Icons.Outlined.QueueMusic,
                            onClick = { showQueueStyleDialog = true }
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showHandlePositionDialog) {
        AlertDialog(
            onDismissRequest = { showHandlePositionDialog = false },
            title = { Text(context.getString(R.string.queue_handle_position)) },
            text = {
                Column {
                    QueueHandlePosition.entries.forEach { position ->
                        val label = if (position == QueueHandlePosition.RIGHT) R.string.queue_handle_right else R.string.queue_handle_left
                        QueueOption(context.getString(label), handlePosition == position) {
                            settings.updateQueueHandlePosition(position)
                            showHandlePositionDialog = false
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showHandlePositionDialog = false }) { Text(context.getString(R.string.done)) } }
        )
    }

    if (showQueueStyleDialog) {
        AlertDialog(
            onDismissRequest = { showQueueStyleDialog = false },
            title = { Text(context.getString(R.string.queue_style)) },
            text = {
                Column {
                    QueueStyle.entries.forEach { style ->
                        val label = if (style == QueueStyle.STANDARD) R.string.queue_style_standard else R.string.queue_style_capsule_active
                        QueueOption(context.getString(label), queueStyle == style) {
                            settings.updateQueueStyle(style)
                            showQueueStyleDialog = false
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showQueueStyleDialog = false }) { Text(context.getString(R.string.done)) } }
        )
    }
}

@Composable
private fun QueueOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
    }
}
