package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.toArgb
import com.example.model.ExportFormat
import com.example.model.SurfacePhysics
import com.example.model.ThemeDeck
import java.util.Locale

object ExportGenerator {

  fun generate(deck: ThemeDeck, format: ExportFormat): String {
    return when (format) {
      ExportFormat.JSON_TOKENS -> generateJsonTokens(deck)
      ExportFormat.XML_TOKENS -> generateXmlTokens(deck)
      ExportFormat.JETPACK_COMPOSE -> generateJetpackComposeTheme(deck)
      ExportFormat.CSS_TOKENS -> generateCssTokens(deck)
      ExportFormat.TAILWIND_V4 -> generateTailwindV4(deck)
      ExportFormat.FLUTTER_DART -> generateFlutterThemeData(deck)
      ExportFormat.SWIFTUI_THEME -> generateSwiftUiTheme(deck)
      ExportFormat.AI_PROMPT -> generateAiPrompt(deck)
    }
  }

  fun suggestedFileName(deck: ThemeDeck, format: ExportFormat): String {
    val slug =
      deck.name
        .lowercase(Locale.US)
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
        .ifEmpty { "kromdeck_theme" }
    val suffix =
      when (format) {
        ExportFormat.JSON_TOKENS -> "tokens"
        ExportFormat.XML_TOKENS -> "theme_resources"
        ExportFormat.JETPACK_COMPOSE -> "Theme"
        ExportFormat.CSS_TOKENS -> "theme"
        ExportFormat.TAILWIND_V4 -> "tailwind_v4"
        ExportFormat.FLUTTER_DART -> "theme_data"
        ExportFormat.SWIFTUI_THEME -> "Theme"
        ExportFormat.AI_PROMPT -> "ai_prompt"
      }
    return "${slug}_${suffix}.${format.fileExtension}"
  }

  fun suggestedSpecCardName(deck: ThemeDeck, ext: String): String {
    val slug =
      deck.name
        .lowercase(Locale.US)
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
        .ifEmpty { "kromdeck_shot" }
    return "${slug}_spec_card.$ext"
  }

  private fun physicsSpec(physics: SurfacePhysics): String =
    when (physics) {
      SurfacePhysics.HARD_INK -> "Neo-Brutalist solid ink border with crisp hard offset shadow; translate(2px, 2px) on active press"
      SurfacePhysics.PUFFY_CLAY -> "Soft elevated clay cards with plush ambient shadow and smooth rounded corners"
      SurfacePhysics.FROSTED_GLASS -> "Glassmorphism translucent surface with backdrop blur and luminous inner hairline"
      SurfacePhysics.ULTRA_THIN -> "Outlined editorial surface with crisp 1px structural hairline and zero drop shadow"
      SurfacePhysics.SOFT_NEUMORPHIC -> "Soft neumorphic extruded bevels with dual ambient shadows"
      SurfacePhysics.FLAT -> "Flat zero-elevation surface planes with crisp visual rhythm"
    }

  private fun shadowCss(deck: ThemeDeck, borderHex: String): String =
    when (deck.physics) {
      SurfacePhysics.HARD_INK -> "4px 4px 0px $borderHex"
      SurfacePhysics.SOFT_NEUMORPHIC -> "6px 6px 14px rgba(15, 23, 42, 0.12), -4px -4px 12px rgba(255, 255, 255, 0.65)"
      SurfacePhysics.FROSTED_GLASS -> "0 8px 28px rgba(15, 23, 42, 0.18)"
      SurfacePhysics.ULTRA_THIN, SurfacePhysics.FLAT -> "none"
      SurfacePhysics.PUFFY_CLAY -> "0 10px 24px rgba(15, 23, 42, 0.14), inset 0 2px 4px rgba(255, 255, 255, 0.45)"
    }

  private fun generateAiPrompt(deck: ThemeDeck): String {
    val p = deck.palette
    val companion = ColorUtils.generateCompanionDeck(deck)
    val cp = companion.palette

    return buildString {
      appendLine("Restyle this application's visual theme using the \"${deck.name}\" design system without modifying business logic or navigation flows.")
      appendLine()
      appendLine("## 1. Core Color Tokens (${if (deck.isDark) "Dark" else "Light"} Mode)")
      appendLine("- Background: ${p.canvasHex} | Card Surface: ${p.surfaceHex} | Elevated: ${p.elevatedSurfaceHex}")
      appendLine("- Primary Text: ${p.textPrimaryHex} | Muted Text: ${p.textMutedHex} | Border: ${p.borderHex}")
      appendLine("- Primary Accent: ${p.primaryHex} | Secondary Accent: ${p.secondaryHex}")
      appendLine("- Warm Highlight: ${p.warmHex} | Success: ${p.successHex}")
      if (deck.gradientEnabled) {
        appendLine("- Gradient Backdrop: linear-gradient(${deck.gradientAngle.toInt()}deg, ${p.primaryHex}, ${p.secondaryHex}) (Mesh Glow: ${deck.gradientMeshGlow})")
      }
      appendLine()
      appendLine("## 2. Companion Mode (${if (companion.isDark) "Dark" else "Light"})")
      appendLine("- Background: ${cp.canvasHex} | Surface: ${cp.surfaceHex} | Text: ${cp.textPrimaryHex} | Primary: ${cp.primaryHex}")
      appendLine()
      appendLine("## 3. Surface, Typography & Motion")
      appendLine("- Surface (${deck.physics.label}): ${physicsSpec(deck.physics)}.")
      appendLine("- Geometry: cornerRadius=${deck.cornerRadiusDp}px, cardPadding=${deck.cardPaddingDp}px, borderWidth=${deck.borderWidthDp}px.")
      appendLine("- Typography (${deck.fontPairing.label}): Display=\"${deck.fontPairing.displayLabel}\", Body=\"${deck.fontPairing.bodyLabel}\", Base=${deck.baseFontSizeSp.toInt()}px, Tracking=${deck.letterSpacingSp}px.")
      appendLine("- Layout: NavShell=${deck.navShell.label}, Density=${deck.gridDensity.label}.")
      appendLine("- Motion (${deck.motionFeel.label}): ${deck.motionFeel.description} (${deck.motionFeel.durationMs}ms).")
      appendLine()
      appendLine("## 4. Accessibility Guardrails")
      appendLine("- Maintain >= 4.5:1 text contrast and >= 3.0:1 primary UI contrast against backgrounds.")
    }.trim()
  }

  private fun generateCssTokens(deck: ThemeDeck): String {
    val lightDeck = if (deck.isDark) ColorUtils.generateCompanionDeck(deck) else deck
    val darkDeck = if (deck.isDark) deck else ColorUtils.generateCompanionDeck(deck)
    val l = lightDeck.palette
    val d = darkDeck.palette
    val ramp = ColorUtils.generateTonalRamp(deck.palette.primaryHex)

    return buildString {
      appendLine("/* Kromdeck CSS Custom Properties — ${deck.name} */")
      appendLine(":root {")
      appendLine("  --kd-bg: ${l.canvasHex};")
      appendLine("  --kd-surface: ${l.surfaceHex};")
      appendLine("  --kd-elevated: ${l.elevatedSurfaceHex};")
      appendLine("  --kd-border: ${l.borderHex};")
      appendLine("  --kd-text: ${l.textPrimaryHex};")
      appendLine("  --kd-text-muted: ${l.textMutedHex};")
      appendLine("  --kd-primary: ${l.primaryHex};")
      appendLine("  --kd-secondary: ${l.secondaryHex};")
      appendLine("  --kd-warm: ${l.warmHex};")
      appendLine("  --kd-success: ${l.successHex};")
      ramp.forEach { shade -> appendLine("  --kd-primary-${shade.step}: ${shade.hex};") }
      appendLine("  --kd-font-display: \"${deck.fontPairing.displayLabel}\", system-ui, sans-serif;")
      appendLine("  --kd-font-body: \"${deck.fontPairing.bodyLabel}\", system-ui, sans-serif;")
      appendLine("  --kd-radius: ${deck.cornerRadiusDp}px;")
      appendLine("  --kd-padding: ${deck.cardPaddingDp}px;")
      appendLine("  --kd-border-width: ${deck.borderWidthDp}px;")
      appendLine("  --kd-shadow: ${shadowCss(deck, l.borderHex)};")
      appendLine("  --kd-duration: ${deck.motionFeel.durationMs}ms;")
      appendLine("}")
      appendLine()
      appendLine("[data-theme=\"dark\"] {")
      appendLine("  --kd-bg: ${d.canvasHex};")
      appendLine("  --kd-surface: ${d.surfaceHex};")
      appendLine("  --kd-elevated: ${d.elevatedSurfaceHex};")
      appendLine("  --kd-border: ${d.borderHex};")
      appendLine("  --kd-text: ${d.textPrimaryHex};")
      appendLine("  --kd-text-muted: ${d.textMutedHex};")
      appendLine("  --kd-primary: ${d.primaryHex};")
      appendLine("  --kd-secondary: ${d.secondaryHex};")
      appendLine("  --kd-shadow: ${shadowCss(deck, d.borderHex)};")
      appendLine("}")
    }.trim()
  }

  private fun generateTailwindV4(deck: ThemeDeck): String {
    val p = deck.palette
    val ramp = ColorUtils.generateTonalRamp(p.primaryHex)
    return buildString {
      appendLine("/* Tailwind CSS v4 @theme — ${deck.name} */")
      appendLine("@import \"tailwindcss\";")
      appendLine()
      appendLine("@theme {")
      appendLine("  --color-canvas: ${p.canvasHex};")
      appendLine("  --color-surface: ${p.surfaceHex};")
      appendLine("  --color-elevated: ${p.elevatedSurfaceHex};")
      appendLine("  --color-ink: ${p.borderHex};")
      appendLine("  --color-text-primary: ${p.textPrimaryHex};")
      appendLine("  --color-text-muted: ${p.textMutedHex};")
      appendLine("  --color-primary: ${p.primaryHex};")
      appendLine("  --color-secondary: ${p.secondaryHex};")
      appendLine("  --color-warm: ${p.warmHex};")
      appendLine("  --color-success: ${p.successHex};")
      ramp.forEach { shade -> appendLine("  --color-brand-${shade.step}: ${shade.hex};") }
      appendLine("  --font-display: \"${deck.fontPairing.displayLabel}\", sans-serif;")
      appendLine("  --font-body: \"${deck.fontPairing.bodyLabel}\", sans-serif;")
      appendLine("  --radius-card: ${deck.cornerRadiusDp}px;")
      appendLine("  --shadow-card: ${shadowCss(deck, p.borderHex)};")
      appendLine("}")
    }.trim()
  }

  private fun generateFlutterThemeData(deck: ThemeDeck): String {
    val lightDeck = if (deck.isDark) ColorUtils.generateCompanionDeck(deck) else deck
    val darkDeck = if (deck.isDark) deck else ColorUtils.generateCompanionDeck(deck)
    fun dartColor(hex: String) = "Color(0xFF${hex.removePrefix("#").uppercase()})"
    val l = lightDeck.palette
    val d = darkDeck.palette

    return """
// Flutter ThemeData — ${deck.name}
import 'package:flutter/material.dart';

class KromdeckThemeData {
  static ThemeData light() => ThemeData(
    useMaterial3: true,
    brightness: Brightness.light,
    scaffoldBackgroundColor: const ${dartColor(l.canvasHex)},
    fontFamily: '${deck.fontPairing.bodyLabel}',
    colorScheme: const ColorScheme.light(
      primary: ${dartColor(l.primaryHex)},
      secondary: ${dartColor(l.secondaryHex)},
      tertiary: ${dartColor(l.warmHex)},
      surface: ${dartColor(l.surfaceHex)},
      onSurface: ${dartColor(l.textPrimaryHex)},
      outline: ${dartColor(l.borderHex)},
    ),
    cardTheme: CardThemeData(
      color: const ${dartColor(l.surfaceHex)},
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(${deck.cornerRadiusDp}.0),
        side: const BorderSide(color: ${dartColor(l.borderHex)}, width: ${deck.borderWidthDp}),
      ),
    ),
  );

  static ThemeData dark() => ThemeData(
    useMaterial3: true,
    brightness: Brightness.dark,
    scaffoldBackgroundColor: const ${dartColor(d.canvasHex)},
    fontFamily: '${deck.fontPairing.bodyLabel}',
    colorScheme: const ColorScheme.dark(
      primary: ${dartColor(d.primaryHex)},
      secondary: ${dartColor(d.secondaryHex)},
      tertiary: ${dartColor(d.warmHex)},
      surface: ${dartColor(d.surfaceHex)},
      onSurface: ${dartColor(d.textPrimaryHex)},
      outline: ${dartColor(d.borderHex)},
    ),
  );
}
""".trim()
  }

  private fun generateJsonTokens(deck: ThemeDeck): String {
    val lightDeck = if (deck.isDark) ColorUtils.generateCompanionDeck(deck) else deck
    val darkDeck = if (deck.isDark) deck else ColorUtils.generateCompanionDeck(deck)
    val l = lightDeck.palette
    val d = darkDeck.palette
    val audit = ColorUtils.auditThemeContrast(deck.palette)
    val primaryRamp = ColorUtils.generateTonalRamp(deck.palette.primaryHex)
    val secondaryRamp = ColorUtils.generateTonalRamp(deck.palette.secondaryHex)
    val neutralRamp = ColorUtils.generateTonalRamp(deck.palette.borderHex)
    val safeName = deck.name.replace("\"", "\\\"")
    val mult = deck.gridDensity.spacingMultiplier
    val base = deck.baseFontSizeSp.toInt()

    fun formatRamp(shades: List<com.example.model.TonalShade>): String =
      shades.joinToString(",\n") { s ->
        "      \"${s.step}\": { \"\$value\": \"${s.hex}\", \"\$type\": \"color\" }"
      }

    val spacingEntries =
      listOf(4, 8, 12, 16, 24, 32, 48).joinToString(",\n") { unit ->
        val scaled = (unit * mult).toInt().coerceAtLeast(2)
        "    \"space-$unit\": { \"\$value\": \"${scaled}px\", \"\$type\": \"dimension\" }"
      }

    return """
{
  "${"$"}schema": "https://tr.designtokens.org/format/",
  "meta": {
    "name": "$safeName",
    "activeMode": "${if (deck.isDark) "dark" else "light"}",
    "surfacePhysics": "${deck.physics.name}",
    "motionFeel": "${deck.motionFeel.name}",
    "motionDurationMs": ${deck.motionFeel.durationMs},
    "navShell": "${deck.navShell.name}",
    "gridDensity": "${deck.gridDensity.name}",
    "wcag": {
      "textOnBackgroundRatio": ${String.format(Locale.US, "%.2f", audit.worstRatio)},
      "primaryOnBackgroundRatio": ${String.format(Locale.US, "%.2f", audit.worstPrimaryRatio)},
      "passesAA": ${audit.passesAA}
    }
  },
  "color": {
    "primaryScale": {
${formatRamp(primaryRamp)}
    },
    "secondaryScale": {
${formatRamp(secondaryRamp)}
    },
    "neutralScale": {
${formatRamp(neutralRamp)}
    },
    "light": {
      "canvas": { "${"$"}value": "${l.canvasHex}", "${"$"}type": "color" },
      "surface": { "${"$"}value": "${l.surfaceHex}", "${"$"}type": "color" },
      "elevated": { "${"$"}value": "${l.elevatedSurfaceHex}", "${"$"}type": "color" },
      "border": { "${"$"}value": "${l.borderHex}", "${"$"}type": "color" },
      "textPrimary": { "${"$"}value": "${l.textPrimaryHex}", "${"$"}type": "color" },
      "textMuted": { "${"$"}value": "${l.textMutedHex}", "${"$"}type": "color" },
      "primary": { "${"$"}value": "${l.primaryHex}", "${"$"}type": "color" },
      "secondary": { "${"$"}value": "${l.secondaryHex}", "${"$"}type": "color" },
      "warm": { "${"$"}value": "${l.warmHex}", "${"$"}type": "color" },
      "success": { "${"$"}value": "${l.successHex}", "${"$"}type": "color" }
    },
    "dark": {
      "canvas": { "${"$"}value": "${d.canvasHex}", "${"$"}type": "color" },
      "surface": { "${"$"}value": "${d.surfaceHex}", "${"$"}type": "color" },
      "elevated": { "${"$"}value": "${d.elevatedSurfaceHex}", "${"$"}type": "color" },
      "border": { "${"$"}value": "${d.borderHex}", "${"$"}type": "color" },
      "textPrimary": { "${"$"}value": "${d.textPrimaryHex}", "${"$"}type": "color" },
      "textMuted": { "${"$"}value": "${d.textMutedHex}", "${"$"}type": "color" },
      "primary": { "${"$"}value": "${d.primaryHex}", "${"$"}type": "color" },
      "secondary": { "${"$"}value": "${d.secondaryHex}", "${"$"}type": "color" },
      "warm": { "${"$"}value": "${d.warmHex}", "${"$"}type": "color" },
      "success": { "${"$"}value": "${d.successHex}", "${"$"}type": "color" }
    }
  },
  "typography": {
    "displayFont": { "${"$"}value": "${deck.fontPairing.displayLabel}", "${"$"}type": "fontFamily" },
    "bodyFont": { "${"$"}value": "${deck.fontPairing.bodyLabel}", "${"$"}type": "fontFamily" },
    "displaySize": { "${"$"}value": "${base + 14}px", "${"$"}type": "dimension" },
    "h1Size": { "${"$"}value": "${base + 8}px", "${"$"}type": "dimension" },
    "h2Size": { "${"$"}value": "${base + 4}px", "${"$"}type": "dimension" },
    "baseSize": { "${"$"}value": "${base}px", "${"$"}type": "dimension" },
    "captionSize": { "${"$"}value": "${(base - 2).coerceAtLeast(10)}px", "${"$"}type": "dimension" },
    "letterSpacing": { "${"$"}value": "${String.format(Locale.US, "%.1f", deck.letterSpacingSp)}px", "${"$"}type": "dimension" },
    "lineHeight": { "${"$"}value": ${String.format(Locale.US, "%.2f", deck.lineHeightMultiplier)}, "${"$"}type": "number" }
  },
  "spacing": {
$spacingEntries
  },
  "surface": {
    "radiusSm": { "${"$"}value": "${(deck.cornerRadiusDp / 2).coerceAtLeast(2)}px", "${"$"}type": "dimension" },
    "radiusMd": { "${"$"}value": "${deck.cornerRadiusDp}px", "${"$"}type": "dimension" },
    "radiusLg": { "${"$"}value": "${(deck.cornerRadiusDp + 8).coerceAtMost(32)}px", "${"$"}type": "dimension" },
    "padding": { "${"$"}value": "${deck.cardPaddingDp}px", "${"$"}type": "dimension" },
    "borderWidth": { "${"$"}value": "${String.format(Locale.US, "%.1f", deck.borderWidthDp)}px", "${"$"}type": "dimension" },
    "shadowIntensity": { "${"$"}value": ${String.format(Locale.US, "%.2f", deck.shadowIntensity)}, "${"$"}type": "number" },
    "shadow": { "${"$"}value": "${shadowCss(deck, deck.palette.borderHex)}", "${"$"}type": "shadow" }
  }
}
""".trim()
  }

  private fun generateXmlTokens(deck: ThemeDeck): String {
    val lightDeck = if (deck.isDark) ColorUtils.generateCompanionDeck(deck) else deck
    val darkDeck = if (deck.isDark) deck else ColorUtils.generateCompanionDeck(deck)
    val l = lightDeck.palette
    val d = darkDeck.palette
    val primaryRamp = ColorUtils.generateTonalRamp(deck.palette.primaryHex)
    val mult = deck.gridDensity.spacingMultiplier
    val escapedName =
      deck.name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    val rampXml =
      primaryRamp.joinToString("\n") { s ->
        "    <color name=\"kd_primary_${s.step}\">${s.hex}</color>"
      }

    val spacingXml =
      listOf(4, 8, 12, 16, 24, 32, 48).joinToString("\n") { unit ->
        val scaled = (unit * mult).toInt().coerceAtLeast(2)
        "    <dimen name=\"kd_space_$unit\">${scaled}dp</dimen>"
      }

    return """
<?xml version="1.0" encoding="utf-8"?>
<!-- Kromdeck Design System Tokens: $escapedName -->
<resources>
    <!-- Active Primary 50-950 Tonal Scale -->
$rampXml

    <!-- Light Mode Semantic Colors -->
    <color name="kd_light_canvas">${l.canvasHex}</color>
    <color name="kd_light_surface">${l.surfaceHex}</color>
    <color name="kd_light_elevated">${l.elevatedSurfaceHex}</color>
    <color name="kd_light_border">${l.borderHex}</color>
    <color name="kd_light_text_primary">${l.textPrimaryHex}</color>
    <color name="kd_light_text_muted">${l.textMutedHex}</color>
    <color name="kd_light_primary">${l.primaryHex}</color>
    <color name="kd_light_secondary">${l.secondaryHex}</color>
    <color name="kd_light_warm">${l.warmHex}</color>
    <color name="kd_light_success">${l.successHex}</color>

    <!-- Dark Companion Semantic Colors -->
    <color name="kd_dark_canvas">${d.canvasHex}</color>
    <color name="kd_dark_surface">${d.surfaceHex}</color>
    <color name="kd_dark_elevated">${d.elevatedSurfaceHex}</color>
    <color name="kd_dark_border">${d.borderHex}</color>
    <color name="kd_dark_text_primary">${d.textPrimaryHex}</color>
    <color name="kd_dark_text_muted">${d.textMutedHex}</color>
    <color name="kd_dark_primary">${d.primaryHex}</color>
    <color name="kd_dark_secondary">${d.secondaryHex}</color>
    <color name="kd_dark_warm">${d.warmHex}</color>
    <color name="kd_dark_success">${d.successHex}</color>

    <!-- Spacing & Radius Dimensions -->
$spacingXml
    <dimen name="kd_radius_sm">${(deck.cornerRadiusDp / 2).coerceAtLeast(2)}dp</dimen>
    <dimen name="kd_card_corner_radius">${deck.cornerRadiusDp}dp</dimen>
    <dimen name="kd_radius_lg">${(deck.cornerRadiusDp + 8).coerceAtMost(32)}dp</dimen>
    <dimen name="kd_card_padding">${deck.cardPaddingDp}dp</dimen>
    <dimen name="kd_card_border_width">${String.format(Locale.US, "%.1f", deck.borderWidthDp)}dp</dimen>
    <dimen name="kd_text_base">${deck.baseFontSizeSp.toInt()}sp</dimen>

    <!-- Typography & Motion Metadata -->
    <string name="kd_theme_name">$escapedName</string>
    <string name="kd_font_display">${deck.fontPairing.displayLabel}</string>
    <string name="kd_font_body">${deck.fontPairing.bodyLabel}</string>
    <string name="kd_surface_physics">${deck.physics.name}</string>
    <string name="kd_motion_feel">${deck.motionFeel.name}</string>
    <integer name="kd_motion_duration_ms">${deck.motionFeel.durationMs}</integer>
</resources>
""".trim()
  }

  private fun generateJetpackComposeTheme(deck: ThemeDeck): String {
    val lightDeck = if (deck.isDark) ColorUtils.generateCompanionDeck(deck) else deck
    val darkDeck = if (deck.isDark) deck else ColorUtils.generateCompanionDeck(deck)
    fun ktColor(hex: String) = "Color(0xFF${hex.removePrefix("#").uppercase()})"
    val l = lightDeck.palette
    val d = darkDeck.palette
    return """
// Jetpack Compose Material 3 Theme — ${deck.name}
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val KromdeckLightScheme = lightColorScheme(
  primary = ${ktColor(l.primaryHex)},
  secondary = ${ktColor(l.secondaryHex)},
  tertiary = ${ktColor(l.warmHex)},
  background = ${ktColor(l.canvasHex)},
  surface = ${ktColor(l.surfaceHex)},
  surfaceVariant = ${ktColor(l.elevatedSurfaceHex)},
  onBackground = ${ktColor(l.textPrimaryHex)},
  onSurface = ${ktColor(l.textPrimaryHex)},
  onSurfaceVariant = ${ktColor(l.textMutedHex)},
  outline = ${ktColor(l.borderHex)},
)

val KromdeckDarkScheme = darkColorScheme(
  primary = ${ktColor(d.primaryHex)},
  secondary = ${ktColor(d.secondaryHex)},
  tertiary = ${ktColor(d.warmHex)},
  background = ${ktColor(d.canvasHex)},
  surface = ${ktColor(d.surfaceHex)},
  surfaceVariant = ${ktColor(d.elevatedSurfaceHex)},
  onBackground = ${ktColor(d.textPrimaryHex)},
  onSurface = ${ktColor(d.textPrimaryHex)},
  onSurfaceVariant = ${ktColor(d.textMutedHex)},
  outline = ${ktColor(d.borderHex)},
)

val KromdeckShapes = Shapes(
  small = RoundedCornerShape(${(deck.cornerRadiusDp / 2).coerceAtLeast(2)}.dp),
  medium = RoundedCornerShape(${deck.cornerRadiusDp}.dp),
  large = RoundedCornerShape(${(deck.cornerRadiusDp + 8).coerceAtMost(32)}.dp),
)
""".trim()
  }

  private fun generateSwiftUiTheme(deck: ThemeDeck): String {
    val p = deck.palette
    fun swiftRgb(hex: String): String {
      val c = ColorUtils.parseHexColor(hex)
      return String.format(Locale.US, "Color(red: %.3f, green: %.3f, blue: %.3f)", c.red, c.green, c.blue)
    }
    return """
// SwiftUI Design Tokens — ${deck.name}
import SwiftUI

public enum KromdeckTheme {
  public static let canvas = ${swiftRgb(p.canvasHex)}
  public static let surface = ${swiftRgb(p.surfaceHex)}
  public static let elevated = ${swiftRgb(p.elevatedSurfaceHex)}
  public static let border = ${swiftRgb(p.borderHex)}
  public static let textPrimary = ${swiftRgb(p.textPrimaryHex)}
  public static let textMuted = ${swiftRgb(p.textMutedHex)}
  public static let primary = ${swiftRgb(p.primaryHex)}
  public static let secondary = ${swiftRgb(p.secondaryHex)}
  public static let warm = ${swiftRgb(p.warmHex)}
  public static let success = ${swiftRgb(p.successHex)}
  public static let cornerRadius: CGFloat = ${deck.cornerRadiusDp}
  public static let cardPadding: CGFloat = ${deck.cardPaddingDp}
  public static let borderWidth: CGFloat = ${String.format(Locale.US, "%.1f", deck.borderWidthDp)}
}
""".trim()
  }

  /**
   * Generates a stylized Dribbble Presentation Spec Card in clean SVG format.
   */
  fun generateSpecCardSvg(deck: ThemeDeck, appName: String, tagline: String): String {
    val p = deck.palette
    val audit = ColorUtils.auditThemeContrast(p)
    val escName = deck.name.replace("&", "&amp;").replace("<", "&lt;")
    val escApp = appName.replace("&", "&amp;").replace("<", "&lt;")
    val escTag = tagline.replace("&", "&amp;").replace("<", "&lt;")
    val swatches =
      listOf(
        "BG" to p.canvasHex,
        "SURF" to p.surfaceHex,
        "PRI" to p.primaryHex,
        "SEC" to p.secondaryHex,
        "WARM" to p.warmHex,
        "OK" to p.successHex,
      )
    val swatchSvg =
      swatches.mapIndexed { i, (lbl, hex) ->
        val x = 56 + i * 116
        """
        <rect x="$x" y="450" width="100" height="72" rx="12" fill="$hex" stroke="#0F172A" stroke-width="2"/>
        <text x="${x + 8}" y="544" font-family="monospace" font-size="13" font-weight="bold" fill="#0F172A">$lbl $hex</text>
        """.trimIndent()
      }.joinToString("\n")

    return """
<svg xmlns="http://www.w3.org/2000/svg" width="800" height="600" viewBox="0 0 800 600">
  <rect width="800" height="600" fill="#FAF9F6"/>
  <rect x="32" y="32" width="736" height="536" rx="24" fill="#FFFFFF" stroke="#0F172A" stroke-width="3"/>
  <text x="56" y="84" font-family="sans-serif" font-size="30" font-weight="800" fill="#0F172A">$escName</text>
  <text x="56" y="114" font-family="monospace" font-size="15" fill="#475569">$escApp — $escTag • ${deck.physics.label} • ${deck.fontPairing.label}</text>
  <rect x="56" y="140" width="688" height="270" rx="20" fill="${p.canvasHex}" stroke="${p.borderHex}" stroke-width="3"/>
  <rect x="84" y="168" width="380" height="214" rx="${deck.cornerRadiusDp}" fill="${p.surfaceHex}" stroke="${p.borderHex}" stroke-width="2"/>
  <rect x="108" y="196" width="210" height="40" rx="10" fill="${p.primaryHex}"/>
  <text x="124" y="222" font-family="sans-serif" font-size="16" font-weight="700" fill="#FFFFFF">$escApp</text>
  <text x="108" y="274" font-family="sans-serif" font-size="22" font-weight="700" fill="${p.textPrimaryHex}">Aa Bb Cc 123</text>
  <text x="108" y="306" font-family="sans-serif" font-size="15" fill="${p.textMutedHex}">${deck.fontPairing.displayLabel} + ${deck.fontPairing.bodyLabel}</text>
  <rect x="108" y="328" width="120" height="32" rx="8" fill="${p.secondaryHex}"/>
  <rect x="240" y="328" width="120" height="32" rx="8" fill="${p.warmHex}"/>
  <rect x="492" y="168" width="224" height="214" rx="${deck.cornerRadiusDp}" fill="${p.elevatedSurfaceHex}" stroke="${p.borderHex}" stroke-width="2"/>
  <text x="514" y="210" font-family="monospace" font-size="14" font-weight="bold" fill="${p.textPrimaryHex}">WCAG 2.1 AUDIT</text>
  <text x="514" y="246" font-family="monospace" font-size="16" font-weight="bold" fill="${p.primaryHex}">Primary/Bg: ${String.format(Locale.US, "%.2f:1", audit.worstPrimaryRatio)}</text>
  <text x="514" y="278" font-family="monospace" font-size="16" font-weight="bold" fill="${p.textPrimaryHex}">Text/Bg: ${String.format(Locale.US, "%.1f:1", audit.worstRatio)}</text>
  <text x="514" y="316" font-family="monospace" font-size="15" font-weight="bold" fill="${if (audit.passesAA) p.successHex else p.secondaryHex}">${if (audit.passesAA) "STATUS: AA PASS" else "STATUS: CHECK"}</text>
  $swatchSvg
</svg>
""".trim()
  }

  /**
   * Renders a high-resolution PNG Bitmap of the Dribbble Presentation Spec Card.
   */
  fun renderSpecCardBitmap(deck: ThemeDeck, appName: String, tagline: String): Bitmap {
    val bmp = Bitmap.createBitmap(1200, 900, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val p = deck.palette
    val audit = ColorUtils.auditThemeContrast(p)

    val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Backdrop
    fill.color = ColorUtils.parseHexColor("#FAF9F6").toArgb()
    canvas.drawRect(0f, 0f, 1200f, 900f, fill)

    // Outer tactile card shadow + shell
    fill.color = ColorUtils.parseHexColor("#0F172A").toArgb()
    canvas.drawRoundRect(RectF(56f, 56f, 1158f, 858f), 32f, 32f, fill)
    fill.color = android.graphics.Color.WHITE
    canvas.drawRoundRect(RectF(46f, 46f, 1148f, 848f), 32f, 32f, fill)
    stroke.color = ColorUtils.parseHexColor("#0F172A").toArgb()
    stroke.strokeWidth = 5f
    canvas.drawRoundRect(RectF(46f, 46f, 1148f, 848f), 32f, 32f, stroke)

    // Title + meta
    textPaint.color = ColorUtils.parseHexColor("#0F172A").toArgb()
    textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    textPaint.textSize = 46f
    canvas.drawText(deck.name, 90f, 126f, textPaint)

    textPaint.color = ColorUtils.parseHexColor("#475569").toArgb()
    textPaint.typeface = Typeface.MONOSPACE
    textPaint.textSize = 22f
    canvas.drawText(
      "$appName • $tagline • ${deck.physics.label} • ${deck.fontPairing.label}",
      90f,
      166f,
      textPaint,
    )

    // Theme preview frame
    val canvasColor = ColorUtils.parseHexColor(p.canvasHex).toArgb()
    val surfaceColor = ColorUtils.parseHexColor(p.surfaceHex).toArgb()
    val borderColor = ColorUtils.parseHexColor(p.borderHex).toArgb()
    val primaryColor = ColorUtils.parseHexColor(p.primaryHex).toArgb()
    val secondaryColor = ColorUtils.parseHexColor(p.secondaryHex).toArgb()
    val textColor = ColorUtils.parseHexColor(p.textPrimaryHex).toArgb()
    val mutedColor = ColorUtils.parseHexColor(p.textMutedHex).toArgb()

    fill.color = canvasColor
    canvas.drawRoundRect(RectF(90f, 206f, 1106f, 606f), 28f, 28f, fill)
    stroke.color = borderColor
    stroke.strokeWidth = 4f
    canvas.drawRoundRect(RectF(90f, 206f, 1106f, 606f), 28f, 28f, stroke)

    // Left hero card inside preview
    val r = (deck.cornerRadiusDp * 1.4f).coerceIn(8f, 36f)
    fill.color = surfaceColor
    canvas.drawRoundRect(RectF(132f, 248f, 690f, 564f), r, r, fill)
    canvas.drawRoundRect(RectF(132f, 248f, 690f, 564f), r, r, stroke)

    fill.color = primaryColor
    canvas.drawRoundRect(RectF(168f, 286f, 430f, 344f), 14f, 14f, fill)
    textPaint.color = ColorUtils.bestOnColor(ColorUtils.parseHexColor(p.primaryHex)).toArgb()
    textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    textPaint.textSize = 26f
    canvas.drawText(appName.take(18), 192f, 325f, textPaint)

    textPaint.color = textColor
    textPaint.textSize = 36f
    canvas.drawText("Aa Bb Cc 0123", 168f, 415f, textPaint)

    textPaint.color = mutedColor
    textPaint.typeface = Typeface.SANS_SERIF
    textPaint.textSize = 22f
    canvas.drawText("${deck.fontPairing.displayLabel} + ${deck.fontPairing.bodyLabel}", 168f, 456f, textPaint)

    fill.color = secondaryColor
    canvas.drawRoundRect(RectF(168f, 488f, 340f, 532f), 12f, 12f, fill)
    fill.color = ColorUtils.parseHexColor(p.warmHex).toArgb()
    canvas.drawRoundRect(RectF(360f, 488f, 532f, 532f), 12f, 12f, fill)

    // Right WCAG audit card
    fill.color = ColorUtils.parseHexColor(p.elevatedSurfaceHex).toArgb()
    canvas.drawRoundRect(RectF(724f, 248f, 1064f, 564f), r, r, fill)
    canvas.drawRoundRect(RectF(724f, 248f, 1064f, 564f), r, r, stroke)

    textPaint.color = textColor
    textPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    textPaint.textSize = 22f
    canvas.drawText("WCAG 2.1 AUDIT", 756f, 306f, textPaint)
    textPaint.textSize = 26f
    canvas.drawText("Primary/Bg ${String.format(Locale.US, "%.2f:1", audit.worstPrimaryRatio)}", 756f, 366f, textPaint)
    canvas.drawText("Text/Bg    ${String.format(Locale.US, "%.1f:1", audit.worstRatio)}", 756f, 412f, textPaint)
    textPaint.color =
      ColorUtils.parseHexColor(if (audit.passesAA) p.successHex else p.secondaryHex).toArgb()
    canvas.drawText(if (audit.passesAA) "PASS • AA READY" else "CHECK CONTRAST", 756f, 474f, textPaint)

    // Bottom palette swatch strip
    val swatches =
      listOf(
        "BG" to p.canvasHex,
        "SURF" to p.surfaceHex,
        "ELEV" to p.elevatedSurfaceHex,
        "PRI" to p.primaryHex,
        "SEC" to p.secondaryHex,
        "WARM" to p.warmHex,
      )
    swatches.forEachIndexed { idx, (label, hex) ->
      val left = 90f + idx * 172f
      val rect = RectF(left, 646f, left + 150f, 756f)
      fill.color = ColorUtils.parseHexColor(hex).toArgb()
      canvas.drawRoundRect(rect, 18f, 18f, fill)
      stroke.color = ColorUtils.parseHexColor("#0F172A").toArgb()
      stroke.strokeWidth = 3f
      canvas.drawRoundRect(rect, 18f, 18f, stroke)
      textPaint.color = ColorUtils.parseHexColor("#0F172A").toArgb()
      textPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
      textPaint.textSize = 18f
      canvas.drawText("$label $hex", left + 4f, 792f, textPaint)
    }

    return bmp
  }
}
