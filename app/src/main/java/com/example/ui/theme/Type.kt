package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.FontPairing

val PlusJakartaSansFamily = FontFamily(Font(R.font.plus_jakarta_sans, FontWeight.Normal))
val InterFamily = FontFamily(Font(R.font.inter, FontWeight.Normal))
val PlayfairDisplayFamily = FontFamily(Font(R.font.playfair_display, FontWeight.Normal))
val SpaceGroteskFamily = FontFamily(Font(R.font.space_grotesk, FontWeight.Normal))
val JetBrainsMonoFamily = FontFamily(Font(R.font.jetbrains_mono, FontWeight.Normal))
val OutfitFamily = FontFamily(Font(R.font.outfit, FontWeight.Normal))
val SoraFamily = FontFamily(Font(R.font.sora, FontWeight.Normal))
val DmSansFamily = FontFamily(Font(R.font.dm_sans, FontWeight.Normal))
val SyneFamily = FontFamily(Font(R.font.syne, FontWeight.Normal))
val ManropeFamily = FontFamily(Font(R.font.manrope, FontWeight.Normal))

data class ResolvedFonts(
  val display: FontFamily,
  val body: FontFamily,
  val mono: FontFamily = JetBrainsMonoFamily,
)

fun resolveFontPairing(pairing: FontPairing): ResolvedFonts =
  when (pairing) {
    FontPairing.GEOMETRIC_SANS ->
      ResolvedFonts(display = PlusJakartaSansFamily, body = InterFamily)
    FontPairing.ROUNDED_DISPLAY ->
      ResolvedFonts(display = OutfitFamily, body = PlusJakartaSansFamily)
    FontPairing.MONOSPACE_TECH ->
      ResolvedFonts(display = SpaceGroteskFamily, body = JetBrainsMonoFamily)
    FontPairing.EDITORIAL_SERIF ->
      ResolvedFonts(display = PlayfairDisplayFamily, body = InterFamily)
    FontPairing.SORA_DM_SANS ->
      ResolvedFonts(display = SoraFamily, body = DmSansFamily)
    FontPairing.SYNE_MANROPE ->
      ResolvedFonts(display = SyneFamily, body = ManropeFamily)
  }

val Typography =
  Typography(
    headlineMedium =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.5).sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.2).sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
      ),
    labelMedium =
      TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.4.sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
      ),
  )
