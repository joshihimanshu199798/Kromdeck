package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.KromdeckMode
import com.example.ui.components.KromdeckLogoMark
import com.example.ui.components.KromdeckNavigationIcons
import com.example.ui.components.KromdeckPillarSpec
import com.example.ui.components.PillarIconBadge
import com.example.ui.components.TactileButton
import com.example.ui.components.TactileCard
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckIndigoTint
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk

private data class ToolbarControlInfo(
  val icon: ImageVector,
  val name: String,
  val description: String,
  val accent: Color,
)

@Composable
fun LandingView(
  onNavigateToMode: (KromdeckMode) -> Unit,
  onOpenImageExtractor: () -> Unit,
  onOpenShareBackup: () -> Unit,
  onOpenStudioExports: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val pillars = remember { KromdeckNavigationIcons.allPillars }
  val toolbarControls = remember { buildToolbarControls() }
  var expandedModes by remember {
    mutableStateOf(setOf(KromdeckMode.DISCOVER, KromdeckMode.STUDIO))
  }

  LazyColumn(
    modifier = modifier.fillMaxSize().testTag("landing_view"),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // 1. HERO OVERVIEW
    item {
      HeroIntroCard(
        onNavigateToMode = onNavigateToMode,
      )
    }

    // 2. SIX CORE PILLARS AT A GLANCE (Mirrors NavigationBar Icons 1:1)
    item {
      SixPillarsOverviewGrid(
        pillars = pillars,
        onSelectPillar = onNavigateToMode,
      )
    }

    // 3. 4-STEP WORKFLOW
    item {
      WorkflowGuideCard()
    }

    // 4. DEEP-DIVE PILLAR GUIDES HEADER
    item {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "All 6 Core Pillars Explained",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
          Text(
            text = "Each section matches its Navigation Bar icon below. Tap to expand or launch.",
            style = MaterialTheme.typography.bodySmall,
            color = KromdeckMutedInk,
          )
        }
        val allExpanded = expandedModes.size == pillars.size
        TactileButton(
          onClick = {
            expandedModes =
              if (allExpanded) emptySet() else pillars.map { it.mode }.toSet()
          },
          containerColor = KromdeckPureSurface,
          contentColor = KromdeckSlateInk,
          shadowOffset = 2.dp,
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          testTag = "landing_expand_all_btn",
        ) {
          Text(
            text = if (allExpanded) "Collapse All" else "Expand All",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
          )
        }
      }
    }

    // 5. EXPANDABLE GUIDE CARDS FOR EACH PILLAR
    pillars.forEach { spec ->
      item(key = spec.mode.name) {
        val isExpanded = spec.mode in expandedModes
        PillarGuideCard(
          spec = spec,
          expanded = isExpanded,
          onToggleExpand = {
            expandedModes =
              if (isExpanded) expandedModes - spec.mode else expandedModes + spec.mode
          },
          onLaunch = { onNavigateToMode(spec.mode) },
        )
      }
    }

    // 6. GLOBAL TOOLBAR CONTROLS EXPLAINED
    item {
      ToolbarControlsCard(controls = toolbarControls)
    }

    // 7. QUICK SCENARIO CHEAT SHEET
    item {
      QuickCheatSheetCard(
        onNavigateToMode = onNavigateToMode,
        onOpenImageExtractor = onOpenImageExtractor,
        onOpenStudioExports = onOpenStudioExports,
        onOpenShareBackup = onOpenShareBackup,
      )
    }

    // 8. BOTTOM LAUNCH CTA
    item {
      TactileButton(
        onClick = { onNavigateToMode(KromdeckMode.DISCOVER) },
        containerColor = KromdeckEmerald,
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth(),
        testTag = "landing_bottom_launch_btn",
      ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Got It — Enter Kromdeck Workspace", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
      }
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }
}

@Composable
private fun HeroIntroCard(
  onNavigateToMode: (KromdeckMode) -> Unit,
) {
  val discoverSpec = remember { KromdeckNavigationIcons.forMode(KromdeckMode.DISCOVER) }
  val studioSpec = remember { KromdeckNavigationIcons.forMode(KromdeckMode.STUDIO) }

  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = Modifier.fillMaxWidth().testTag("landing_hero_card"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        KromdeckLogoMark()
        Column {
          Box(
            modifier =
              Modifier.clip(RoundedCornerShape(999.dp))
                .background(KromdeckIndigoTint)
                .border(1.5.dp, KromdeckIndigo, RoundedCornerShape(999.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp),
          ) {
            Text(
              text = "INTERACTIVE DESIGN STUDIO GUIDE",
              fontFamily = JetBrainsMonoFamily,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckIndigo,
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Welcome to Kromdeck",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
        }
      }

      Image(
        painter = painterResource(id = R.drawable.img_landing_hero),
        contentDescription = "Kromdeck fanned swatch cards and multi-device wireframe preview",
        contentScale = ContentScale.Crop,
        modifier =
          Modifier.fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .border(BorderStroke(2.dp, KromdeckSlateInk), RoundedCornerShape(12.dp)),
      )

      Text(
        text = "What does Kromdeck do?",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = KromdeckSlateInk,
      )
      Text(
        text =
          "Kromdeck eliminates UI selection paralysis. Browse real app themes, customize colors, shadows, and Google Fonts " +
            "on live multi-device previews, run 1v1 A/B tournaments, check WCAG 2.1 contrast, and export production-ready " +
            "tokens in one tap.",
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 20.sp,
        color = KromdeckMutedInk,
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        TactileButton(
          onClick = { onNavigateToMode(KromdeckMode.DISCOVER) },
          containerColor = KromdeckIndigo,
          contentColor = Color.White,
          modifier = Modifier.weight(1f),
          testTag = "landing_cta_discover",
        ) {
          Icon(discoverSpec.filledIcon, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Explore Themes", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
        }
        TactileButton(
          onClick = { onNavigateToMode(KromdeckMode.STUDIO) },
          containerColor = KromdeckAmber,
          contentColor = KromdeckSlateInk,
          modifier = Modifier.weight(1f),
          testTag = "landing_cta_studio",
        ) {
          Icon(studioSpec.filledIcon, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Open Studio", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
private fun SixPillarsOverviewGrid(
  pillars: List<KromdeckPillarSpec>,
  onSelectPillar: (KromdeckMode) -> Unit,
) {
  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = Modifier.fillMaxWidth().testTag("landing_six_pillars_card"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Text(
        text = "Navigation Bar: The 6 Core Pillars",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = KromdeckSlateInk,
      )
      Text(
        text = "Every icon in the bottom navigation bar maps to one core pillar—tap any tile to jump straight there:",
        style = MaterialTheme.typography.bodySmall,
        color = KromdeckMutedInk,
      )

      pillars.chunked(2).forEach { rowSpecs ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          rowSpecs.forEach { spec ->
            Column(
              modifier =
                Modifier.weight(1f)
                  .defaultMinSize(minHeight = 96.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(spec.tintColor)
                  .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(12.dp))
                  .clickable { onSelectPillar(spec.mode) }
                  .padding(10.dp)
                  .testTag("pillar_directory_${spec.mode.name.lowercase()}"),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  PillarIconBadge(spec = spec, boxSize = 32.dp, iconSize = 17.dp, cornerRadius = 8.dp)
                  Text(
                    text = spec.navLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = KromdeckSlateInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = "Go to ${spec.navLabel}",
                  tint = KromdeckSlateInk,
                  modifier = Modifier.size(14.dp),
                )
              }
              Text(
                text = spec.shortTagline,
                style = MaterialTheme.typography.bodySmall,
                color = KromdeckMutedInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun WorkflowGuideCard() {
  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = Modifier.fillMaxWidth().testTag("landing_workflow_card"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Text(
        text = "How Kromdeck Works (4 Steps)",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = KromdeckSlateInk,
      )
      WorkflowStepRow(
        stepNumber = "1",
        title = "Discover or Extract from an Image",
        description = "Browse 15 curated themes in Discover or extract balanced Light & Dark palettes from any photo.",
        accent = KromdeckIndigo,
      )
      WorkflowStepRow(
        stepNumber = "2",
        title = "Customize Live in Studio",
        description = "Resize Mobile/Tablet/Desktop viewports, test 6 interactive apps, and tune colors, surfaces, fonts & motion.",
        accent = KromdeckCoral,
      )
      WorkflowStepRow(
        stepNumber = "3",
        title = "Compare, Decide & Verify Contrast",
        description = "Run A/B Split, Wipe Slider, or 1v1 Tournaments in Compare and verify Components & Tokens WCAG ratios.",
        accent = KromdeckAmber,
      )
      WorkflowStepRow(
        stepNumber = "4",
        title = "Save, Share & Export Code",
        description = "Pin favorites to Saved, share #theme links, or export AI Prompts, CSS, Tailwind v4, Flutter, JSON & XML.",
        accent = KromdeckEmerald,
      )
    }
  }
}

@Composable
private fun WorkflowStepRow(
  stepNumber: String,
  title: String,
  description: String,
  accent: Color,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Top,
  ) {
    Box(
      modifier =
        Modifier.size(30.dp)
          .clip(CircleShape)
          .background(accent)
          .border(2.dp, KromdeckSlateInk, CircleShape),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = stepNumber,
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 13.sp,
        color = if (accent == KromdeckAmber) KromdeckSlateInk else Color.White,
      )
    }
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
      Text(description, style = MaterialTheme.typography.bodySmall, color = KromdeckMutedInk)
    }
  }
}

@Composable
private fun PillarGuideCard(
  spec: KromdeckPillarSpec,
  expanded: Boolean,
  onToggleExpand: () -> Unit,
  onLaunch: () -> Unit,
) {
  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = Modifier.fillMaxWidth().testTag("guide_card_${spec.mode.name.lowercase()}"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clickable(onClick = onToggleExpand),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          PillarIconBadge(spec = spec, boxSize = 42.dp, iconSize = 22.dp, cornerRadius = 10.dp)
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "${spec.stepNumber}. ${spec.navLabel}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckSlateInk,
            )
            Text(
              text = spec.subtitle,
              style = MaterialTheme.typography.labelSmall,
              color = KromdeckMutedInk,
            )
          }
        }
        Icon(
          imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
          contentDescription = if (expanded) "Collapse ${spec.navLabel} details" else "Expand ${spec.navLabel} details",
          tint = KromdeckSlateInk,
          modifier = Modifier.size(24.dp),
        )
      }

      Text(
        text = spec.summary,
        style = MaterialTheme.typography.bodySmall,
        color = KromdeckSlateInk,
      )

      AnimatedVisibility(visible = expanded) {
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(spec.tintColor)
              .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(12.dp))
              .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = "KEY CAPABILITIES IN ${spec.navLabel.uppercase()}:",
            fontFamily = JetBrainsMonoFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
          spec.keyFunctions.forEach { (fnName, fnDesc) ->
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
              Text(
                text = "• $fnName",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = KromdeckSlateInk,
              )
              Text(
                text = fnDesc,
                style = MaterialTheme.typography.bodySmall,
                color = KromdeckMutedInk,
                modifier = Modifier.padding(start = 10.dp),
              )
            }
          }
        }
      }

      TactileButton(
        onClick = onLaunch,
        containerColor = spec.accentColor,
        contentColor = spec.onAccentColor,
        shadowOffset = 2.dp,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxWidth(),
        testTag = "guide_try_${spec.mode.name.lowercase()}",
      ) {
        Icon(spec.filledIcon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(spec.ctaLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
      }
    }
  }
}

@Composable
private fun ToolbarControlsCard(controls: List<ToolbarControlInfo>) {
  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = Modifier.fillMaxWidth().testTag("landing_header_guide_card"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Text(
        text = "Global Top Toolbar Controls",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = KromdeckSlateInk,
      )
      Text(
        text = "Always available at the top of the workspace:",
        style = MaterialTheme.typography.bodySmall,
        color = KromdeckMutedInk,
      )

      controls.forEach { item ->
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(KromdeckCreamCanvas)
              .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(10.dp))
              .padding(10.dp),
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Box(
            modifier =
              Modifier.size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(item.accent)
                .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              item.icon,
              contentDescription = null,
              tint = if (item.accent == KromdeckAmber) KromdeckSlateInk else Color.White,
              modifier = Modifier.size(18.dp),
            )
          }
          Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(item.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
            Text(item.description, style = MaterialTheme.typography.bodySmall, color = KromdeckMutedInk)
          }
        }
      }
    }
  }
}

@Composable
private fun QuickCheatSheetCard(
  onNavigateToMode: (KromdeckMode) -> Unit,
  onOpenImageExtractor: () -> Unit,
  onOpenStudioExports: () -> Unit,
  onOpenShareBackup: () -> Unit,
) {
  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = Modifier.fillMaxWidth().testTag("landing_cheatsheet_card"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Text(
        text = "Quick Cheat Sheet: Where Should I Start?",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = KromdeckSlateInk,
      )
      QuickScenarioRow(
        icon = Icons.Filled.ImageSearch,
        accent = KromdeckAmber,
        question = "\"I want colors extracted from a photo or screenshot\"",
        answer = "Launch Image-to-Theme Extractor to build balanced Light & Dark palettes.",
        actionLabel = "Extract",
        onClick = onOpenImageExtractor,
      )
      QuickScenarioRow(
        icon = Icons.Filled.EmojiEvents,
        accent = KromdeckCoral,
        question = "\"My team or I can't pick between two themes\"",
        answer = "Run the 1v1 This-or-That Tournament to reveal your winning Design DNA.",
        actionLabel = "Compare",
        onClick = { onNavigateToMode(KromdeckMode.COMPARE) },
      )
      QuickScenarioRow(
        icon = Icons.Filled.Visibility,
        accent = KromdeckIndigo,
        question = "\"Will my theme stay readable for color-blind users?\"",
        answer = "Test Vision Sim in Studio and inspect the WCAG 2.1 Matrix inside Tokens.",
        actionLabel = "Studio",
        onClick = { onNavigateToMode(KromdeckMode.STUDIO) },
      )
      QuickScenarioRow(
        icon = Icons.Filled.Code,
        accent = KromdeckEmerald,
        question = "\"I'm ready to ship tokens into code\"",
        answer = "Open Studio Drawer -> WCAG & Export for AI Prompts, CSS, Tailwind, Flutter, JSON & XML.",
        actionLabel = "Exports",
        onClick = onOpenStudioExports,
      )
      QuickScenarioRow(
        icon = Icons.Filled.Share,
        accent = KromdeckSlateInk,
        question = "\"I want to share this preset with a teammate\"",
        answer = "Copy a #theme= link or export a full JSON backup via Share / JSON.",
        actionLabel = "Share",
        onClick = onOpenShareBackup,
      )
    }
  }
}

@Composable
private fun QuickScenarioRow(
  icon: ImageVector,
  accent: Color,
  question: String,
  answer: String,
  actionLabel: String,
  onClick: () -> Unit,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(KromdeckCreamCanvas)
        .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(12.dp))
        .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Box(
      modifier =
        Modifier.size(34.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(accent)
          .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(8.dp)),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        icon,
        contentDescription = null,
        tint = if (accent == KromdeckAmber) KromdeckSlateInk else Color.White,
        modifier = Modifier.size(18.dp),
      )
    }
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(question, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
      Text(answer, style = MaterialTheme.typography.bodySmall, color = KromdeckMutedInk)
    }
    TactileButton(
      onClick = onClick,
      containerColor = KromdeckPureSurface,
      contentColor = KromdeckSlateInk,
      shadowOffset = 2.dp,
      cornerRadius = 8.dp,
      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
      testTag = "cheatsheet_${actionLabel.lowercase()}",
    ) {
      Text(actionLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
    }
  }
}

private fun buildToolbarControls(): List<ToolbarControlInfo> =
  listOf(
    ToolbarControlInfo(
      icon = Icons.Filled.Contrast,
      name = "Dark / Light Pair Flip",
      description = "Flips your active theme between Dark and Light companion palettes while preserving WCAG contrast.",
      accent = KromdeckSlateInk,
    ),
    ToolbarControlInfo(
      icon = Icons.AutoMirrored.Filled.Undo,
      name = "Undo & Redo Stack",
      description = "Every color, font, surface, or Dice edit is tracked (up to 30 steps) so you can revert freely.",
      accent = KromdeckIndigo,
    ),
    ToolbarControlInfo(
      icon = Icons.Filled.Casino,
      name = "Dice (Smart Randomizer)",
      description = "Rolls a fresh, contrast-verified palette, font pairing, and surface style in one tap.",
      accent = KromdeckCoral,
    ),
    ToolbarControlInfo(
      icon = Icons.Filled.Lock,
      name = "Parameter Locks",
      description = "Lock Palette, Typography, or Surface so Dice only randomizes the dimensions you leave unlocked.",
      accent = KromdeckAmber,
    ),
    ToolbarControlInfo(
      icon = Icons.Filled.Share,
      name = "Share / JSON Backup",
      description = "Generates a shareable #theme= hash link or exports/imports full JSON theme backups.",
      accent = KromdeckEmerald,
    ),
  )
