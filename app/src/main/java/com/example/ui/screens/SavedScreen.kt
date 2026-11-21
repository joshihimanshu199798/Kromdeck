package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.unit.dp
import com.example.model.ThemeDeck
import com.example.ui.components.DiscoverMiniPreview
import com.example.ui.components.TactileButton
import com.example.ui.components.TactileCard
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.util.ColorUtils
import com.example.viewmodel.KromdeckUiState

@Composable
fun SavedScreen(
  state: KromdeckUiState,
  onSaveCurrentActive: () -> Unit,
  onOpenShareBackup: () -> Unit,
  onOpenInStudio: (ThemeDeck) -> Unit,
  onSetCompareLeft: (ThemeDeck) -> Unit,
  onSetWipeBaseline: (ThemeDeck) -> Unit,
  onUpdateNote: (String, String) -> Unit,
  onRemoveSaved: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize().testTag("saved_moodboard_screen"),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    // Header & Actions
    item {
      TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Saved Moodboard (${state.shortlist.size})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
          Text(
            text = "Persisted locally across sessions. Pin favorite looks, attach stakeholder notes, or back up as JSON.",
            style = MaterialTheme.typography.bodySmall,
            color = KromdeckMutedInk,
          )
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TactileButton(
              onClick = onSaveCurrentActive,
              containerColor = KromdeckIndigo,
              contentColor = Color.White,
              shadowOffset = 2.dp,
              modifier = Modifier.weight(1f),
              testTag = "saved_pin_active_btn",
            ) {
              Icon(Icons.Filled.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Save Active Look", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
            }
            TactileButton(
              onClick = onOpenShareBackup,
              containerColor = KromdeckEmerald,
              contentColor = Color.White,
              shadowOffset = 2.dp,
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
              testTag = "saved_json_backup_btn",
            ) {
              Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Backup", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    if (state.shortlist.isEmpty()) {
      item {
        TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text("Your moodboard is empty", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
            Text(
              text = "Tap the heart icon on any Discover card or save your customized theme from Studio.",
              style = MaterialTheme.typography.bodySmall,
              color = KromdeckMutedInk,
            )
          }
        }
      }
    } else {
      items(state.shortlist, key = { it.id }) { deck ->
        val p = deck.palette
        TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth(), testTag = "saved_card_${deck.id}") {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DiscoverMiniPreview(deck = deck)

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(deck.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
                Text("${deck.physics.label} • ${deck.fontPairing.label}", style = MaterialTheme.typography.labelSmall, color = KromdeckMutedInk)
              }
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(p.canvasHex, p.primaryHex, p.secondaryHex, p.warmHex).forEach { hex ->
                  Box(
                    modifier =
                      Modifier.size(18.dp)
                        .clip(CircleShape)
                        .background(ColorUtils.parseHexColor(hex))
                        .border(1.dp, KromdeckSlateInk, CircleShape),
                  )
                }
              }
            }

            OutlinedTextField(
              value = deck.note,
              onValueChange = { onUpdateNote(deck.id, it) },
              label = { Text("Stakeholder / Moodboard Note") },
              singleLine = true,
              shape = RoundedCornerShape(10.dp),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = KromdeckIndigo,
                  unfocusedBorderColor = KromdeckSlateInk,
                  focusedContainerColor = KromdeckCreamCanvas,
                  unfocusedContainerColor = KromdeckCreamCanvas,
                ),
              modifier = Modifier.fillMaxWidth(),
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              TactileButton(
                onClick = { onOpenInStudio(deck) },
                containerColor = KromdeckIndigo,
                contentColor = Color.White,
                shadowOffset = 2.dp,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.weight(1f),
                testTag = "saved_open_studio_${deck.id}",
              ) {
                Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Studio", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              }
              TactileButton(
                onClick = { onSetCompareLeft(deck) },
                containerColor = KromdeckAmber,
                contentColor = KromdeckSlateInk,
                shadowOffset = 2.dp,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                testTag = "saved_compare_${deck.id}",
              ) {
                Icon(Icons.Filled.Compare, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Compare A", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              }
              TactileButton(
                onClick = { onSetWipeBaseline(deck) },
                containerColor = KromdeckPureSurface,
                contentColor = KromdeckSlateInk,
                shadowOffset = 2.dp,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                testTag = "saved_baseline_${deck.id}",
              ) {
                Text("Baseline", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              }
              TactileButton(
                onClick = { onRemoveSaved(deck.id) },
                containerColor = KromdeckCoral,
                contentColor = Color.White,
                shadowOffset = 2.dp,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                testTag = "saved_delete_${deck.id}",
              ) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove saved preset", modifier = Modifier.size(15.dp))
              }
            }
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }
}
