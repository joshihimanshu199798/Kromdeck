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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.RandomizerLocks
import com.example.model.ThemeDeck
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.util.ColorUtils

@Composable
fun KromdeckHeader(
  activeDeck: ThemeDeck,
  isGuideActive: Boolean,
  canUndo: Boolean,
  canRedo: Boolean,
  randomizerLocks: RandomizerLocks,
  onToggleGuide: () -> Unit,
  onOpenStudio: () -> Unit,
  onToggleCompanion: () -> Unit,
  onUndo: () -> Unit,
  onRedo: () -> Unit,
  onRandomize: () -> Unit,
  onToggleLockPalette: () -> Unit,
  onToggleLockTypography: () -> Unit,
  onToggleLockSurface: () -> Unit,
  onOpenShareBackup: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var showLocksMenu by remember { mutableStateOf(false) }
  val activeLocksCount =
    listOf(randomizerLocks.lockPalette, randomizerLocks.lockTypography, randomizerLocks.lockSurface).count { it }

  Surface(
    color = KromdeckCreamCanvas,
    modifier = modifier.fillMaxWidth(),
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        // Row 1: Brand Logo + Active Theme Pill + Light/Dark Flip
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f).padding(end = 8.dp),
          ) {
            KromdeckLogoMark()
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "Kromdeck",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = KromdeckSlateInk,
                maxLines = 1,
              )
              Row(
                modifier =
                  Modifier.clip(RoundedCornerShape(999.dp))
                    .background(KromdeckPureSurface)
                    .border(BorderStroke(1.5.dp, KromdeckSlateInk), RoundedCornerShape(999.dp))
                    .clickable(onClick = onOpenStudio)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
              ) {
                Box(
                  modifier =
                    Modifier.size(10.dp)
                      .clip(CircleShape)
                      .background(ColorUtils.parseHexColor(activeDeck.palette.primaryHex))
                      .border(1.dp, KromdeckSlateInk, CircleShape),
                )
                Text(
                  text = activeDeck.name,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = KromdeckSlateInk,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Icon(
                  imageVector = if (activeDeck.isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                  contentDescription = if (activeDeck.isDark) "Dark theme active" else "Light theme active",
                  tint = if (activeDeck.isDark) KromdeckIndigo else KromdeckAmber,
                  modifier = Modifier.size(12.dp),
                )
              }
            }
          }

          TactileButton(
            onClick = onToggleCompanion,
            containerColor = if (activeDeck.isDark) KromdeckAmber else KromdeckSlateInk,
            contentColor = if (activeDeck.isDark) KromdeckSlateInk else Color.White,
            shadowOffset = 2.dp,
            cornerRadius = 10.dp,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            testTag = "auto_companion_button",
          ) {
            Icon(Icons.Filled.Contrast, contentDescription = "Flip Light or Dark pair", modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (activeDeck.isDark) "Light" else "Dark",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.ExtraBold,
            )
          }
        }

        // Row 2: Global Action Bar (Guide / Undo / Redo / Dice Randomizer + Locks / Share & Backup)
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          TactileButton(
            onClick = onToggleGuide,
            containerColor = if (isGuideActive) KromdeckSlateInk else KromdeckAmber,
            contentColor = if (isGuideActive) Color.White else KromdeckSlateInk,
            shadowOffset = 2.dp,
            cornerRadius = 10.dp,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            testTag = "global_guide_button",
          ) {
            Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Open Kromdeck guide", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isGuideActive) "Close Guide" else "Guide",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.ExtraBold,
            )
          }

          TactileButton(
            onClick = onUndo,
            enabled = canUndo,
            containerColor = KromdeckPureSurface,
            contentColor = KromdeckSlateInk,
            shadowOffset = 2.dp,
            cornerRadius = 10.dp,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            testTag = "global_undo_button",
          ) {
            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo style change", modifier = Modifier.size(16.dp))
          }

          TactileButton(
            onClick = onRedo,
            enabled = canRedo,
            containerColor = KromdeckPureSurface,
            contentColor = KromdeckSlateInk,
            shadowOffset = 2.dp,
            cornerRadius = 10.dp,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            testTag = "global_redo_button",
          ) {
            Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo style change", modifier = Modifier.size(16.dp))
          }

          TactileButton(
            onClick = onRandomize,
            containerColor = KromdeckIndigo,
            contentColor = Color.White,
            shadowOffset = 2.dp,
            cornerRadius = 10.dp,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            testTag = "global_dice_button",
          ) {
            Icon(Icons.Filled.Casino, contentDescription = "Roll smart random theme", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text("Dice", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
          }

          Box {
            TactileButton(
              onClick = { showLocksMenu = true },
              containerColor = if (activeLocksCount > 0) KromdeckAmber else KromdeckPureSurface,
              contentColor = KromdeckSlateInk,
              shadowOffset = 2.dp,
              cornerRadius = 10.dp,
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              testTag = "randomizer_locks_button",
            ) {
              Icon(
                imageVector = if (activeLocksCount > 0) Icons.Filled.Lock else Icons.Filled.LockOpen,
                contentDescription = "Configure randomizer parameter locks",
                modifier = Modifier.size(15.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (activeLocksCount > 0) "Locks ($activeLocksCount)" else "Locks",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
              )
            }

            DropdownMenu(
              expanded = showLocksMenu,
              onDismissRequest = { showLocksMenu = false },
              modifier = Modifier.background(KromdeckPureSurface),
            ) {
              LockMenuItem("Lock Palette", randomizerLocks.lockPalette, onToggleLockPalette)
              LockMenuItem("Lock Typography", randomizerLocks.lockTypography, onToggleLockTypography)
              LockMenuItem("Lock Surface", randomizerLocks.lockSurface, onToggleLockSurface)
            }
          }

          TactileButton(
            onClick = onOpenShareBackup,
            containerColor = KromdeckEmerald,
            contentColor = Color.White,
            shadowOffset = 2.dp,
            cornerRadius = 10.dp,
            contentPadding = PaddingValues(horizontal = 11.dp, vertical = 6.dp),
            testTag = "global_share_backup_button",
          ) {
            Icon(Icons.Filled.Share, contentDescription = "Share theme hash or backup JSON", modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Share / JSON", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          }
        }
      }
      HorizontalDivider(thickness = 2.dp, color = KromdeckSlateInk)
    }
  }
}

@Composable
private fun LockMenuItem(
  label: String,
  locked: Boolean,
  onClick: () -> Unit,
) {
  DropdownMenuItem(
    text = {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(
          imageVector = if (locked) Icons.Filled.Lock else Icons.Filled.LockOpen,
          contentDescription = null,
          tint = if (locked) KromdeckIndigo else KromdeckMutedInk,
          modifier = Modifier.size(16.dp),
        )
        Text(
          text = label,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = if (locked) FontWeight.ExtraBold else FontWeight.Medium,
          color = KromdeckSlateInk,
        )
      }
    },
    onClick = onClick,
  )
}
