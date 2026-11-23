package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PresetDecks
import com.example.model.ColorHarmonyRule
import com.example.model.PaletteMoodFilter
import com.example.model.ThemeDeck
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
import com.example.util.ColorUtils
import com.example.viewmodel.KromdeckUiState
import kotlin.math.roundToInt

/**
 * Infinite Theme Lab inside Studio Drawer:
 * 1. Inline Custom Theme Naming
 * 2. 360° Seed Hue + 6 Scientific Color Harmony Generators (Light & Dark)
 * 3. Theme DNA Blender (0%..100% live morph between ANY 2 themes)
 * 4. Global Palette Color Grading (Hue Shift, Saturation, Warm/Cool Temperature)
 * 5. 6 One-Tap Mood Atmosphere Transformers
 */
@Composable
fun InfiniteThemeLabTab(
  state: KromdeckUiState,
  onRenameDeck: (String) -> Unit,
  onGenerateHarmony: (Float, ColorHarmonyRule, Boolean) -> Unit,
  onBlendDecks: (ThemeDeck, ThemeDeck, Float) -> Unit,
  onApplyGlobalGrade: (Float, Float, Float) -> Unit,
  onApplyMoodTransform: (PaletteMoodFilter) -> Unit,
) {
  val deck = state.activeDeck
  val initialHue = remember(deck.palette.primaryHex) { ColorUtils.rgbToHsv(ColorUtils.parseHexColor(deck.palette.primaryHex)).first }

  var seedHue by remember { mutableFloatStateOf(initialHue) }
  var selectedRule by remember { mutableStateOf(ColorHarmonyRule.TRIADIC) }
  var harmonyDark by remember { mutableStateOf(deck.isDark) }

  // Theme DNA Blender state
  val blendPool = remember(state.shortlist) { (PresetDecks.all + state.shortlist).distinctBy { it.id } }
  var blendA by remember { mutableStateOf(blendPool.first()) }
  var blendB by remember { mutableStateOf(blendPool.getOrElse(5) { blendPool.last() }) }
  var blendFraction by remember { mutableFloatStateOf(0.5f) }
  var showMenuA by remember { mutableStateOf(false) }
  var showMenuB by remember { mutableStateOf(false) }

  // Global Grading state
  var hueShift by remember { mutableFloatStateOf(0f) }
  var satShift by remember { mutableFloatStateOf(0f) }
  var tempShift by remember { mutableFloatStateOf(0f) }

  val rainbowBrush =
    remember {
      Brush.horizontalGradient(
        listOf(
          Color(0xFFFF0000),
          Color(0xFFFFFF00),
          Color(0xFF00FF00),
          Color(0xFF00FFFF),
          Color(0xFF0000FF),
          Color(0xFFFF00FF),
          Color(0xFFFF0000),
        ),
      )
    }

  Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.testTag("infinite_theme_lab_tab")) {
    // 1. Custom Theme Name Editor
    LabSectionBox(title = "Custom Theme Name", icon = Icons.Filled.AutoAwesome, accent = KromdeckSlateInk) {
      OutlinedTextField(
        value = deck.name,
        onValueChange = onRenameDeck,
        singleLine = true,
        label = { Text("Theme Display Name (used in JSON/XML export)") },
        colors =
          OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KromdeckIndigo,
            unfocusedBorderColor = KromdeckSlateInk,
          ),
        modifier = Modifier.fillMaxWidth().testTag("lab_theme_name_input"),
      )
    }

    // 2. Color Harmony Studio (360° Seed Hue + 6 Scientific Rules)
    LabSectionBox(title = "Color Harmony Studio (Infinite Seed)", icon = Icons.Filled.ColorLens, accent = KromdeckIndigo) {
      val seedColor = ColorUtils.hsvToColor(seedHue, 0.80f, 0.78f)
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier =
              Modifier.size(26.dp)
                .clip(CircleShape)
                .background(seedColor)
                .border(1.5.dp, KromdeckSlateInk, CircleShape),
          )
          Text(
            text = "Seed Hue: ${seedHue.toInt()}° (${ColorUtils.colorToHex(seedColor)})",
            fontFamily = JetBrainsMonoFamily,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = KromdeckSlateInk,
          )
        }
        TactilePillChip(
          label = if (harmonyDark) "Dark" else "Light",
          selected = true,
          onClick = { harmonyDark = !harmonyDark },
          activeColor = if (harmonyDark) KromdeckSlateInk else KromdeckAmber,
          testTag = "lab_harmony_polarity_btn",
        )
      }

      // Rainbow Hue Strip + Slider
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(rainbowBrush)
            .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(999.dp)),
      )
      Slider(
        value = seedHue,
        onValueChange = {
          seedHue = it
          onGenerateHarmony(it, selectedRule, harmonyDark)
        },
        valueRange = 0f..360f,
        colors = SliderDefaults.colors(thumbColor = KromdeckSlateInk, activeTrackColor = seedColor),
      )

      // 6 Harmony Rules Grid
      ColorHarmonyRule.entries.chunked(2).forEach { pair ->
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          pair.forEach { rule ->
            val active = selectedRule == rule
            Column(
              modifier =
                Modifier.weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (active) KromdeckIndigoTint else KromdeckPureSurface)
                  .border(BorderStroke(if (active) 2.dp else 1.2.dp, if (active) KromdeckIndigo else KromdeckSlateInk), RoundedCornerShape(8.dp))
                  .clickable {
                    selectedRule = rule
                    onGenerateHarmony(seedHue, rule, harmonyDark)
                  }
                  .padding(8.dp)
                  .testTag("lab_harmony_${rule.name.lowercase()}"),
            ) {
              Text(rule.label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
              Text(rule.description, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = KromdeckMutedInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
          }
        }
      }
    }

    // 3. Theme DNA Blender (Morph any 2 themes from 0% to 100%)
    LabSectionBox(title = "Theme DNA Blender (Morph Any 2 Looks)", icon = Icons.Filled.BlurOn, accent = KromdeckCoral) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.weight(1f)) {
          DeckMiniPicker(label = "Theme A", deck = blendA, onClick = { showMenuA = true }, testTag = "lab_blend_picker_a")
          DropdownMenu(expanded = showMenuA, onDismissRequest = { showMenuA = false }, modifier = Modifier.background(KromdeckPureSurface)) {
            blendPool.forEach { d ->
              DropdownMenuItem(
                text = { Text(d.name, color = KromdeckSlateInk) },
                onClick = {
                  blendA = d
                  showMenuA = false
                },
              )
            }
          }
        }
        Box(modifier = Modifier.weight(1f)) {
          DeckMiniPicker(label = "Theme B", deck = blendB, onClick = { showMenuB = true }, testTag = "lab_blend_picker_b")
          DropdownMenu(expanded = showMenuB, onDismissRequest = { showMenuB = false }, modifier = Modifier.background(KromdeckPureSurface)) {
            blendPool.forEach { d ->
              DropdownMenuItem(
                text = { Text(d.name, color = KromdeckSlateInk) },
                onClick = {
                  blendB = d
                  showMenuB = false
                },
              )
            }
          }
        }
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("${100 - (blendFraction * 100).roundToInt()}% A", fontFamily = JetBrainsMonoFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
        Text("Morph Mix", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = KromdeckCoral)
        Text("${(blendFraction * 100).roundToInt()}% B", fontFamily = JetBrainsMonoFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
      }
      Slider(
        value = blendFraction,
        onValueChange = { blendFraction = it },
        valueRange = 0f..1f,
        colors = SliderDefaults.colors(thumbColor = KromdeckSlateInk, activeTrackColor = KromdeckCoral),
      )
      TactileButton(
        onClick = { onBlendDecks(blendA, blendB, blendFraction) },
        containerColor = KromdeckCoral,
        contentColor = Color.White,
        shadowOffset = 2.dp,
        modifier = Modifier.fillMaxWidth(),
        testTag = "lab_apply_blend_btn",
      ) {
        Text("Apply ${(blendFraction * 100).roundToInt()}% Hybrid Blend", fontWeight = FontWeight.ExtraBold)
      }
    }

    // 4. Global Palette Grading (Hue Shift, Saturation, Warm/Cool Temperature)
    LabSectionBox(title = "Global Palette Color Grading", icon = Icons.Filled.Tune, accent = KromdeckEmerald) {
      GradeSliderRow("Hue Rotate", "${if (hueShift > 0) "+" else ""}${hueShift.toInt()}°", hueShift, -180f..180f) { hueShift = it }
      GradeSliderRow("Saturation Boost", "${if (satShift > 0) "+" else ""}${(satShift * 100).toInt()}%", satShift, -0.45f..0.45f) { satShift = it }
      GradeSliderRow("Temperature (Cool ↔ Warm)", "${if (tempShift > 0) "+" else ""}${(tempShift * 100).toInt()}%", tempShift, -1f..1f) { tempShift = it }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TactileButton(
          onClick = {
            hueShift = 0f
            satShift = 0f
            tempShift = 0f
          },
          containerColor = KromdeckPureSurface,
          contentColor = KromdeckSlateInk,
          shadowOffset = 2.dp,
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          testTag = "lab_grade_reset_btn",
        ) {
          Icon(Icons.Filled.Refresh, contentDescription = "Reset sliders", modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Reset", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        TactileButton(
          onClick = {
            onApplyGlobalGrade(hueShift, satShift, tempShift)
            hueShift = 0f
            satShift = 0f
            tempShift = 0f
          },
          containerColor = KromdeckEmerald,
          contentColor = Color.White,
          shadowOffset = 2.dp,
          modifier = Modifier.weight(1f),
          testTag = "lab_grade_apply_btn",
        ) {
          Text("Apply Color Grade", fontWeight = FontWeight.ExtraBold)
        }
      }
    }

    // 5. 1-Tap Mood Atmosphere Transformers
    LabSectionBox(title = "1-Tap Mood Atmosphere Transforms", icon = Icons.Filled.AutoAwesome, accent = KromdeckAmber) {
      PaletteMoodFilter.entries.chunked(2).forEach { pair ->
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          pair.forEach { mood ->
            Column(
              modifier =
                Modifier.weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(KromdeckPureSurface)
                  .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(8.dp))
                  .clickable { onApplyMoodTransform(mood) }
                  .padding(8.dp)
                  .testTag("lab_mood_${mood.name.lowercase()}"),
            ) {
              Text(mood.label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
              Text(mood.subtitle, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = KromdeckMutedInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun LabSectionBox(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accent: Color,
  content: @Composable () -> Unit,
) {
  Column(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(KromdeckCreamCanvas)
        .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(12.dp))
        .padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      Box(
        modifier =
          Modifier.size(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(accent)
            .border(1.dp, KromdeckSlateInk, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (accent == KromdeckAmber) KromdeckSlateInk else Color.White,
          modifier = Modifier.size(14.dp),
        )
      }
      Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
    }
    content()
  }
}

@Composable
private fun DeckMiniPicker(
  label: String,
  deck: ThemeDeck,
  onClick: () -> Unit,
  testTag: String,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(KromdeckPureSurface)
        .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(8.dp))
        .clickable(onClick = onClick)
        .padding(8.dp)
        .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Box(
      modifier =
        Modifier.size(18.dp)
          .clip(CircleShape)
          .background(ColorUtils.parseHexColor(deck.palette.primaryHex))
          .border(1.dp, KromdeckSlateInk, CircleShape),
    )
    Column(modifier = Modifier.weight(1f)) {
      Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = KromdeckMutedInk)
      Text(deck.name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
  }
}

@Composable
private fun GradeSliderRow(
  label: String,
  valueText: String,
  value: Float,
  range: ClosedFloatingPointRange<Float>,
  onChange: (Float) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
      Text(valueText, fontFamily = JetBrainsMonoFamily, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = KromdeckIndigo)
    }
    Slider(
      value = value,
      onValueChange = onChange,
      valueRange = range,
      colors = SliderDefaults.colors(thumbColor = KromdeckSlateInk, activeTrackColor = KromdeckEmerald),
    )
  }
}
