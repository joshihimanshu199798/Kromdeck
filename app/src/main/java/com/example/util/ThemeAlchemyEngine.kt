package com.example.util

import androidx.compose.ui.graphics.Color
import com.example.model.ColorHarmonyRule
import com.example.model.PaletteMoodFilter
import com.example.model.ThemeDeck
import com.example.model.ThemePalette
import kotlin.math.roundToInt

/**
 * Infinite Theme Customization Engine:
 * - Color-wheel harmony generator (6 scientific rules from any 360° seed hue)
 * - Continuous Theme DNA Blender (0%..100% morph between any two themes)
 * - Global palette grading (Hue rotate, Saturation boost, Warm/Cool temperature)
 * - 1-tap Mood Atmosphere transformers
 */
object ThemeAlchemyEngine {

  fun generateFromHarmony(
    seedHue: Float,
    rule: ColorHarmonyRule,
    isDark: Boolean,
    baseDeck: ThemeDeck,
  ): ThemeDeck {
    val h = ((seedHue % 360f) + 360f) % 360f
    val (secHue, warmHue, satPrimary, satSecondary) =
      when (rule) {
        ColorHarmonyRule.COMPLEMENTARY -> Quad((h + 180f) % 360f, (h + 36f) % 360f, 0.76f, 0.74f)
        ColorHarmonyRule.ANALOGOUS -> Quad((h + 34f) % 360f, (h + 326f) % 360f, 0.72f, 0.68f)
        ColorHarmonyRule.TRIADIC -> Quad((h + 120f) % 360f, (h + 240f) % 360f, 0.74f, 0.72f)
        ColorHarmonyRule.SPLIT_COMPLEMENTARY -> Quad((h + 150f) % 360f, (h + 210f) % 360f, 0.76f, 0.72f)
        ColorHarmonyRule.TETRADIC -> Quad((h + 90f) % 360f, (h + 270f) % 360f, 0.75f, 0.72f)
        ColorHarmonyRule.MONOCHROMATIC -> Quad(h, (h + 16f) % 360f, 0.68f, 0.42f)
      }

    val canvas =
      if (isDark) ColorUtils.hsvToColor(h, if (rule == ColorHarmonyRule.MONOCHROMATIC) 0.28f else 0.22f, 0.07f)
      else ColorUtils.hsvToColor(h, 0.045f, 0.98f)
    val surface =
      if (isDark) ColorUtils.hsvToColor(h, 0.22f, 0.13f)
      else Color(0xFFFFFFFF)
    val elevated =
      if (isDark) ColorUtils.hsvToColor(h, 0.24f, 0.20f)
      else ColorUtils.hsvToColor(h, 0.10f, 0.93f)
    val border =
      if (isDark) ColorUtils.hsvToColor(h, 0.36f, 0.46f)
      else ColorUtils.hsvToColor(h, 0.30f, 0.14f)
    val text = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val muted = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)

    val primary = ColorUtils.hsvToColor(h, satPrimary, if (isDark) 0.88f else 0.60f)
    val secondary = ColorUtils.hsvToColor(secHue, satSecondary, if (isDark) 0.86f else 0.62f)
    val warm = ColorUtils.hsvToColor(warmHue, 0.74f, if (isDark) 0.88f else 0.68f)
    val success = if (isDark) Color(0xFF34D399) else Color(0xFF059669)

    val auditedPalette =
      ColorUtils.autoFixContrast(
        ThemePalette(
          canvasHex = ColorUtils.colorToHex(canvas),
          surfaceHex = ColorUtils.colorToHex(surface),
          elevatedSurfaceHex = ColorUtils.colorToHex(elevated),
          borderHex = ColorUtils.colorToHex(border),
          textPrimaryHex = ColorUtils.colorToHex(text),
          textMutedHex = ColorUtils.colorToHex(muted),
          primaryHex = ColorUtils.colorToHex(primary),
          secondaryHex = ColorUtils.colorToHex(secondary),
          warmHex = ColorUtils.colorToHex(warm),
          successHex = ColorUtils.colorToHex(success),
        ),
      )

    return baseDeck.copy(
      id = "harmony_${rule.name.lowercase()}_${h.toInt()}_${if (isDark) "d" else "l"}",
      name = "${hueName(h)} ${rule.label}",
      tagline = "Generated ${rule.label.lowercase()} palette at ${h.toInt()}°",
      isDark = isDark,
      palette = auditedPalette,
      swatchIconRes = null,
    )
  }

  /**
   * Smoothly interpolates every color token and structural dimension between Deck A and Deck B.
   */
  fun blendDecks(deckA: ThemeDeck, deckB: ThemeDeck, fraction: Float): ThemeDeck {
    val t = fraction.coerceIn(0f, 1f)
    if (t <= 0.001f) return deckA
    if (t >= 0.999f) return deckB

    fun lerpHex(hexA: String, hexB: String): String {
      val a = ColorUtils.parseHexColor(hexA)
      val b = ColorUtils.parseHexColor(hexB)
      val mixed =
        Color(
          red = a.red + (b.red - a.red) * t,
          green = a.green + (b.green - a.green) * t,
          blue = a.blue + (b.blue - a.blue) * t,
          alpha = 1f,
        )
      return ColorUtils.colorToHex(mixed)
    }

    fun escapeMidGraySurface(hex: String, preferDark: Boolean, isCardSurface: Boolean): String {
      val color = ColorUtils.parseHexColor(hex)
      val lum = ColorUtils.relativeLuminance(color)
      if (lum in 0.14..0.58) {
        val (h, s, _) = ColorUtils.rgbToHsv(color)
        val targetV =
          if (preferDark) {
            if (isCardSurface) 0.15f else 0.08f
          } else {
            if (isCardSurface) 0.99f else 0.95f
          }
        return ColorUtils.colorToHex(ColorUtils.hsvToColor(h, s.coerceAtMost(0.22f), targetV))
      }
      return hex
    }

    val pA = deckA.palette
    val pB = deckB.palette
    val preferDark = if (t < 0.5f) deckA.isDark else deckB.isDark
    val rawCanvas = escapeMidGraySurface(lerpHex(pA.canvasHex, pB.canvasHex), preferDark, isCardSurface = false)
    val rawSurface = escapeMidGraySurface(lerpHex(pA.surfaceHex, pB.surfaceHex), preferDark, isCardSurface = true)
    val rawElevated = escapeMidGraySurface(lerpHex(pA.elevatedSurfaceHex, pB.elevatedSurfaceHex), preferDark, isCardSurface = true)

    val blendedRaw =
      ThemePalette(
        canvasHex = rawCanvas,
        surfaceHex = rawSurface,
        elevatedSurfaceHex = rawElevated,
        borderHex = lerpHex(pA.borderHex, pB.borderHex),
        textPrimaryHex = if (preferDark) "#F8FAFC" else "#0F172A",
        textMutedHex = if (preferDark) "#CBD5E1" else "#475569",
        primaryHex = lerpHex(pA.primaryHex, pB.primaryHex),
        secondaryHex = lerpHex(pA.secondaryHex, pB.secondaryHex),
        warmHex = lerpHex(pA.warmHex, pB.warmHex),
        successHex = lerpHex(pA.successHex, pB.successHex),
      )
    val isDark = ColorUtils.relativeLuminance(ColorUtils.parseHexColor(blendedRaw.canvasHex)) < 0.25
    val audited = ColorUtils.autoFixContrast(blendedRaw)
    val pctB = (t * 100).roundToInt()

    return (if (t < 0.5f) deckA else deckB).copy(
      id = "blend_${deckA.id.take(8)}_${deckB.id.take(8)}_$pctB",
      name = "${deckA.name.split(" ").first()} × ${deckB.name.split(" ").first()} ($pctB%)",
      tagline = "Blended ${100 - pctB}% ${deckA.name} + $pctB% ${deckB.name}",
      isDark = isDark,
      palette = audited,
      cornerRadiusDp = (deckA.cornerRadiusDp + (deckB.cornerRadiusDp - deckA.cornerRadiusDp) * t).roundToInt(),
      cardPaddingDp = (deckA.cardPaddingDp + (deckB.cardPaddingDp - deckA.cardPaddingDp) * t).roundToInt(),
      borderWidthDp = deckA.borderWidthDp + (deckB.borderWidthDp - deckA.borderWidthDp) * t,
      shadowIntensity = deckA.shadowIntensity + (deckB.shadowIntensity - deckA.shadowIntensity) * t,
      baseFontSizeSp = deckA.baseFontSizeSp + (deckB.baseFontSizeSp - deckA.baseFontSizeSp) * t,
      swatchIconRes = null,
    )
  }

  /**
   * Globally grades the entire palette: rotates Hue (-180°..+180°), scales Saturation (-0.5..+0.5),
   * and shifts Warm/Cool color temperature (-1f..+1f) while preserving WCAG readability.
   */
  fun gradePalette(
    palette: ThemePalette,
    hueShiftDeg: Float,
    saturationDelta: Float,
    temperatureDelta: Float,
  ): ThemePalette {
    fun transformColor(hex: String, preserveNeutralText: Boolean = false): String {
      val color = ColorUtils.parseHexColor(hex)
      val (h, s, v) = ColorUtils.rgbToHsv(color)
      val shiftedHue = ((h + hueShiftDeg) % 360f + 360f) % 360f
      val satScale = if (preserveNeutralText) 0.15f else 1f
      val nextSat = (s + saturationDelta * satScale).coerceIn(0f, 1f)
      var graded = ColorUtils.hsvToColor(shiftedHue, nextSat, v)

      if (temperatureDelta != 0f && !preserveNeutralText) {
        val warmTint = if (temperatureDelta > 0f) Color(0xFFFFB74D) else Color(0xFF60A5FA)
        val amt = (kotlin.math.abs(temperatureDelta) * 0.18f).coerceIn(0f, 0.22f)
        graded =
          Color(
            red = (graded.red * (1f - amt) + warmTint.red * amt).coerceIn(0f, 1f),
            green = (graded.green * (1f - amt) + warmTint.green * amt).coerceIn(0f, 1f),
            blue = (graded.blue * (1f - amt) + warmTint.blue * amt).coerceIn(0f, 1f),
            alpha = 1f,
          )
      }
      return ColorUtils.colorToHex(graded)
    }

    return ColorUtils.autoFixContrast(
      palette.copy(
        canvasHex = transformColor(palette.canvasHex, preserveNeutralText = true),
        surfaceHex = transformColor(palette.surfaceHex, preserveNeutralText = true),
        elevatedSurfaceHex = transformColor(palette.elevatedSurfaceHex),
        borderHex = transformColor(palette.borderHex),
        primaryHex = transformColor(palette.primaryHex),
        secondaryHex = transformColor(palette.secondaryHex),
        warmHex = transformColor(palette.warmHex),
        successHex = transformColor(palette.successHex),
      ),
    )
  }

  /**
   * 1-tap Mood Atmosphere transform that reshapes any active theme while keeping its brand hue.
   */
  fun applyMoodTransform(deck: ThemeDeck, mood: PaletteMoodFilter): ThemeDeck {
    val p = deck.palette
    val (priH, priS, _) = ColorUtils.rgbToHsv(ColorUtils.parseHexColor(p.primaryHex))
    val (secH, _, _) = ColorUtils.rgbToHsv(ColorUtils.parseHexColor(p.secondaryHex))

    val next =
      when (mood) {
        PaletteMoodFilter.HIGH_CONTRAST_AAA -> {
          val isDark = deck.isDark
          val nextPalette =
            if (isDark) {
              p.copy(
                canvasHex = "#000000",
                surfaceHex = "#0B0F17",
                elevatedSurfaceHex = "#161E2E",
                borderHex = "#E2E8F0",
                textPrimaryHex = "#FFFFFF",
                textMutedHex = "#E2E8F0",
                primaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, 0.72f, 0.98f)),
                secondaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(secH, 0.70f, 0.96f)),
              )
            } else {
              p.copy(
                canvasHex = "#FFFFFF",
                surfaceHex = "#F8FAFC",
                elevatedSurfaceHex = "#E2E8F0",
                borderHex = "#020617",
                textPrimaryHex = "#020617",
                textMutedHex = "#1E293B",
                primaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, 0.90f, 0.42f)),
                secondaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(secH, 0.88f, 0.44f)),
              )
            }
          deck.copy(palette = ColorUtils.autoFixContrast(nextPalette), borderWidthDp = 2.5f)
        }

        PaletteMoodFilter.PASTEL_SOFT -> {
          val nextPalette =
            ThemePalette(
              canvasHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, 0.08f, 0.98f)),
              surfaceHex = "#FFFFFF",
              elevatedSurfaceHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, 0.16f, 0.94f)),
              borderHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, 0.25f, 0.35f)),
              textPrimaryHex = "#1E293B",
              textMutedHex = "#52627A",
              primaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, 0.54f, 0.62f)),
              secondaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(secH, 0.50f, 0.65f)),
              warmHex = "#D98A4E",
              successHex = "#2E9A74",
            )
          deck.copy(isDark = false, palette = ColorUtils.autoFixContrast(nextPalette), cornerRadiusDp = 22, borderWidthDp = 1f)
        }

        PaletteMoodFilter.NEON_CYBER -> {
          val nextPalette =
            ThemePalette(
              canvasHex = "#070514",
              surfaceHex = "#120E29",
              elevatedSurfaceHex = "#1C163D",
              borderHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, 0.85f, 0.95f)),
              textPrimaryHex = "#F8FAFC",
              textMutedHex = "#C4B5FD",
              primaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, 0.90f, 0.98f)),
              secondaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor((priH + 155f) % 360f, 0.88f, 0.96f)),
              warmHex = "#FACC15",
              successHex = "#22F59E",
            )
          deck.copy(isDark = true, palette = ColorUtils.autoFixContrast(nextPalette), gradientEnabled = true, gradientMeshGlow = true)
        }

        PaletteMoodFilter.WARM_PAPER -> {
          val nextPalette =
            ThemePalette(
              canvasHex = "#F7F1E5",
              surfaceHex = "#FFFDF7",
              elevatedSurfaceHex = "#EDE3D0",
              borderHex = "#2B2118",
              textPrimaryHex = "#221912",
              textMutedHex = "#5C4B3B",
              primaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, priS.coerceIn(0.45f, 0.78f), 0.58f)),
              secondaryHex = "#B45309",
              warmHex = "#C27D1A",
              successHex = "#2F6F4E",
            )
          deck.copy(isDark = false, palette = ColorUtils.autoFixContrast(nextPalette), gradientEnabled = false)
        }

        PaletteMoodFilter.AMOLED_PURE -> {
          val nextPalette =
            ThemePalette(
              canvasHex = "#000000",
              surfaceHex = "#0D0F14",
              elevatedSurfaceHex = "#171B24",
              borderHex = "#2C3445",
              textPrimaryHex = "#F8FAFC",
              textMutedHex = "#94A3B8",
              primaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, priS.coerceIn(0.60f, 0.92f), 0.92f)),
              secondaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(secH, 0.75f, 0.88f)),
              warmHex = "#FBBF24",
              successHex = "#34D399",
            )
          deck.copy(isDark = true, palette = ColorUtils.autoFixContrast(nextPalette))
        }

        PaletteMoodFilter.MUTED_EARTH -> {
          val nextPalette =
            ThemePalette(
              canvasHex = "#F2EFE9",
              surfaceHex = "#FAF8F4",
              elevatedSurfaceHex = "#E4DDD2",
              borderHex = "#3A352F",
              textPrimaryHex = "#23201C",
              textMutedHex = "#5D574F",
              primaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor(priH, 0.40f, 0.48f)),
              secondaryHex = ColorUtils.colorToHex(ColorUtils.hsvToColor((priH + 35f) % 360f, 0.42f, 0.52f)),
              warmHex = "#A67C52",
              successHex = "#4A6B53",
            )
          deck.copy(isDark = false, palette = ColorUtils.autoFixContrast(nextPalette))
        }
      }

    val baseName = deck.name.substringBefore(" •").take(16)
    return next.copy(
      id = "mood_${mood.name.lowercase()}_${System.currentTimeMillis() % 10000}",
      name = "$baseName • ${mood.label}",
      tagline = "Transformed with ${mood.label} atmosphere",
      swatchIconRes = null,
    )
  }

  /**
   * Produces 7 evenly stepped tints & shades for a single color inside the Color Picker.
   */
  fun generateColorVariations(hex: String): List<String> {
    val (h, s, v) = ColorUtils.rgbToHsv(ColorUtils.parseHexColor(hex))
    val targets = listOf(0.96f, 0.84f, 0.70f, v.coerceIn(0.25f, 0.85f), 0.46f, 0.30f, 0.16f)
    return targets.mapIndexed { idx, tv ->
      val ts =
        when (idx) {
          0 -> (s * 0.22f)
          1 -> (s * 0.48f)
          2 -> (s * 0.78f)
          else -> s.coerceIn(0.15f, 0.95f)
        }
      ColorUtils.colorToHex(ColorUtils.hsvToColor(h, ts, tv))
    }
  }

  private fun hueName(hue: Float): String =
    when {
      hue < 18f || hue >= 342f -> "Crimson"
      hue < 45f -> "Amber"
      hue < 75f -> "Citrine"
      hue < 150f -> "Emerald"
      hue < 195f -> "Lagoon"
      hue < 245f -> "Cobalt"
      hue < 290f -> "Indigo"
      else -> "Orchid"
    }

  private data class Quad(val a: Float, val b: Float, val c: Float, val d: Float)
}
