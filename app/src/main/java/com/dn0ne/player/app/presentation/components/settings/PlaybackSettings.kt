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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.FilterCenterFocus
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.fastForEachIndexed
import com.dn0ne.player.app.presentation.components.playback.WavingSeekBar
import com.dn0ne.player.EqualizerController
import com.dn0ne.player.R
import com.dn0ne.player.app.presentation.components.snackbar.SnackbarController
import com.dn0ne.player.app.presentation.components.snackbar.SnackbarEvent
import com.dn0ne.player.app.presentation.components.topbar.ColumnWithCollapsibleTopBar
import com.dn0ne.player.core.data.Settings
import com.dn0ne.player.core.data.SeekBarStyle
import com.dn0ne.player.core.data.PlayerCarouselPreview
import kotlinx.coroutines.launch

@Composable
fun PlaybackSettings(
    settings: Settings,
    equalizerController: EqualizerController,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var collapseFraction by remember {
        mutableFloatStateOf(0f)
    }
    val seekBarStyle by settings.seekBarStyle.collectAsState()
    val carouselPreview by settings.playerCarouselPreview.collectAsState()
    val carouselPreviewTint by settings.carouselPreviewTint.collectAsState()
    val carouselPreviewBlur by settings.carouselPreviewBlur.collectAsState()
    var showSeekBarStyleDialog by remember { mutableStateOf(false) }
    var showCarouselPreviewDialog by remember { mutableStateOf(false) }
    var pendingSeekBarStyle by remember { mutableStateOf(seekBarStyle) }
    LaunchedEffect(showSeekBarStyleDialog, seekBarStyle) {
        if (showSeekBarStyleDialog) pendingSeekBarStyle = seekBarStyle
    }

    ColumnWithCollapsibleTopBar(
        topBarContent = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = context.resources.getString(R.string.back)
                )
            }

            Text(
                text = context.resources.getString(R.string.playback),
                fontSize = lerp(
                    MaterialTheme.typography.titleLarge.fontSize,
                    MaterialTheme.typography.displaySmall.fontSize,
                    collapseFraction
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
            )
        },
        collapseFraction = {
            collapseFraction = it
        },
        contentPadding = PaddingValues(horizontal = 28.dp),
        contentHorizontalAlignment = Alignment.CenterHorizontally,
        contentVerticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        val coroutineScope = rememberCoroutineScope()
        var handleAudioFocus by remember {
            mutableStateOf(settings.handleAudioFocus)
        }
        SettingSwitch(
            title = context.resources.getString(R.string.audio_focus),
            supportingText = context.resources.getString(R.string.audio_focus_explain),
            icon = Icons.Rounded.FilterCenterFocus,
            isChecked = handleAudioFocus,
            onCheckedChange = {
                settings.handleAudioFocus = it
                handleAudioFocus = it
                coroutineScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = R.string.changes_will_take_effect_on_next_launch
                        )
                    )
                }
            }
        )

        var jumpToBeginning by remember {
            mutableStateOf(settings.jumpToBeginning)
        }
        SettingSwitch(
            title = context.resources.getString(R.string.jump_to_beginning),
            supportingText = context.resources.getString(R.string.jump_to_beginning_explain),
            icon = Icons.Rounded.SkipPrevious,
            isChecked = jumpToBeginning,
            onCheckedChange = {
                settings.jumpToBeginning = it
                jumpToBeginning = it
            }
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = context.getString(R.string.seek_bar_style),
                            supportingText = context.getString(seekBarStyle.labelResource()),
                            icon = Icons.Rounded.Equalizer,
                            onClick = { showSeekBarStyleDialog = true }
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = context.getString(R.string.indicator_preview),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        WavingSeekBar(
                            position = 42_000L,
                            duration = 100_000L,
                            isPlaying = true,
                            onPositionChange = {},
                            style = seekBarStyle,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SettingsGroup(
                    items = listOf(
                        SettingsItem(
                            title = context.getString(R.string.carousel_preview),
                            supportingText = context.getString(carouselPreview.labelResource()),
                            icon = Icons.AutoMirrored.Rounded.ShowChart,
                            onClick = { showCarouselPreviewDialog = true }
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                AnimatedVisibility(
                    visible = carouselPreview != PlayerCarouselPreview.NONE,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        SettingSwitch(
                            title = context.getString(R.string.carousel_preview_tint),
                            supportingText = context.getString(R.string.carousel_preview_tint_explain),
                            icon = Icons.Rounded.Circle,
                            isChecked = carouselPreviewTint,
                            onCheckedChange = settings::updateCarouselPreviewTint,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        )
                        SettingSwitch(
                            title = context.getString(R.string.carousel_preview_blur),
                            supportingText = context.getString(R.string.carousel_preview_blur_explain),
                            icon = Icons.Rounded.FilterCenterFocus,
                            isChecked = carouselPreviewBlur,
                            onCheckedChange = settings::updateCarouselPreviewBlur,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }

        val isEqEnabled by equalizerController.isEqEnabled.collectAsState()
        SettingSwitch(
            title = context.resources.getString(R.string.equalizer),
            supportingText = context.resources.getString(R.string.equalizer_explain),
            icon = Icons.Rounded.Equalizer,
            isChecked = isEqEnabled,
            onCheckedChange = equalizerController::updateIsEqEnabled,
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(
            visible = isEqEnabled
        ) {
            EqualizerSettings(
                equalizerController = equalizerController,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        if (showSeekBarStyleDialog) {
            AlertDialog(
                onDismissRequest = { showSeekBarStyleDialog = false },
                title = { Text(context.getString(R.string.seek_bar_style)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        SeekBarStyle.entries.forEach { style ->
                            PlaybackPreferenceOption(
                                label = context.getString(style.labelResource()),
                                selected = pendingSeekBarStyle == style,
                                onClick = { pendingSeekBarStyle = style }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        settings.updateSeekBarStyle(pendingSeekBarStyle)
                        showSeekBarStyleDialog = false
                    }) { Text(context.getString(R.string.done)) }
                },
                dismissButton = {
                    TextButton(onClick = { showSeekBarStyleDialog = false }) {
                        Text(context.getString(R.string.cancel))
                    }
                }
            )
        }

        if (showCarouselPreviewDialog) {
            AlertDialog(
                onDismissRequest = { showCarouselPreviewDialog = false },
                title = { Text(context.getString(R.string.carousel_preview)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        PlayerCarouselPreview.entries.forEach { preview ->
                            PlaybackPreferenceOption(
                                label = context.getString(preview.labelResource()),
                                selected = carouselPreview == preview,
                                onClick = {
                                    settings.updatePlayerCarouselPreview(preview)
                                    showCarouselPreviewDialog = false
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCarouselPreviewDialog = false }) {
                        Text(context.getString(R.string.done))
                    }
                }
            )
        }
    }
}

@Composable
private fun PlaybackPreferenceOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
    }
}

private fun SeekBarStyle.labelResource(): Int = when (this) {
    SeekBarStyle.DEFAULT_WAVE -> R.string.seek_bar_style_default_wave
    SeekBarStyle.TRADITIONAL_LINE -> R.string.seek_bar_style_traditional_line
    SeekBarStyle.SUBTLE_WAVE -> R.string.seek_bar_style_subtle_wave
    SeekBarStyle.MULTI_WAVE_SAMSUNG -> R.string.seek_bar_style_multi_wave_samsung
    SeekBarStyle.DOTTED_GRANULAR -> R.string.seek_bar_style_dotted_granular
}

private fun PlayerCarouselPreview.labelResource(): Int = when (this) {
    PlayerCarouselPreview.NONE -> R.string.carousel_preview_none
    PlayerCarouselPreview.SINGLE_SIDE -> R.string.carousel_preview_single_side
    PlayerCarouselPreview.BOTH_SIDES -> R.string.carousel_preview_both_sides
}

@Composable
fun EqualizerSettings(
    equalizerController: EqualizerController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val lowerLevelLimit by equalizerController.lowerLevelLimit.collectAsState()
        val upperLevelLimit by equalizerController.upperLevelLimit.collectAsState()
        val bandFrequencies by equalizerController.bandFrequencies.collectAsState()
        val bandLevels by equalizerController.bandLevels.collectAsState()
        bandLevels?.fastForEachIndexed { index, level ->
            SettingSlider(
                title = bandFrequencies?.get(index) ?: "",
                value = level.toFloat(),
                valueToShow = "${level / 100f}dB",
                onValueChange = {
                    bandLevels?.let { bandLevels ->
                        equalizerController.updateBandLevels(
                            bandLevels.toMutableList().apply {
                                set(index, it.toInt().toShort())
                            }
                        )
                    }
                },
                onValueChangeFinished = {},
                valueRange = lowerLevelLimit.toFloat()..upperLevelLimit.toFloat(),
            )
        }

        val context = LocalContext.current
        FilledTonalButton(
            onClick = equalizerController::resetBandLevels
        ) {
            Text(text = context.resources.getString(R.string.reset))
        }
    }
}
