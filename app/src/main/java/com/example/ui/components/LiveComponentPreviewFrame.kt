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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Widgets
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemeDeck
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.ui.theme.resolveFontPairing
import com.example.util.ColorUtils
import java.util.Locale

/**
 * Live UI Component Preview Frame that updates instantaneously whenever theme tokens
 * (colors, typography scale, font pairing, surface physics, corner radius, border width) are adjusted.
 * Shows how the active design system looks on real interactive buttons, text input fields,
 * search bars, selection switches/chips, and typography specimens.
 */
@Composable
fun LiveComponentPreviewFrame(
  deck: ThemeDeck,
  modifier: Modifier = Modifier,
  compact: Boolean = false,
) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  val radius = deck.cornerRadiusDp.dp
  val bw = deck.borderWidthDp.coerceAtLeast(1f).dp
  val baseSp = deck.baseFontSizeSp
  val tracking = deck.letterSpacingSp.sp
  val audit = remember(deck.palette) { ColorUtils.auditThemeContrast(deck.palette) }

  var inputEmail by remember { mutableStateOf("alex@kromdeck.studio") }
  var searchFilter by remember { mutableStateOf("") }
  var toggleEnabled by remember { mutableStateOf(true) }
  var selectedPill by remember { mutableIntStateOf(0) }
  var buttonTapCount by remember { mutableIntStateOf(0) }

  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = modifier.fillMaxWidth().testTag("live_component_preview_frame"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(if (compact) 10.dp else 14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      // Top Frame Header Bar showing live synced tokens
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f),
        ) {
          Box(
            modifier =
              Modifier.size(28.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(KromdeckIndigo)
                .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Filled.Widgets,
              contentDescription = "Live UI component preview frame",
              tint = Color.White,
              modifier = Modifier.size(16.dp),
            )
          }
          Column {
            Text(
              text = "Live UI Component Preview Frame",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckSlateInk,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              text = "${deck.name} • ${deck.fontPairing.label} (${baseSp.toInt()}sp) • ${deck.cornerRadiusDp}dp radius",
              style = MaterialTheme.typography.labelSmall,
              color = KromdeckMutedInk,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }

        // Live WCAG Contrast Status Pill
        Box(
          modifier =
            Modifier.clip(RoundedCornerShape(999.dp))
              .background(if (audit.passesAA) KromdeckEmerald.copy(alpha = 0.16f) else colors.warm.copy(alpha = 0.22f))
              .border(1.dp, if (audit.passesAA) KromdeckEmerald else KromdeckSlateInk, RoundedCornerShape(999.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
          Text(
            text = "${String.format(Locale.US, "%.1f:1", audit.worstRatio)} ${if (audit.passesAA) "AA" else "CHK"}",
            fontFamily = JetBrainsMonoFamily,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
        }
      }

      // Live Themed Canvas Container rendering real buttons, inputs, and typography
      Column(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape((deck.cornerRadiusDp + 4).coerceIn(8, 28).dp))
            .background(colors.canvas)
            .border(
              BorderStroke(deck.borderWidthDp.coerceAtLeast(1.5f).dp, colors.border),
              RoundedCornerShape((deck.cornerRadiusDp + 4).coerceIn(8, 28).dp),
            )
            .padding(if (compact) 10.dp else 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        // 1. Live Typography & Surface Card Header
        ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(deck.cardPaddingDp.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Design System Specimen",
                  fontFamily = fonts.display,
                  fontWeight = if (deck.headingWeightBold) FontWeight.ExtraBold else FontWeight.SemiBold,
                  fontSize = (baseSp + 4f).sp,
                  letterSpacing = tracking,
                  color = colors.textPrimary,
                )
                Text(
                  text = "Instant preview of buttons, inputs & type tokens.",
                  fontFamily = fonts.body,
                  fontSize = baseSp.sp,
                  letterSpacing = tracking,
                  lineHeight = (baseSp * deck.lineHeightMultiplier).sp,
                  color = colors.textMuted,
                )
              }

              Switch(
                checked = toggleEnabled,
                onCheckedChange = { toggleEnabled = it },
                colors =
                  SwitchDefaults.colors(
                    checkedThumbColor = colors.onPrimary,
                    checkedTrackColor = colors.primary,
                    uncheckedThumbColor = colors.textMuted,
                    uncheckedTrackColor = colors.elevated,
                  ),
                modifier = Modifier.testTag("live_frame_switch"),
              )
            }

            // Interactive Filter Chips / Radio Pills
            Row(
              modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              listOf("Primary Action", "Surface Token", "Typography").forEachIndexed { idx, label ->
                val selected = selectedPill == idx
                Row(
                  modifier =
                    Modifier.clip(RoundedCornerShape(radius))
                      .background(if (selected) colors.primary else colors.elevated)
                      .border(BorderStroke(bw, colors.border), RoundedCornerShape(radius))
                      .clickable { selectedPill = idx }
                      .padding(horizontal = 10.dp, vertical = 5.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                  if (selected) {
                    Icon(
                      imageVector = Icons.Filled.Check,
                      contentDescription = null,
                      tint = colors.onPrimary,
                      modifier = Modifier.size(12.dp),
                    )
                  }
                  Text(
                    text = label,
                    fontFamily = fonts.body,
                    fontSize = (baseSp - 2f).coerceAtLeast(10f).sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = tracking,
                    color = if (selected) colors.onPrimary else colors.textPrimary,
                  )
                }
              }
            }
          }
        }

        // 2. Real Interactive Input Fields (Text Field + Search Field)
        ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(deck.cardPaddingDp.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(
              text = "LIVE INPUT FIELDS",
              fontFamily = fonts.mono,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = colors.primary,
            )

            OutlinedTextField(
              value = inputEmail,
              onValueChange = { inputEmail = it },
              label = {
                Text(
                  text = "Workspace Member Email",
                  fontFamily = fonts.body,
                  fontSize = (baseSp - 2f).coerceAtLeast(10f).sp,
                )
              },
              singleLine = true,
              shape = RoundedCornerShape(radius),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = colors.primary,
                  unfocusedBorderColor = colors.border,
                  focusedTextColor = colors.textPrimary,
                  unfocusedTextColor = colors.textPrimary,
                  focusedContainerColor = colors.canvas,
                  unfocusedContainerColor = colors.canvas,
                  focusedLabelColor = colors.primary,
                  unfocusedLabelColor = colors.textMuted,
                ),
              modifier = Modifier.fillMaxWidth().testTag("live_frame_email_input"),
            )

            if (!compact) {
              OutlinedTextField(
                value = searchFilter,
                onValueChange = { searchFilter = it },
                placeholder = {
                  Text(
                    text = "Filter components or tokens...",
                    fontFamily = fonts.body,
                    fontSize = (baseSp - 1f).coerceAtLeast(11f).sp,
                    color = colors.textMuted,
                  )
                },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(16.dp),
                  )
                },
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
                modifier = Modifier.fillMaxWidth().testTag("live_frame_search_input"),
              )
            }
          }
        }

        // 3. Real Interactive Buttons Row (Primary, Secondary, Outlined Ghost, Disabled)
        ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(deck.cardPaddingDp.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "LIVE BUTTON STATES",
                fontFamily = fonts.mono,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.primary,
              )
              if (buttonTapCount > 0) {
                Text(
                  text = "Tested ${buttonTapCount}x",
                  fontFamily = fonts.mono,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.success,
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              // Primary CTA Button
              Row(
                modifier =
                  Modifier.testTag("live_frame_primary_btn")
                    .clip(RoundedCornerShape(radius))
                    .background(colors.primary)
                    .border(BorderStroke(bw, colors.border), RoundedCornerShape(radius))
                    .clickable { buttonTapCount++ }
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.Send,
                  contentDescription = null,
                  tint = colors.onPrimary,
                  modifier = Modifier.size(13.dp),
                )
                Text(
                  text = "Primary CTA",
                  fontFamily = fonts.display,
                  fontWeight = if (deck.headingWeightBold) FontWeight.ExtraBold else FontWeight.Bold,
                  fontSize = (baseSp - 1f).coerceAtLeast(11f).sp,
                  letterSpacing = tracking,
                  color = colors.onPrimary,
                )
              }

              // Secondary Accent Button
              Box(
                modifier =
                  Modifier.testTag("live_frame_secondary_btn")
                    .clip(RoundedCornerShape(radius))
                    .background(colors.secondary)
                    .border(BorderStroke(bw, colors.border), RoundedCornerShape(radius))
                    .clickable { buttonTapCount++ }
                    .padding(horizontal = 13.dp, vertical = 9.dp),
              ) {
                Text(
                  text = "Secondary",
                  fontFamily = fonts.display,
                  fontWeight = FontWeight.Bold,
                  fontSize = (baseSp - 1f).coerceAtLeast(11f).sp,
                  letterSpacing = tracking,
                  color = colors.onSecondary,
                )
              }

              // Outlined Ghost Button
              Box(
                modifier =
                  Modifier.testTag("live_frame_ghost_btn")
                    .clip(RoundedCornerShape(radius))
                    .background(colors.surface)
                    .border(BorderStroke(bw, colors.border), RoundedCornerShape(radius))
                    .clickable { buttonTapCount++ }
                    .padding(horizontal = 13.dp, vertical = 9.dp),
              ) {
                Text(
                  text = "Outlined",
                  fontFamily = fonts.display,
                  fontWeight = FontWeight.Bold,
                  fontSize = (baseSp - 1f).coerceAtLeast(11f).sp,
                  letterSpacing = tracking,
                  color = colors.textPrimary,
                )
              }

              // Disabled Button State
              Box(
                modifier =
                  Modifier.alpha(0.45f)
                    .clip(RoundedCornerShape(radius))
                    .background(colors.elevated)
                    .border(BorderStroke(1.dp, colors.border.copy(alpha = 0.4f)), RoundedCornerShape(radius))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
              ) {
                Text(
                  text = "Disabled",
                  fontFamily = fonts.display,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = (baseSp - 1f).coerceAtLeast(11f).sp,
                  color = colors.textMuted,
                )
              }
            }
          }
        }
      }
    }
  }
}
