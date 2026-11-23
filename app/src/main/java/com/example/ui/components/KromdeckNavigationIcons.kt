package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Compare
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KromdeckMode
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckAmberTint
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCoralTint
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckEmeraldTint
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckIndigoTint
import com.example.ui.theme.KromdeckSlateInk

/**
 * Unified specification for each of Kromdeck's six core pillars, shared across
 * the NavigationBar, NavigationRail, and LandingView guide sections so every
 * destination icon, label, and accent remains visually consistent.
 */
data class KromdeckPillarSpec(
  val mode: KromdeckMode,
  val stepNumber: Int,
  val navLabel: String,
  val shortTagline: String,
  val subtitle: String,
  val filledIcon: ImageVector,
  val outlinedIcon: ImageVector,
  val iconContentDescription: String,
  val accentColor: Color,
  val onAccentColor: Color,
  val tintColor: Color,
  val summary: String,
  val keyFunctions: List<Pair<String, String>>,
  val ctaLabel: String,
)

object KromdeckNavigationIcons {

  val allPillars: List<KromdeckPillarSpec> =
    listOf(
      KromdeckPillarSpec(
        mode = KromdeckMode.DISCOVER,
        stepNumber = 1,
        navLabel = "Discover",
        shortTagline = "Browse 27 live looks & extract palettes",
        subtitle = "Dribbble-style inspiration feed & Image Extractor",
        filledIcon = Icons.Filled.Explore,
        outlinedIcon = Icons.Outlined.Explore,
        iconContentDescription = "Discover pillar compass icon",
        accentColor = KromdeckIndigo,
        onAccentColor = Color.White,
        tintColor = KromdeckIndigoTint,
        summary =
          "Explore 27 curated UI looks rendered with live interactive mini-screens. " +
            "Filter by Light/Dark mode, category, or surface style, flip any card's polarity, copy hex dots, or extract a theme from any photo.",
        keyFunctions =
          listOf(
            "Instant Search & Polarity Filters" to "Filter all 27 themes by Light/Dark mode, keyword, app category, or surface physics.",
            "Per-Card Light / Dark Flip" to "Preview the auto-generated companion polarity directly on any feed card.",
            "1-Click Swatch Copy" to "Tap any circular color dot on a card to copy its #HEX value.",
            "Image-to-Theme Extractor" to "Upload a screenshot or pick sample artwork to derive balanced Light & Dark color presets.",
          ),
        ctaLabel = "Open Discover",
      ),
      KromdeckPillarSpec(
        mode = KromdeckMode.STUDIO,
        stepNumber = 2,
        navLabel = "Studio",
        shortTagline = "Infinite Lab, morph blender & 7-tab drawer",
        subtitle = "Resizable preview frame + infinite customization engine",
        filledIcon = Icons.Filled.DesignServices,
        outlinedIcon = Icons.Outlined.DesignServices,
        iconContentDescription = "Studio pillar design tools icon",
        accentColor = KromdeckSlateInk,
        onAccentColor = Color.White,
        tintColor = KromdeckCreamCanvas,
        summary =
          "Your primary design workbench with infinite possibilities. Use the Infinite Lab to generate 360° color harmonies, " +
            "morph-blend any 2 themes from 0–100%, grade hue/saturation/warmth globally, or apply 1-tap mood atmospheres.",
        keyFunctions =
          listOf(
            "Infinite Lab (Harmonies, DNA Blender & Grading)" to "Generate 6 color-wheel harmonies from any hue, morph any 2 themes continuously, adjust global hue/saturation/temperature, or apply 6 mood transforms.",
            "Responsive Viewport & Resize Handle" to "Switch Mobile/Tablet/Desktop or drag the amber edge handle with live dp width readout.",
            "6 Interactive App Templates & Vision Sim" to "Test live interactive apps with Split Wipe and Protanopia/Deuteranopia/Tritanopia filters.",
            "8-Format Export & DataStore Persist" to "Export JSON, Android XML, Jetpack Compose, CSS, Tailwind v4, Flutter, SwiftUI, and AI Prompts.",
          ),
        ctaLabel = "Open Studio",
      ),
      KromdeckPillarSpec(
        mode = KromdeckMode.COMPARE,
        stepNumber = 3,
        navLabel = "Compare",
        shortTagline = "A/B split, wipe & 1v1 tournament",
        subtitle = "Side-by-side evaluation & Design DNA leaderboard",
        filledIcon = Icons.Filled.Compare,
        outlinedIcon = Icons.Outlined.Compare,
        iconContentDescription = "Compare pillar split-view icon",
        accentColor = KromdeckCoral,
        onAccentColor = Color.White,
        tintColor = KromdeckCoralTint,
        summary =
          "Eliminate UI selection paralysis for solo builds or client reviews. Compare any two themes side-by-side, " +
            "drag an A/B wipe slider, or run a fast 1v1 elimination bracket to uncover your preferred Design DNA.",
        keyFunctions =
          listOf(
            "Side-by-Side A/B Split" to "Inspect Deck A and Deck B simultaneously on the exact same interactive app template.",
            "Interactive Wipe Slider" to "Drag across a single shared viewport to compare A on the left with B on the right.",
            "This-or-That Tournament" to "8-deck 1v1 bracket where quick choices crown a single winning design system.",
            "Design DNA Leaderboard" to "Tracks win-loss records across surface styles, font pairings, and Light/Dark polarity.",
          ),
        ctaLabel = "Open Compare",
      ),
      KromdeckPillarSpec(
        mode = KromdeckMode.COMPONENTS,
        stepNumber = 4,
        navLabel = "Components",
        shortTagline = "Live UI kit & interactive state tester",
        subtitle = "Specimen sheet for buttons, inputs, alerts & dialogs",
        filledIcon = Icons.Filled.Widgets,
        outlinedIcon = Icons.Outlined.Widgets,
        iconContentDescription = "Components pillar UI blocks icon",
        accentColor = KromdeckIndigo,
        onAccentColor = Color.White,
        tintColor = KromdeckIndigoTint,
        summary =
          "Audit every core UI primitive under your active theme before writing code. Switch between Default, Focused, " +
            "and Disabled states across buttons, form inputs, toggles, badges, alerts, skeletons, and modals.",
        keyFunctions =
          listOf(
            "State Mode Switcher" to "Preview Default, Focused, and Disabled visual states across the entire specimen sheet.",
            "Buttons & Form Inputs" to "Check Primary, Secondary, Ghost, and Disabled buttons alongside outlined inputs and search bars.",
            "Controls, Badges & Alerts" to "Test switches, checkboxes, radio pills, status badges, and semantic alert banners.",
            "Modal & Skeleton Preview" to "Open a themed preview dialog and inspect loading skeleton states.",
          ),
        ctaLabel = "Open Components",
      ),
      KromdeckPillarSpec(
        mode = KromdeckMode.TOKENS,
        stepNumber = 5,
        navLabel = "Tokens",
        shortTagline = "Export JSON/XML, 50–950 ramps & WCAG",
        subtitle = "1-tap JSON & XML export, scales & contrast matrix",
        filledIcon = Icons.Filled.Palette,
        outlinedIcon = Icons.Outlined.Palette,
        iconContentDescription = "Tokens pillar color palette icon",
        accentColor = KromdeckEmerald,
        onAccentColor = Color.White,
        tintColor = KromdeckEmeraldTint,
        summary =
          "Export your active theme into JSON (W3C Design Tokens), Android XML, Compose, SwiftUI, CSS, Tailwind, or Flutter in one tap, " +
            "and inspect 50–950 tonal ramps, typography specimens, spacing scales, and the WCAG 2.1 contrast matrix.",
        keyFunctions =
          listOf(
            "1-Tap JSON & XML Export Center" to "Copy, save .json/.xml files, or share complete Light/Dark tokens, tonal ramps, spacing, and typography.",
            "50–950 Tonal Shade Ramps" to "Auto-generated 11-step ramps for Primary, Secondary, and Neutral tokens with 1-tap hex copy.",
            "Typography, Spacing & Radius Scales" to "Displays live font hierarchy and density-scaled spacing (4dp–48dp) plus sm/md/lg corner radii.",
            "WCAG 2.1 Contrast Matrix" to "Audits Text, Muted, Primary, and Secondary against Canvas, Surface, and Elevated cards (AAA/AA/AA18/FAIL).",
          ),
        ctaLabel = "Open Tokens",
      ),
      KromdeckPillarSpec(
        mode = KromdeckMode.SAVED,
        stepNumber = 6,
        navLabel = "Saved",
        shortTagline = "Moodboard, notes & DataStore persistence",
        subtitle = "Shortlist presets, stakeholder notes & JSON backups",
        filledIcon = Icons.Filled.Bookmarks,
        outlinedIcon = Icons.Outlined.Bookmarks,
        iconContentDescription = "Saved pillar moodboard bookmarks icon",
        accentColor = KromdeckAmber,
        onAccentColor = KromdeckSlateInk,
        tintColor = KromdeckAmberTint,
        summary =
          "Your personal moodboard and review tray. Pin custom or curated themes to persistent DataStore storage, " +
            "attach stakeholder notes, and route any saved deck straight into Studio, Compare, or Split Wipe.",
        keyFunctions =
          listOf(
            "Save Active Look" to "Pin your current Studio theme to your moodboard and persist it in DataStore.",
            "Stakeholder Review Notes" to "Attach short review notes (e.g., 'Approved for Q3 release') to each saved card.",
            "1-Tap Workflow Routing" to "Load any saved look into Studio, Compare Slot A, or Studio Split Wipe Baseline.",
            "JSON Backup & Share Hash" to "Export your entire saved moodboard to JSON or share compact #theme= links.",
          ),
        ctaLabel = "Open Saved",
      ),
    )

  private val byMode: Map<KromdeckMode, KromdeckPillarSpec> = allPillars.associateBy { it.mode }

  fun forMode(mode: KromdeckMode): KromdeckPillarSpec =
    byMode[mode] ?: error("Missing KromdeckPillarSpec for mode $mode")

  fun iconFor(mode: KromdeckMode, selected: Boolean): ImageVector {
    val spec = forMode(mode)
    return if (selected) spec.filledIcon else spec.outlinedIcon
  }
}

@Composable
fun PillarIconBadge(
  spec: KromdeckPillarSpec,
  modifier: Modifier = Modifier,
  boxSize: Dp = 40.dp,
  iconSize: Dp = 20.dp,
  cornerRadius: Dp = 10.dp,
) {
  Box(
    modifier =
      modifier
        .size(boxSize)
        .clip(RoundedCornerShape(cornerRadius))
        .background(spec.accentColor)
        .border(2.dp, KromdeckSlateInk, RoundedCornerShape(cornerRadius)),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      imageVector = spec.filledIcon,
      contentDescription = spec.iconContentDescription,
      tint = spec.onAccentColor,
      modifier = Modifier.size(iconSize),
    )
  }
}

@Composable
fun PillarNavIcon(
  mode: KromdeckMode,
  selected: Boolean,
  savedCount: Int,
  modifier: Modifier = Modifier,
) {
  val spec = KromdeckNavigationIcons.forMode(mode)
  val iconVector = if (selected) spec.filledIcon else spec.outlinedIcon
  if (mode == KromdeckMode.SAVED && savedCount > 0) {
    BadgedBox(
      modifier = modifier,
      badge = {
        Badge(containerColor = KromdeckCoral, contentColor = Color.White) {
          Text(
            text = savedCount.toString(),
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
          )
        }
      },
    ) {
      Icon(
        imageVector = iconVector,
        contentDescription = spec.iconContentDescription,
        modifier = Modifier.size(22.dp),
      )
    }
  } else {
    Icon(
      imageVector = iconVector,
      contentDescription = spec.iconContentDescription,
      modifier = modifier.size(22.dp),
    )
  }
}
