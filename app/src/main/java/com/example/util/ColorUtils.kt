package com.example.util

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import com.example.model.ColorBlindnessMode
import com.example.model.FontPairing
import com.example.model.MotionFeel
import com.example.model.RandomizerLocks
import com.example.model.SurfacePhysics
import com.example.model.ThemeDeck
import com.example.model.ThemePalette
import com.example.model.TonalShade
import com.example.model.WcagAudit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

object ColorUtils {

  fun normalizeHex(input: String): String? {
    val cleaned = input.trim().removePrefix("#").uppercase()
    val valid =
      when (cleaned.length) {
        6 -> cleaned
        3 -> cleaned.map { "$it$it" }.joinToString("")
        8 -> cleaned.substring(2)
        else -> return null
      }
    return if (valid.all { it in '0'..'9' || it in 'A'..'F' }) "#$valid" else null
  }

  fun parseHexColor(hex: String, fallback: Color = Color(0xFF0F172A)): Color {
    val normalized = normalizeHex(hex) ?: return fallback
    return try {
      val rgb = normalized.removePrefix("#").toLong(16)
      Color(0xFF000000L or rgb)
    } catch (_: Exception) {
      fallback
    }
  }

  fun colorToHex(color: Color): String {
    val argb = color.toArgb()
    val r = (argb shr 16) and 0xFF
    val g = (argb shr 8) and 0xFF
    val b = argb and 0xFF
    return String.format(Locale.US, "#%02X%02X%02X", r, g, b)
  }

  fun relativeLuminance(color: Color): Double {
    fun channel(c: Float): Double {
      val v = c.toDouble()
      return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    }
    return 0.2126 * channel(color.red) +
      0.7152 * channel(color.green) +
      0.0722 * channel(color.blue)
  }

  fun contrastRatio(a: Color, b: Color): Double {
    val lumA = relativeLuminance(a)
    val lumB = relativeLuminance(b)
    val brightest = max(lumA, lumB)
    val darkest = min(lumA, lumB)
    return (brightest + 0.05) / (darkest + 0.05)
  }

  fun bestOnColor(background: Color): Color {
    val darkInk = Color(0xFF0F172A)
    val lightInk = Color(0xFFF8FAFC)
    return if (contrastRatio(darkInk, background) >= contrastRatio(lightInk, background)) {
      darkInk
    } else {
      lightInk
    }
  }

  /**
   * Generates a complete 11-step 50–950 tonal shade ramp from any base hex color.
   */
  fun generateTonalRamp(baseHex: String): List<TonalShade> {
    val base = parseHexColor(baseHex)
    val (h, s, _) = rgbToHsv(base)
    val steps =
      listOf(
        50 to Pair(s * 0.14f, 0.98f),
        100 to Pair(s * 0.24f, 0.95f),
        200 to Pair(s * 0.38f, 0.90f),
        300 to Pair(s * 0.56f, 0.84f),
        400 to Pair(s * 0.78f, 0.76f),
        500 to Pair(max(s, 0.12f).coerceAtMost(0.95f), 0.66f),
        600 to Pair(min(s * 1.05f, 0.98f), 0.54f),
        700 to Pair(min(s * 1.08f, 0.98f), 0.42f),
        800 to Pair(min(s * 1.05f, 0.95f), 0.30f),
        900 to Pair(min(s * 0.98f, 0.92f), 0.20f),
        950 to Pair(min(s * 0.90f, 0.88f), 0.11f),
      )
    return steps.map { (step, sv) ->
      TonalShade(step = step, hex = colorToHex(hsvToColor(h, sv.first.coerceIn(0f, 1f), sv.second)))
    }
  }

  fun harmonizePalette(palette: ThemePalette): ThemePalette {
    val primary = parseHexColor(palette.primaryHex)
    val (h, s, v) = rgbToHsv(primary)
    val isDark = relativeLuminance(parseHexColor(palette.canvasHex)) < 0.25
    val compSecondary = hsvToColor((h + 150f) % 360f, s.coerceIn(0.45f, 0.85f), if (isDark) max(v, 0.78f) else min(v, 0.74f))
    val compWarm = hsvToColor((h + 45f) % 360f, s.coerceIn(0.50f, 0.85f), if (isDark) 0.88f else 0.72f)
    val compSuccess = hsvToColor(154f, 0.72f, if (isDark) 0.80f else 0.58f)
    val tintedElevated =
      if (isDark) hsvToColor(h, min(s, 0.24f), 0.20f)
      else hsvToColor(h, min(s, 0.12f), 0.94f)
    return autoFixContrast(
      palette.copy(
        elevatedSurfaceHex = colorToHex(tintedElevated),
        secondaryHex = colorToHex(compSecondary),
        warmHex = colorToHex(compWarm),
        successHex = colorToHex(compSuccess),
      ),
    )
  }

  fun swapAccents(palette: ThemePalette): ThemePalette {
    return palette.copy(
      primaryHex = palette.secondaryHex,
      secondaryHex = palette.primaryHex,
    )
  }

  fun generateSmartRandomDeck(
    current: ThemeDeck,
    locks: RandomizerLocks,
    random: Random = Random.Default,
  ): ThemeDeck {
    val newPaletteAndDark: Pair<ThemePalette, Boolean> =
      if (locks.lockPalette) {
        current.palette to current.isDark
      } else {
        val hue = random.nextFloat() * 360f
        val isDark = random.nextBoolean()
        val secHue = (hue + listOf(120f, 150f, 210f)[random.nextInt(3)]) % 360f
        val warmHue = (hue + 38f) % 360f
        val primary = hsvToColor(hue, random.nextFloat() * 0.30f + 0.58f, if (isDark) 0.88f else 0.62f)
        val secondary = hsvToColor(secHue, random.nextFloat() * 0.28f + 0.55f, if (isDark) 0.86f else 0.64f)
        val warm = hsvToColor(warmHue, 0.78f, if (isDark) 0.90f else 0.74f)
        val canvas =
          if (isDark) hsvToColor(hue, 0.24f, 0.07f) else hsvToColor(hue, 0.05f, 0.975f)
        val surface =
          if (isDark) hsvToColor(hue, 0.22f, 0.13f) else Color(0xFFFFFFFF)
        val elevated =
          if (isDark) hsvToColor(hue, 0.22f, 0.19f) else hsvToColor(hue, 0.10f, 0.93f)
        val border =
          if (isDark) hsvToColor(hue, 0.36f, 0.42f) else hsvToColor(hue, 0.30f, 0.14f)
        val text = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
        val muted = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)

        val p =
          autoFixContrast(
            ThemePalette(
              canvasHex = colorToHex(canvas),
              surfaceHex = colorToHex(surface),
              elevatedSurfaceHex = colorToHex(elevated),
              borderHex = colorToHex(border),
              textPrimaryHex = colorToHex(text),
              textMutedHex = colorToHex(muted),
              primaryHex = colorToHex(primary),
              secondaryHex = colorToHex(secondary),
              warmHex = colorToHex(warm),
              successHex = if (isDark) "#34D399" else "#059669",
            ),
          )
        p to isDark
      }

    val newFont =
      if (locks.lockTypography) current.fontPairing
      else FontPairing.entries[random.nextInt(FontPairing.entries.size)]

    val newPhysics =
      if (locks.lockSurface) current.physics
      else SurfacePhysics.entries[random.nextInt(SurfacePhysics.entries.size)]

    val newRadius =
      if (locks.lockSurface) current.cornerRadiusDp
      else listOf(8, 12, 16, 20, 24)[random.nextInt(5)]

    val newMotion = MotionFeel.entries[random.nextInt(MotionFeel.entries.size)]
    val adjectives = listOf("Aura", "Prism", "Solstice", "Monolith", "Vesper", "Tactile", "Lumen", "Kinetix")
    val nouns = listOf("Studio", "Deck", "Paper", "Noir", "Drift", "Mint", "Slate", "Pulse")
    val title = "${adjectives[random.nextInt(adjectives.size)]} ${nouns[random.nextInt(nouns.size)]}"

    return current.copy(
      id = "rnd_${System.currentTimeMillis() % 100000}",
      name = title,
      tagline = "Smart randomized ${newPhysics.label.lowercase()} system",
      isDark = newPaletteAndDark.second,
      palette = newPaletteAndDark.first,
      physics = newPhysics,
      fontPairing = newFont,
      motionFeel = newMotion,
      cornerRadiusDp = newRadius,
      swatchIconRes = null,
    )
  }

  fun colorBlindnessMatrix(mode: ColorBlindnessMode): ColorMatrix? {
    val values: FloatArray =
      when (mode) {
        ColorBlindnessMode.NORMAL -> return null
        ColorBlindnessMode.PROTANOPIA ->
          floatArrayOf(
            0.567f, 0.433f, 0.0f, 0f, 0f,
            0.558f, 0.442f, 0.0f, 0f, 0f,
            0.0f, 0.242f, 0.758f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
          )
        ColorBlindnessMode.DEUTERANOPIA ->
          floatArrayOf(
            0.625f, 0.375f, 0.0f, 0f, 0f,
            0.700f, 0.300f, 0.0f, 0f, 0f,
            0.0f, 0.300f, 0.700f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
          )
        ColorBlindnessMode.TRITANOPIA ->
          floatArrayOf(
            0.950f, 0.050f, 0.0f, 0f, 0f,
            0.0f, 0.433f, 0.567f, 0f, 0f,
            0.0f, 0.475f, 0.525f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
          )
        ColorBlindnessMode.ACHROMATOPSIA ->
          floatArrayOf(
            0.299f, 0.587f, 0.114f, 0f, 0f,
            0.299f, 0.587f, 0.114f, 0f, 0f,
            0.299f, 0.587f, 0.114f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
          )
      }
    return ColorMatrix(values)
  }

  /**
   * Encodes a ThemeDeck into a compact shareable URL hash (#theme=...).
   */
  fun encodeThemeHash(deck: ThemeDeck): String {
    val p = deck.palette
    val safeName = deck.name.replace("|", " ").replace("=", " ")
    val raw =
      listOf(
        "n=$safeName",
        "d=${if (deck.isDark) 1 else 0}",
        "ph=${deck.physics.name}",
        "fp=${deck.fontPairing.name}",
        "mf=${deck.motionFeel.name}",
        "r=${deck.cornerRadiusDp}",
        "bw=${deck.borderWidthDp}",
        "g=${if (deck.gradientEnabled) 1 else 0}",
        "ga=${deck.gradientAngle}",
        "c=${p.canvasHex}",
        "s=${p.surfaceHex}",
        "e=${p.elevatedSurfaceHex}",
        "b=${p.borderHex}",
        "t=${p.textPrimaryHex}",
        "m=${p.textMutedHex}",
        "p=${p.primaryHex}",
        "sc=${p.secondaryHex}",
        "w=${p.warmHex}",
        "ok=${p.successHex}",
      ).joinToString("|")
    val bytes = raw.toByteArray(Charsets.UTF_8)
    val encoded =
      try {
        Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
      } catch (_: RuntimeException) {
        java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
      }
    return "#theme=$encoded"
  }

  fun decodeThemeHash(input: String): ThemeDeck? {
    val trimmed = input.trim()
    val payload =
      when {
        trimmed.contains("#theme=") -> trimmed.substringAfter("#theme=").trim()
        trimmed.startsWith("theme=") -> trimmed.removePrefix("theme=").trim()
        else -> trimmed
      }
    if (payload.isBlank()) return null
    return try {
      val bytes =
        try {
          Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
        } catch (_: RuntimeException) {
          java.util.Base64.getUrlDecoder().decode(payload)
        }
      val decodedStr = String(bytes, Charsets.UTF_8)
      val map =
        decodedStr
          .split("|")
          .mapNotNull { entry ->
            val idx = entry.indexOf('=')
            if (idx > 0) entry.substring(0, idx) to entry.substring(idx + 1) else null
          }
          .toMap()
      if (map["c"] == null || map["p"] == null) return null

      val surface = map["s"] ?: "#FFFFFF"
      val palette =
        ThemePalette(
          canvasHex = map["c"] ?: "#FAF9F6",
          surfaceHex = surface,
          elevatedSurfaceHex = map["e"] ?: surface,
          borderHex = map["b"] ?: "#0F172A",
          textPrimaryHex = map["t"] ?: "#0F172A",
          textMutedHex = map["m"] ?: "#475569",
          primaryHex = map["p"] ?: "#4F46E5",
          secondaryHex = map["sc"] ?: "#F43F5E",
          warmHex = map["w"] ?: "#F59E0B",
          successHex = map["ok"] ?: "#10B981",
        )
      ThemeDeck(
        id = "shared_${System.currentTimeMillis() % 100000}",
        name = map["n"]?.ifBlank { "Shared Kromdeck" } ?: "Shared Kromdeck",
        tagline = "Imported from #theme share link",
        isDark = map["d"] == "1" || map["d"] == "true",
        palette = palette,
        physics = runCatching { SurfacePhysics.valueOf(map["ph"].orEmpty()) }.getOrDefault(SurfacePhysics.HARD_INK),
        fontPairing = runCatching { FontPairing.valueOf(map["fp"].orEmpty()) }.getOrDefault(FontPairing.GEOMETRIC_SANS),
        motionFeel = runCatching { MotionFeel.valueOf(map["mf"].orEmpty()) }.getOrDefault(MotionFeel.HARD_SNAP),
        cornerRadiusDp = map["r"]?.toIntOrNull()?.coerceIn(0, 32) ?: 14,
        borderWidthDp = map["bw"]?.toFloatOrNull()?.coerceIn(0f, 3f) ?: 2f,
        gradientEnabled = map["g"] == "1" || map["g"] == "true",
        gradientAngle = map["ga"]?.toFloatOrNull() ?: 135f,
      )
    } catch (_: Exception) {
      null
    }
  }

  fun auditThemeContrast(palette: ThemePalette): WcagAudit {
    val canvas = parseHexColor(palette.canvasHex, Color(0xFFFAF9F6))
    val surface = parseHexColor(palette.surfaceHex, Color.White)
    val text = parseHexColor(palette.textPrimaryHex, Color(0xFF0F172A))
    val primary = parseHexColor(palette.primaryHex, Color(0xFF4F46E5))

    val canvasTextRatio = contrastRatio(text, canvas)
    val surfaceTextRatio = contrastRatio(text, surface)
    val primaryOnCanvasRatio = contrastRatio(primary, canvas)
    val primaryOnSurfaceRatio = contrastRatio(primary, surface)

    val worstText = min(canvasTextRatio, surfaceTextRatio)
    val worstPrimary = min(primaryOnCanvasRatio, primaryOnSurfaceRatio)
    val textPassesAA = worstText >= 4.5
    val primaryPassesUI = worstPrimary >= 3.0
    val primaryPassesAA = worstPrimary >= 4.5

    val warnings = buildList {
      if (canvasTextRatio < 4.5) {
        add(
          String.format(
            Locale.US,
            "Primary Text (%s) vs Background (%s) is %.2f:1 — needs >= 4.5:1 (WCAG AA).",
            palette.textPrimaryHex,
            palette.canvasHex,
            canvasTextRatio,
          ),
        )
      }
      if (surfaceTextRatio < 4.5) {
        add(
          String.format(
            Locale.US,
            "Primary Text (%s) vs Surface (%s) is %.2f:1 — needs >= 4.5:1 (WCAG AA).",
            palette.textPrimaryHex,
            palette.surfaceHex,
            surfaceTextRatio,
          ),
        )
      }
      if (primaryOnCanvasRatio < 3.0) {
        add(
          String.format(
            Locale.US,
            "Primary (%s) vs Background (%s) is %.2f:1 — fails 3.0:1 UI contrast.",
            palette.primaryHex,
            palette.canvasHex,
            primaryOnCanvasRatio,
          ),
        )
      }
      if (primaryOnSurfaceRatio < 3.0) {
        add(
          String.format(
            Locale.US,
            "Primary (%s) vs Surface (%s) is %.2f:1 — fails 3.0:1 UI contrast.",
            palette.primaryHex,
            palette.surfaceHex,
            primaryOnSurfaceRatio,
          ),
        )
      }
    }

    return WcagAudit(
      canvasTextRatio = canvasTextRatio,
      surfaceTextRatio = surfaceTextRatio,
      primaryOnCanvasRatio = primaryOnCanvasRatio,
      primaryOnSurfaceRatio = primaryOnSurfaceRatio,
      worstRatio = worstText,
      worstPrimaryRatio = worstPrimary,
      textPassesAA = textPassesAA,
      primaryPassesUI = primaryPassesUI,
      primaryPassesAA = primaryPassesAA,
      passesAA = textPassesAA && primaryPassesUI,
      passesLargeAA = worstText >= 3.0 && primaryPassesUI,
      warnings = warnings,
    )
  }

  fun autoFixContrast(palette: ThemePalette): ThemePalette {
    val canvas = parseHexColor(palette.canvasHex)
    val surface = parseHexColor(palette.surfaceHex)
    val text = parseHexColor(palette.textPrimaryHex)
    val primary = parseHexColor(palette.primaryHex)

    val avgLum = (relativeLuminance(canvas) + relativeLuminance(surface)) / 2.0
    val shouldBeDarkText = avgLum > 0.35

    val fixedText =
      if (contrastRatio(text, canvas) >= 4.5 && contrastRatio(text, surface) >= 4.5) {
        text
      } else {
        adjustLuminanceUntilContrast(
          base = text,
          targetA = canvas,
          targetB = surface,
          darken = shouldBeDarkText,
          minRatio = 5.2,
        )
      }

    val fixedMuted =
      adjustLuminanceUntilContrast(
        base = parseHexColor(palette.textMutedHex),
        targetA = canvas,
        targetB = surface,
        darken = shouldBeDarkText,
        minRatio = 4.6,
      )

    val fixedPrimary =
      if (contrastRatio(primary, surface) >= 4.5 && contrastRatio(primary, canvas) >= 4.5) {
        primary
      } else if (contrastRatio(primary, surface) >= 3.2 && contrastRatio(primary, canvas) >= 3.2) {
        primary
      } else {
        adjustLuminanceUntilContrast(
          base = primary,
          targetA = surface,
          targetB = canvas,
          darken = shouldBeDarkText,
          minRatio = 4.5,
        )
      }

    return palette.copy(
      textPrimaryHex = colorToHex(fixedText),
      textMutedHex = colorToHex(fixedMuted),
      primaryHex = colorToHex(fixedPrimary),
    )
  }

  private fun adjustLuminanceUntilContrast(
    base: Color,
    targetA: Color,
    targetB: Color,
    darken: Boolean,
    minRatio: Double,
  ): Color {
    var (h, s, v) = rgbToHsv(base)
    repeat(30) {
      val candidate = hsvToColor(h, s, v)
      if (contrastRatio(candidate, targetA) >= minRatio && contrastRatio(candidate, targetB) >= minRatio) {
        return candidate
      }
      if (darken) {
        v = (v - 0.04f).coerceAtLeast(0.04f)
        if (v <= 0.08f) s *= 0.85f
      } else {
        v = (v + 0.04f).coerceAtMost(0.98f)
        s *= 0.92f
      }
    }
    return if (darken) Color(0xFF0F172A) else Color(0xFFF8FAFC)
  }

  fun generateCompanionDeck(deck: ThemeDeck): ThemeDeck {
    val p = deck.palette
    val currentlyDark = deck.isDark || relativeLuminance(parseHexColor(p.canvasHex)) < 0.25

    val newPalette =
      if (currentlyDark) {
        val canvas = shiftHsl(parseHexColor(p.canvasHex), targetSatMax = 0.08f, targetVal = 0.975f)
        val surface = Color(0xFFFFFFFF)
        val elevated = shiftHsl(parseHexColor(p.primaryHex), targetSatMax = 0.10f, targetVal = 0.93f)
        val text = shiftHsl(parseHexColor(p.canvasHex), targetSatMax = 0.35f, targetVal = 0.14f)
        val muted = shiftHsl(parseHexColor(p.canvasHex), targetSatMax = 0.22f, targetVal = 0.40f)
        val border = shiftHsl(parseHexColor(p.borderHex), targetSatMax = 0.35f, targetVal = 0.18f)
        val primary = ensureAccentContrast(parseHexColor(p.primaryHex), surface, darken = true)
        val secondary = ensureAccentContrast(parseHexColor(p.secondaryHex), surface, darken = true)
        val warm = ensureAccentContrast(parseHexColor(p.warmHex), surface, darken = true)
        val success = ensureAccentContrast(parseHexColor(p.successHex), surface, darken = true)
        ThemePalette(
          canvasHex = colorToHex(canvas),
          surfaceHex = colorToHex(surface),
          elevatedSurfaceHex = colorToHex(elevated),
          borderHex = colorToHex(border),
          textPrimaryHex = colorToHex(text),
          textMutedHex = colorToHex(muted),
          primaryHex = colorToHex(primary),
          secondaryHex = colorToHex(secondary),
          warmHex = colorToHex(warm),
          successHex = colorToHex(success),
        )
      } else {
        val canvas = shiftHsl(parseHexColor(p.primaryHex), targetSatMax = 0.24f, targetVal = 0.08f)
        val surface = shiftHsl(parseHexColor(p.primaryHex), targetSatMax = 0.22f, targetVal = 0.14f)
        val elevated = shiftHsl(parseHexColor(p.primaryHex), targetSatMax = 0.24f, targetVal = 0.21f)
        val text = Color(0xFFF8FAFC)
        val muted = Color(0xFFCBD5E1)
        val border = shiftHsl(parseHexColor(p.primaryHex), targetSatMax = 0.35f, targetVal = 0.38f)
        val primary = ensureAccentContrast(parseHexColor(p.primaryHex), surface, darken = false)
        val secondary = ensureAccentContrast(parseHexColor(p.secondaryHex), surface, darken = false)
        val warm = ensureAccentContrast(parseHexColor(p.warmHex), surface, darken = false)
        val success = ensureAccentContrast(parseHexColor(p.successHex), surface, darken = false)
        ThemePalette(
          canvasHex = colorToHex(canvas),
          surfaceHex = colorToHex(surface),
          elevatedSurfaceHex = colorToHex(elevated),
          borderHex = colorToHex(border),
          textPrimaryHex = colorToHex(text),
          textMutedHex = colorToHex(muted),
          primaryHex = colorToHex(primary),
          secondaryHex = colorToHex(secondary),
          warmHex = colorToHex(warm),
          successHex = colorToHex(success),
        )
      }

    val companionSuffix = if (currentlyDark) "Light" else "Dark"
    val cleanBaseName =
      deck.name
        .removeSuffix(" (Dark)")
        .removeSuffix(" (Light)")
        .removeSuffix(" Dark")
        .removeSuffix(" Light")
    return deck.copy(
      id = "${deck.id}_${companionSuffix.lowercase()}",
      name = "$cleanBaseName ($companionSuffix)",
      tagline = "Auto-generated $companionSuffix companion",
      isDark = !currentlyDark,
      palette = autoFixContrast(newPalette),
    )
  }

  private fun shiftHsl(color: Color, targetSatMax: Float, targetVal: Float): Color {
    val (h, s, _) = rgbToHsv(color)
    return hsvToColor(h, min(s, targetSatMax), targetVal)
  }

  private fun ensureAccentContrast(accent: Color, bg: Color, darken: Boolean): Color {
    var (h, s, v) = rgbToHsv(accent)
    if (!darken) {
      v = max(v, 0.85f)
      s = s.coerceIn(0.45f, 0.85f)
    } else {
      v = min(v, 0.72f)
    }
    var candidate = hsvToColor(h, s, v)
    repeat(12) {
      if (contrastRatio(candidate, bg) >= 3.2) return candidate
      v = if (darken) (v - 0.05f).coerceAtLeast(0.20f) else (v + 0.05f).coerceAtMost(0.98f)
      candidate = hsvToColor(h, s, v)
    }
    return candidate
  }

  fun extractPaletteFromBitmap(bitmap: Bitmap): Pair<ThemePalette, List<String>> {
    val scaled = Bitmap.createScaledBitmap(bitmap, 48, 48, true)
    val buckets = HashMap<Int, Int>()
    val pixels = IntArray(48 * 48)
    scaled.getPixels(pixels, 0, 48, 0, 0, 48, 48)

    for (px in pixels) {
      val a = android.graphics.Color.alpha(px)
      if (a < 128) continue
      val r = (android.graphics.Color.red(px) / 24) * 24 + 12
      val g = (android.graphics.Color.green(px) / 24) * 24 + 12
      val b = (android.graphics.Color.blue(px) / 24) * 24 + 12
      val quantized = android.graphics.Color.rgb(r.coerceAtMost(255), g.coerceAtMost(255), b.coerceAtMost(255))
      buckets[quantized] = (buckets[quantized] ?: 0) + 1
    }

    val sortedColors =
      buckets.entries
        .sortedByDescending { it.value }
        .map { Color(it.key) }
        .distinctBy { colorToHex(it) }

    val topSwatches = sortedColors.take(6).map { colorToHex(it) }

    fun sat(c: Color): Float = rgbToHsv(c).second
    fun lum(c: Color): Double = relativeLuminance(c)

    val lightCandidate = sortedColors.firstOrNull { lum(it) > 0.68 } ?: Color(0xFFFAF9F6)
    val darkCandidate = sortedColors.firstOrNull { lum(it) < 0.16 } ?: Color(0xFF0F172A)

    val dominantIsDark = sortedColors.take(3).count { lum(it) < 0.28 } >= 2
    val canvas =
      if (dominantIsDark) shiftHsl(darkCandidate, 0.22f, 0.08f)
      else shiftHsl(lightCandidate, 0.10f, 0.97f)
    val surface =
      if (dominantIsDark) shiftHsl(darkCandidate, 0.24f, 0.15f)
      else Color(0xFFFFFFFF)
    val elevated =
      if (dominantIsDark) shiftHsl(darkCandidate, 0.24f, 0.22f)
      else shiftHsl(lightCandidate, 0.12f, 0.93f)

    val vibrant =
      sortedColors
        .filter { sat(it) >= 0.28f && lum(it) in 0.08..0.82 }
        .sortedByDescending { sat(it) * (1.0f - abs(it.luminance() - 0.5f)) }

    val primary = vibrant.getOrNull(0) ?: Color(0xFF4F46E5)
    val secondary = vibrant.firstOrNull { colorDistance(it, primary) > 90.0 } ?: vibrant.getOrNull(1) ?: Color(0xFFF43F5E)
    val warm =
      vibrant.firstOrNull { colorDistance(it, primary) > 70.0 && colorDistance(it, secondary) > 70.0 }
        ?: Color(0xFFF59E0B)

    val textPrimary = if (dominantIsDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textMuted = if (dominantIsDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val border = if (dominantIsDark) shiftHsl(primary, 0.45f, 0.55f) else Color(0xFF0F172A)

    val rawPalette =
      ThemePalette(
        canvasHex = colorToHex(canvas),
        surfaceHex = colorToHex(surface),
        elevatedSurfaceHex = colorToHex(elevated),
        borderHex = colorToHex(border),
        textPrimaryHex = colorToHex(textPrimary),
        textMutedHex = colorToHex(textMuted),
        primaryHex = colorToHex(primary),
        secondaryHex = colorToHex(secondary),
        warmHex = colorToHex(warm),
        successHex = "#10B981",
      )

    return autoFixContrast(rawPalette) to topSwatches.ifEmpty { listOf("#4F46E5", "#F43F5E", "#F59E0B", "#10B981") }
  }

  private fun colorDistance(a: Color, b: Color): Double {
    val dr = (a.red - b.red) * 255.0
    val dg = (a.green - b.green) * 255.0
    val db = (a.blue - b.blue) * 255.0
    return kotlin.math.sqrt(dr * dr + dg * dg + db * db)
  }

  fun rgbToHsv(color: Color): Triple<Float, Float, Float> {
    val r = color.red
    val g = color.green
    val b = color.blue
    val maxC = max(r, max(g, b))
    val minC = min(r, min(g, b))
    val delta = maxC - minC

    val h =
      when {
        delta == 0f -> 0f
        maxC == r -> 60f * (((g - b) / delta) % 6f)
        maxC == g -> 60f * (((b - r) / delta) + 2f)
        else -> 60f * (((r - g) / delta) + 4f)
      }.let { if (it < 0f) it + 360f else it }

    val s = if (maxC == 0f) 0f else delta / maxC
    val v = maxC
    return Triple(h, s, v)
  }

  fun hsvToColor(h: Float, s: Float, v: Float): Color {
    val hh = ((h % 360f) + 360f) % 360f
    val ss = s.coerceIn(0f, 1f)
    val vv = v.coerceIn(0f, 1f)
    val c = vv * ss
    val x = c * (1f - abs((hh / 60f) % 2f - 1f))
    val m = vv - c
    val (r1, g1, b1) =
      when {
        hh < 60f -> Triple(c, x, 0f)
        hh < 120f -> Triple(x, c, 0f)
        hh < 180f -> Triple(0f, c, x)
        hh < 240f -> Triple(0f, x, c)
        hh < 300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
      }
    return Color(
      red = (r1 + m).coerceIn(0f, 1f),
      green = (g1 + m).coerceIn(0f, 1f),
      blue = (b1 + m).coerceIn(0f, 1f),
      alpha = 1f,
    )
  }
}
