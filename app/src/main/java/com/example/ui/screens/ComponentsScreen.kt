package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.TactileCard
import com.example.ui.components.TactilePillChip
import com.example.ui.components.ThemedCard
import com.example.ui.components.resolveColors
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.ui.theme.resolveFontPairing
import com.example.viewmodel.KromdeckUiState

@Composable
fun ComponentsScreen(
  state: KromdeckUiState,
  modifier: Modifier = Modifier,
) {
  val deck = state.activeDeck
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  val radius = deck.cornerRadiusDp.dp
  val bw = deck.borderWidthDp.coerceAtLeast(1f).dp

  var uiStateMode by remember { mutableIntStateOf(0) } // 0 = Default, 1 = Focused, 2 = Disabled
  var sampleInput by remember { mutableStateOf("hello@kromdeck.design") }
  var searchQuery by remember { mutableStateOf("") }
  var switchA by remember { mutableStateOf(true) }
  var switchB by remember { mutableStateOf(false) }
  var checkA by remember { mutableStateOf(true) }
  var checkB by remember { mutableStateOf(false) }
  var selectedRadio by remember { mutableIntStateOf(0) }
  var showSampleModal by remember { mutableStateOf(false) }

  val isDisabled = uiStateMode == 2
  val isFocused = uiStateMode == 1

  val pulseTransition = rememberInfiniteTransition(label = "skeletonPulse")
  val pulseAlpha by
    pulseTransition.animateFloat(
      initialValue = 0.35f,
      targetValue = 0.85f,
      animationSpec = infiniteRepeatable(tween(850), RepeatMode.Reverse),
      label = "pulseAlpha",
    )

  LazyColumn(
    modifier = modifier.fillMaxSize().testTag("components_specimen_screen"),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    // Header + Interactive State Mode Switcher
    item {
      TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Live Component Specimen Sheet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
          Text(
            text = "Every button, form input, switch, badge, banner, modal, and skeleton responds live to \"${deck.name}\".",
            style = MaterialTheme.typography.bodySmall,
            color = KromdeckMutedInk,
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Default", "Focused", "Disabled").forEachIndexed { idx, label ->
              TactilePillChip(
                label = label,
                selected = uiStateMode == idx,
                onClick = { uiStateMode = idx },
                activeColor = KromdeckIndigo,
                testTag = "specimen_state_${label.lowercase()}",
              )
            }
          }
        }
      }
    }

    // Active Theme Canvas Specimen Container
    item {
      Column(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.canvas)
            .border(BorderStroke(2.dp, colors.border), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        // 1. Buttons (Primary, Secondary, Ghost, Disabled)
        ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Buttons", fontFamily = fonts.display, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = colors.textPrimary)
            Row(
              modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              // Primary
              Box(
                modifier =
                  Modifier.alpha(if (isDisabled) 0.45f else 1f)
                    .clip(RoundedCornerShape(radius))
                    .background(colors.primary)
                    .border(BorderStroke(if (isFocused) 2.5.dp else bw, if (isFocused) colors.warm else colors.border), RoundedCornerShape(radius))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
              ) {
                Text("Primary", fontFamily = fonts.display, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = colors.onPrimary)
              }
              // Secondary
              Box(
                modifier =
                  Modifier.alpha(if (isDisabled) 0.45f else 1f)
                    .clip(RoundedCornerShape(radius))
                    .background(colors.secondary)
                    .border(BorderStroke(bw, colors.border), RoundedCornerShape(radius))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
              ) {
                Text("Secondary", fontFamily = fonts.display, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = colors.onSecondary)
              }
              // Ghost
              Box(
                modifier =
                  Modifier.alpha(if (isDisabled) 0.45f else 1f)
                    .clip(RoundedCornerShape(radius))
                    .background(Color.Transparent)
                    .border(BorderStroke(bw, colors.border), RoundedCornerShape(radius))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
              ) {
                Text("Ghost", fontFamily = fonts.display, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textPrimary)
              }
              // Disabled
              Box(
                modifier =
                  Modifier.alpha(0.4f)
                    .clip(RoundedCornerShape(radius))
                    .background(colors.elevated)
                    .border(BorderStroke(1.dp, colors.border.copy(alpha = 0.4f)), RoundedCornerShape(radius))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
              ) {
                Text("Disabled", fontFamily = fonts.display, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textMuted)
              }
            }
          }
        }

        // 2. Text Input & Search Bar
        ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Inputs & Search", fontFamily = fonts.display, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = colors.textPrimary)
            OutlinedTextField(
              value = sampleInput,
              onValueChange = { sampleInput = it },
              enabled = !isDisabled,
              label = { Text("Work Email", fontFamily = fonts.body) },
              singleLine = true,
              shape = RoundedCornerShape(radius),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = colors.primary,
                  unfocusedBorderColor = if (isFocused) colors.primary else colors.border,
                  focusedTextColor = colors.textPrimary,
                  unfocusedTextColor = colors.textPrimary,
                  focusedContainerColor = colors.canvas,
                  unfocusedContainerColor = colors.canvas,
                  focusedLabelColor = colors.primary,
                  unfocusedLabelColor = colors.textMuted,
                ),
              modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              enabled = !isDisabled,
              placeholder = { Text("Search design tokens...", fontFamily = fonts.body, color = colors.textMuted) },
              leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = colors.primary) },
              singleLine = true,
              shape = RoundedCornerShape(999.dp),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = colors.primary,
                  unfocusedBorderColor = colors.border.copy(alpha = 0.7f),
                  focusedTextColor = colors.textPrimary,
                  unfocusedTextColor = colors.textPrimary,
                  focusedContainerColor = colors.elevated,
                  unfocusedContainerColor = colors.elevated,
                ),
              modifier = Modifier.fillMaxWidth(),
            )
          }
        }

        // 3. Switches, Checkboxes & Radio Pills
        ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Selection Controls", fontFamily = fonts.display, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = colors.textPrimary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Text("Auto-sync Dark Pair", fontFamily = fonts.body, fontSize = 13.sp, color = colors.textPrimary)
              Switch(
                checked = switchA,
                onCheckedChange = { switchA = it },
                enabled = !isDisabled,
                colors = SwitchDefaults.colors(checkedThumbColor = colors.onPrimary, checkedTrackColor = colors.primary),
              )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Text("Strict WCAG AAA Mode", fontFamily = fonts.body, fontSize = 13.sp, color = colors.textPrimary)
              Switch(
                checked = switchB,
                onCheckedChange = { switchB = it },
                enabled = !isDisabled,
                colors = SwitchDefaults.colors(checkedThumbColor = colors.onSecondary, checkedTrackColor = colors.secondary),
              )
            }

            // Interactive Checkboxes
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
              SpecimenCheckbox(label = "Export CSS", checked = checkA, enabled = !isDisabled, primary = colors.primary, onPrimary = colors.onPrimary, border = colors.border, text = colors.textPrimary) { checkA = !checkA }
              SpecimenCheckbox(label = "Include Ramp", checked = checkB, enabled = !isDisabled, primary = colors.primary, onPrimary = colors.onPrimary, border = colors.border, text = colors.textPrimary) { checkB = !checkB }
            }

            // Radio Pills
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf("Compact", "Balanced", "Airy").forEachIndexed { idx, opt ->
                val sel = selectedRadio == idx
                Box(
                  modifier =
                    Modifier.clip(RoundedCornerShape(999.dp))
                      .background(if (sel) colors.primary else colors.elevated)
                      .border(1.5.dp, colors.border, RoundedCornerShape(999.dp))
                      .clickable(enabled = !isDisabled) { selectedRadio = idx }
                      .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                  Text(opt, fontFamily = fonts.body, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (sel) colors.onPrimary else colors.textPrimary)
                }
              }
            }
          }
        }

        // 4. Semantic Badges & Alert Banners
        ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Badges & Alerts", fontFamily = fonts.display, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = colors.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              SpecimenBadge("NEW", colors.primary, colors.onPrimary)
              SpecimenBadge("BETA", colors.secondary, colors.onSecondary)
              SpecimenBadge("WARN", colors.warm, Color(0xFF0F172A))
              SpecimenBadge("READY", colors.success, Color.White)
            }
            SpecimenAlertRow(Icons.Filled.Info, "Design tokens synced with active deck.", colors.primary, colors.textPrimary)
            SpecimenAlertRow(Icons.Filled.CheckCircle, "All primary & body text pairs pass WCAG AA.", colors.success, colors.textPrimary)
            SpecimenAlertRow(Icons.Filled.Warning, "Ambient shadows adapt automatically on flat mode.", colors.warm, colors.textPrimary)
            SpecimenAlertRow(Icons.Filled.Error, "Low-contrast overrides trigger live guard warnings.", colors.secondary, colors.textPrimary)
          }
        }

        // 5. Modal Trigger & Skeleton Loaders
        ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Text("Modal & Skeletons", fontFamily = fonts.display, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = colors.textPrimary)
              Box(
                modifier =
                  Modifier.testTag("specimen_open_modal_btn")
                    .clip(RoundedCornerShape(radius))
                    .background(colors.primary)
                    .clickable { showSampleModal = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
              ) {
                Text("Preview Modal", fontFamily = fonts.display, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.onPrimary)
              }
            }
            // Pulsing Skeleton Loader
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.alpha(pulseAlpha)) {
              Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(colors.elevated))
              Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.fillMaxWidth(0.75f).height(12.dp).clip(RoundedCornerShape(6.dp)).background(colors.elevated))
                Box(modifier = Modifier.fillMaxWidth(0.50f).height(10.dp).clip(RoundedCornerShape(6.dp)).background(colors.elevated))
              }
            }
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }

  if (showSampleModal) {
    Dialog(onDismissRequest = { showSampleModal = false }) {
      ThemedCard(deck = deck, backgroundOverride = colors.surface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Themed Dialog Preview", fontFamily = fonts.display, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = colors.textPrimary)
          Text(
            text = "This modal inherits ${deck.name}'s surface color, ${deck.cornerRadiusDp}dp corner radius, and ${deck.fontPairing.label} typography.",
            fontFamily = fonts.body,
            fontSize = 13.sp,
            color = colors.textMuted,
          )
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
              modifier =
                Modifier.clip(RoundedCornerShape(radius))
                  .background(colors.primary)
                  .clickable { showSampleModal = false }
                  .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
              Text("Got it", fontFamily = fonts.display, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.onPrimary)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SpecimenCheckbox(
  label: String,
  checked: Boolean,
  enabled: Boolean,
  primary: Color,
  onPrimary: Color,
  border: Color,
  text: Color,
  onToggle: () -> Unit,
) {
  Row(
    modifier = Modifier.clickable(enabled = enabled, onClick = onToggle),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Box(
      modifier =
        Modifier.size(20.dp)
          .clip(RoundedCornerShape(5.dp))
          .background(if (checked) primary else Color.Transparent)
          .border(1.5.dp, border, RoundedCornerShape(5.dp)),
      contentAlignment = Alignment.Center,
    ) {
      if (checked) Icon(Icons.Filled.Check, contentDescription = null, tint = onPrimary, modifier = Modifier.size(14.dp))
    }
    Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = text)
  }
}

@Composable
private fun SpecimenBadge(text: String, bg: Color, fg: Color) {
  Box(
    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(bg).padding(horizontal = 8.dp, vertical = 4.dp),
  ) {
    Text(text, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = fg)
  }
}

@Composable
private fun SpecimenAlertRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  message: String,
  accent: Color,
  textColor: Color,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(accent.copy(alpha = 0.14f))
        .border(1.dp, accent, RoundedCornerShape(10.dp))
        .padding(horizontal = 10.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
    Text(message, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textColor)
  }
}
