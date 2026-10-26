package com.dn0ne.player.app.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dn0ne.player.R
import com.dn0ne.player.core.data.PlaybackIndicatorStyle
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

@Composable
fun PlayingAudioAnimation(
    style: PlaybackIndicatorStyle,
    modifier: Modifier = Modifier.size(width = 24.dp, height = 20.dp)
) {
    val transition = rememberInfiniteTransition(label = "playing-audio-animation")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1_600), RepeatMode.Restart),
        label = "playing-audio-phase"
    )
    val alternatePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1_500), RepeatMode.Restart),
        label = "playing-audio-alternate-phase"
    )
    val primary = MaterialTheme.colorScheme.primary
    val description = LocalContext.current.getString(R.string.now_playing)

    Box(
        modifier = modifier.semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (style) {
                PlaybackIndicatorStyle.CenterBars -> {
                    val barWidth = size.width * 0.13f
                    val gap = (size.width - barWidth * 4f) / 5f
                    val heights = listOf(0.48f, 0.86f, 0.62f, 0.98f)
                    heights.forEachIndexed { index, baseHeight ->
                        val animated = baseHeight * (
                            0.45f + 0.55f * abs(sin((phase + index * 1.4f).toDouble()).toFloat())
                        )
                        val barHeight = size.height * animated
                        drawRoundRect(
                            color = primary,
                            topLeft = Offset(gap + index * (barWidth + gap), (size.height - barHeight) / 2f),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barWidth / 2f)
                        )
                    }
                }

                PlaybackIndicatorStyle.PixelBlocks -> {
                    val columns = 5
                    val rows = 4
                    val gapX = size.width * 0.055f
                    val gapY = size.height * 0.08f
                    val blockWidth = (size.width - gapX * (columns - 1)) / columns
                    val blockHeight = (size.height - gapY * (rows - 1)) / rows
                    repeat(columns) { column ->
                        val activeRows = 1 + (
                            abs(sin((phase * 1.2f + column * 1.1f).toDouble()).toFloat()) * rows
                        ).toInt()
                        repeat(rows) { row ->
                            val active = row >= rows - activeRows
                            drawRoundRect(
                                color = primary.copy(alpha = if (active) 1f else 0.15f),
                                topLeft = Offset(
                                    x = column * (blockWidth + gapX),
                                    y = row * (blockHeight + gapY)
                                ),
                                size = Size(blockWidth, blockHeight),
                                cornerRadius = CornerRadius(blockWidth * 0.16f)
                            )
                        }
                    }
                }

                PlaybackIndicatorStyle.FlowingWave -> {
                    repeat(5) { layer ->
                        val path = Path()
                        val verticalOffset = size.width * (0.15f + layer * 0.175f)
                        for (pixelY in 0..size.height.toInt()) {
                            val y = pixelY.toFloat()
                            val normalizedY = y / size.height
                            val edgeFade = sin(normalizedY * Math.PI).toFloat()
                            val x = verticalOffset +
                                sin(normalizedY * Math.PI * 2.4 + phase + layer * 0.75f).toFloat() *
                                size.width * 0.11f * edgeFade
                            if (pixelY == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(
                            path = path,
                            color = primary.copy(alpha = 0.35f + layer * 0.12f),
                            style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                PlaybackIndicatorStyle.ClassicBars -> {
                    val barCount = 4
                    val barWidth = size.width * 0.13f
                    val gap = (size.width - barWidth * barCount) / (barCount + 1)
                    repeat(barCount) { index ->
                        val animated = 0.2f + 0.8f * abs(
                            sin((phase * (1f + index * 0.14f) + index * 1.25f).toDouble()).toFloat()
                        )
                        val barHeight = size.height * animated
                        drawRoundRect(
                            color = primary,
                            topLeft = Offset(gap + index * (barWidth + gap), size.height - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barWidth / 2f)
                        )
                    }
                }

                PlaybackIndicatorStyle.SpectrumBars -> {
                    val barCount = 16
                    val gap = size.width * 0.025f
                    val barWidth = (size.width - gap * (barCount - 1)) / barCount
                    repeat(barCount) { index ->
                        val x = index * (barWidth + gap)
                        val frequency = abs(
                            sin((index * 0.66f + phase * (0.72f + index % 4 * 0.13f)).toDouble()).toFloat() * 0.62f +
                                sin((index * 0.27f - alternatePhase * 1.3f).toDouble()).toFloat() * 0.38f
                        )
                        val barHeight = size.height * (0.16f + frequency * 0.84f)
                        drawRoundRect(
                            color = primary,
                            topLeft = Offset(x, size.height - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barWidth / 2f)
                        )
                    }
                }

                PlaybackIndicatorStyle.HarmonicPulse -> {
                    val path = Path()
                    val centerY = size.height / 2f
                    for (pixelX in 0..size.width.toInt()) {
                        val x = pixelX.toFloat()
                        val normalizedX = x / size.width
                        val distance = (normalizedX - 0.5f) * 2f
                        val envelope = exp((-distance * distance * 4.5f).toDouble()).toFloat()
                        val carrier = sin(normalizedX * Math.PI * 12.0 - phase * 0.8).toFloat()
                        val harmonic = sin(normalizedX * Math.PI * 24.0 + alternatePhase * 0.65).toFloat() * 0.28f
                        val y = centerY + (carrier + harmonic) * envelope * size.height * 0.42f
                        if (pixelX == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(
                        path = path,
                        color = primary,
                        style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
