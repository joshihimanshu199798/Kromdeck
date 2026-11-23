package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PresetDecks
import com.example.data.ShortlistRepository
import com.example.model.ColorBlindnessMode
import com.example.model.ColorHarmonyRule
import com.example.model.ColorRole
import com.example.model.DesignPatternSummary
import com.example.model.DiscoverCategory
import com.example.model.ExportFormat
import com.example.model.ExtractedPaletteResult
import com.example.model.FontPairing
import com.example.model.GridDensity
import com.example.model.KromdeckMode
import com.example.model.MotionFeel
import com.example.model.NavShellStyle
import com.example.model.PaletteMoodFilter
import com.example.model.PreviewTemplate
import com.example.model.RandomizerLocks
import com.example.model.StudioDrawerTab
import com.example.model.SurfacePhysics
import com.example.model.ThemeDeck
import com.example.model.ThemeWinLossRecord
import com.example.model.TournamentState
import com.example.model.ViewportMode
import com.example.model.WcagAudit
import com.example.util.ColorUtils
import com.example.util.ThemeAlchemyEngine
import com.example.util.TournamentAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class KromdeckUiState(
  val activeMode: KromdeckMode = KromdeckMode.DISCOVER,
  val activeDeck: ThemeDeck = PresetDecks.all.first(),
  val activeTemplate: PreviewTemplate = PreviewTemplate.MUSIC_PLAYER,
  val wcagAudit: WcagAudit = ColorUtils.auditThemeContrast(PresetDecks.all.first().palette),
  // Undo / Redo history
  val undoStack: List<ThemeDeck> = emptyList(),
  val redoStack: List<ThemeDeck> = emptyList(),
  // Smart Randomizer
  val randomizerLocks: RandomizerLocks = RandomizerLocks(),
  // Discover Feed State
  val discoverSearch: String = "",
  val discoverCategory: DiscoverCategory = DiscoverCategory.ALL,
  val discoverSurfaceFilter: String = "All",
  val discoverPolarityFilter: String = "All",
  val discoverFlippedIds: Set<String> = emptySet(),
  // Studio Stage & Drawer State
  val viewportMode: ViewportMode = ViewportMode.MOBILE,
  val colorBlindnessMode: ColorBlindnessMode = ColorBlindnessMode.NORMAL,
  val brandAppName: String = "Kromdeck App",
  val brandTagline: String = "Crafted tactile interface",
  val splitWipeEnabled: Boolean = false,
  val splitWipeBaseline: ThemeDeck = PresetDecks.all[5],
  val presentationShotMode: Boolean = false,
  val activeStudioTab: StudioDrawerTab = StudioDrawerTab.COLORS,
  // Modals
  val showShareBackupModal: Boolean = false,
  val showExtractorModal: Boolean = false,
  val editingColorRole: ColorRole? = null,
  // Saved Moodboard, Compare & Tournament
  val shortlist: List<ThemeDeck> = emptyList(),
  val compareLeftDeck: ThemeDeck = PresetDecks.all[0],
  val compareRightDeck: ThemeDeck = PresetDecks.all[5],
  val tournament: TournamentState = TournamentState(),
  val tournamentRecords: List<ThemeWinLossRecord> = emptyList(),
  val patternSummary: DesignPatternSummary = DesignPatternSummary(),
  // Extractor & Exports
  val extractedResult: ExtractedPaletteResult? = null,
  val isExtractingPalette: Boolean = false,
  val selectedExportFormat: ExportFormat = ExportFormat.JSON_TOKENS,
  val statusMessage: String? = null,
) {
  val canUndo: Boolean get() = undoStack.isNotEmpty()
  val canRedo: Boolean get() = redoStack.isNotEmpty()
}

class KromdeckViewModel(application: Application) : AndroidViewModel(application) {

  private val shortlistRepo = ShortlistRepository(application.applicationContext)

  private val _uiState = MutableStateFlow(KromdeckUiState())
  val uiState: StateFlow<KromdeckUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      shortlistRepo.shortlistFlow.collect { saved ->
        _uiState.update { it.copy(shortlist = saved) }
      }
    }
    viewModelScope.launch {
      shortlistRepo.tournamentRecordsFlow.collect { records ->
        val sorted = TournamentAnalytics.sortLeaderboard(records)
        _uiState.update {
          it.copy(
            tournamentRecords = sorted,
            patternSummary = TournamentAnalytics.computePatternSummary(sorted),
          )
        }
      }
    }
  }

  /**
   * Central helper that records the previous deck into the Undo stack before mutating activeDeck.
   */
  private fun mutateActiveDeck(
    status: String? = null,
    switchToStudio: Boolean = false,
    transform: (ThemeDeck) -> ThemeDeck,
  ) {
    _uiState.update { state ->
      val nextDeck = transform(state.activeDeck)
      if (nextDeck == state.activeDeck && !switchToStudio) return@update state
      val newUndo = (state.undoStack + state.activeDeck).takeLast(30)
      state.copy(
        activeDeck = nextDeck,
        activeMode = if (switchToStudio) KromdeckMode.STUDIO else state.activeMode,
        wcagAudit = ColorUtils.auditThemeContrast(nextDeck.palette),
        undoStack = newUndo,
        redoStack = emptyList(),
        statusMessage = status ?: state.statusMessage,
      )
    }
  }

  fun undo() {
    _uiState.update { state ->
      val previous = state.undoStack.lastOrNull() ?: return@update state
      state.copy(
        activeDeck = previous,
        wcagAudit = ColorUtils.auditThemeContrast(previous.palette),
        undoStack = state.undoStack.dropLast(1),
        redoStack = (state.redoStack + state.activeDeck).takeLast(30),
        statusMessage = "Undid change (${previous.name})",
      )
    }
  }

  fun redo() {
    _uiState.update { state ->
      val next = state.redoStack.lastOrNull() ?: return@update state
      state.copy(
        activeDeck = next,
        wcagAudit = ColorUtils.auditThemeContrast(next.palette),
        undoStack = (state.undoStack + state.activeDeck).takeLast(30),
        redoStack = state.redoStack.dropLast(1),
        statusMessage = "Redid change (${next.name})",
      )
    }
  }

  fun setMode(mode: KromdeckMode) {
    _uiState.update { it.copy(activeMode = mode) }
  }

  fun setTemplate(template: PreviewTemplate) {
    _uiState.update { it.copy(activeTemplate = template) }
  }

  // --- Smart Randomizer ---
  fun toggleLockPalette() {
    _uiState.update { it.copy(randomizerLocks = it.randomizerLocks.copy(lockPalette = !it.randomizerLocks.lockPalette)) }
  }

  fun toggleLockTypography() {
    _uiState.update { it.copy(randomizerLocks = it.randomizerLocks.copy(lockTypography = !it.randomizerLocks.lockTypography)) }
  }

  fun toggleLockSurface() {
    _uiState.update { it.copy(randomizerLocks = it.randomizerLocks.copy(lockSurface = !it.randomizerLocks.lockSurface)) }
  }

  fun randomizeDeck() {
    val locks = _uiState.value.randomizerLocks
    mutateActiveDeck(status = "Rolled new theme look") { current ->
      ColorUtils.generateSmartRandomDeck(current, locks)
    }
  }

  // --- Discover Feed Controls ---
  fun setDiscoverSearch(query: String) {
    _uiState.update { it.copy(discoverSearch = query) }
  }

  fun setDiscoverCategory(category: DiscoverCategory) {
    _uiState.update { it.copy(discoverCategory = category) }
  }

  fun setDiscoverSurfaceFilter(filter: String) {
    _uiState.update { it.copy(discoverSurfaceFilter = filter) }
  }

  fun setDiscoverPolarityFilter(polarity: String) {
    _uiState.update { it.copy(discoverPolarityFilter = polarity) }
  }

  fun toggleDiscoverCardFlip(deckId: String) {
    _uiState.update { state ->
      val next =
        if (deckId in state.discoverFlippedIds) state.discoverFlippedIds - deckId
        else state.discoverFlippedIds + deckId
      state.copy(discoverFlippedIds = next)
    }
  }

  fun openInStudio(deck: ThemeDeck) {
    val matchingTemplate =
      PreviewTemplate.entries.firstOrNull { it.category == deck.category } ?: _uiState.value.activeTemplate
    mutateActiveDeck(status = "Opened \"${deck.name}\" in Studio", switchToStudio = true) { deck }
    _uiState.update { it.copy(activeTemplate = matchingTemplate) }
  }

  fun selectPreset(deck: ThemeDeck) {
    mutateActiveDeck(status = "Loaded \"${deck.name}\"") { deck }
  }

  // --- Studio Stage Controls ---
  fun setViewportMode(mode: ViewportMode) {
    _uiState.update { it.copy(viewportMode = mode) }
  }

  fun setColorBlindnessMode(mode: ColorBlindnessMode) {
    _uiState.update { it.copy(colorBlindnessMode = mode) }
  }

  fun setBrandIdentity(name: String, tagline: String) {
    _uiState.update { it.copy(brandAppName = name, brandTagline = tagline) }
  }

  fun toggleSplitWipe() {
    _uiState.update { it.copy(splitWipeEnabled = !it.splitWipeEnabled) }
  }

  fun setSplitWipeBaseline(deck: ThemeDeck) {
    _uiState.update {
      it.copy(
        splitWipeBaseline = deck,
        statusMessage = "Pinned \"${deck.name}\" as wipe baseline",
      )
    }
  }

  fun togglePresentationShotMode() {
    _uiState.update { it.copy(presentationShotMode = !it.presentationShotMode) }
  }

  fun setStudioDrawerTab(tab: StudioDrawerTab) {
    _uiState.update { it.copy(activeStudioTab = tab) }
  }

  // --- Deep Customization Controls ---
  fun updateColorRole(role: ColorRole, hex: String) {
    val normalized = ColorUtils.normalizeHex(hex) ?: return
    mutateActiveDeck { deck ->
      val updatedPalette = deck.palette.withRoleHex(role, normalized)
      val isDarkCanvas =
        if (role == ColorRole.CANVAS) {
          ColorUtils.relativeLuminance(ColorUtils.parseHexColor(normalized)) < 0.25
        } else {
          deck.isDark
        }
      val customId = if (deck.id.startsWith("custom_")) deck.id else "custom_${deck.id}"
      val customName = if (deck.name.endsWith("Custom")) deck.name else "${deck.name.take(18)} Custom"
      deck.copy(id = customId, name = customName, isDark = isDarkCanvas, palette = updatedPalette)
    }
  }

  fun harmonizePalette() {
    mutateActiveDeck(status = "Harmonized accents & elevated tones") { deck ->
      deck.copy(palette = ColorUtils.harmonizePalette(deck.palette))
    }
  }

  fun swapAccents() {
    mutateActiveDeck(status = "Swapped Primary & Secondary accents") { deck ->
      deck.copy(palette = ColorUtils.swapAccents(deck.palette))
    }
  }

  fun updateGradient(enabled: Boolean, angle: Float, meshGlow: Boolean) {
    mutateActiveDeck { deck ->
      deck.copy(gradientEnabled = enabled, gradientAngle = angle, gradientMeshGlow = meshGlow)
    }
  }

  fun setPhysics(physics: SurfacePhysics) {
    mutateActiveDeck { deck -> deck.copy(physics = physics) }
  }

  fun updateSurfaceGeometry(
    cornerRadiusDp: Int? = null,
    cardPaddingDp: Int? = null,
    borderWidthDp: Float? = null,
    shadowIntensity: Float? = null,
    glassBlurAlpha: Float? = null,
  ) {
    mutateActiveDeck { deck ->
      deck.copy(
        cornerRadiusDp = cornerRadiusDp ?: deck.cornerRadiusDp,
        cardPaddingDp = cardPaddingDp ?: deck.cardPaddingDp,
        borderWidthDp = borderWidthDp ?: deck.borderWidthDp,
        shadowIntensity = shadowIntensity ?: deck.shadowIntensity,
        glassBlurAlpha = glassBlurAlpha ?: deck.glassBlurAlpha,
      )
    }
  }

  fun setFontPairing(pairing: FontPairing) {
    mutateActiveDeck { deck -> deck.copy(fontPairing = pairing) }
  }

  fun updateTypographyScale(
    baseFontSizeSp: Float? = null,
    headingWeightBold: Boolean? = null,
    letterSpacingSp: Float? = null,
    lineHeightMultiplier: Float? = null,
  ) {
    mutateActiveDeck { deck ->
      deck.copy(
        baseFontSizeSp = baseFontSizeSp ?: deck.baseFontSizeSp,
        headingWeightBold = headingWeightBold ?: deck.headingWeightBold,
        letterSpacingSp = letterSpacingSp ?: deck.letterSpacingSp,
        lineHeightMultiplier = lineHeightMultiplier ?: deck.lineHeightMultiplier,
      )
    }
  }

  fun updateLayoutConfig(
    navShell: NavShellStyle? = null,
    gridDensity: GridDensity? = null,
    iconStrokeWeight: Float? = null,
  ) {
    mutateActiveDeck { deck ->
      deck.copy(
        navShell = navShell ?: deck.navShell,
        gridDensity = gridDensity ?: deck.gridDensity,
        iconStrokeWeight = iconStrokeWeight ?: deck.iconStrokeWeight,
      )
    }
  }

  fun setMotionFeel(feel: MotionFeel) {
    mutateActiveDeck { deck -> deck.copy(motionFeel = feel) }
  }

  fun toggleCompanionDarkLight() {
    val companion = ColorUtils.generateCompanionDeck(_uiState.value.activeDeck)
    mutateActiveDeck(
      status = if (companion.isDark) "Switched to Dark Pair" else "Switched to Light Pair",
    ) { companion }
  }

  fun autoFixContrast() {
    mutateActiveDeck(status = "Auto-fixed contrast to WCAG AA") { deck ->
      deck.copy(palette = ColorUtils.autoFixContrast(deck.palette))
    }
  }

  // --- Infinite Theme Lab (Harmony, DNA Blender, Global Grading, Moods, Rename) ---
  fun renameActiveDeck(newName: String) {
    val sanitized = newName.take(36)
    _uiState.update { state ->
      state.copy(activeDeck = state.activeDeck.copy(name = sanitized))
    }
  }

  fun generateHarmonyTheme(seedHue: Float, rule: ColorHarmonyRule, isDark: Boolean) {
    mutateActiveDeck { current ->
      ThemeAlchemyEngine.generateFromHarmony(seedHue, rule, isDark, current)
    }
  }

  fun applyBlendedTheme(deckA: ThemeDeck, deckB: ThemeDeck, fraction: Float) {
    mutateActiveDeck(status = "Applied blended hybrid theme") {
      ThemeAlchemyEngine.blendDecks(deckA, deckB, fraction)
    }
  }

  fun applyGlobalColorGrade(hueShiftDeg: Float, saturationDelta: Float, temperatureDelta: Float) {
    mutateActiveDeck(status = "Applied global color grade") { deck ->
      val graded = ThemeAlchemyEngine.gradePalette(deck.palette, hueShiftDeg, saturationDelta, temperatureDelta)
      deck.copy(
        id = "graded_${System.currentTimeMillis() % 10000}",
        palette = graded,
        swatchIconRes = null,
      )
    }
  }

  fun applyMoodTransform(mood: PaletteMoodFilter) {
    mutateActiveDeck(status = "Transformed theme with ${mood.label}") { deck ->
      ThemeAlchemyEngine.applyMoodTransform(deck, mood)
    }
  }

  // --- Image-to-Theme Extractor ---
  fun openExtractorModal() {
    _uiState.update { it.copy(showExtractorModal = true) }
  }

  fun closeExtractorModal() {
    _uiState.update { it.copy(showExtractorModal = false) }
  }

  fun extractPaletteFromBitmap(sourceLabel: String, bitmap: Bitmap) {
    viewModelScope.launch {
      _uiState.update { it.copy(isExtractingPalette = true) }
      val (palette, swatches) =
        withContext(Dispatchers.Default) {
          ColorUtils.extractPaletteFromBitmap(bitmap)
        }
      val isDark = ColorUtils.relativeLuminance(ColorUtils.parseHexColor(palette.canvasHex)) < 0.25
      val tempDeck =
        _uiState.value.activeDeck.copy(
          id = "extracted_${System.currentTimeMillis() % 10000}",
          name = "$sourceLabel Extract",
          tagline = "Sampled from $sourceLabel reference",
          isDark = isDark,
          palette = palette,
          swatchIconRes = null,
        )
      val opposite = ColorUtils.generateCompanionDeck(tempDeck)
      val result =
        ExtractedPaletteResult(
          sourceLabel = sourceLabel,
          palette = palette,
          darkCompanionPalette = opposite.palette,
          swatches = swatches,
        )
      _uiState.update { it.copy(isExtractingPalette = false, extractedResult = result) }
      mutateActiveDeck(status = "Extracted & applied palette from $sourceLabel") { tempDeck }
    }
  }

  fun applyExtractedDeck(useCompanion: Boolean) {
    val res = _uiState.value.extractedResult ?: return
    val chosenPalette = if (useCompanion) res.darkCompanionPalette else res.palette
    val isDark = ColorUtils.relativeLuminance(ColorUtils.parseHexColor(chosenPalette.canvasHex)) < 0.25
    val newDeck =
      _uiState.value.activeDeck.copy(
        id = "extracted_${System.currentTimeMillis() % 10000}",
        name = "${res.sourceLabel} ${if (isDark) "(Dark)" else "(Light)"}",
        tagline = "Extracted from ${res.sourceLabel}",
        isDark = isDark,
        palette = chosenPalette,
        swatchIconRes = null,
      )
    mutateActiveDeck(status = "Loaded extracted theme into Studio", switchToStudio = true) { newDeck }
    closeExtractorModal()
  }

  // --- Share Hash & JSON Backup ---
  fun openShareBackupModal() {
    _uiState.update { it.copy(showShareBackupModal = true) }
  }

  fun closeShareBackupModal() {
    _uiState.update { it.copy(showShareBackupModal = false) }
  }

  fun getActiveThemeHash(): String = ColorUtils.encodeThemeHash(_uiState.value.activeDeck)

  fun importThemeFromHash(rawHash: String): Boolean {
    val decoded = ColorUtils.decodeThemeHash(rawHash) ?: return false
    mutateActiveDeck(status = "Imported \"${decoded.name}\" from share hash", switchToStudio = true) { decoded }
    closeShareBackupModal()
    return true
  }

  fun buildJsonBackupString(): String {
    return shortlistRepo.exportBackupJson(_uiState.value.activeDeck, _uiState.value.shortlist)
  }

  fun importJsonBackup(rawJson: String): Boolean {
    val parsed = shortlistRepo.importBackupJson(rawJson) ?: return false
    val (active, saved) = parsed
    viewModelScope.launch {
      if (saved.isNotEmpty()) {
        val merged = (saved + _uiState.value.shortlist).distinctBy { it.id }
        shortlistRepo.saveShortlist(merged)
      }
    }
    if (active != null) {
      mutateActiveDeck(status = "Imported backup \"${active.name}\"", switchToStudio = true) { active }
    } else {
      notifyMessage("Imported ${saved.size} saved preset(s)")
    }
    closeShareBackupModal()
    return true
  }

  // --- Color Picker Modal ---
  fun openColorPicker(role: ColorRole) {
    _uiState.update { it.copy(editingColorRole = role) }
  }

  fun closeColorPicker() {
    _uiState.update { it.copy(editingColorRole = null) }
  }

  // --- Saved Moodboard ---
  fun toggleSaveDeck(deck: ThemeDeck = _uiState.value.activeDeck) {
    viewModelScope.launch {
      val current = _uiState.value.shortlist
      val exists = current.any { it.id == deck.id }
      val updated =
        if (exists) {
          current.filterNot { it.id == deck.id }
        } else {
          listOf(deck) + current
        }
      shortlistRepo.saveShortlist(updated)
      _uiState.update {
        it.copy(
          statusMessage =
            if (exists) "Removed \"${deck.name}\" from Saved"
            else "Saved \"${deck.name}\" to Moodboard",
        )
      }
    }
  }

  fun pinDeckToShortlist(deck: ThemeDeck = _uiState.value.activeDeck) {
    viewModelScope.launch {
      val current = _uiState.value.shortlist
      val existingIndex = current.indexOfFirst { it.id == deck.id }
      val updated =
        if (existingIndex >= 0) {
          current.toMutableList().apply { this[existingIndex] = deck }
        } else {
          listOf(deck) + current
        }
      shortlistRepo.saveShortlist(updated)
      _uiState.update {
        it.copy(statusMessage = "Saved \"${deck.name}\" to Moodboard (${updated.size})")
      }
    }
  }

  fun removeFromShortlist(deckId: String) {
    viewModelScope.launch {
      val updated = _uiState.value.shortlist.filterNot { it.id == deckId }
      shortlistRepo.saveShortlist(updated)
      _uiState.update { it.copy(statusMessage = "Removed from Saved Moodboard") }
    }
  }

  fun updateShortlistNote(deckId: String, note: String) {
    viewModelScope.launch {
      val updated =
        _uiState.value.shortlist.map {
          if (it.id == deckId) it.copy(note = note) else it
        }
      shortlistRepo.saveShortlist(updated)
    }
  }

  fun loadDeckIntoBuilder(deck: ThemeDeck) {
    openInStudio(deck)
  }

  // --- Compare & Tournament ---
  fun setCompareLeft(deck: ThemeDeck) {
    _uiState.update { it.copy(compareLeftDeck = deck) }
  }

  fun setCompareRight(deck: ThemeDeck) {
    _uiState.update { it.copy(compareRightDeck = deck) }
  }

  fun swapCompareDecks() {
    _uiState.update {
      it.copy(compareLeftDeck = it.compareRightDeck, compareRightDeck = it.compareLeftDeck)
    }
  }

  fun startTournament() {
    val active = _uiState.value.activeDeck
    val pool =
      (listOf(active) + PresetDecks.all + _uiState.value.shortlist)
        .distinctBy { it.id }
        .take(8)
        .toMutableList()
    val bracketSize = if (pool.size >= 8) 8 else if (pool.size >= 4) 4 else 2
    val contenders = pool.take(bracketSize)

    _uiState.update {
      it.copy(
        tournament =
          TournamentState(
            isActive = true,
            roundNumber = 1,
            totalStartingDecks = contenders.size,
            bracket = contenders,
            pairIndex = 0,
            nextRoundWinners = emptyList(),
            champion = null,
          ),
      )
    }
  }

  fun pickTournamentWinner(chooseLeft: Boolean) {
    var updatedRecordsToPersist: List<ThemeWinLossRecord>? = null

    _uiState.update { state ->
      val t = state.tournament
      if (!t.isActive || t.champion != null) return@update state
      val winner = (if (chooseLeft) t.leftContender else t.rightContender) ?: return@update state
      val loser = (if (chooseLeft) t.rightContender else t.leftContender) ?: return@update state
      val updatedWinners = t.nextRoundWinners + winner
      val nextPair = t.pairIndex + 1

      val nextTournament =
        if (nextPair * 2 + 1 < t.bracket.size) {
          t.copy(pairIndex = nextPair, nextRoundWinners = updatedWinners)
        } else {
          val carryOver = if (t.bracket.size % 2 == 1) listOf(t.bracket.last()) else emptyList()
          val roundPool = updatedWinners + carryOver
          if (roundPool.size == 1) {
            t.copy(nextRoundWinners = roundPool, champion = roundPool.first())
          } else if (roundPool.size % 2 == 1) {
            t.copy(
              roundNumber = t.roundNumber + 1,
              bracket = roundPool.drop(1),
              pairIndex = 0,
              nextRoundWinners = listOf(roundPool.first()),
            )
          } else {
            t.copy(
              roundNumber = t.roundNumber + 1,
              bracket = roundPool,
              pairIndex = 0,
              nextRoundWinners = emptyList(),
            )
          }
        }

      val isChampionship = nextTournament.champion?.id == winner.id
      val newRecords =
        TournamentAnalytics.recordMatchup(
          existing = state.tournamentRecords,
          winner = winner,
          loser = loser,
          isChampionship = isChampionship,
        )
      updatedRecordsToPersist = newRecords

      state.copy(
        tournament = nextTournament,
        tournamentRecords = newRecords,
        patternSummary = TournamentAnalytics.computePatternSummary(newRecords),
      )
    }

    updatedRecordsToPersist?.let { toSave ->
      viewModelScope.launch { shortlistRepo.saveTournamentRecords(toSave) }
    }
  }

  fun resetTournament() {
    _uiState.update { it.copy(tournament = TournamentState()) }
  }

  fun clearTournamentRecords() {
    viewModelScope.launch {
      shortlistRepo.clearTournamentRecords()
      _uiState.update {
        it.copy(
          tournamentRecords = emptyList(),
          patternSummary = DesignPatternSummary(),
          statusMessage = "Cleared tournament win-loss history",
        )
      }
    }
  }

  fun setExportFormat(format: ExportFormat) {
    _uiState.update { it.copy(selectedExportFormat = format) }
  }

  fun notifyMessage(message: String) {
    _uiState.update { it.copy(statusMessage = message) }
  }

  fun clearStatusMessage() {
    _uiState.update { it.copy(statusMessage = null) }
  }
}
