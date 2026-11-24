package com.example

import com.example.data.PresetDecks
import com.example.model.ExportFormat
import com.example.model.RandomizerLocks
import com.example.model.ThemePalette
import com.example.model.toKromdeckTheme
import com.example.util.ColorUtils
import com.example.util.ExportGenerator
import com.example.util.TournamentAnalytics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class KromdeckLogicTest {

  @Test
  fun allTwentySevenPresetDecksPassWcagContrast() {
    assertTrue("Expected at least 27 curated presets", PresetDecks.all.size >= 27)
    assertEquals(PresetDecks.all.size, PresetDecks.all.map { it.id }.distinct().size)
    PresetDecks.all.forEach { deck ->
      val audit = ColorUtils.auditThemeContrast(deck.palette)
      assertTrue(
        "Preset '${deck.name}' failed WCAG AA text contrast (${audit.worstRatio})",
        audit.textPassesAA,
      )
      assertTrue(
        "Preset '${deck.name}' failed WCAG primary UI contrast (${audit.worstPrimaryRatio})",
        audit.primaryPassesUI,
      )
    }
  }

  @Test
  fun wcagValidatorDetectsPoorPrimaryAndBackgroundCombinationAndAutoFixes() {
    val poorPalette =
      ThemePalette(
        canvasHex = "#FAF9F6",
        surfaceHex = "#FFFFFF",
        elevatedSurfaceHex = "#F1F5F9",
        borderHex = "#0F172A",
        textPrimaryHex = "#CBD5E1",
        textMutedHex = "#94A3B8",
        primaryHex = "#E5E7EB",
        secondaryHex = "#F43F5E",
        warmHex = "#F59E0B",
        successHex = "#10B981",
      )

    val beforeAudit = ColorUtils.auditThemeContrast(poorPalette)
    assertFalse(beforeAudit.passesAA)
    assertFalse(beforeAudit.primaryPassesUI)
    assertTrue(beforeAudit.warnings.any { it.contains("Primary", ignoreCase = true) })

    val fixedPalette = ColorUtils.autoFixContrast(poorPalette)
    val afterAudit = ColorUtils.auditThemeContrast(fixedPalette)
    assertTrue(afterAudit.textPassesAA)
    assertTrue(afterAudit.primaryPassesUI)
    assertTrue(afterAudit.passesAA)
  }

  @Test
  fun tonalRampGeneratesElevenStepsFrom50To950() {
    val ramp = ColorUtils.generateTonalRamp("#4F46E5")
    assertEquals(11, ramp.size)
    assertEquals(50, ramp.first().step)
    assertEquals(950, ramp.last().step)
    ramp.forEach { shade ->
      assertNotNull(ColorUtils.normalizeHex(shade.hex))
    }
  }

  @Test
  fun smartRandomizerRespectsParameterLocks() {
    val base = PresetDecks.all.first()
    val locks = RandomizerLocks(lockPalette = true, lockTypography = true, lockSurface = false)
    val rolled = ColorUtils.generateSmartRandomDeck(base, locks, Random(42))

    assertEquals(base.palette, rolled.palette)
    assertEquals(base.fontPairing, rolled.fontPairing)
  }

  @Test
  fun themeHashEncodeDecodeRoundTripPreservesTokens() {
    val deck = PresetDecks.all[2] // Nanda Devi Glacier & Copper
    val hash = ColorUtils.encodeThemeHash(deck)
    assertTrue(hash.startsWith("#theme="))

    val decoded = ColorUtils.decodeThemeHash("https://kromdeck.app/$hash")
    assertNotNull(decoded)
    assertEquals(deck.name, decoded?.name)
    assertEquals(deck.palette.primaryHex, decoded?.palette?.primaryHex)
    assertEquals(deck.palette.canvasHex, decoded?.palette?.canvasHex)
    assertEquals(deck.physics, decoded?.physics)
    assertEquals(deck.fontPairing, decoded?.fontPairing)
  }

  @Test
  fun exportGeneratorProducesAllSixFormatsAndSpecCardSvg() {
    val deck = PresetDecks.all.first()

    val aiPrompt = ExportGenerator.generate(deck, ExportFormat.AI_PROMPT)
    assertTrue(aiPrompt.contains(deck.palette.primaryHex))

    val css = ExportGenerator.generate(deck, ExportFormat.CSS_TOKENS)
    assertTrue(css.contains(":root"))
    assertTrue(css.contains("--kd-primary-500:"))

    val tw4 = ExportGenerator.generate(deck, ExportFormat.TAILWIND_V4)
    assertTrue(tw4.contains("@theme"))
    assertTrue(tw4.contains("--color-primary:"))

    val flutter = ExportGenerator.generate(deck, ExportFormat.FLUTTER_DART)
    assertTrue(flutter.contains("ThemeData light()"))
    assertTrue(flutter.contains("ThemeData dark()"))

    val json = ExportGenerator.generate(deck, ExportFormat.JSON_TOKENS)
    assertTrue(json.contains("\"\$schema\""))
    assertTrue(json.contains("\"primaryScale\""))
    assertTrue(json.contains("\"secondaryScale\""))
    assertTrue(json.contains("\"neutralScale\""))
    assertTrue(json.contains("\"spacing\""))

    val xml = ExportGenerator.generate(deck, ExportFormat.XML_TOKENS)
    assertTrue(xml.contains("<resources>"))
    assertTrue(xml.contains("<color name=\"kd_light_primary\">"))
    assertTrue(xml.contains("<color name=\"kd_primary_500\">"))
    assertTrue(xml.contains("<dimen name=\"kd_space_16\">"))

    val compose = ExportGenerator.generate(deck, ExportFormat.JETPACK_COMPOSE)
    assertTrue(compose.contains("KromdeckLightScheme"))
    assertTrue(compose.contains("KromdeckDarkScheme"))

    val swift = ExportGenerator.generate(deck, ExportFormat.SWIFTUI_THEME)
    assertTrue(swift.contains("import SwiftUI"))
    assertTrue(swift.contains("public enum KromdeckTheme"))

    val svg = ExportGenerator.generateSpecCardSvg(deck, "Kromdeck", "Tactile Studio")
    assertTrue(svg.contains("<svg"))
    assertTrue(svg.contains(deck.palette.primaryHex))
  }

  @Test
  fun tournamentWinLossTrackingIdentifiesPreferredPatterns() {
    val tactile = PresetDecks.all[0]
    val obsidian = PresetDecks.all[5]
    val botanical = PresetDecks.all[1]

    var records = TournamentAnalytics.recordMatchup(emptyList(), winner = tactile, loser = obsidian, isChampionship = false)
    records = TournamentAnalytics.recordMatchup(records, winner = tactile, loser = botanical, isChampionship = true)

    val sorted = TournamentAnalytics.sortLeaderboard(records)
    assertEquals(tactile.id, sorted.first().deckId)
    assertEquals(2, sorted.first().wins)
    assertEquals(1, sorted.first().championships)

    val summary = TournamentAnalytics.computePatternSummary(sorted)
    assertEquals(tactile.physics, summary.preferredPhysics)
    assertEquals(tactile.fontPairing, summary.preferredFont)
    assertTrue(summary.preferredPolarity.orEmpty().startsWith("Light Themes"))
    assertEquals(2, summary.totalMatchupsRecorded)
  }

  @Test
  fun kromdeckThemeDataClassRoundTripsAllTokens() {
    val preset = PresetDecks.all[6] // Solaris Pay
    val theme = com.example.model.KromdeckTheme(
      id = preset.id,
      name = preset.name,
      tagline = preset.tagline,
      isDark = preset.isDark,
      category = preset.category,
      palette = preset.palette,
      typography =
        com.example.model.TypographyTokens(
          fontPairing = preset.fontPairing,
          baseFontSizeSp = 15f,
          headingWeightBold = false,
          letterSpacingSp = 0.5f,
          lineHeightMultiplier = 1.45f,
        ),
      surface =
        com.example.model.SurfaceStyleTokens(
          physics = preset.physics,
          cornerRadiusDp = 22,
          cardPaddingDp = 16,
          borderWidthDp = 1.5f,
          shadowIntensity = 0.6f,
          gradientEnabled = true,
          gradientAngle = 90f,
        ),
    )

    val deck = theme.toThemeDeck()
    assertEquals(theme.palette, deck.palette)
    assertEquals(15f, deck.baseFontSizeSp)
    assertEquals(22, deck.cornerRadiusDp)
    assertEquals(90f, deck.gradientAngle)
    assertEquals(theme, deck.toKromdeckTheme())
  }

  @Test
  fun navigationPillarsCoverAllSixModesWithDistinctDescriptiveIcons() {
    val pillars = com.example.ui.components.KromdeckNavigationIcons.allPillars
    assertEquals(com.example.model.KromdeckMode.entries.size, pillars.size)
    assertEquals(
      listOf("Discover", "Studio", "Compare", "Components", "Tokens", "Saved"),
      pillars.map { it.navLabel },
    )
    assertEquals(6, pillars.map { it.filledIcon }.distinct().size)
    assertEquals(6, pillars.map { it.outlinedIcon }.distinct().size)
    pillars.forEach { spec ->
      assertTrue(spec.summary.isNotBlank())
      assertTrue(spec.iconContentDescription.isNotBlank())
      assertTrue(spec.keyFunctions.isNotEmpty())
      assertEquals(spec, com.example.ui.components.KromdeckNavigationIcons.forMode(spec.mode))
    }
  }

  @Test
  fun themeAlchemyHarmonyBlendingGradingAndMoodsPreserveWcagContrast() {
    val base = PresetDecks.all.first()
    val darkBase = PresetDecks.all[5]

    // 1. All 6 Color Harmony Rules across Light & Dark
    com.example.model.ColorHarmonyRule.entries.forEach { rule ->
      listOf(false, true).forEach { isDark ->
        val generated = com.example.util.ThemeAlchemyEngine.generateFromHarmony(215f, rule, isDark, base)
        val audit = ColorUtils.auditThemeContrast(generated.palette)
        assertTrue("Harmony $rule (dark=$isDark) failed text AA", audit.textPassesAA)
        assertTrue("Harmony $rule (dark=$isDark) failed primary UI contrast", audit.primaryPassesUI)
      }
    }

    // 2. Theme DNA Blender at 0%, 50%, 100%
    assertEquals(base, com.example.util.ThemeAlchemyEngine.blendDecks(base, darkBase, 0f))
    assertEquals(darkBase, com.example.util.ThemeAlchemyEngine.blendDecks(base, darkBase, 1f))
    val midBlend = com.example.util.ThemeAlchemyEngine.blendDecks(base, darkBase, 0.5f)
    assertTrue(ColorUtils.auditThemeContrast(midBlend.palette).passesAA)

    // 3. Global Color Grading
    val graded = com.example.util.ThemeAlchemyEngine.gradePalette(base.palette, 90f, 0.15f, 0.35f)
    assertTrue(ColorUtils.auditThemeContrast(graded).passesAA)

    // 4. All 6 Mood Atmosphere Transforms
    com.example.model.PaletteMoodFilter.entries.forEach { mood ->
      val transformed = com.example.util.ThemeAlchemyEngine.applyMoodTransform(base, mood)
      val audit = ColorUtils.auditThemeContrast(transformed.palette)
      assertTrue("Mood $mood failed WCAG AA", audit.passesAA)
    }
  }

  @Test
  fun aiThemeResponseParserBuildsCohesiveThemesAndTypographyScales() {
    val sampleJson =
      """
      {
        "themes": [
          {
            "themeName": "Nordic Glacier Bank",
            "tagline": "Glacial slate with warm copper accents",
            "designRationale": "High-contrast slate ink paired with Playfair Display and Inter.",
            "isDark": false,
            "canvasHex": "#F8FAFC",
            "surfaceHex": "#FFFFFF",
            "elevatedSurfaceHex": "#F1F5F9",
            "borderHex": "#0F172A",
            "textPrimaryHex": "#0F172A",
            "textMutedHex": "#475569",
            "primaryHex": "#B45309",
            "secondaryHex": "#1D4ED8",
            "warmHex": "#D97706",
            "successHex": "#047857",
            "fontPairing": "EDITORIAL_SERIF",
            "baseFontSizeSp": 15.0,
            "typeScaleRatio": 1.25,
            "headingWeightBold": true,
            "letterSpacingSp": 0.2,
            "lineHeightMultiplier": 1.4,
            "surfacePhysics": "ULTRA_THIN",
            "cornerRadiusDp": 10,
            "borderWidthDp": 1.5,
            "motionFeel": "SILKY_SMOOTH"
          }
        ]
      }
      """.trimIndent()

    val parsed = com.example.util.AiThemeResponseParser.parseThemesJson(sampleJson, PresetDecks.all.first())
    assertEquals(1, parsed.size)
    val first = parsed.first()
    assertEquals("Nordic Glacier Bank", first.deck.name)
    assertEquals("#B45309", first.deck.palette.primaryHex)
    assertEquals(com.example.model.FontPairing.EDITORIAL_SERIF, first.deck.fontPairing)
    assertEquals(com.example.model.SurfacePhysics.ULTRA_THIN, first.deck.physics)
    assertEquals(15, first.scaleSpec.bodySp)
    assertTrue(first.scaleSpec.displaySp > first.scaleSpec.h1Sp)
    assertTrue(first.wcagAudit.passesAA)
  }
}
