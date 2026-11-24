package com.example.model

enum class KromdeckMode(val label: String) {
  DISCOVER("Discover"),
  STUDIO("Studio"),
  COMPARE("Compare"),
  COMPONENTS("Components"),
  TOKENS("Tokens & Scale"),
  SAVED("Saved"),
}

enum class DiscoverCategory(val label: String) {
  ALL("All"),
  MUSIC_RADIO("Music & Radio"),
  PHOTO_GALLERY("Photo & Gallery"),
  SAAS_FINANCE("SaaS & Finance"),
  SOCIAL_FEED("Social & Feed"),
  ECOMMERCE("E-Commerce"),
  MINIMAL_NOTES("Minimal & Notes"),
}

enum class SurfacePhysics(
  val label: String,
  val filterTag: String,
  val description: String,
) {
  HARD_INK("Neo-Brutalist", "Neo-Brutalist", "2dp solid border with crisp hard offset shadow"),
  PUFFY_CLAY("Soft Shadow", "Soft Shadow", "Elevated plush cards with soft ambient depth"),
  FROSTED_GLASS("Glassmorphism", "Glassmorphism", "Translucent surface tint with luminous hairline"),
  ULTRA_THIN("Outlined", "Outlined", "Editorial precision with crisp structural hairline"),
  SOFT_NEUMORPHIC("Soft Neumorphic", "Soft Shadow", "Extruded tactile bevels with dual ambient shadows"),
  FLAT("Flat", "Flat", "Clean zero-elevation color planes with tight rhythm"),
}

enum class FontPairing(
  val label: String,
  val displayLabel: String,
  val bodyLabel: String,
) {
  GEOMETRIC_SANS("Jakarta + Inter", "Plus Jakarta Sans", "Inter"),
  ROUNDED_DISPLAY("Outfit + Jakarta", "Outfit", "Plus Jakarta Sans"),
  MONOSPACE_TECH("Space + JetBrains", "Space Grotesk", "JetBrains Mono"),
  EDITORIAL_SERIF("Playfair + Inter", "Playfair Display", "Inter"),
  SORA_DM_SANS("Sora + DM Sans", "Sora", "DM Sans"),
  SYNE_MANROPE("Syne + Manrope", "Syne", "Manrope"),
}

enum class MotionFeel(
  val label: String,
  val description: String,
  val durationMs: Int,
) {
  HARD_SNAP("Hard Snap 75ms", "Instant mechanical click detent", 75),
  SPRING_BOUNCE("Spring Bounce", "Playful bouncy spring physics", 180),
  SOFT_GLOW("Gentle Ease 220ms", "Smooth damped transition curve", 220),
  SILKY_SMOOTH("Silky Smooth 350ms", "Cinematic luxury decelerate", 350),
}

enum class PreviewTemplate(val label: String, val category: DiscoverCategory) {
  MUSIC_PLAYER("Music & Radio", DiscoverCategory.MUSIC_RADIO),
  WALLPAPER_GALLERY("Photo & Gallery", DiscoverCategory.PHOTO_GALLERY),
  SAAS_DASHBOARD("SaaS & Finance", DiscoverCategory.SAAS_FINANCE),
  SOCIAL_FEED("Social & Feed", DiscoverCategory.SOCIAL_FEED),
  ECOMMERCE("E-Commerce", DiscoverCategory.ECOMMERCE),
  TASKS_NOTES("Tasks & Notes", DiscoverCategory.MINIMAL_NOTES),
}

enum class ViewportMode(val label: String, val maxWidthDp: Int) {
  MOBILE("Mobile", 360),
  TABLET("Tablet", 540),
  DESKTOP("Desktop", 960),
}

enum class ColorBlindnessMode(val label: String) {
  NORMAL("Normal"),
  PROTANOPIA("Protanopia"),
  DEUTERANOPIA("Deuteranopia"),
  TRITANOPIA("Tritanopia"),
  ACHROMATOPSIA("Achromatopsia"),
}

enum class NavShellStyle(val label: String) {
  BOTTOM_PILL("Bottom Pill Bar"),
  FLOATING_DOCK("Floating Dock"),
  TOP_TABS("Top Header Tabs"),
  MINIMAL_DRAWER("Minimal Drawer"),
}

enum class GridDensity(val label: String, val spacingMultiplier: Float) {
  COMPACT("Compact", 0.78f),
  COMFORTABLE("Comfortable", 1.0f),
  SPACIOUS("Spacious", 1.25f),
}

enum class StudioDrawerTab(val label: String) {
  AI_GENERATOR("AI Prompt"),
  COLORS("Colors"),
  MIXER_LAB("Infinite Lab"),
  SURFACE("Surface"),
  TYPOGRAPHY("Type"),
  LAYOUT("Layout"),
  MOTION("Motion"),
  WCAG_EXPORTS("WCAG & Export"),
}

enum class ColorHarmonyRule(val label: String, val description: String) {
  COMPLEMENTARY("Complementary", "Bold 180° opposite accent punch"),
  ANALOGOUS("Analogous", "Smooth neighboring hues (+32° / -32°)"),
  TRIADIC("Triadic", "Balanced 120° color-wheel triad"),
  SPLIT_COMPLEMENTARY("Split-Comp", "Refined +150° / +210° high-energy pair"),
  TETRADIC("Tetradic", "Rich 90° four-point studio palette"),
  MONOCHROMATIC("Monochrome", "Sculpted single-hue tonal elegance"),
}

enum class PaletteMoodFilter(val label: String, val subtitle: String) {
  HIGH_CONTRAST_AAA("AAA Contrast", "Ultra-crisp 7:1+ accessible polarity"),
  PASTEL_SOFT("Pastel Soft", "Airy low-chroma studio tints"),
  NEON_CYBER("Neon Cyber", "Electric dark glow accents"),
  WARM_PAPER("Warm Paper", "Editorial cream & sepia ink"),
  AMOLED_PURE("True AMOLED", "0% pitch-black battery-saving dark"),
  MUTED_EARTH("Muted Earth", "Desaturated organic clay & stone"),
}

enum class ColorRole(val label: String) {
  CANVAS("Background"),
  SURFACE("Card Surface"),
  ELEVATED("Elevated"),
  PRIMARY("Primary"),
  SECONDARY("Secondary"),
  TEXT_PRIMARY("Primary Text"),
  TEXT_MUTED("Muted Text"),
  BORDER("Border"),
  WARM("Warm"),
  SUCCESS("Success"),
}

enum class ExportFormat(
  val label: String,
  val subtitle: String,
  val fileExtension: String,
  val mimeType: String,
) {
  JSON_TOKENS("JSON Tokens", "W3C Design Tokens JSON (ramps, light & dark)", "json", "application/json"),
  XML_TOKENS("Android XML", "Android colors.xml, dimens & theme resources", "xml", "application/xml"),
  JETPACK_COMPOSE("Compose Kotlin", "Jetpack Compose Material 3 Theme.kt", "kt", "text/plain"),
  CSS_TOKENS("CSS :root", "CSS Custom Properties (:root & dark)", "css", "text/css"),
  TAILWIND_V4("Tailwind v4", "Tailwind CSS v4 @theme config", "css", "text/css"),
  FLUTTER_DART("Flutter Dart", "Flutter ThemeData (Light + Dark)", "dart", "text/plain"),
  SWIFTUI_THEME("SwiftUI", "Apple SwiftUI Color & Theme extension", "swift", "text/plain"),
  AI_PROMPT("AI Prompt", "Plain-English AI Builder restyling prompt", "txt", "text/plain"),
}

data class RandomizerLocks(
  val lockPalette: Boolean = false,
  val lockTypography: Boolean = false,
  val lockSurface: Boolean = false,
)

data class TonalShade(
  val step: Int,
  val hex: String,
)

data class ThemePalette(
  val canvasHex: String,
  val surfaceHex: String,
  val borderHex: String,
  val textPrimaryHex: String,
  val textMutedHex: String,
  val primaryHex: String,
  val secondaryHex: String,
  val warmHex: String,
  val successHex: String,
  val elevatedSurfaceHex: String = surfaceHex,
) {
  fun hexFor(role: ColorRole): String =
    when (role) {
      ColorRole.CANVAS -> canvasHex
      ColorRole.SURFACE -> surfaceHex
      ColorRole.ELEVATED -> elevatedSurfaceHex
      ColorRole.BORDER -> borderHex
      ColorRole.TEXT_PRIMARY -> textPrimaryHex
      ColorRole.TEXT_MUTED -> textMutedHex
      ColorRole.PRIMARY -> primaryHex
      ColorRole.SECONDARY -> secondaryHex
      ColorRole.WARM -> warmHex
      ColorRole.SUCCESS -> successHex
    }

  fun withRoleHex(role: ColorRole, hex: String): ThemePalette =
    when (role) {
      ColorRole.CANVAS -> copy(canvasHex = hex)
      ColorRole.SURFACE -> copy(surfaceHex = hex)
      ColorRole.ELEVATED -> copy(elevatedSurfaceHex = hex)
      ColorRole.BORDER -> copy(borderHex = hex)
      ColorRole.TEXT_PRIMARY -> copy(textPrimaryHex = hex)
      ColorRole.TEXT_MUTED -> copy(textMutedHex = hex)
      ColorRole.PRIMARY -> copy(primaryHex = hex)
      ColorRole.SECONDARY -> copy(secondaryHex = hex)
      ColorRole.WARM -> copy(warmHex = hex)
      ColorRole.SUCCESS -> copy(successHex = hex)
    }
}

data class ThemeDeck(
  val id: String,
  val name: String,
  val tagline: String,
  val isDark: Boolean,
  val palette: ThemePalette,
  val physics: SurfacePhysics,
  val fontPairing: FontPairing,
  val motionFeel: MotionFeel,
  val note: String = "",
  val swatchIconRes: Int? = null,
  val category: DiscoverCategory = DiscoverCategory.MUSIC_RADIO,
  val gradientEnabled: Boolean = false,
  val gradientAngle: Float = 135f,
  val gradientMeshGlow: Boolean = false,
  val cornerRadiusDp: Int = 14,
  val cardPaddingDp: Int = 12,
  val borderWidthDp: Float = 2f,
  val shadowIntensity: Float = 0.85f,
  val glassBlurAlpha: Float = 0.78f,
  val baseFontSizeSp: Float = 14f,
  val headingWeightBold: Boolean = true,
  val letterSpacingSp: Float = 0f,
  val lineHeightMultiplier: Float = 1.35f,
  val navShell: NavShellStyle = NavShellStyle.BOTTOM_PILL,
  val gridDensity: GridDensity = GridDensity.COMFORTABLE,
  val iconStrokeWeight: Float = 1.8f,
)

data class WcagAudit(
  val canvasTextRatio: Double,
  val surfaceTextRatio: Double,
  val primaryOnCanvasRatio: Double,
  val primaryOnSurfaceRatio: Double,
  val worstRatio: Double,
  val worstPrimaryRatio: Double,
  val textPassesAA: Boolean,
  val primaryPassesUI: Boolean,
  val primaryPassesAA: Boolean,
  val passesAA: Boolean,
  val passesLargeAA: Boolean,
  val warnings: List<String>,
)

data class ThemeWinLossRecord(
  val deckId: String,
  val deckName: String,
  val physics: SurfacePhysics,
  val fontPairing: FontPairing,
  val isDark: Boolean,
  val primaryHex: String,
  val canvasHex: String,
  val wins: Int = 0,
  val losses: Int = 0,
  val championships: Int = 0,
) {
  val totalMatches: Int
    get() = wins + losses

  val winRatePercent: Int
    get() = if (totalMatches == 0) 0 else ((wins * 100.0) / totalMatches).toInt()
}

data class DesignPatternSummary(
  val topDeck: ThemeWinLossRecord? = null,
  val preferredPhysics: SurfacePhysics? = null,
  val preferredFont: FontPairing? = null,
  val preferredPolarity: String? = null,
  val totalMatchupsRecorded: Int = 0,
)

data class TournamentState(
  val isActive: Boolean = false,
  val roundNumber: Int = 1,
  val totalStartingDecks: Int = 0,
  val bracket: List<ThemeDeck> = emptyList(),
  val pairIndex: Int = 0,
  val nextRoundWinners: List<ThemeDeck> = emptyList(),
  val champion: ThemeDeck? = null,
) {
  val leftContender: ThemeDeck?
    get() = bracket.getOrNull(pairIndex * 2)

  val rightContender: ThemeDeck?
    get() = bracket.getOrNull(pairIndex * 2 + 1)

  val totalMatchesInRound: Int
    get() = bracket.size / 2

  val currentMatchNumber: Int
    get() = pairIndex + 1

  val roundLabel: String
    get() =
      when (bracket.size) {
        2 -> "Championship Final"
        4 -> "Semifinals"
        else -> "Round $roundNumber"
      }
}

data class ExtractedPaletteResult(
  val sourceLabel: String,
  val palette: ThemePalette,
  val darkCompanionPalette: ThemePalette,
  val swatches: List<String>,
)
