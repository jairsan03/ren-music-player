package com.dn0ne.player.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.dn0ne.player.R

// ── Proveedor de Google Fonts ────────────────────────────────────────────────
val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
// ── Catálogo de familias tipográficas disponibles ───────────────────────────
enum class AppFont(
    val displayName: String,
    val description: String,
    val fontFamily: FontFamily
) {
    Manrope(
        displayName = "Manrope",
        description = "Predeterminada · Minimalista",
        fontFamily = googleFontFamily("Manrope")
    ),
    DmSans(
        displayName = "DM Sans",
        description = "Suave y equilibrada",
        fontFamily = googleFontFamily("DM Sans")
    ),
    PlusJakartaSans(
        displayName = "Plus Jakarta Sans",
        description = "Moderna y refinada",
        fontFamily = googleFontFamily("Plus Jakarta Sans")
    ),
    NunitoSans(
        displayName = "Nunito Sans",
        description = "Redondeada y acogedora",
        fontFamily = googleFontFamily("Nunito Sans")
    ),
    Outfit(
        displayName = "Outfit",
        description = "Geométrica y estilizada",
        fontFamily = googleFontFamily("Outfit")
    ),
    Inter(
        displayName = "Inter",
        description = "Neutra y funcional",
        fontFamily = googleFontFamily("Inter")
    ),
    SpaceGrotesk(
        displayName = "Space Grotesk",
        description = "Tecnológica y distintiva",
        fontFamily = googleFontFamily("Space Grotesk")
    ),
    Figtree(
        displayName = "Figtree",
        description = "Limpia y amigable",
        fontFamily = googleFontFamily("Figtree")
    ),
    Lora(
        displayName = "Lora",
        description = "Editorial y literaria · Serif",
        fontFamily = googleFontFamily("Lora")
    ),
    InstrumentSerif(
        displayName = "Instrument Serif",
        description = "Sofisticada y expresiva · Serif",
        fontFamily = googleFontFamily("Instrument Serif")
    );

    companion object {
        fun fromName(name: String): AppFont =
            entries.firstOrNull { it.name == name } ?: Manrope
    }
}

/** Construye una [FontFamily] cargada desde el proveedor de Google Fonts. */
private fun googleFontFamily(name: String): FontFamily {
    val googleFont = GoogleFont(name)
    return FontFamily(
        Font(googleFont = googleFont, fontProvider = googleFontProvider, weight = FontWeight.Light, style = FontStyle.Normal),
        Font(googleFont = googleFont, fontProvider = googleFontProvider, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(googleFont = googleFont, fontProvider = googleFontProvider, weight = FontWeight.Medium, style = FontStyle.Normal),
        Font(googleFont = googleFont, fontProvider = googleFontProvider, weight = FontWeight.SemiBold, style = FontStyle.Normal),
        Font(googleFont = googleFont, fontProvider = googleFontProvider, weight = FontWeight.Bold, style = FontStyle.Normal),
        Font(googleFont = googleFont, fontProvider = googleFontProvider, weight = FontWeight.Normal, style = FontStyle.Italic),
    )
}

/** Genera una [Typography] de Material3 completa usando la [FontFamily] dada. */
fun buildTypography(fontFamily: FontFamily): Typography = Typography(
    displayLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = 0.sp),
    displaySmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp),
    headlineLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp),
    headlineMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp),
    headlineSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp),
    titleLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
    titleSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    bodySmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    labelLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
)
