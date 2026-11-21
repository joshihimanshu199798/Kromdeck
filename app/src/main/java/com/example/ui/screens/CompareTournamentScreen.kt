package com.example.ui.screens

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
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.PresetDecks
import com.example.model.DesignPatternSummary
import com.example.model.PreviewTemplate
import com.example.model.ThemeDeck
import com.example.model.ThemeWinLossRecord
import com.example.ui.components.LiveTemplatePreviewFrame
import com.example.ui.components.TactileButton
import com.example.ui.components.TactileCard
import com.example.ui.components.TactilePillChip
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckAmberTint
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckEmeraldTint
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckIndigoTint
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.util.ColorUtils
import com.example.viewmodel.KromdeckUiState

@Composable
fun CompareTournamentScreen(
  state: KromdeckUiState,
  onSelectTemplate: (PreviewTemplate) -> Unit,
  onSelectLeftDeck: (ThemeDeck) -> Unit,
  onSelectRightDeck: (ThemeDeck) -> Unit,
  onSwapCompareDecks: () -> Unit,
  onStartTournament: () -> Unit,
  onPickTournamentWinner: (Boolean) -> Unit,
  onResetTournament: () -> Unit,
  onClearTournamentRecords: () -> Unit,
  onSaveDeck: (ThemeDeck) -> Unit,
  onOpenInStudio: (ThemeDeck) -> Unit,
  modifier: Modifier = Modifier,
) {
  var subTab by remember { mutableIntStateOf(0) } // 0 = A/B Split, 1 = Wipe Slider, 2 = This or That
  var wipeFraction by remember { mutableFloatStateOf(0.5f) }

  val allAvailableDecks =
    remember(state.activeDeck, state.shortlist) {
      (listOf(state.activeDeck) + state.shortlist + PresetDecks.all).distinctBy { it.id }
    }

  LazyColumn(
    modifier = modifier.fillMaxSize().testTag("compare_tournament_screen"),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    // Sub-mode switcher + Template picker
    item {
      TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            TactilePillChip(
              label = "A/B Split",
              selected = subTab == 0,
              onClick = { subTab = 0 },
              activeColor = KromdeckIndigo,
              testTag = "compare_subtab_split",
            )
            TactilePillChip(
              label = "Wipe Slider",
              selected = subTab == 1,
              onClick = { subTab = 1 },
              activeColor = KromdeckEmerald,
              testTag = "compare_subtab_wipe",
            )
            TactilePillChip(
              label = "This or That",
              selected = subTab == 2,
              onClick = { subTab = 2 },
              activeColor = KromdeckCoral,
              testTag = "compare_subtab_tournament",
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            PreviewTemplate.entries.forEach { tpl ->
              TactilePillChip(
                label = tpl.label,
                selected = state.activeTemplate == tpl,
                onClick = { onSelectTemplate(tpl) },
                activeColor = KromdeckSlateInk,
                testTag = "compare_tpl_${tpl.name.lowercase()}",
              )
            }
          }
        }
      }
    }

    when (subTab) {
      0 -> {
        item {
          AbSideBySideSection(
            leftDeck = state.compareLeftDeck,
            rightDeck = state.compareRightDeck,
            availableDecks = allAvailableDecks,
            template = state.activeTemplate,
            brandAppName = state.brandAppName,
            brandTagline = state.brandTagline,
            onSelectLeft = onSelectLeftDeck,
            onSelectRight = onSelectRightDeck,
            onSwap = onSwapCompareDecks,
            onOpenInStudio = onOpenInStudio,
            onSaveDeck = onSaveDeck,
          )
        }
      }

      1 -> {
        item {
          TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Icon(Icons.Filled.Compare, contentDescription = null, tint = KromdeckEmerald, modifier = Modifier.size(18.dp))
                  Text("Interactive Split-Screen Wipe", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
                }
                Text("${(wipeFraction * 100).toInt()}%", fontFamily = JetBrainsMonoFamily, style = MaterialTheme.typography.labelMedium, color = KromdeckIndigo)
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DeckSelectorChip(label = "Left A", selectedDeck = state.compareLeftDeck, options = allAvailableDecks, onSelect = onSelectLeftDeck, modifier = Modifier.weight(1f))
                DeckSelectorChip(label = "Right B", selectedDeck = state.compareRightDeck, options = allAvailableDecks, onSelect = onSelectRightDeck, modifier = Modifier.weight(1f))
              }

              Text(
                text = "Drag horizontally across the preview to wipe between Deck A and Deck B.",
                style = MaterialTheme.typography.bodySmall,
                color = KromdeckMutedInk,
              )

              SplitWipePreviewStage(
                leftDeck = state.compareLeftDeck,
                rightDeck = state.compareRightDeck,
                template = state.activeTemplate,
                brandAppName = state.brandAppName,
                brandTagline = state.brandTagline,
                wipeFraction = wipeFraction,
                onWipeChange = { wipeFraction = it },
              )
            }
          }
        }
      }

      else -> {
        item {
          TournamentBracketSection(
            state = state,
            onStartTournament = onStartTournament,
            onPickWinner = onPickTournamentWinner,
            onReset = onResetTournament,
            onOpenInStudio = onOpenInStudio,
            onSaveDeck = onSaveDeck,
          )
        }
        item {
          TournamentLeaderboardSection(
            records = state.tournamentRecords,
            summary = state.patternSummary,
            onClearHistory = onClearTournamentRecords,
          )
        }
      }
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }
}

@Composable
private fun AbSideBySideSection(
  leftDeck: ThemeDeck,
  rightDeck: ThemeDeck,
  availableDecks: List<ThemeDeck>,
  template: PreviewTemplate,
  brandAppName: String,
  brandTagline: String,
  onSelectLeft: (ThemeDeck) -> Unit,
  onSelectRight: (ThemeDeck) -> Unit,
  onSwap: () -> Unit,
  onOpenInStudio: (ThemeDeck) -> Unit,
  onSaveDeck: (ThemeDeck) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        DeckSelectorChip(label = "Deck A", selectedDeck = leftDeck, options = availableDecks, onSelect = onSelectLeft, modifier = Modifier.weight(1f))
        TactileButton(
          onClick = onSwap,
          containerColor = KromdeckAmber,
          contentColor = KromdeckSlateInk,
          shadowOffset = 2.dp,
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
          testTag = "compare_swap_button",
        ) {
          Icon(Icons.Filled.SwapHoriz, contentDescription = "Swap decks", modifier = Modifier.size(18.dp))
        }
        DeckSelectorChip(label = "Deck B", selectedDeck = rightDeck, options = availableDecks, onSelect = onSelectRight, modifier = Modifier.weight(1f))
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      ComparePaneColumn(
        badge = "A",
        badgeColor = KromdeckIndigo,
        deck = leftDeck,
        template = template,
        brandAppName = brandAppName,
        brandTagline = brandTagline,
        onOpenInStudio = { onOpenInStudio(leftDeck) },
        onSaveDeck = { onSaveDeck(leftDeck) },
        modifier = Modifier.weight(1f),
      )
      ComparePaneColumn(
        badge = "B",
        badgeColor = KromdeckCoral,
        deck = rightDeck,
        template = template,
        brandAppName = brandAppName,
        brandTagline = brandTagline,
        onOpenInStudio = { onOpenInStudio(rightDeck) },
        onSaveDeck = { onSaveDeck(rightDeck) },
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun ComparePaneColumn(
  badge: String,
  badgeColor: Color,
  deck: ThemeDeck,
  template: PreviewTemplate,
  brandAppName: String,
  brandTagline: String,
  onOpenInStudio: () -> Unit,
  onSaveDeck: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      Box(
        modifier =
          Modifier.size(22.dp)
            .clip(CircleShape)
            .background(badgeColor)
            .border(1.5.dp, KromdeckSlateInk, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Text(badge, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
      }
      Text(deck.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = KromdeckSlateInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    LiveTemplatePreviewFrame(
      deck = deck,
      template = template,
      compact = true,
      brandAppName = brandAppName,
      brandTagline = brandTagline,
      modifier = Modifier.fillMaxWidth(),
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      TactileButton(
        onClick = onOpenInStudio,
        containerColor = KromdeckPureSurface,
        contentColor = KromdeckSlateInk,
        shadowOffset = 2.dp,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        modifier = Modifier.weight(1f),
        testTag = "compare_use_${badge.lowercase()}",
      ) {
        Text("Studio", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
      }
      TactileButton(
        onClick = onSaveDeck,
        containerColor = badgeColor,
        contentColor = Color.White,
        shadowOffset = 2.dp,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        testTag = "compare_save_${badge.lowercase()}",
      ) {
        Icon(Icons.Filled.BookmarkAdd, contentDescription = "Save to Moodboard", modifier = Modifier.size(14.dp))
      }
    }
  }
}

@Composable
private fun DeckSelectorChip(
  label: String,
  selectedDeck: ThemeDeck,
  options: List<ThemeDeck>,
  onSelect: (ThemeDeck) -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }
  Box(modifier = modifier) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(KromdeckCreamCanvas)
          .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(10.dp))
          .clickable { expanded = true }
          .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
      Text(label, style = MaterialTheme.typography.labelSmall, color = KromdeckMutedInk)
      Text(selectedDeck.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = KromdeckSlateInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(KromdeckPureSurface)) {
      options.forEach { deck ->
        DropdownMenuItem(
          text = { Text(deck.name, color = KromdeckSlateInk) },
          onClick = {
            onSelect(deck)
            expanded = false
          },
        )
      }
    }
  }
}

@Composable
private fun TournamentBracketSection(
  state: KromdeckUiState,
  onStartTournament: () -> Unit,
  onPickWinner: (Boolean) -> Unit,
  onReset: () -> Unit,
  onOpenInStudio: (ThemeDeck) -> Unit,
  onSaveDeck: (ThemeDeck) -> Unit,
) {
  val t = state.tournament

  if (!t.isActive) {
    TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = KromdeckAmber, modifier = Modifier.size(26.dp))
          Text("\"This or That\" Elimination Tournament", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
        }
        Text(
          text = "Eliminate UI decision paralysis with quick 1v1 matchups. Every pick updates your persistent Design DNA win-loss record.",
          style = MaterialTheme.typography.bodyMedium,
          color = KromdeckMutedInk,
        )
        TactileButton(
          onClick = onStartTournament,
          containerColor = KromdeckCoral,
          contentColor = Color.White,
          modifier = Modifier.fillMaxWidth(),
          testTag = "start_tournament_button",
        ) {
          Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Start 8-Deck Elimination Bracket", fontWeight = FontWeight.ExtraBold)
        }
      }
    }
    return
  }

  val champion = t.champion
  if (champion != null) {
    TactileCard(containerColor = KromdeckEmeraldTint, borderColor = KromdeckEmerald, modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = KromdeckAmber, modifier = Modifier.size(28.dp))
          Column {
            Text("TOURNAMENT CHAMPION", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = KromdeckEmerald)
            Text(champion.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
          }
        }
        LiveTemplatePreviewFrame(deck = champion, template = state.activeTemplate, compact = true, modifier = Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          TactileButton(onClick = { onOpenInStudio(champion) }, containerColor = KromdeckIndigo, contentColor = Color.White, modifier = Modifier.weight(1f), testTag = "champion_open_studio") {
            Text("Open in Studio", fontWeight = FontWeight.Bold)
          }
          TactileButton(onClick = { onSaveDeck(champion) }, containerColor = KromdeckEmerald, contentColor = Color.White, modifier = Modifier.weight(1f), testTag = "champion_save_btn") {
            Text("Save Winner", fontWeight = FontWeight.Bold)
          }
        }
        TactileButton(onClick = onReset, containerColor = KromdeckPureSurface, contentColor = KromdeckSlateInk, modifier = Modifier.fillMaxWidth(), testTag = "tournament_replay_btn") {
          Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Run Another Bracket", fontWeight = FontWeight.Bold)
        }
      }
    }
    return
  }

  val left = t.leftContender ?: return
  val right = t.rightContender ?: return

  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    TactileCard(containerColor = KromdeckAmberTint, modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(t.roundLabel.uppercase(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = KromdeckCoral)
          Text("Match ${t.currentMatchNumber} of ${t.totalMatchesInRound} • Tap your winner", style = MaterialTheme.typography.bodySmall, color = KromdeckSlateInk)
        }
        TactileButton(onClick = onReset, containerColor = KromdeckPureSurface, contentColor = KromdeckSlateInk, shadowOffset = 2.dp, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp), testTag = "tournament_reset_btn") {
          Text("Reset", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(left.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
        LiveTemplatePreviewFrame(deck = left, template = state.activeTemplate, compact = true, modifier = Modifier.fillMaxWidth())
        TactileButton(onClick = { onPickWinner(true) }, containerColor = KromdeckIndigo, contentColor = Color.White, modifier = Modifier.fillMaxWidth(), testTag = "pick_left_winner") {
          Text("Pick Left", fontWeight = FontWeight.ExtraBold)
        }
      }
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(right.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
        LiveTemplatePreviewFrame(deck = right, template = state.activeTemplate, compact = true, modifier = Modifier.fillMaxWidth())
        TactileButton(onClick = { onPickWinner(false) }, containerColor = KromdeckCoral, contentColor = Color.White, modifier = Modifier.fillMaxWidth(), testTag = "pick_right_winner") {
          Text("Pick Right", fontWeight = FontWeight.ExtraBold)
        }
      }
    }
  }
}

@Composable
private fun TournamentLeaderboardSection(
  records: List<ThemeWinLossRecord>,
  summary: DesignPatternSummary,
  onClearHistory: () -> Unit,
) {
  TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth().testTag("tournament_leaderboard_card")) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Icon(Icons.Filled.Leaderboard, contentDescription = null, tint = KromdeckIndigo, modifier = Modifier.size(18.dp))
          Text("Design DNA & Win-Loss Tracker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
        }
        if (records.isNotEmpty()) {
          TactileButton(
            onClick = onClearHistory,
            containerColor = KromdeckCreamCanvas,
            contentColor = KromdeckCoral,
            shadowOffset = 1.dp,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            testTag = "clear_tournament_stats_btn",
          ) {
            Icon(Icons.Filled.DeleteSweep, contentDescription = "Clear records", modifier = Modifier.size(14.dp))
          }
        }
      }

      if (records.isEmpty()) {
        Text("Play 1v1 matchups above to uncover your favorite surface physics, typography, and Light/Dark affinity.", style = MaterialTheme.typography.bodySmall, color = KromdeckMutedInk)
      } else {
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          summary.preferredPhysics?.let { phys ->
            PatternStatPill("Favorite Surface", phys.label, KromdeckIndigoTint)
          }
          summary.preferredFont?.let { font ->
            PatternStatPill("Favorite Type", font.label, KromdeckEmeraldTint)
          }
          summary.preferredPolarity?.let { pol ->
            PatternStatPill("Polarity", pol, KromdeckAmberTint)
          }
        }

        records.take(6).forEachIndexed { idx, rec ->
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(KromdeckCreamCanvas)
                .border(1.dp, KromdeckSlateInk, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
              Text("#${idx + 1}", fontFamily = JetBrainsMonoFamily, fontWeight = FontWeight.ExtraBold, color = KromdeckIndigo)
              Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(ColorUtils.parseHexColor(rec.primaryHex)).border(1.dp, KromdeckSlateInk, CircleShape))
              Text(rec.deckName, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = KromdeckSlateInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(
              text = "${rec.wins}W–${rec.losses}L (${rec.winRatePercent}%)",
              fontFamily = JetBrainsMonoFamily,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckEmerald,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PatternStatPill(
  title: String,
  value: String,
  bg: Color,
) {
  Column(
    modifier =
      Modifier.clip(RoundedCornerShape(10.dp))
        .background(bg)
        .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(10.dp))
        .padding(horizontal = 10.dp, vertical = 6.dp),
  ) {
    Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, color = KromdeckMutedInk)
    Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
  }
}
