package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiPolarityPreference
import com.example.model.AiThemeSuggestion
import com.example.model.AiThemeUiState
import com.example.model.ThemeDeck
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCoralTint
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckEmeraldTint
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.ui.theme.resolveFontPairing
import com.example.util.ColorUtils
import java.util.Locale

private val SampleAiPrompts =
  listOf(
    "Nordic fintech banking app with glacial slate, warm copper accents & editorial serif headers",
    "Kyoto matcha tea house e-commerce with organic washi paper & soft clay cards",
    "Cyberpunk synthwave music studio in deep obsidian with electric magenta & monospace type",
    "Minimalist Swiss architecture portfolio with stark ink borders & geometric grotesk scale",
    "Sunlit Mediterranean wellness journal with terracotta clay, olive sage & airy typography",
  )

@Composable
fun AiThemeGeneratorCard(
  prompt: String,
  polarity: AiPolarityPreference,
  aiState: AiThemeUiState,
  onPromptChange: (String) -> Unit,
  onPolarityChange: (AiPolarityPreference) -> Unit,
  onGenerate: () -> Unit,
  onApplySuggestion: (ThemeDeck) -> Unit,
  onSaveSuggestion: (ThemeDeck) -> Unit,
  modifier: Modifier = Modifier,
) {
  val isLoading = aiState is AiThemeUiState.Loading

  Column(
    modifier = modifier.fillMaxWidth().testTag("ai_theme_generator_card"),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    // Prompt & Controls Container
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(KromdeckCreamCanvas)
          .border(BorderStroke(2.dp, KromdeckSlateInk), RoundedCornerShape(14.dp))
          .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Box(
          modifier =
            Modifier.size(30.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(KromdeckIndigo)
              .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(8.dp)),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Filled.AutoAwesome,
            contentDescription = "AI Design Token Theme Generator",
            tint = Color.White,
            modifier = Modifier.size(17.dp),
          )
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "AI Design System & Token Generator",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
          Text(
            text = "Describe your product mood, brand, or aesthetic to generate 3 complete color & typography systems.",
            style = MaterialTheme.typography.labelSmall,
            color = KromdeckMutedInk,
          )
        }
      }

      // Prompt Text Field
      OutlinedTextField(
        value = prompt,
        onValueChange = onPromptChange,
        enabled = !isLoading,
        label = { Text("Describe your theme prompt (mood, domain, colors, typography)") },
        placeholder = { Text("e.g., Warm brutalist coffee roastery with espresso ink & cream paper...") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(10.dp),
        colors =
          OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KromdeckIndigo,
            unfocusedBorderColor = KromdeckSlateInk,
            focusedContainerColor = KromdeckPureSurface,
            unfocusedContainerColor = KromdeckPureSurface,
          ),
        modifier = Modifier.fillMaxWidth().testTag("ai_theme_prompt_input"),
      )

      // Quick Inspiration Chips
      Text(
        text = "Quick Inspiration Prompts:",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = KromdeckMutedInk,
      )
      Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        SampleAiPrompts.forEachIndexed { idx, sample ->
          TactilePillChip(
            label = sample.take(34) + "…",
            selected = prompt == sample,
            onClick = { if (!isLoading) onPromptChange(sample) },
            activeColor = KromdeckIndigo,
            testTag = "ai_sample_prompt_$idx",
          )
        }
      }

      // Polarity Preference + Generate CTA
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          AiPolarityPreference.entries.forEach { pref ->
            TactilePillChip(
              label = pref.label,
              selected = polarity == pref,
              onClick = { if (!isLoading) onPolarityChange(pref) },
              activeColor = KromdeckSlateInk,
              testTag = "ai_polarity_${pref.name.lowercase()}",
            )
          }
        }
      }

      TactileButton(
        onClick = onGenerate,
        enabled = !isLoading && prompt.isNotBlank(),
        containerColor = KromdeckIndigo,
        contentColor = Color.White,
        shadowOffset = 2.dp,
        modifier = Modifier.fillMaxWidth(),
        testTag = "ai_generate_themes_btn",
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 2.dp,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Synthesizing Palettes & Type Scales...", fontWeight = FontWeight.ExtraBold)
        } else {
          Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Generate Cohesive Theme Tokens", fontWeight = FontWeight.ExtraBold)
        }
      }
    }

    // AI State Feedback & Generated Theme Suggestions
    when (aiState) {
      AiThemeUiState.Idle -> Unit

      AiThemeUiState.Loading -> {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(KromdeckPureSurface)
              .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(12.dp))
              .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          CircularProgressIndicator(
            color = KromdeckIndigo,
            strokeWidth = 2.5.dp,
            modifier = Modifier.size(22.dp),
          )
          Column {
            Text(
              text = "Crafting 3 cohesive design token themes with Gemini...",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckSlateInk,
            )
            Text(
              text = "Computing 10 semantic color tokens, modular typography scales, and WCAG contrast ratios.",
              style = MaterialTheme.typography.labelSmall,
              color = KromdeckMutedInk,
            )
          }
        }
      }

      is AiThemeUiState.Error -> {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .testTag("ai_theme_error_banner")
              .clip(RoundedCornerShape(12.dp))
              .background(KromdeckCoralTint)
              .border(1.5.dp, KromdeckCoral, RoundedCornerShape(12.dp))
              .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            tint = KromdeckCoral,
            modifier = Modifier.size(20.dp),
          )
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "AI Theme Generation Failed",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckSlateInk,
            )
            Text(
              text = aiState.message,
              style = MaterialTheme.typography.bodySmall,
              color = KromdeckSlateInk,
            )
          }
        }
      }

      is AiThemeUiState.Success -> {
        Text(
          text = "Generated ${aiState.suggestions.size} Cohesive Theme Systems — Tap Apply to load live:",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.ExtraBold,
          color = KromdeckSlateInk,
        )
        aiState.suggestions.forEachIndexed { index, suggestion ->
          AiSuggestionResultCard(
            suggestion = suggestion,
            index = index,
            onApply = { onApplySuggestion(suggestion.deck) },
            onSave = { onSaveSuggestion(suggestion.deck) },
          )
        }
      }
    }
  }
}

@Composable
private fun AiSuggestionResultCard(
  suggestion: AiThemeSuggestion,
  index: Int,
  onApply: () -> Unit,
  onSave: () -> Unit,
) {
  val deck = suggestion.deck
  val p = deck.palette
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  val scale = suggestion.scaleSpec
  val audit = suggestion.wcagAudit

  Column(
    modifier =
      Modifier.fillMaxWidth()
        .testTag("ai_suggestion_card_$index")
        .clip(RoundedCornerShape(14.dp))
        .background(KromdeckPureSurface)
        .border(BorderStroke(2.dp, KromdeckSlateInk), RoundedCornerShape(14.dp))
        .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    // Title + Polarity & WCAG Badge
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = deck.name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold,
          color = KromdeckSlateInk,
        )
        Text(
          text = deck.tagline,
          style = MaterialTheme.typography.labelSmall,
          color = KromdeckMutedInk,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier =
            Modifier.clip(RoundedCornerShape(6.dp))
              .background(if (deck.isDark) KromdeckSlateInk else KromdeckAmber)
              .padding(horizontal = 6.dp, vertical = 3.dp),
        ) {
          Text(
            text = if (deck.isDark) "DARK" else "LIGHT",
            fontFamily = JetBrainsMonoFamily,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (deck.isDark) Color.White else KromdeckSlateInk,
          )
        }
        Box(
          modifier =
            Modifier.clip(RoundedCornerShape(6.dp))
              .background(if (audit.passesAA) KromdeckEmeraldTint else KromdeckCoralTint)
              .border(1.dp, if (audit.passesAA) KromdeckEmerald else KromdeckCoral, RoundedCornerShape(6.dp))
              .padding(horizontal = 6.dp, vertical = 3.dp),
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Icon(
              imageVector = if (audit.passesAA) Icons.Filled.CheckCircle else Icons.Filled.Warning,
              contentDescription = null,
              tint = if (audit.passesAA) KromdeckEmerald else KromdeckCoral,
              modifier = Modifier.size(11.dp),
            )
            Text(
              text = "${String.format(Locale.US, "%.1f:1", audit.worstRatio)} AA",
              fontFamily = JetBrainsMonoFamily,
              fontSize = 9.sp,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckSlateInk,
            )
          }
        }
      }
    }

    // Color Palette Strip (8 key swatches)
    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      listOf(
        "BG" to p.canvasHex,
        "SURF" to p.surfaceHex,
        "ELEV" to p.elevatedSurfaceHex,
        "PRI" to p.primaryHex,
        "SEC" to p.secondaryHex,
        "TEXT" to p.textPrimaryHex,
        "WARM" to p.warmHex,
        "OK" to p.successHex,
      ).forEach { (role, hex) ->
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Box(
            modifier =
              Modifier.size(28.dp)
                .clip(CircleShape)
                .background(ColorUtils.parseHexColor(hex))
                .border(1.5.dp, KromdeckSlateInk, CircleShape),
          )
          Text(role, fontFamily = JetBrainsMonoFamily, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
          Text(hex.removePrefix("#"), fontFamily = JetBrainsMonoFamily, fontSize = 8.sp, color = KromdeckMutedInk)
        }
      }
    }

    // Typography Scale & Surface Mini Specimen
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .clip(RoundedCornerShape(deck.cornerRadiusDp.coerceIn(6, 20).dp))
          .background(colors.canvas)
          .border(1.5.dp, colors.border, RoundedCornerShape(deck.cornerRadiusDp.coerceIn(6, 20).dp))
          .padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
          text = "TYPE SCALE • ${scale.scaleRatioLabel}",
          fontFamily = JetBrainsMonoFamily,
          fontSize = 9.sp,
          fontWeight = FontWeight.ExtraBold,
          color = colors.primary,
        )
        Text(
          text = "${deck.physics.label} • ${deck.cornerRadiusDp}dp",
          fontFamily = JetBrainsMonoFamily,
          fontSize = 9.sp,
          color = colors.textMuted,
        )
      }
      Text(
        text = "Display ${scale.displaySp}sp / H1 ${scale.h1Sp}sp — ${deck.fontPairing.displayLabel}",
        fontFamily = fonts.display,
        fontWeight = if (deck.headingWeightBold) FontWeight.ExtraBold else FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = deck.letterSpacingSp.sp,
        color = colors.textPrimary,
      )
      Text(
        text = "Body ${scale.bodySp}sp / Caption ${scale.captionSp}sp (${deck.fontPairing.bodyLabel}) • Line-height ${String.format(Locale.US, "%.2fx", deck.lineHeightMultiplier)}",
        fontFamily = fonts.body,
        fontSize = 11.sp,
        color = colors.textMuted,
      )
    }

    // AI Design Rationale
    Text(
      text = suggestion.designRationale,
      style = MaterialTheme.typography.bodySmall,
      color = KromdeckMutedInk,
    )

    // Action Buttons: Apply Live to Studio & Save to Moodboard
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      TactileButton(
        onClick = onApply,
        containerColor = KromdeckEmerald,
        contentColor = Color.White,
        shadowOffset = 2.dp,
        modifier = Modifier.weight(1f),
        testTag = "ai_apply_suggestion_$index",
      ) {
        Text("Apply Theme Live", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
      }

      TactileButton(
        onClick = onSave,
        containerColor = KromdeckAmber,
        contentColor = KromdeckSlateInk,
        shadowOffset = 2.dp,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        testTag = "ai_save_suggestion_$index",
      ) {
        Icon(Icons.Filled.BookmarkAdd, contentDescription = "Save AI theme to moodboard", modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Save", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
      }
    }
  }
}
