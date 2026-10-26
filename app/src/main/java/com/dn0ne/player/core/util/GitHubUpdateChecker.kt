package com.dn0ne.player.core.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
    // The repository can be changed here without touching the UI or version comparison.
    private const val LATEST_RELEASE_API =
        "https://api.github.com/repos/jssanchez03/lotus/releases/latest"

    suspend fun check(context: Context): UpdateCheckResult = withContext(Dispatchers.IO) {
        val connection = (URL(LATEST_RELEASE_API).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "Lotus-Android")
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
                val htmlUrl = json.optString("html_url", "https://github.com/jssanchez03/lotus/releases")
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
        fun components(version: String): List<Int> =
            Regex("\\d+").findAll(version).take(3).map { it.value.toInt() }.toList()

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
