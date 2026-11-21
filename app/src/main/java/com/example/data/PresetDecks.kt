package com.example.data

import com.example.R
import com.example.model.DiscoverCategory
import com.example.model.FontPairing
import com.example.model.GridDensity
import com.example.model.MotionFeel
import com.example.model.NavShellStyle
import com.example.model.SurfacePhysics
import com.example.model.ThemeDeck
import com.example.model.ThemePalette

data class SampleTrack(
  val id: String,
  val title: String,
  val artist: String,
  val genre: String,
  val duration: String,
  val artworkRes: Int?,
  val badge: String,
)

data class SampleWallpaper(
  val id: String,
  val title: String,
  val author: String,
  val resolution: String,
  val verified: Boolean,
  val imageRes: Int?,
  val tag: String,
)

data class SampleSaasMetric(
  val label: String,
  val value: String,
  val delta: String,
  val isPositive: Boolean,
)

data class SampleSaasRow(
  val workspace: String,
  val plan: String,
  val status: String,
  val usagePct: Int,
  val mrr: String,
)

data class SampleSocialPost(
  val id: String,
  val author: String,
  val handle: String,
  val timeAgo: String,
  val body: String,
  val imageRes: Int?,
  val likes: Int,
  val comments: Int,
)

data class SampleProduct(
  val id: String,
  val name: String,
  val subtitle: String,
  val price: String,
  val rating: String,
  val badge: String,
  val imageRes: Int,
  val sizes: List<String>,
)

data class SampleTaskItem(
  val id: String,
  val title: String,
  val priority: String,
  val dueLabel: String,
  val done: Boolean,
)

object PresetDecks {
  val all: List<ThemeDeck> =
    listOf(
      ThemeDeck(
        id = "tactile_cream_ink",
        name = "Tactile Cream & Ink",
        tagline = "Warm paper canvas with crisp neo-brutalist ink",
        isDark = false,
        category = DiscoverCategory.MINIMAL_NOTES,
        palette =
          ThemePalette(
            canvasHex = "#FAF9F6",
            surfaceHex = "#FFFFFF",
            elevatedSurfaceHex = "#F3F1EA",
            borderHex = "#0F172A",
            textPrimaryHex = "#0F172A",
            textMutedHex = "#475569",
            primaryHex = "#4F46E5",
            secondaryHex = "#F43F5E",
            warmHex = "#F59E0B",
            successHex = "#10B981",
          ),
        physics = SurfacePhysics.HARD_INK,
        fontPairing = FontPairing.GEOMETRIC_SANS,
        motionFeel = MotionFeel.HARD_SNAP,
        swatchIconRes = R.drawable.swatch_tactile_cream,
        cornerRadiusDp = 12,
        borderWidthDp = 2f,
      ),
      ThemeDeck(
        id = "botanical_extract",
        name = "Botanical Extract",
        tagline = "Organic studio sage paper with warm terracotta clay",
        isDark = false,
        category = DiscoverCategory.PHOTO_GALLERY,
        palette =
          ThemePalette(
            canvasHex = "#F4F6F0",
            surfaceHex = "#FCFDFB",
            elevatedSurfaceHex = "#E8EFE2",
            borderHex = "#2D3B32",
            textPrimaryHex = "#1F2D24",
            textMutedHex = "#4E6355",
            primaryHex = "#2F6F4E",
            secondaryHex = "#C95D3A",
            warmHex = "#C98A2E",
            successHex = "#2E8B57",
          ),
        physics = SurfacePhysics.PUFFY_CLAY,
        fontPairing = FontPairing.EDITORIAL_SERIF,
        motionFeel = MotionFeel.SPRING_BOUNCE,
        swatchIconRes = R.drawable.swatch_botanical_sage,
        cornerRadiusDp = 20,
        borderWidthDp = 1.5f,
      ),
      ThemeDeck(
        id = "nanda_devi_glacier",
        name = "Nanda Devi Glacier & Copper",
        tagline = "Crisp Himalayan snow canvas with burnished copper",
        isDark = false,
        category = DiscoverCategory.PHOTO_GALLERY,
        palette =
          ThemePalette(
            canvasHex = "#F2F7FA",
            surfaceHex = "#FFFFFF",
            elevatedSurfaceHex = "#E2EEF5",
            borderHex = "#1E3A5F",
            textPrimaryHex = "#0F2238",
            textMutedHex = "#45627D",
            primaryHex = "#B45309",
            secondaryHex = "#0284C7",
            warmHex = "#D97706",
            successHex = "#0D9488",
          ),
        physics = SurfacePhysics.ULTRA_THIN,
        fontPairing = FontPairing.SORA_DM_SANS,
        motionFeel = MotionFeel.SOFT_GLOW,
        cornerRadiusDp = 14,
        borderWidthDp = 1f,
      ),
      ThemeDeck(
        id = "nordic_slate_gold",
        name = "Nordic Slate & Gold",
        tagline = "Calm fjord mist with sculpted cobalt and brushed gold",
        isDark = false,
        category = DiscoverCategory.SAAS_FINANCE,
        palette =
          ThemePalette(
            canvasHex = "#EEF2F6",
            surfaceHex = "#F8FAFC",
            elevatedSurfaceHex = "#E2E8F0",
            borderHex = "#475569",
            textPrimaryHex = "#0F172A",
            textMutedHex = "#475569",
            primaryHex = "#1D4ED8",
            secondaryHex = "#B45309",
            warmHex = "#D97706",
            successHex = "#0D9488",
          ),
        physics = SurfacePhysics.SOFT_NEUMORPHIC,
        fontPairing = FontPairing.ROUNDED_DISPLAY,
        motionFeel = MotionFeel.SPRING_BOUNCE,
        swatchIconRes = R.drawable.swatch_nordic_slate,
        cornerRadiusDp = 16,
        borderWidthDp = 1f,
      ),
      ThemeDeck(
        id = "devbhoomi_pine_terracotta",
        name = "Devbhoomi Pine & Terracotta",
        tagline = "Mountain cedar deep greens paired with kiln clay",
        isDark = false,
        category = DiscoverCategory.ECOMMERCE,
        palette =
          ThemePalette(
            canvasHex = "#F7F3EC",
            surfaceHex = "#FFFDF9",
            elevatedSurfaceHex = "#EFE6D8",
            borderHex = "#243B2E",
            textPrimaryHex = "#1A2E23",
            textMutedHex = "#516457",
            primaryHex = "#B84A27",
            secondaryHex = "#1E5C45",
            warmHex = "#C27D1A",
            successHex = "#1F7A52",
          ),
        physics = SurfacePhysics.HARD_INK,
        fontPairing = FontPairing.EDITORIAL_SERIF,
        motionFeel = MotionFeel.HARD_SNAP,
        cornerRadiusDp = 10,
        borderWidthDp = 2f,
      ),
      ThemeDeck(
        id = "obsidian_pro",
        name = "Obsidian Pro",
        tagline = "True-black AMOLED telemetry canvas with acid-lime punch",
        isDark = true,
        category = DiscoverCategory.SAAS_FINANCE,
        palette =
          ThemePalette(
            canvasHex = "#06070A",
            surfaceHex = "#12141B",
            elevatedSurfaceHex = "#1C202B",
            borderHex = "#2E3546",
            textPrimaryHex = "#F8FAFC",
            textMutedHex = "#94A3B8",
            primaryHex = "#84CC16",
            secondaryHex = "#22D3EE",
            warmHex = "#FACC15",
            successHex = "#10B981",
          ),
        physics = SurfacePhysics.ULTRA_THIN,
        fontPairing = FontPairing.MONOSPACE_TECH,
        motionFeel = MotionFeel.HARD_SNAP,
        swatchIconRes = R.drawable.swatch_amoled_obsidian,
        cornerRadiusDp = 10,
        borderWidthDp = 1f,
      ),
      ThemeDeck(
        id = "solaris_pay",
        name = "Solaris Pay",
        tagline = "High-trust fintech amber & midnight slate ledger",
        isDark = false,
        category = DiscoverCategory.SAAS_FINANCE,
        palette =
          ThemePalette(
            canvasHex = "#F8F7F4",
            surfaceHex = "#FFFFFF",
            elevatedSurfaceHex = "#FEF3C7",
            borderHex = "#1E293B",
            textPrimaryHex = "#0F172A",
            textMutedHex = "#475569",
            primaryHex = "#B45309",
            secondaryHex = "#0F766E",
            warmHex = "#D97706",
            successHex = "#059669",
          ),
        physics = SurfacePhysics.PUFFY_CLAY,
        fontPairing = FontPairing.SORA_DM_SANS,
        motionFeel = MotionFeel.SOFT_GLOW,
        gradientEnabled = true,
        gradientAngle = 120f,
        cornerRadiusDp = 18,
        borderWidthDp = 1.2f,
      ),
      ThemeDeck(
        id = "kanso_notes",
        name = "Kanso Notes",
        tagline = "Japanese wabi-sabi flat minimalism for deep focus",
        isDark = false,
        category = DiscoverCategory.MINIMAL_NOTES,
        palette =
          ThemePalette(
            canvasHex = "#F6F5F1",
            surfaceHex = "#FFFFFF",
            elevatedSurfaceHex = "#ECEAE2",
            borderHex = "#27272A",
            textPrimaryHex = "#18181B",
            textMutedHex = "#52525B",
            primaryHex = "#3F3F46",
            secondaryHex = "#DC2626",
            warmHex = "#B45309",
            successHex = "#15803D",
          ),
        physics = SurfacePhysics.FLAT,
        fontPairing = FontPairing.SYNE_MANROPE,
        motionFeel = MotionFeel.SILKY_SMOOTH,
        cornerRadiusDp = 8,
        borderWidthDp = 1f,
        navShell = NavShellStyle.MINIMAL_DRAWER,
      ),
      ThemeDeck(
        id = "velvet_studio",
        name = "Velvet Studio",
        tagline = "Plum-noir audio booth with rose-gold mastering faders",
        isDark = true,
        category = DiscoverCategory.MUSIC_RADIO,
        palette =
          ThemePalette(
            canvasHex = "#140D1B",
            surfaceHex = "#21162C",
            elevatedSurfaceHex = "#2D1E3D",
            borderHex = "#6D4C8F",
            textPrimaryHex = "#FAF5FF",
            textMutedHex = "#C4B5FD",
            primaryHex = "#F472B6",
            secondaryHex = "#FBBF24",
            warmHex = "#FB923C",
            successHex = "#34D399",
          ),
        physics = SurfacePhysics.PUFFY_CLAY,
        fontPairing = FontPairing.ROUNDED_DISPLAY,
        motionFeel = MotionFeel.SILKY_SMOOTH,
        gradientEnabled = true,
        gradientMeshGlow = true,
        cornerRadiusDp = 20,
        borderWidthDp = 1.2f,
      ),
      ThemeDeck(
        id = "luminous_glass",
        name = "Luminous Glass",
        tagline = "Frosted sapphire aurora planes with translucent depth",
        isDark = true,
        category = DiscoverCategory.SOCIAL_FEED,
        palette =
          ThemePalette(
            canvasHex = "#0B1120",
            surfaceHex = "#172540",
            elevatedSurfaceHex = "#1E3257",
            borderHex = "#60A5FA",
            textPrimaryHex = "#F8FAFC",
            textMutedHex = "#BFDBFE",
            primaryHex = "#38BDF8",
            secondaryHex = "#A78BFA",
            warmHex = "#FBBF24",
            successHex = "#34D399",
          ),
        physics = SurfacePhysics.FROSTED_GLASS,
        fontPairing = FontPairing.SORA_DM_SANS,
        motionFeel = MotionFeel.SOFT_GLOW,
        gradientEnabled = true,
        gradientMeshGlow = true,
        cornerRadiusDp = 20,
        borderWidthDp = 1.2f,
        navShell = NavShellStyle.FLOATING_DOCK,
      ),
      ThemeDeck(
        id = "pulse_fit",
        name = "Pulse Fit",
        tagline = "High-energy coral sprint metrics on matte charcoal",
        isDark = true,
        category = DiscoverCategory.SOCIAL_FEED,
        palette =
          ThemePalette(
            canvasHex = "#111216",
            surfaceHex = "#1C1E26",
            elevatedSurfaceHex = "#272A35",
            borderHex = "#3F4556",
            textPrimaryHex = "#FFFFFF",
            textMutedHex = "#A1A7B8",
            primaryHex = "#FF5A5F",
            secondaryHex = "#38BDF8",
            warmHex = "#FACC15",
            successHex = "#22C55E",
          ),
        physics = SurfacePhysics.HARD_INK,
        fontPairing = FontPairing.SYNE_MANROPE,
        motionFeel = MotionFeel.SPRING_BOUNCE,
        cornerRadiusDp = 14,
        borderWidthDp = 2f,
      ),
      ThemeDeck(
        id = "swiss_editorial",
        name = "Swiss Editorial Minimalist",
        tagline = "Gallery-grade grid with vermilion signal accents",
        isDark = false,
        category = DiscoverCategory.MINIMAL_NOTES,
        palette =
          ThemePalette(
            canvasHex = "#F5F5F4",
            surfaceHex = "#FFFFFF",
            elevatedSurfaceHex = "#E7E5E4",
            borderHex = "#18181B",
            textPrimaryHex = "#09090B",
            textMutedHex = "#52525B",
            primaryHex = "#DC2626",
            secondaryHex = "#2563EB",
            warmHex = "#D97706",
            successHex = "#059669",
          ),
        physics = SurfacePhysics.ULTRA_THIN,
        fontPairing = FontPairing.EDITORIAL_SERIF,
        motionFeel = MotionFeel.HARD_SNAP,
        swatchIconRes = R.drawable.swatch_swiss_editorial,
        cornerRadiusDp = 6,
        borderWidthDp = 1f,
        navShell = NavShellStyle.TOP_TABS,
      ),
      ThemeDeck(
        id = "cyberpunk_vaporwave",
        name = "Cyberpunk Vaporwave",
        tagline = "Ultraviolet twilight glass with synthwave neon",
        isDark = true,
        category = DiscoverCategory.MUSIC_RADIO,
        palette =
          ThemePalette(
            canvasHex = "#130926",
            surfaceHex = "#20123D",
            elevatedSurfaceHex = "#2D1956",
            borderHex = "#F472B6",
            textPrimaryHex = "#FDF4FF",
            textMutedHex = "#D8B4FE",
            primaryHex = "#F472B6",
            secondaryHex = "#22D3EE",
            warmHex = "#FBBF24",
            successHex = "#34D399",
          ),
        physics = SurfacePhysics.FROSTED_GLASS,
        fontPairing = FontPairing.MONOSPACE_TECH,
        motionFeel = MotionFeel.SOFT_GLOW,
        swatchIconRes = R.drawable.swatch_cyberpunk_vaporwave,
        gradientEnabled = true,
        gradientMeshGlow = true,
        cornerRadiusDp = 18,
        borderWidthDp = 1.5f,
      ),
      ThemeDeck(
        id = "kyoto_matcha_paper",
        name = "Kyoto Matcha Paper",
        tagline = "Warm washi cream with ceremonial matcha & indigo",
        isDark = false,
        category = DiscoverCategory.ECOMMERCE,
        palette =
          ThemePalette(
            canvasHex = "#F5F4EC",
            surfaceHex = "#FCFBF7",
            elevatedSurfaceHex = "#E6E9D8",
            borderHex = "#2E3A28",
            textPrimaryHex = "#1E261B",
            textMutedHex = "#4E5B48",
            primaryHex = "#3F6212",
            secondaryHex = "#3730A3",
            warmHex = "#B45309",
            successHex = "#15803D",
          ),
        physics = SurfacePhysics.SOFT_NEUMORPHIC,
        fontPairing = FontPairing.EDITORIAL_SERIF,
        motionFeel = MotionFeel.SILKY_SMOOTH,
        cornerRadiusDp = 16,
        borderWidthDp = 1f,
      ),
      ThemeDeck(
        id = "cobalt_commerce",
        name = "Cobalt Commerce",
        tagline = "Crisp retail storefront with electric royal blue CTA",
        isDark = false,
        category = DiscoverCategory.ECOMMERCE,
        palette =
          ThemePalette(
            canvasHex = "#F4F6FB",
            surfaceHex = "#FFFFFF",
            elevatedSurfaceHex = "#E0E7FF",
            borderHex = "#0F172A",
            textPrimaryHex = "#0F172A",
            textMutedHex = "#475569",
            primaryHex = "#2563EB",
            secondaryHex = "#E11D48",
            warmHex = "#D97706",
            successHex = "#059669",
          ),
        physics = SurfacePhysics.PUFFY_CLAY,
        fontPairing = FontPairing.GEOMETRIC_SANS,
        motionFeel = MotionFeel.SPRING_BOUNCE,
        cornerRadiusDp = 16,
        borderWidthDp = 1.5f,
        gridDensity = GridDensity.COMFORTABLE,
      ),
    )

  val sampleTracks =
    listOf(
      SampleTrack("t1", "Midnight Analog", "Kora & Synth", "Synthwave", "03:42", R.drawable.img_album_synth, "Hi-Res"),
      SampleTrack("t2", "Monstera Morning", "Studio Kyoto", "Lo-Fi", "02:58", R.drawable.img_album_botanical, "Chill"),
      SampleTrack("t3", "Design Systems Ep. 42", "Grid & Ink", "Podcast", "24:15", R.drawable.img_wallpaper_abstract, "New"),
      SampleTrack("t4", "Alpine Echoes", "Nordic Tape", "Ambient", "04:19", R.drawable.img_wallpaper_alpine, "Spatial"),
    )

  val sampleWallpapers =
    listOf(
      SampleWallpaper("w1", "Alpine Golden Dusk", "M. Lindqvist", "4K", true, R.drawable.img_wallpaper_alpine, "Minimal"),
      SampleWallpaper("w2", "Bauhaus Arch #09", "S. Albers", "4K", true, R.drawable.img_wallpaper_abstract, "Vector"),
      SampleWallpaper("w3", "Neon Analog Grid", "V. Synth", "5K", false, R.drawable.img_album_synth, "Dark"),
      SampleWallpaper("w4", "Botanical Sunroom", "H. Sato", "4K", true, R.drawable.img_album_botanical, "Photo"),
    )

  val sampleSaasMetrics =
    mapOf(
      "7D" to
        listOf(
          SampleSaasMetric("Net Balance", "$48.2K", "+12.4%", true),
          SampleSaasMetric("Active Seats", "1,420", "+6.8%", true),
          SampleSaasMetric("Burn Rate", "1.4%", "-0.3%", true),
        ),
      "30D" to
        listOf(
          SampleSaasMetric("Net Balance", "$194.8K", "+18.9%", true),
          SampleSaasMetric("Active Seats", "1,685", "+11.2%", true),
          SampleSaasMetric("Burn Rate", "1.9%", "+0.2%", false),
        ),
      "90D" to
        listOf(
          SampleSaasMetric("Net Balance", "$542.0K", "+31.5%", true),
          SampleSaasMetric("Active Seats", "2,104", "+24.0%", true),
          SampleSaasMetric("Burn Rate", "1.2%", "-0.8%", true),
        ),
    )

  val sampleSaasRows =
    listOf(
      SampleSaasRow("Northstar Labs", "Enterprise", "Settled", 88, "+$4,200"),
      SampleSaasRow("Studio Kanso", "Growth", "Settled", 72, "+$1,450"),
      SampleSaasRow("Vektor Cloud", "Scale", "Pending", 94, "-$680"),
      SampleSaasRow("Boreal Media", "Growth", "Review", 31, "+$890"),
    )

  val sampleSocialPosts =
    listOf(
      SampleSocialPost(
        id = "p1",
        author = "Maya Lin",
        handle = "@mayadesigns",
        timeAgo = "12m",
        body = "Testing our new tactile token ramp across dark & light pairs. Zero contrast regressions!",
        imageRes = R.drawable.img_wallpaper_abstract,
        likes = 128,
        comments = 19,
      ),
      SampleSocialPost(
        id = "p2",
        author = "Arjun Rao",
        handle = "@arjun_ux",
        timeAgo = "1h",
        body = "Morning studio light + warm botanical surfaces for our editorial reader refresh.",
        imageRes = R.drawable.img_album_botanical,
        likes = 84,
        comments = 7,
      ),
    )

  val sampleProducts =
    listOf(
      SampleProduct(
        id = "prod1",
        name = "Tactile Field Desk Lamp",
        subtitle = "Matte anodized aluminum • 2700K–5000K",
        price = "$148",
        rating = "4.9",
        badge = "LIMITED DROP",
        imageRes = R.drawable.img_album_botanical,
        sizes = listOf("Mini", "Desk", "Studio"),
      ),
      SampleProduct(
        id = "prod2",
        name = "Analog Synth Pocket",
        subtitle = "16-step tactile sequencer • USB-C MIDI",
        price = "$229",
        rating = "4.8",
        badge = "BESTSELLER",
        imageRes = R.drawable.img_album_synth,
        sizes = listOf("Standard", "Pro Kit"),
      ),
    )

  val sampleTasks =
    listOf(
      SampleTaskItem("tk1", "Finalize 50–950 tonal scale tokens", "High", "Today", true),
      SampleTaskItem("tk2", "Verify WCAG AA primary contrast", "High", "Today", true),
      SampleTaskItem("tk3", "Export Flutter & Tailwind v4 themes", "Medium", "Tomorrow", false),
      SampleTaskItem("tk4", "Share Dribbble shot spec with client", "Low", "Fri", false),
    )

  val sampleExtractorAssets =
    listOf(
      "Synth" to R.drawable.img_album_synth,
      "Botanical" to R.drawable.img_album_botanical,
      "Alpine" to R.drawable.img_wallpaper_alpine,
      "Bauhaus" to R.drawable.img_wallpaper_abstract,
    )
}
