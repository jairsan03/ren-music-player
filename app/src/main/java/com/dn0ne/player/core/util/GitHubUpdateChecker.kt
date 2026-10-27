package com.dn0ne.player.core.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.dn0ne.player.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class GitHubRelease(
    val tagName: String,
    val releaseNotes: String,
    val releaseUrl: String,
    val downloadUrl: String
)

sealed interface UpdateCheckResult {
    data class Available(val release: GitHubRelease) : UpdateCheckResult
    data class UpToDate(val versionName: String) : UpdateCheckResult
}

object GitHubUpdateChecker {
    suspend fun check(context: Context): UpdateCheckResult = withContext(Dispatchers.IO) {
        val latestReleaseApi = context.getString(R.string.url_github_latest_release_api)
        val connection = (URL(latestReleaseApi).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "Ren-Android")
        }

        try {
            val installedVersion = context.getInstalledVersionName()
            if (connection.responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return@withContext UpdateCheckResult.UpToDate(installedVersion.displayVersion())
            }
            if (connection.responseCode !in 200..299) {
                throw IllegalStateException("GitHub returned HTTP ${connection.responseCode}")
            }
            val release = connection.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                val json = JSONObject(reader.readText())
                val tagName = json.optString("tag_name")
                require(tagName.isNotBlank()) { "Release tag is missing" }
                val htmlUrl = json.optString(
                    "html_url",
                    "${context.getString(R.string.url_ren_repository)}/releases"
                )
                val assets = json.optJSONArray("assets")
                val apkUrl = (0 until (assets?.length() ?: 0))
                    .mapNotNull { assets?.optJSONObject(it) }
                    .firstOrNull { it.optString("name").endsWith(".apk", ignoreCase = true) }
                    ?.optString("browser_download_url")
                    ?.takeIf(String::isNotBlank)

                GitHubRelease(
                    tagName = tagName,
                    releaseNotes = json.optString("body").ifBlank { "" },
                    releaseUrl = htmlUrl,
                    downloadUrl = apkUrl ?: htmlUrl
                )
            }
            if (compareVersions(release.tagName, installedVersion) > 0) {
                UpdateCheckResult.Available(release)
            } else {
                UpdateCheckResult.UpToDate(installedVersion.displayVersion())
            }
        } finally {
            connection.disconnect()
        }
    }

    @Suppress("DEPRECATION")
    private fun Context.getInstalledVersionName(): String {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
        return packageInfo.versionName.orEmpty()
    }

    private fun compareVersions(remote: String, installed: String): Int {
        fun components(version: String): List<Int> {
            val normalized = version.trim()
                .removePrefix("v")
                .removePrefix("V")
                .substringBefore('-')
                .substringBefore('+')
            return normalized.split('.')
                .take(3)
                .map { it.takeWhile(Char::isDigit).toIntOrNull() ?: 0 }
        }

        val left = components(remote)
        val right = components(installed)
        for (index in 0 until 3) {
            val comparison = (left.getOrNull(index) ?: 0).compareTo(right.getOrNull(index) ?: 0)
            if (comparison != 0) return comparison
        }
        return 0
    }

    private fun String.displayVersion(): String {
        val numbers = Regex("\\d+").findAll(this).take(3).joinToString(".") { it.value }
        return if (numbers.isBlank()) this else "v$numbers"
    }
}
