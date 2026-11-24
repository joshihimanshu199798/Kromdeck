package com.example.util

import android.util.Log
import com.example.model.AiPolarityPreference
import com.example.model.AiThemeSuggestion
import com.example.model.AiTypographyScaleSpec
import com.example.model.DiscoverCategory
import com.example.model.FontPairing
import com.example.model.MotionFeel
import com.example.model.SurfacePhysics
import com.example.model.ThemeDeck
import com.example.model.ThemePalette
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt

private const val TAG = "AiThemeGenerator"

object AiThemeResponseParser {

  private val objectBlockRegex = Regex("""\{[^{}]*"themeName"\s*:[^{}]*\}""", RegexOption.DOT_MATCHES_ALL)

  fun parseThemesJson(rawJson: String, baseDeck: ThemeDeck): List<AiThemeSuggestion> {
    val objectMatches = objectBlockRegex.findAll(rawJson).map { it.value }.toList()
    if (objectMatches.isEmpty()) {
      throw IllegalStateException("AI returned an empty list of themes.")
    }

    return objectMatches.mapIndexed { i, objText ->
      val isDark = extractBoolean(objText, "isDark", false)
      val fallbackCanvas = if (isDark) "#0F172A" else "#FAF9F6"
      val fallbackSurface = if (isDark) "#1E293B" else "#FFFFFF"
      val fallbackElevated = if (isDark) "#273549" else "#F1F5F9"
      val fallbackBorder = if (isDark) "#475569" else "#0F172A"
      val fallbackText = if (isDark) "#F8FAFC" else "#0F172A"
      val fallbackMuted = if (isDark) "#94A3B8" else "#475569"

      val palette =
        ThemePalette(
          canvasHex = ColorUtils.normalizeHex(extractString(objText, "canvasHex")) ?: fallbackCanvas,
          surfaceHex = ColorUtils.normalizeHex(extractString(objText, "surfaceHex")) ?: fallbackSurface,
          elevatedSurfaceHex =
            ColorUtils.normalizeHex(extractString(objText, "elevatedSurfaceHex")) ?: fallbackElevated,
          borderHex = ColorUtils.normalizeHex(extractString(objText, "borderHex")) ?: fallbackBorder,
          textPrimaryHex =
            ColorUtils.normalizeHex(extractString(objText, "textPrimaryHex")) ?: fallbackText,
          textMutedHex = ColorUtils.normalizeHex(extractString(objText, "textMutedHex")) ?: fallbackMuted,
          primaryHex = ColorUtils.normalizeHex(extractString(objText, "primaryHex")) ?: "#4F46E5",
          secondaryHex = ColorUtils.normalizeHex(extractString(objText, "secondaryHex")) ?: "#F43F5E",
          warmHex = ColorUtils.normalizeHex(extractString(objText, "warmHex")) ?: "#F59E0B",
          successHex = ColorUtils.normalizeHex(extractString(objText, "successHex")) ?: "#10B981",
        )

      val fontPairing = enumOrDefault(extractString(objText, "fontPairing"), FontPairing.GEOMETRIC_SANS)
      val surfacePhysics = enumOrDefault(extractString(objText, "surfacePhysics"), SurfacePhysics.PUFFY_CLAY)
      val motionFeel = enumOrDefault(extractString(objText, "motionFeel"), MotionFeel.SOFT_GLOW)
      val baseFontSize = extractDouble(objText, "baseFontSizeSp", 14.0).toFloat().coerceIn(12f, 18f)
      val scaleRatio = extractDouble(objText, "typeScaleRatio", 1.25).toFloat().coerceIn(1.12f, 1.45f)
      val headingBold = extractBoolean(objText, "headingWeightBold", true)
      val letterSpacing = extractDouble(objText, "letterSpacingSp", 0.0).toFloat().coerceIn(-0.6f, 2.0f)
      val lineHeight = extractDouble(objText, "lineHeightMultiplier", 1.38).toFloat().coerceIn(1.15f, 1.65f)
      val radiusDp = extractInt(objText, "cornerRadiusDp", 14).coerceIn(0, 32)
      val borderDp = extractDouble(objText, "borderWidthDp", 1.5).toFloat().coerceIn(0f, 3f)

      val themeName = extractString(objText, "themeName").trim().ifEmpty { "AI Studio Theme ${i + 1}" }
      val tagline = extractString(objText, "tagline").trim().ifEmpty { "Cohesive prompt-crafted design tokens" }
      val rationale =
        extractString(objText, "designRationale").trim().ifEmpty {
          "Balanced ${fontPairing.label} scale paired with ${surfacePhysics.label} surfaces."
        }

      val deck =
        baseDeck.copy(
          id = "ai_${System.currentTimeMillis() % 100000}_$i",
          name = themeName.take(34),
          tagline = tagline.take(80),
          isDark = isDark,
          category = DiscoverCategory.SAAS_FINANCE,
          palette = palette,
          physics = surfacePhysics,
          fontPairing = fontPairing,
          motionFeel = motionFeel,
          note = rationale,
          swatchIconRes = null,
          cornerRadiusDp = radiusDp,
          borderWidthDp = borderDp,
          baseFontSizeSp = baseFontSize,
          headingWeightBold = headingBold,
          letterSpacingSp = letterSpacing,
          lineHeightMultiplier = lineHeight,
        )

      val scaleSpec =
        AiTypographyScaleSpec(
          displaySp = (baseFontSize * scaleRatio.pow(3)).roundToInt().coerceIn(24, 48),
          h1Sp = (baseFontSize * scaleRatio.pow(2)).roundToInt().coerceIn(20, 36),
          h2Sp = (baseFontSize * scaleRatio).roundToInt().coerceIn(16, 28),
          bodySp = baseFontSize.roundToInt(),
          captionSp = (baseFontSize / scaleRatio).roundToInt().coerceIn(10, 14),
          scaleRatioLabel = "${String.format(Locale.US, "%.2f", scaleRatio)}x (${fontPairing.label})",
        )

      AiThemeSuggestion(
        deck = deck,
        designRationale = rationale,
        scaleSpec = scaleSpec,
        wcagAudit = ColorUtils.auditThemeContrast(palette),
      )
    }
  }

  private fun extractString(json: String, key: String): String {
    val regex = Regex("\"$key\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"")
    val raw = regex.find(json)?.groupValues?.getOrNull(1) ?: return ""
    return raw.replace("\\\"", "\"").replace("\\n", " ").replace("\\\\", "\\")
  }

  private fun extractDouble(json: String, key: String, fallback: Double): Double {
    val regex = Regex("\"$key\"\\s*:\\s*(-?[0-9]+(?:\\.[0-9]+)?)")
    return regex.find(json)?.groupValues?.getOrNull(1)?.toDoubleOrNull() ?: fallback
  }

  private fun extractInt(json: String, key: String, fallback: Int): Int {
    val regex = Regex("\"$key\"\\s*:\\s*(-?[0-9]+)")
    return regex.find(json)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: fallback
  }

  private fun extractBoolean(json: String, key: String, fallback: Boolean): Boolean {
    val regex = Regex("\"$key\"\\s*:\\s*(true|false)", RegexOption.IGNORE_CASE)
    return regex.find(json)?.groupValues?.getOrNull(1)?.lowercase(Locale.US)?.toBooleanStrictOrNull() ?: fallback
  }

  private inline fun <reified T : Enum<T>> enumOrDefault(raw: String, fallback: T): T {
    return try {
      enumValueOf<T>(raw.trim().uppercase(Locale.US))
    } catch (_: Exception) {
      fallback
    }
  }
}

class AiThemeGeneratorService {

  private val singleThemeSchema =
    Schema.obj(
      properties =
        mapOf(
          "themeName" to Schema.string(description = "Creative 2-4 word name for this design system"),
          "tagline" to Schema.string(description = "Concise 5-10 word aesthetic summary"),
          "designRationale" to Schema.string(description = "1-2 sentences explaining color harmony and typography scale pairing"),
          "isDark" to Schema.boolean(description = "True for dark theme, false for light theme"),
          "canvasHex" to Schema.string(description = "Main background hex color (#RRGGBB)"),
          "surfaceHex" to Schema.string(description = "Card surface hex color (#RRGGBB)"),
          "elevatedSurfaceHex" to Schema.string(description = "Elevated container hex color (#RRGGBB)"),
          "borderHex" to Schema.string(description = "Border/outline hex color (#RRGGBB)"),
          "textPrimaryHex" to Schema.string(description = "Primary readable text hex color (#RRGGBB, >= 4.5:1 vs canvas & surface)"),
          "textMutedHex" to Schema.string(description = "Secondary muted text hex color (#RRGGBB)"),
          "primaryHex" to Schema.string(description = "Primary brand accent hex color (#RRGGBB, >= 3.0:1 vs canvas & surface)"),
          "secondaryHex" to Schema.string(description = "Complementary secondary accent hex color (#RRGGBB)"),
          "warmHex" to Schema.string(description = "Warm highlight/badge hex color (#RRGGBB)"),
          "successHex" to Schema.string(description = "Semantic success hex color (#RRGGBB)"),
          "fontPairing" to
            Schema.enumeration(
              values = FontPairing.entries.map { it.name },
              description = "Typography pairing ID matching the aesthetic",
            ),
          "baseFontSizeSp" to Schema.double(description = "Base body font size in sp between 13.0 and 17.0"),
          "typeScaleRatio" to Schema.double(description = "Modular typography scale ratio between 1.15 and 1.35"),
          "headingWeightBold" to Schema.boolean(description = "True for extra-bold display headings"),
          "letterSpacingSp" to Schema.double(description = "Letter tracking in sp between -0.5 and 1.4"),
          "lineHeightMultiplier" to Schema.double(description = "Body line-height multiplier between 1.25 and 1.55"),
          "surfacePhysics" to
            Schema.enumeration(
              values = SurfacePhysics.entries.map { it.name },
              description = "Card surface style",
            ),
          "cornerRadiusDp" to Schema.integer(description = "Corner radius in dp from 0 to 28"),
          "borderWidthDp" to Schema.double(description = "Border width in dp from 0.0 to 2.5"),
          "motionFeel" to
            Schema.enumeration(
              values = MotionFeel.entries.map { it.name },
              description = "Interactive motion physics preset",
            ),
        ),
    )

  private val themesResponseSchema =
    Schema.obj(
      properties =
        mapOf(
          "themes" to
            Schema.array(
              items = singleThemeSchema,
              description = "3 distinct, aesthetically cohesive design token theme variations",
            ),
        ),
    )

  private val model by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash",
      generationConfig =
        generationConfig {
          responseMimeType = "application/json"
          responseSchema = themesResponseSchema
          temperature = 0.85f
        },
    )
  }

  suspend fun generateThemes(
    userPrompt: String,
    polarity: AiPolarityPreference,
    baseDeck: ThemeDeck,
  ): List<AiThemeSuggestion> {
    val polarityInstruction =
      when (polarity) {
        AiPolarityPreference.AUTO ->
          "Include a balanced mix of light and dark variations that best suit the prompt (e.g., 2 matching the natural mood + 1 opposite-polarity counterpart)."
        AiPolarityPreference.LIGHT ->
          "All 3 theme variations MUST be Light Mode (isDark = false) with bright, clean canvases and crisp dark typography."
        AiPolarityPreference.DARK ->
          "All 3 theme variations MUST be Dark Mode (isDark = true) with rich dark canvases and luminous, readable typography."
      }

    val fullPrompt =
      """
      You are an expert principal UI/UX design systems architect.
      Generate 3 distinct, aesthetically cohesive design token themes inspired by this user brief:
      "$userPrompt"

      Requirements:
      1. $polarityInstruction
      2. Every hex color MUST be a valid 6-digit uppercase hex string like #4F46E5.
      3. Guarantee WCAG 2.1 AA accessibility:
         - textPrimaryHex against BOTH canvasHex and surfaceHex MUST achieve >= 4.5:1 contrast.
         - primaryHex against BOTH canvasHex and surfaceHex MUST achieve >= 3.0:1 contrast (prefer >= 4.5:1).
      4. Select cohesive typography pairings (`fontPairing`), modular `typeScaleRatio` (1.15..1.35), `baseFontSizeSp` (13..17), `letterSpacingSp` (-0.5..1.4), `surfacePhysics`, `cornerRadiusDp`, and `motionFeel` that genuinely match the prompt's visual personality.
      5. Make the 3 themes distinctly different directions (e.g., Signature Editorial, High-Contrast Functional, Expressive Accent) while all honoring the prompt.
      """.trimIndent()

    val response =
      withTimeout(60_000L) {
        model.generateContent(fullPrompt)
      }

    val jsonText = response.text
    if (jsonText.isNullOrBlank()) {
      Log.e(TAG, "Gemini returned an empty response body.")
      throw IllegalStateException("AI model returned an empty response. Please try again.")
    }

    return AiThemeResponseParser.parseThemesJson(jsonText, baseDeck)
  }
}
