package com.example.util

import com.example.model.DesignPatternSummary
import com.example.model.FontPairing
import com.example.model.SurfacePhysics
import com.example.model.ThemeDeck
import com.example.model.ThemeWinLossRecord

object TournamentAnalytics {

  fun recordMatchup(
    existing: List<ThemeWinLossRecord>,
    winner: ThemeDeck,
    loser: ThemeDeck,
    isChampionship: Boolean,
  ): List<ThemeWinLossRecord> {
    val byId = existing.associateBy { it.deckId }.toMutableMap()

    val prevWinner = byId[winner.id]
    byId[winner.id] =
      ThemeWinLossRecord(
        deckId = winner.id,
        deckName = winner.name,
        physics = winner.physics,
        fontPairing = winner.fontPairing,
        isDark = winner.isDark,
        primaryHex = winner.palette.primaryHex,
        canvasHex = winner.palette.canvasHex,
        wins = (prevWinner?.wins ?: 0) + 1,
        losses = prevWinner?.losses ?: 0,
        championships = (prevWinner?.championships ?: 0) + if (isChampionship) 1 else 0,
      )

    val prevLoser = byId[loser.id]
    byId[loser.id] =
      ThemeWinLossRecord(
        deckId = loser.id,
        deckName = loser.name,
        physics = loser.physics,
        fontPairing = loser.fontPairing,
        isDark = loser.isDark,
        primaryHex = loser.palette.primaryHex,
        canvasHex = loser.palette.canvasHex,
        wins = prevLoser?.wins ?: 0,
        losses = (prevLoser?.losses ?: 0) + 1,
        championships = prevLoser?.championships ?: 0,
      )

    return sortLeaderboard(byId.values.toList())
  }

  fun sortLeaderboard(records: List<ThemeWinLossRecord>): List<ThemeWinLossRecord> {
    return records.sortedWith(
      compareByDescending<ThemeWinLossRecord> { it.wins }
        .thenByDescending { it.championships }
        .thenByDescending { it.winRatePercent }
        .thenBy { it.losses },
    )
  }

  fun computePatternSummary(records: List<ThemeWinLossRecord>): DesignPatternSummary {
    if (records.isEmpty()) return DesignPatternSummary()

    val sorted = sortLeaderboard(records)
    val totalMatchups = records.sumOf { it.wins }

    val physicsScores = mutableMapOf<SurfacePhysics, Int>()
    val fontScores = mutableMapOf<FontPairing, Int>()
    var darkWins = 0
    var lightWins = 0

    for (rec in records) {
      if (rec.wins <= 0) continue
      physicsScores[rec.physics] = (physicsScores[rec.physics] ?: 0) + rec.wins
      fontScores[rec.fontPairing] = (fontScores[rec.fontPairing] ?: 0) + rec.wins
      if (rec.isDark) darkWins += rec.wins else lightWins += rec.wins
    }

    val topPhysics = physicsScores.maxByOrNull { it.value }?.key
    val topFont = fontScores.maxByOrNull { it.value }?.key
    val polarity =
      when {
        darkWins == 0 && lightWins == 0 -> null
        darkWins > lightWins -> "Dark Themes (${((darkWins * 100.0) / (darkWins + lightWins)).toInt()}% wins)"
        lightWins > darkWins -> "Light Themes (${((lightWins * 100.0) / (darkWins + lightWins)).toInt()}% wins)"
        else -> "Balanced Light & Dark"
      }

    return DesignPatternSummary(
      topDeck = sorted.firstOrNull { it.wins > 0 },
      preferredPhysics = topPhysics,
      preferredFont = topFont,
      preferredPolarity = polarity,
      totalMatchupsRecorded = totalMatchups,
    )
  }
}
