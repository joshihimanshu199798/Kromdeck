package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.PresetDecks
import com.example.model.DiscoverCategory
import com.example.model.ThemeDeck
import com.example.ui.components.DiscoverMiniPreview
import com.example.ui.components.TactileButton
import com.example.ui.components.TactileCard
import com.example.ui.components.TactilePillChip
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.util.ColorUtils
import com.example.viewmodel.KromdeckUiState

@Composable
fun DiscoverScreen(
  state: KromdeckUiState,
  onSearchChange: (String) -> Unit,
  onCategorySelect: (DiscoverCategory) -> Unit,
  onSurfaceFilterSelect: (String) -> Unit,
  onToggleCardFlip: (String) -> Unit,
  onToggleSave: (ThemeDeck) -> Unit,
  onOpenInStudio: (ThemeDeck) -> Unit,
  onOpenExtractor: () -> Unit,
  onNotify: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val clipboard = LocalClipboardManager.current
  val surfaceFilters = listOf("All", "Flat", "Soft Shadow", "Glassmorphism", "Neo-Brutalist", "Outlined")

  val filteredDecks =
    remember(state.discoverSearch, state.discoverCategory, state.discoverSurfaceFilter) {
      val query = state.discoverSearch.trim().lowercase()
      PresetDecks.all.filter { deck ->
        val matchesCategory =
          state.discoverCategory == DiscoverCategory.ALL || deck.category == state.discoverCategory
        val matchesSurface =
          state.discoverSurfaceFilter == "All" ||
            deck.physics.filterTag.equals(state.discoverSurfaceFilter, ignoreCase = true)
        val matchesQuery =
          query.isEmpty() ||
            deck.name.lowercase().contains(query) ||
            deck.tagline.lowercase().contains(query) ||
            deck.physics.label.lowercase().contains(query) ||
            deck.fontPairing.label.lowercase().contains(query)
        matchesCategory && matchesSurface && matchesQuery
      }
    }

  val savedIds = remember(state.shortlist) { state.shortlist.map { it.id }.toSet() }

  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val columns = if (maxWidth >= 660.dp) 2 else 1
    val rows = remember(filteredDecks, columns) { filteredDecks.chunked(columns) }

    LazyColumn(
      modifier = Modifier.fillMaxSize().testTag("discover_feed_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      // Hero / Search + Image-to-Theme Extractor CTA
      item {
        TactileCard(
          containerColor = KromdeckPureSurface,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                  text = "UI/UX Discovery Feed",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.ExtraBold,
                  color = KromdeckSlateInk,
                )
                Text(
                  text = "${filteredDecks.size} curated looks • Flip Light/Dark, tap swatch to copy, open in Studio",
                  style = MaterialTheme.typography.bodySmall,
                  color = KromdeckMutedInk,
                )
              }
              TactileButton(
                onClick = onOpenExtractor,
                containerColor = KromdeckAmber,
                contentColor = KromdeckSlateInk,
                shadowOffset = 2.dp,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                testTag = "open_image_extractor_button",
              ) {
                Icon(Icons.Filled.ImageSearch, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Extract", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
              }
            }

            // Instant Keyword Search
            OutlinedTextField(
              value = state.discoverSearch,
              onValueChange = onSearchChange,
              placeholder = { Text("Search looks (e.g., Glacier, Obsidian, Glass, Serif)...") },
              leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = KromdeckSlateInk) },
              trailingIcon = {
                if (state.discoverSearch.isNotEmpty()) {
                  IconButton(onClick = { onSearchChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear search", tint = KromdeckSlateInk)
                  }
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = KromdeckIndigo,
                  unfocusedBorderColor = KromdeckSlateInk,
                  focusedContainerColor = KromdeckCreamCanvas,
                  unfocusedContainerColor = KromdeckCreamCanvas,
                ),
              modifier = Modifier.fillMaxWidth().testTag("discover_search_input"),
            )

            // Category Pills
            Row(
              modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              DiscoverCategory.entries.forEach { cat ->
                TactilePillChip(
                  label = cat.label,
                  selected = state.discoverCategory == cat,
                  onClick = { onCategorySelect(cat) },
                  activeColor = KromdeckIndigo,
                  testTag = "discover_cat_${cat.name.lowercase()}",
                )
              }
            }

            // Surface Style Filters
            Row(
              modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "Surface:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = KromdeckMutedInk,
              )
              surfaceFilters.forEach { sf ->
                val sel = state.discoverSurfaceFilter == sf
                Box(
                  modifier =
                    Modifier.testTag("discover_surface_${sf.lowercase().replace(" ", "_")}")
                      .clip(RoundedCornerShape(999.dp))
                      .background(if (sel) KromdeckSlateInk else KromdeckCreamCanvas)
                      .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(999.dp))
                      .clickable { onSurfaceFilterSelect(sf) }
                      .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                  Text(
                    text = sf,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (sel) Color.White else KromdeckSlateInk,
                  )
                }
              }
            }
          }
        }
      }

      if (rows.isEmpty()) {
        item {
          TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Text(
                text = "No matching UI looks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = KromdeckSlateInk,
              )
              TactileButton(
                onClick = {
                  onSearchChange("")
                  onCategorySelect(DiscoverCategory.ALL)
                  onSurfaceFilterSelect("All")
                },
                containerColor = KromdeckIndigo,
                contentColor = Color.White,
                testTag = "discover_reset_filters_button",
              ) {
                Text("Reset Filters", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      } else {
        items(rows, key = { chunk -> chunk.joinToString("_") { it.id } }) { rowDecks ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            rowDecks.forEach { baseDeck ->
              val isFlipped = baseDeck.id in state.discoverFlippedIds
              val displayDeck =
                remember(baseDeck, isFlipped) {
                  if (isFlipped) ColorUtils.generateCompanionDeck(baseDeck) else baseDeck
                }
              val isSaved = displayDeck.id in savedIds || baseDeck.id in savedIds

              DiscoverLookCard(
                deck = displayDeck,
                isSaved = isSaved,
                onFlipLightDark = { onToggleCardFlip(baseDeck.id) },
                onCopyHex = { hex ->
                  clipboard.setText(AnnotatedString(hex))
                  onNotify("Copied $hex")
                },
                onToggleSave = { onToggleSave(displayDeck) },
                onOpenInStudio = { onOpenInStudio(displayDeck) },
                modifier = Modifier.weight(1f),
              )
            }
            if (rowDecks.size < columns) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(24.dp)) }
    }
  }
}

@Composable
private fun DiscoverLookCard(
  deck: ThemeDeck,
  isSaved: Boolean,
  onFlipLightDark: () -> Unit,
  onCopyHex: (String) -> Unit,
  onToggleSave: () -> Unit,
  onOpenInStudio: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val p = deck.palette
  val swatches = listOf(p.canvasHex, p.surfaceHex, p.primaryHex, p.secondaryHex, p.warmHex)

  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = modifier,
    testTag = "discover_card_${deck.id}",
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      // Live mini-UI preview thumbnail with quick Light/Dark flip overlay button
      Box(modifier = Modifier.fillMaxWidth()) {
        DiscoverMiniPreview(deck = deck)
        Box(
          modifier =
            Modifier.align(Alignment.BottomEnd)
              .padding(6.dp)
              .clip(RoundedCornerShape(999.dp))
              .background(KromdeckPureSurface.copy(alpha = 0.94f))
              .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(999.dp))
              .clickable(onClick = onFlipLightDark)
              .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(
              imageVector = Icons.Filled.Contrast,
              contentDescription = "Flip card Light or Dark",
              tint = KromdeckSlateInk,
              modifier = Modifier.size(12.dp),
            )
            Text(
              text = if (deck.isDark) "Dark" else "Light",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckSlateInk,
            )
          }
        }
      }

      // Title + Tagline + Heart/Save
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
          Text(
            text = deck.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = "${deck.category.label} • ${deck.fontPairing.label}",
            style = MaterialTheme.typography.bodySmall,
            color = KromdeckMutedInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        Box(
          modifier =
            Modifier.size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSaved) KromdeckCoral.copy(alpha = 0.14f) else KromdeckCreamCanvas)
              .border(1.5.dp, if (isSaved) KromdeckCoral else KromdeckSlateInk, RoundedCornerShape(10.dp))
              .clickable(onClick = onToggleSave),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isSaved) "Remove from Saved Moodboard" else "Save to Moodboard",
            tint = if (isSaved) KromdeckCoral else KromdeckSlateInk,
            modifier = Modifier.size(18.dp),
          )
        }
      }

      // Clickable Color Swatch Dots + Open in Studio
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          swatches.forEach { hex ->
            Box(
              modifier =
                Modifier.size(24.dp)
                  .clip(CircleShape)
                  .background(ColorUtils.parseHexColor(hex))
                  .border(1.5.dp, KromdeckSlateInk, CircleShape)
                  .clickable { onCopyHex(hex) },
            )
          }
        }

        TactileButton(
          onClick = onOpenInStudio,
          containerColor = KromdeckIndigo,
          contentColor = Color.White,
          shadowOffset = 2.dp,
          cornerRadius = 10.dp,
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          testTag = "open_in_studio_${deck.id}",
        ) {
          Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Open in Studio", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
        }
      }
    }
  }
}
