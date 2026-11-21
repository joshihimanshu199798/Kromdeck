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
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Widgets
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.KromdeckMode
import com.example.ui.components.KromdeckLogoMark
import com.example.ui.components.TactileButton
import com.example.ui.components.TactileCard
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckAmberTint
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCoralTint
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckEmeraldTint
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckIndigoTint
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk

private data class FeatureGuideItem(
  val mode: KromdeckMode,
  val title: String,
  val subtitle: String,
  val accentColor: Color,
  val tintColor: Color,
  val icon: ImageVector,
  val summary: String,
  val functions: List<Pair<String, String>>,
  val ctaLabel: String,
)

private data class HeaderControlGuide(
  val icon: ImageVector,
  val name: String,
  val description: String,
  val accent: Color,
)

@Composable
fun LandingPageScreen(
  onNavigateToMode: (KromdeckMode) -> Unit,
  onOpenImageExtractor: () -> Unit,
  onOpenShareBackup: () -> Unit,
  onOpenStudioExports: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val featureGuides = remember { buildFeatureGuides() }
  val headerGuides = remember { buildHeaderControlGuides() }
  var expandedModes by remember {
    mutableStateOf(setOf(KromdeckMode.DISCOVER, KromdeckMode.STUDIO))
  }

  LazyColumn(
    modifier = modifier.fillMaxSize().testTag("landing_page_screen"),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // 1. HERO SECTION
    item {
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
                  text = "COMPLETE PRODUCT GUIDE",
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
            text = "What is Kromdeck?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
          Text(
            text =
              "Kromdeck is an all-in-one UI theme playground, design system studio, and developer handoff tool. " +
                "Instead of guessing hex codes, fonts, or shadows—or wasting hours tweaking CSS—you can browse real app looks, " +
                "customize every token on live interactive screens, compare styles head-to-head, verify WCAG accessibility, " +
                "and export production-ready code in one tap.",
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
              Text("Explore Themes", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(15.dp))
            }
            TactileButton(
              onClick = { onNavigateToMode(KromdeckMode.STUDIO) },
              containerColor = KromdeckAmber,
              contentColor = KromdeckSlateInk,
              modifier = Modifier.weight(1f),
              testTag = "landing_cta_studio",
            ) {
              Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Open Studio", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            }
          }
        }
      }
    }

    // 2. HOW IT WORKS IN 4 SIMPLE STEPS
    item {
      TactileCard(
        containerColor = KromdeckPureSurface,
        modifier = Modifier.fillMaxWidth().testTag("landing_workflow_card"),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Text(
            text = "How Kromdeck Works (4 Simple Steps)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
          Text(
            text = "Follow this flow from raw idea to developer-ready design system:",
            style = MaterialTheme.typography.bodySmall,
            color = KromdeckMutedInk,
          )

          WorkflowStepRow(
            stepNumber = "1",
            title = "Pick a Look or Extract from Any Image",
            description = "Start in Discover to browse 15 curated themes, or upload any screenshot/photo to extract a balanced Light & Dark color palette automatically.",
            accent = KromdeckIndigo,
          )
          WorkflowStepRow(
            stepNumber = "2",
            title = "Customize Live in Studio",
            description = "Resize between Mobile, Tablet & Desktop, test 6 real interactive app templates, and open the 6-tab Sidebar Drawer to fine-tune colors, shadows, Google Fonts, layout & motion.",
            accent = KromdeckCoral,
          )
          WorkflowStepRow(
            stepNumber = "3",
            title = "Compare, Decide & Audit Accessibility",
            description = "Use A/B Split, the draggable Wipe Slider, or the 1v1 This-or-That Tournament to eliminate indecision—while WCAG 2.1 checks ensure readable contrast.",
            accent = KromdeckAmber,
          )
          WorkflowStepRow(
            stepNumber = "4",
            title = "Persist, Share & Export Code",
            description = "Save to your Moodboard or DataStore, share a #theme link, export Dribbble PNG/SVG spec cards, or copy AI Prompts, CSS, Tailwind v4, Flutter, Figma JSON & Android XML.",
            accent = KromdeckEmerald,
          )
        }
      }
    }

    // 3. TOP BAR CONTROLS EXPLAINED
    item {
      TactileCard(
        containerColor = KromdeckPureSurface,
        modifier = Modifier.fillMaxWidth().testTag("landing_header_guide_card"),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Text(
            text = "Top Toolbar Buttons Explained",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
          Text(
            text = "These global controls stay accessible across every screen:",
            style = MaterialTheme.typography.bodySmall,
            color = KromdeckMutedInk,
          )

          headerGuides.forEach { item ->
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
                Icon(item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              }
              Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                  text = item.name,
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.ExtraBold,
                  color = KromdeckSlateInk,
                )
                Text(
                  text = item.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = KromdeckMutedInk,
                )
              }
            }
          }
        }
      }
    }

    // 4. SECTION HEADER: EVERY VIEW & FUNCTION EXPLAINED
    item {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "All 6 Views & Every Function",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
          Text(
            text = "Tap any card to expand or collapse details, or tap its button to jump right in.",
            style = MaterialTheme.typography.bodySmall,
            color = KromdeckMutedInk,
          )
        }
        val allExpanded = expandedModes.size == featureGuides.size
        TactileButton(
          onClick = {
            expandedModes =
              if (allExpanded) emptySet()
              else featureGuides.map { it.mode }.toSet()
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

    // 5. EXPANDABLE FEATURE GUIDE CARDS FOR EACH OF THE 6 VIEWS
    featureGuides.forEach { guide ->
      item(key = guide.mode.name) {
        val isExpanded = guide.mode in expandedModes
        FeatureGuideCard(
          guide = guide,
          expanded = isExpanded,
          onToggleExpand = {
            expandedModes =
              if (isExpanded) expandedModes - guide.mode
              else expandedModes + guide.mode
          },
          onTryNow = { onNavigateToMode(guide.mode) },
        )
      }
    }

    // 6. QUICK CHEAT SHEET: "WHAT SHOULD I USE?"
    item {
      TactileCard(
        containerColor = KromdeckPureSurface,
        modifier = Modifier.fillMaxWidth().testTag("landing_cheatsheet_card"),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Text(
            text = "Quick Cheat Sheet: Which Tool Should I Use?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )

          QuickScenarioRow(
            icon = Icons.Filled.ImageSearch,
            accent = KromdeckAmber,
            question = "\"I found a screenshot or photo whose colors I love\"",
            answer = "Use Image-to-Theme Extractor to generate a balanced Light & Dark preset instantly.",
            actionLabel = "Extract",
            onClick = onOpenImageExtractor,
          )
          QuickScenarioRow(
            icon = Icons.Filled.EmojiEvents,
            accent = KromdeckCoral,
            question = "\"My client or I can't decide between styles\"",
            answer = "Run the 1v1 This-or-That Elimination Tournament to find the winning style & Design DNA.",
            actionLabel = "Compare",
            onClick = { onNavigateToMode(KromdeckMode.COMPARE) },
          )
          QuickScenarioRow(
            icon = Icons.Filled.Visibility,
            accent = KromdeckIndigo,
            question = "\"Will color-blind users be able to read my UI?\"",
            answer = "Open Studio -> Vision Sim (Protanopia/Deuteranopia/Tritanopia) & check Tokens -> WCAG Matrix.",
            actionLabel = "Studio",
            onClick = { onNavigateToMode(KromdeckMode.STUDIO) },
          )
          QuickScenarioRow(
            icon = Icons.Filled.Code,
            accent = KromdeckEmerald,
            question = "\"I want to paste this theme into my code or AI Builder\"",
            answer = "Open Studio Drawer -> WCAG & Export tab to copy AI Prompts, CSS, Tailwind v4, Flutter, JSON or XML.",
            actionLabel = "Exports",
            onClick = onOpenStudioExports,
          )
          QuickScenarioRow(
            icon = Icons.Filled.Share,
            accent = KromdeckSlateInk,
            question = "\"I want to send my exact theme to a teammate\"",
            answer = "Open Share / JSON to copy a #theme=... link or download a full JSON backup file.",
            actionLabel = "Share",
            onClick = onOpenShareBackup,
          )
        }
      }
    }

    // 7. BOTTOM LAUNCH CTA
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
private fun FeatureGuideCard(
  guide: FeatureGuideItem,
  expanded: Boolean,
  onToggleExpand: () -> Unit,
  onTryNow: () -> Unit,
) {
  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = Modifier.fillMaxWidth().testTag("guide_card_${guide.mode.name.lowercase()}"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      // Header row (clickable to expand/collapse)
      Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggleExpand),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Box(
            modifier =
              Modifier.size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(guide.accentColor)
                .border(2.dp, KromdeckSlateInk, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(guide.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(guide.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
            Text(guide.subtitle, style = MaterialTheme.typography.labelSmall, color = KromdeckMutedInk)
          }
        }
        Icon(
          imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
          contentDescription = if (expanded) "Collapse details" else "Expand details",
          tint = KromdeckSlateInk,
          modifier = Modifier.size(24.dp),
        )
      }

      Text(
        text = guide.summary,
        style = MaterialTheme.typography.bodySmall,
        color = KromdeckSlateInk,
      )

      AnimatedVisibility(visible = expanded) {
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(guide.tintColor)
              .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(12.dp))
              .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = "EVERY FUNCTION INSIDE ${guide.mode.label.uppercase()}:",
            fontFamily = JetBrainsMonoFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
          guide.functions.forEach { (fnName, fnDesc) ->
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
        onClick = onTryNow,
        containerColor = guide.accentColor,
        contentColor = Color.White,
        shadowOffset = 2.dp,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxWidth(),
        testTag = "guide_try_${guide.mode.name.lowercase()}",
      ) {
        Text(guide.ctaLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
      }
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

private fun buildHeaderControlGuides(): List<HeaderControlGuide> =
  listOf(
    HeaderControlGuide(
      icon = Icons.Filled.Contrast,
      name = "Dark / Light Pair Flip",
      description = "Instantly generates the matching Dark or Light companion palette of your active theme while automatically preserving readable WCAG contrast.",
      accent = KromdeckSlateInk,
    ),
    HeaderControlGuide(
      icon = Icons.AutoMirrored.Filled.Undo,
      name = "Undo & Redo Stack",
      description = "Every color edit, font change, surface toggle, or Dice roll is recorded (up to 30 steps). Tap Undo or Redo to experiment fearlessly.",
      accent = KromdeckIndigo,
    ),
    HeaderControlGuide(
      icon = Icons.Filled.Casino,
      name = "Dice (Smart Randomizer)",
      description = "Generates a fresh, harmonious color palette, font pairing, surface physics, and motion feel in one click—always WCAG-checked.",
      accent = KromdeckCoral,
    ),
    HeaderControlGuide(
      icon = Icons.Filled.Lock,
      name = "Locks (Palette / Typography / Surface)",
      description = "Love your current colors but want new fonts? Lock Palette before tapping Dice so only unlocked dimensions randomize.",
      accent = KromdeckAmber,
    ),
    HeaderControlGuide(
      icon = Icons.Filled.Share,
      name = "Share / JSON Backup",
      description = "Encodes your entire active theme into a compact #theme=... share link, lets you paste links from teammates, or exports/imports a JSON backup file.",
      accent = KromdeckEmerald,
    ),
  )

private fun buildFeatureGuides(): List<FeatureGuideItem> =
  listOf(
    FeatureGuideItem(
      mode = KromdeckMode.DISCOVER,
      title = "1. Discover — Curated Inspiration Feed",
      subtitle = "Browse 15 complete app looks & extract palettes from images",
      accentColor = KromdeckIndigo,
      tintColor = KromdeckIndigoTint,
      icon = Icons.Filled.Explore,
      summary = "Start here when you want instant visual inspiration. Every card renders a live mini-UI using real colors, borders, shadows, and fonts—not flat static mockups.",
      functions =
        listOf(
          "Instant Search & Filters" to "Filter all 15 looks by keyword, app category (Music, Gallery, SaaS, Social, E-Commerce, Minimal), or surface style (Flat, Soft Shadow, Glassmorphism, Neo-Brutalist, Outlined).",
          "Per-Card Light / Dark Flip" to "Tap the Light/Dark pill on any card to preview its auto-generated companion theme right inside the grid.",
          "1-Click Swatch Copy" to "Tap any circular color dot on a card to copy its #HEX code directly to your clipboard.",
          "Heart / Save to Moodboard" to "Bookmark any look into your persistent Saved Moodboard for later review.",
          "Image-to-Theme Extractor" to "Tap 'Extract' to pick any screenshot/photo from your device (or sample built-in artworks) and auto-build balanced Light & Dark presets.",
          "Open in Studio" to "Loads that exact look into the live Studio workspace for deep customization.",
        ),
      ctaLabel = "Open Discover Feed",
    ),
    FeatureGuideItem(
      mode = KromdeckMode.STUDIO,
      title = "2. Studio — Multi-Device Live Customizer",
      subtitle = "Resizable preview stage + 6-tab Inspector Sidebar Drawer",
      accentColor = KromdeckSlateInk,
      tintColor = KromdeckCreamCanvas,
      icon = Icons.Filled.Tune,
      summary = "Your main design workbench. Test your theme on 6 real interactive app templates, resize viewports freely, simulate color blindness, and fine-tune every design token.",
      functions =
        listOf(
          "Mobile / Tablet / Desktop Toggle + Resize Handle" to "Switch viewport presets in one tap or drag the amber right-edge handle to resize the preview frame continuously (with live dp width readout).",
          "6 Interactive App Templates" to "Click real tabs, play/pause tracks, open photo lightboxes, toggle SaaS periods, like social posts, add products to bag, and check off tasks.",
          "Brand Text Editor" to "Rename the preview app's title and tagline inline to see your actual brand inside the theme.",
          "Split Wipe Comparison" to "Enable Split Wipe to drag a vertical divider comparing your active theme against any baseline preset.",
          "Vision Sim (Color-Blindness)" to "Preview your UI through Protanopia, Deuteranopia, Tritanopia, or Achromatopsia filters in real time.",
          "Shot Mode (Dribbble Export)" to "Wraps your preview in a presentation frame with 1-click PNG and SVG Spec Card downloads.",
          "6-Tab Sidebar Drawer + Persist" to "Open Drawer to edit Colors (10 roles, Harmonize, Swap Accents, Gradient/Mesh), Surface (6 physics + radius/padding/border/shadow sliders), Type (6 Google Font pairings + size/tracking/line-height), Layout (4 Nav Shells + Grid Density), Motion (4 curves + live press tester), and WCAG & Export (6 code formats). Tap Persist to save to DataStore.",
        ),
      ctaLabel = "Open Live Studio",
    ),
    FeatureGuideItem(
      mode = KromdeckMode.COMPARE,
      title = "3. Compare — A/B Split, Wipe & Tournament",
      subtitle = "Eliminate UI decision paralysis with structured visual tests",
      accentColor = KromdeckCoral,
      tintColor = KromdeckCoralTint,
      icon = Icons.Filled.Compare,
      summary = "Built for solo makers and client review calls when two or more themes look great and you need a clear winner.",
      functions =
        listOf(
          "A/B Side-by-Side Split" to "Compare any two themes simultaneously on the exact same app template, swap A/B positions, or jump either into Studio.",
          "Interactive Wipe Slider" to "Drag smoothly across a single preview frame to compare Deck A on the left against Deck B on the right.",
          "This-or-That Elimination Tournament" to "Runs a quick 8-deck 1v1 bracket where you simply pick Left or Right until one Champion design system emerges.",
          "Design DNA Win-Loss Leaderboard" to "Persists your tournament win-loss history to reveal which Surface Physics, Font Pairing, and Light/Dark polarity you consistently prefer.",
        ),
      ctaLabel = "Open Compare & Tournament",
    ),
    FeatureGuideItem(
      mode = KromdeckMode.COMPONENTS,
      title = "4. UI Kit — Live Component Specimen Sheet",
      subtitle = "Inspect every interactive UI element under your active theme",
      accentColor = KromdeckIndigo,
      tintColor = KromdeckIndigoTint,
      icon = Icons.Filled.Widgets,
      summary = "See how your active colors, corners, borders, and typography behave across standard UI building blocks before writing production code.",
      functions =
        listOf(
          "State Mode Tester" to "Toggle between Default, Focused, and Disabled states across the entire specimen sheet.",
          "Buttons & Inputs" to "Inspect Primary, Secondary, Ghost, and Disabled buttons alongside outlined text fields and pill search bars.",
          "Controls, Badges & Alerts" to "Interact with switches, checkboxes, radio pills, semantic badges (New/Beta/Warn/Ready), and Info/Success/Warning/Error banners.",
          "Modal & Skeleton Preview" to "Launch a live themed dialog modal and inspect pulsing skeleton loading states.",
        ),
      ctaLabel = "Inspect UI Components",
    ),
    FeatureGuideItem(
      mode = KromdeckMode.TOKENS,
      title = "5. Tokens & Scale — Ramps, Type & WCAG Matrix",
      subtitle = "Developer-grade token sheet & full accessibility verification",
      accentColor = KromdeckEmerald,
      tintColor = KromdeckEmeraldTint,
      icon = Icons.Filled.Palette,
      summary = "Automatically derives every design token scale and contrast ratio from your active theme so engineers have zero guesswork.",
      functions =
        listOf(
          "50–950 Tonal Shade Ramps" to "Auto-generates 11 shades (50 through 950) for Primary, Secondary, and Neutral tokens. Tap any shade to copy its hex.",
          "Typography Scale Specimen" to "Displays Display, H1, H2, H3, Body, Caption, and Mono tokens rendered in your chosen Google Font pairing.",
          "Spacing & Radius Scale" to "Visualizes spacing multiples (4dp–48dp) scaled by Grid Density plus sm/md/lg corner radii.",
          "WCAG 2.1 Contrast Matrix" to "Checks Text, Muted, Primary, and Secondary against Background, Surface, and Elevated cards with AAA, AA, AA18, and FAIL badges.",
        ),
      ctaLabel = "View Tokens & Scale",
    ),
    FeatureGuideItem(
      mode = KromdeckMode.SAVED,
      title = "6. Saved — Personal Moodboard & Notes",
      subtitle = "Persistent local collection for client handoff & comparisons",
      accentColor = KromdeckCoral,
      tintColor = KromdeckAmberTint,
      icon = Icons.Filled.Bookmark,
      summary = "Keep all your favorite curated looks and custom themes stored locally in DataStore across sessions.",
      functions =
        listOf(
          "Save Active Look" to "Pins your current Studio theme into your Moodboard and persists it to DataStore.",
          "Stakeholder Notes" to "Attach custom review notes (e.g., 'Approved for v2 dark mode') directly to each saved card.",
          "1-Tap Workflow Routing" to "Send any saved card straight to Studio, Compare A, or set it as your Studio Split Wipe Baseline.",
          "JSON Backup & Restore" to "Export your entire saved moodboard to a portable JSON file or import backups anytime.",
        ),
      ctaLabel = "Open Saved Moodboard",
    ),
  )
