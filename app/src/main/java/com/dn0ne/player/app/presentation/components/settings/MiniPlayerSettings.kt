package com.dn0ne.player.app.presentation.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.Dock
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ShapeDefaults
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
import com.dn0ne.player.app.presentation.components.PlayingAudioAnimation
import com.dn0ne.player.core.data.MiniPlayerStyle
import com.dn0ne.player.core.data.PlaybackIndicatorStyle
import com.dn0ne.player.core.data.ProgressStyle
import com.dn0ne.player.core.data.Settings

@Composable
fun MiniPlayerSettings(
    settings: Settings,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var collapseFraction by remember { mutableFloatStateOf(0f) }
    var showIndicatorStyleDialog by remember { mutableStateOf(false) }
    var showProgressStyleDialog by remember { mutableStateOf(false) }
    var showMiniPlayerStyleDialog by remember { mutableStateOf(false) }
    val showPreviousButton by settings.showMiniPlayerPreviousButton.collectAsState()
    val showProgress by settings.showMiniPlayerProgress.collectAsState()
    val progressStyle by settings.progressStyle.collectAsState()
    val showAnimatedIndicator by settings.showAnimatedPlaybackIndicator.collectAsState()
    val indicatorStyle by settings.playbackIndicatorStyle.collectAsState()
    val circularCover by settings.circularMiniPlayerCover.collectAsState()
    val rotateCover by settings.rotateMiniPlayerCover.collectAsState()
    val compactMiniPlayer by settings.compactMiniPlayer.collectAsState()
    val miniPlayerStyle by settings.miniPlayerStyle.collectAsState()

    ColumnWithCollapsibleTopBar(
        topBarContent = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.BottomStart).padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = context.getString(R.string.back)
                )
            }
            Text(
                text = context.getString(R.string.mini_player),
                fontSize = lerp(
                    MaterialTheme.typography.titleLarge.fontSize,
                    MaterialTheme.typography.displaySmall.fontSize,
                    collapseFraction
                ),
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
        SettingsSection(title = context.getString(R.string.mini_player_controls)) {
            SettingSwitch(
                title = context.getString(R.string.show_previous_button),
                supportingText = context.getString(R.string.show_previous_button_explain),
                icon = Icons.Rounded.SkipPrevious,
                isChecked = showPreviousButton,
                onCheckedChange = settings::updateShowMiniPlayerPreviousButton,
                modifier = Modifier.fillMaxWidth()
            )
            SettingSwitch(
                title = context.getString(R.string.mini_player_progress),
                supportingText = context.getString(R.string.mini_player_progress_explain),
                icon = Icons.AutoMirrored.Rounded.ShowChart,
                isChecked = showProgress,
                onCheckedChange = { enabled ->
                    settings.updateShowMiniPlayerProgress(enabled)
                    if (!enabled) showProgressStyleDialog = false
                },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(
                visible = showProgress,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = context.getString(R.string.mini_player_progress_style),
                            supportingText = context.getString(progressStyle.labelResource()),
                            icon = Icons.Rounded.Tune,
                            onClick = {
                                if (showProgress) showProgressStyleDialog = true
                            }
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        SettingsSection(title = context.getString(R.string.mini_player_cover_section)) {
            SettingSwitch(
                title = context.getString(R.string.mini_player_cover_shape),
                supportingText = context.getString(
                    if (circularCover) R.string.cover_shape_circular else R.string.cover_shape_square
                ),
                icon = Icons.Rounded.Circle,
                isChecked = circularCover,
                onCheckedChange = settings::updateCircularMiniPlayerCover,
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(
                visible = circularCover,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SettingSwitch(
                    title = context.getString(R.string.rotate_cover_while_playing),
                    supportingText = context.getString(R.string.rotate_cover_while_playing_explain),
                    icon = Icons.Rounded.Repeat,
                    isChecked = rotateCover,
                    onCheckedChange = settings::updateRotateMiniPlayerCover,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        SettingsSection(title = context.getString(R.string.animated_playback_indicator)) {
            SettingSwitch(
                title = context.getString(R.string.animated_playback_indicator),
                supportingText = context.getString(R.string.animated_playback_indicator_explain),
                icon = Icons.Outlined.GraphicEq,
                isChecked = showAnimatedIndicator,
                onCheckedChange = { enabled ->
                    settings.updateShowAnimatedPlaybackIndicator(enabled)
                    if (!enabled) showIndicatorStyleDialog = false
                },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(
                visible = showAnimatedIndicator,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsGroup(
                        items = listOf(
                            SettingsItem(
                                title = context.getString(R.string.playback_indicator_style),
                                supportingText = context.getString(indicatorStyle.stringResource()),
                                icon = Icons.Rounded.Tune,
                                onClick = {
                                    if (showAnimatedIndicator) showIndicatorStyleDialog = true
                                }
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeDefaults.Medium,
                        color = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = context.getString(R.string.indicator_preview),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            PlayingAudioAnimation(
                                style = indicatorStyle,
                                modifier = Modifier.size(width = 24.dp, height = 20.dp)
                            )
                        }
                    }
                }
            }
        }

        SettingsSection(title = context.getString(R.string.mini_player_style_section)) {
            SettingSwitch(
                title = context.getString(R.string.compact_mini_player),
                supportingText = context.getString(R.string.compact_mini_player_explain),
                icon = Icons.Outlined.Compress,
                isChecked = compactMiniPlayer,
                onCheckedChange = settings::updateCompactMiniPlayer,
                modifier = Modifier.fillMaxWidth()
            )
            SettingsGroup(
                items = listOf(
                    SettingsItem(
                        title = context.getString(R.string.mini_player_style),
                        supportingText = context.getString(miniPlayerStyle.labelResource()),
                        icon = Icons.Outlined.Dock,
                        onClick = { showMiniPlayerStyleDialog = true }
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showProgressStyleDialog) {
        AlertDialog(
            onDismissRequest = { showProgressStyleDialog = false },
            title = { Text(context.getString(R.string.mini_player_progress_style)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ProgressStyle.entries.forEach { style ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settings.updateProgressStyle(style)
                                    showProgressStyleDialog = false
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = progressStyle == style,
                                onClick = {
                                    settings.updateProgressStyle(style)
                                    showProgressStyleDialog = false
                                }
                            )
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text = context.getString(style.labelResource()),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = context.getString(style.descriptionResource()),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProgressStyleDialog = false }) {
                    Text(context.getString(R.string.done))
                }
            }
        )
    }

    if (showIndicatorStyleDialog) {
        AlertDialog(
            onDismissRequest = { showIndicatorStyleDialog = false },
            title = { Text(context.getString(R.string.playback_indicator_style)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PlaybackIndicatorStyle.entries.forEach { style ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { settings.updatePlaybackIndicatorStyle(style) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = indicatorStyle == style,
                                onClick = { settings.updatePlaybackIndicatorStyle(style) }
                            )
                            Text(
                                text = context.getString(style.stringResource()),
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIndicatorStyleDialog = false }) {
                    Text(context.getString(R.string.done))
                }
            }
        )
    }

    if (showMiniPlayerStyleDialog) {
        AlertDialog(
            onDismissRequest = { showMiniPlayerStyleDialog = false },
            title = { Text(context.getString(R.string.mini_player_style)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    MiniPlayerStyle.entries.forEach { style ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settings.updateMiniPlayerStyle(style)
                                    showMiniPlayerStyleDialog = false
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = miniPlayerStyle == style,
                                onClick = { settings.updateMiniPlayerStyle(style) }
                            )
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text = context.getString(style.labelResource()),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = context.getString(style.descriptionResource()),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMiniPlayerStyleDialog = false }) {
                    Text(context.getString(R.string.done))
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeDefaults.Large,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

private fun PlaybackIndicatorStyle.stringResource(): Int = when (this) {
    PlaybackIndicatorStyle.CenterBars -> R.string.indicator_style_center_bars
    PlaybackIndicatorStyle.PixelBlocks -> R.string.indicator_style_pixel_blocks
    PlaybackIndicatorStyle.FlowingWave -> R.string.indicator_style_flowing_wave
    PlaybackIndicatorStyle.ClassicBars -> R.string.indicator_style_classic_bars
    PlaybackIndicatorStyle.SpectrumBars -> R.string.indicator_style_frequency_spectrum
    PlaybackIndicatorStyle.HarmonicPulse -> R.string.indicator_style_harmonic_pulse
}

private fun MiniPlayerStyle.labelResource(): Int = when (this) {
    MiniPlayerStyle.CLASSIC_ROUNDED -> R.string.mini_player_style_classic_rounded
    MiniPlayerStyle.CLASSIC_FLAT -> R.string.mini_player_style_classic_flat
    MiniPlayerStyle.FLOATING_RECT -> R.string.mini_player_style_floating_rect
    MiniPlayerStyle.FLOATING_CAPSULE -> R.string.mini_player_style_floating_capsule
}

private fun MiniPlayerStyle.descriptionResource(): Int = when (this) {
    MiniPlayerStyle.CLASSIC_ROUNDED -> R.string.mini_player_style_classic_rounded_explain
    MiniPlayerStyle.CLASSIC_FLAT -> R.string.mini_player_style_classic_flat_explain
    MiniPlayerStyle.FLOATING_RECT -> R.string.mini_player_style_floating_rect_explain
    MiniPlayerStyle.FLOATING_CAPSULE -> R.string.mini_player_style_floating_capsule_explain
}

private fun ProgressStyle.labelResource(): Int = when (this) {
    ProgressStyle.LEFT_TO_RIGHT -> R.string.progress_style_left_to_right
    ProgressStyle.CENTER_OUTWARD -> R.string.progress_style_center_outward
    ProgressStyle.GRADIENT_SWEEP -> R.string.progress_style_gradient_sweep
    ProgressStyle.TOP_LINE -> R.string.progress_style_top_line
}

private fun ProgressStyle.descriptionResource(): Int = when (this) {
    ProgressStyle.LEFT_TO_RIGHT -> R.string.progress_style_left_to_right_explain
    ProgressStyle.CENTER_OUTWARD -> R.string.progress_style_center_outward_explain
    ProgressStyle.GRADIENT_SWEEP -> R.string.progress_style_gradient_sweep_explain
    ProgressStyle.TOP_LINE -> R.string.progress_style_top_line_explain
}
