package com.dn0ne.player.core.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.dn0ne.player.R
import com.dn0ne.player.core.util.getAppVersionName

@Composable
fun AppDetails(
    modifier: Modifier = Modifier,
    description: String? = null,
    showOpenSourceBadge: Boolean = false,
    bentoStyle: Boolean = false
) {
    val context = LocalContext.current
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val logo = if (isDarkTheme) R.drawable.ren_logo_completo_oscuro_sin_fondo
    else R.drawable.ren_logo_completo_claro_sin_fondo

    if (!bentoStyle) {
        val logoVisibility = remember {
            MutableTransitionState(false).apply { targetState = true }
        }
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visibleState = logoVisibility,
                enter = fadeIn(animationSpec = tween(420)) + scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(420)
                )
            ) {
                Image(
                    bitmap = ImageBitmap.imageResource(logo),
                    contentDescription = context.getString(R.string.app_name),
                    modifier = Modifier.size(210.dp),
                    contentScale = ContentScale.Fit,
                    filterQuality = FilterQuality.High
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = context.getAppVersionName(), color = MaterialTheme.colorScheme.outline)
        }
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(width = 220.dp, height = 172.dp)
                    .clipToBounds(),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    bitmap = ImageBitmap.imageResource(logo),
                    contentDescription = context.getString(R.string.app_name),
                    modifier = Modifier.size(220.dp),
                    contentScale = ContentScale.Fit,
                    filterQuality = FilterQuality.High
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(
                        text = "v${context.getAppVersionName()}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
                if (showOpenSourceBadge) {
                    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.tertiaryContainer) {
                        Text(
                            text = context.getString(R.string.about_open_source),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
            if (description != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
