package com.dn0ne.player.app.presentation.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import coil3.compose.AsyncImage
import com.dn0ne.player.R
import com.dn0ne.player.app.presentation.components.topbar.ColumnWithCollapsibleTopBar
import com.dn0ne.player.core.presentation.AppDetails
import com.dn0ne.player.core.util.GitHubRelease
import com.dn0ne.player.core.util.GitHubUpdateChecker
import com.dn0ne.player.core.util.UpdateCheckResult
import kotlinx.coroutines.launch

private sealed interface AboutUpdateDialog {
    data object Checking : AboutUpdateDialog
    data class Available(val release: GitHubRelease) : AboutUpdateDialog
    data class UpToDate(val versionName: String) : AboutUpdateDialog
    data object Failed : AboutUpdateDialog
}

@Composable
fun AboutPage(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    var updateDialog by remember { mutableStateOf<AboutUpdateDialog?>(null) }
    var collapseFraction by remember {
        mutableFloatStateOf(0f)
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
                text = context.resources.getString(R.string.about_app),
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
        contentPadding = PaddingValues(horizontal = 24.dp),
        contentHorizontalAlignment = Alignment.CenterHorizontally,
        contentVerticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        AppDetails(
            modifier = Modifier.fillMaxWidth(),
            description = context.getString(R.string.about_description),
            showOpenSourceBadge = true,
            bentoStyle = true
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "JS",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    AsyncImage(
                        model = context.getString(R.string.developer_github_avatar_url),
                        contentDescription = context.getString(R.string.developer_name),
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = context.getString(R.string.developer_name),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = context.getString(R.string.developer_role),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = {
                    uriHandler.openUri(context.getString(R.string.developer_github_url))
                }) {
                    Icon(
                        imageVector = Icons.Rounded.OpenInNew,
                        contentDescription = context.getString(R.string.open_developer_profile)
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column {
                AboutGroupedAction(
                    title = context.getString(R.string.about_credits_title),
                    supportingText = context.getString(R.string.about_attribution),
                    icon = Icons.Rounded.OpenInNew,
                    onClick = { uriHandler.openUri(context.getString(R.string.original_project_url)) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                AboutGroupedAction(
                    title = context.getString(R.string.feedback),
                    supportingText = context.getString(R.string.feedback_explain),
                    icon = Icons.Rounded.QuestionAnswer,
                    onClick = { uriHandler.openUri(context.getString(R.string.feedback_url)) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                AboutGroupedAction(
                    title = context.getString(R.string.check_updates),
                    supportingText = context.getString(R.string.check_updates_explain),
                    icon = Icons.Rounded.Update,
                    onClick = {
                        updateDialog = AboutUpdateDialog.Checking
                        coroutineScope.launch {
                            updateDialog = try {
                                when (val result = GitHubUpdateChecker.check(context)) {
                                    is UpdateCheckResult.Available -> AboutUpdateDialog.Available(result.release)
                                    is UpdateCheckResult.UpToDate -> AboutUpdateDialog.UpToDate(result.versionName)
                                }
                            } catch (_: Exception) {
                                AboutUpdateDialog.Failed
                            }
                        }
                    }
                )
            }
        }
    }

    when (val dialog = updateDialog) {
        AboutUpdateDialog.Checking -> AlertDialog(
            onDismissRequest = { updateDialog = null },
            title = { Text(context.resources.getString(R.string.checking_updates)) },
            text = { Text(context.resources.getString(R.string.checking_updates_explain)) },
            confirmButton = {}
        )
        is AboutUpdateDialog.Available -> AlertDialog(
            onDismissRequest = { updateDialog = null },
            title = { Text(context.resources.getString(R.string.update_available_title)) },
            text = {
                Text(
                    context.getString(
                        R.string.update_available_body,
                        dialog.release.tagName,
                        dialog.release.releaseNotes.ifBlank {
                            context.getString(R.string.update_release_notes_empty)
                        }
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    uriHandler.openUri(dialog.release.downloadUrl)
                    updateDialog = null
                }) { Text(context.resources.getString(R.string.update_download)) }
            },
            dismissButton = {
                TextButton(onClick = { updateDialog = null }) {
                    Text(context.resources.getString(R.string.update_later))
                }
            }
        )
        is AboutUpdateDialog.UpToDate -> AlertDialog(
            onDismissRequest = { updateDialog = null },
            title = { Text(context.resources.getString(R.string.update_current_title)) },
            text = { Text(context.getString(R.string.update_current_message, dialog.versionName)) },
            confirmButton = {
                TextButton(onClick = { updateDialog = null }) {
                    Text(context.resources.getString(R.string.update_done))
                }
            }
        )
        AboutUpdateDialog.Failed -> AlertDialog(
            onDismissRequest = { updateDialog = null },
            title = { Text(context.resources.getString(R.string.update_failed_title)) },
            text = { Text(context.resources.getString(R.string.update_failed_message)) },
            confirmButton = {
                TextButton(onClick = { updateDialog = null }) {
                    Text(context.resources.getString(R.string.update_done))
                }
            }
        )
        null -> Unit
    }
}

@Composable
private fun AboutGroupedAction(
    title: String,
    supportingText: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(supportingText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
