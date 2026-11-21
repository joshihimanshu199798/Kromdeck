package com.example.model

import com.example.data.PresetDecks

/**
 * Typography configuration tokens managed by [KromdeckTheme].
 */
data class TypographyTokens(
  val fontPairing: FontPairing = FontPairing.GEOMETRIC_SANS,
  val baseFontSizeSp: Float = 14f,
  val headingWeightBold: Boolean = true,
  val letterSpacingSp: Float = 0f,
  val lineHeightMultiplier: Float = 1.35f,
)

/**
 * Surface & elevation style tokens managed by [KromdeckTheme].
 */
data class SurfaceStyleTokens(
  val physics: SurfacePhysics = SurfacePhysics.HARD_INK,
  val cornerRadiusDp: Int = 14,
  val cardPaddingDp: Int = 12,
  val borderWidthDp: Float = 2f,
  val shadowIntensity: Float = 0.85f,
  val glassBlurAlpha: Float = 0.78f,
  val gradientEnabled: Boolean = false,
  val gradientAngle: Float = 135f,
  val gradientMeshGlow: Boolean = false,
)

/**
 * Canonical design system state representing an active Kromdeck theme:
 * color palette, typography settings, and surface style tokens.
 */
data class KromdeckTheme(
  val id: String = "tactile_cream_ink",
  val name: String = "Tactile Cream & Ink",
  val tagline: String = "Warm paper canvas with crisp neo-brutalist ink",
  val isDark: Boolean = false,
  val category: DiscoverCategory = DiscoverCategory.MINIMAL_NOTES,
  val palette: ThemePalette = PresetDecks.all.first().palette,
  val typography: TypographyTokens = TypographyTokens(),
  val surface: SurfaceStyleTokens = SurfaceStyleTokens(),
  val motionFeel: MotionFeel = MotionFeel.HARD_SNAP,
  val navShell: NavShellStyle = NavShellStyle.BOTTOM_PILL,
  val gridDensity: GridDensity = GridDensity.COMFORTABLE,
  val iconStrokeWeight: Float = 1.8f,
) {
  fun toThemeDeck(note: String = "", swatchIconRes: Int? = null): ThemeDeck =
    ThemeDeck(
      id = id,
      name = name,
      tagline = tagline,
      isDark = isDark,
      category = category,
      palette = palette,
      physics = surface.physics,
      fontPairing = typography.fontPairing,
      motionFeel = motionFeel,
      note = note,
      swatchIconRes = swatchIconRes,
      gradientEnabled = surface.gradientEnabled,
      gradientAngle = surface.gradientAngle,
      gradientMeshGlow = surface.gradientMeshGlow,
      cornerRadiusDp = surface.cornerRadiusDp,
      cardPaddingDp = surface.cardPaddingDp,
      borderWidthDp = surface.borderWidthDp,
      shadowIntensity = surface.shadowIntensity,
      glassBlurAlpha = surface.glassBlurAlpha,
      baseFontSizeSp = typography.baseFontSizeSp,
      headingWeightBold = typography.headingWeightBold,
      letterSpacingSp = typography.letterSpacingSp,
      lineHeightMultiplier = typography.lineHeightMultiplier,
      navShell = navShell,
      gridDensity = gridDensity,
      iconStrokeWeight = iconStrokeWeight,
    )

  companion object {
    val Default: KromdeckTheme
      get() = PresetDecks.all.first().toKromdeckTheme()
  }
}

fun ThemeDeck.toKromdeckTheme(): KromdeckTheme =
  KromdeckTheme(
    id = id,
    name = name,
    tagline = tagline,
    isDark = isDark,
    category = category,
    palette = palette,
    typography =
      TypographyTokens(
        fontPairing = fontPairing,
        baseFontSizeSp = baseFontSizeSp,
        headingWeightBold = headingWeightBold,
        letterSpacingSp = letterSpacingSp,
        lineHeightMultiplier = lineHeightMultiplier,
      ),
    surface =
      SurfaceStyleTokens(
        physics = physics,
        cornerRadiusDp = cornerRadiusDp,
        cardPaddingDp = cardPaddingDp,
        borderWidthDp = borderWidthDp,
        shadowIntensity = shadowIntensity,
        glassBlurAlpha = glassBlurAlpha,
        gradientEnabled = gradientEnabled,
        gradientAngle = gradientAngle,
        gradientMeshGlow = gradientMeshGlow,
      ),
    motionFeel = motionFeel,
    navShell = navShell,
    gridDensity = gridDensity,
    iconStrokeWeight = iconStrokeWeight,
  )
