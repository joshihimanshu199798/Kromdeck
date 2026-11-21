package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Compare
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.KromdeckMode
import com.example.model.StudioDrawerTab
import com.example.model.toKromdeckTheme
import com.example.ui.components.HexColorPickerDialog
import com.example.ui.components.ImageExtractorDialog
import com.example.ui.components.KromdeckHeader
import com.example.ui.components.ShareBackupDialog
import com.example.ui.screens.CompareTournamentScreen
import com.example.ui.screens.ComponentsScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.LandingPageScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.StudioView
import com.example.ui.screens.TokensScaleScreen
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckIndigoTint
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.ui.theme.KromdeckTheme
import com.example.viewmodel.KromdeckViewModel
import com.example.viewmodel.ThemeViewModel

private sealed interface ActiveScreen {
  data object Landing : ActiveScreen
  data class Workspace(val mode: KromdeckMode) : ActiveScreen
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      KromdeckTheme {
        KromdeckApp()
      }
    }
  }
}

@Composable
fun KromdeckApp(
  vm: KromdeckViewModel = viewModel(),
  themeViewModel: ThemeViewModel = viewModel(),
) {
  val state by vm.uiState.collectAsStateWithLifecycle()
  val persistedTheme by themeViewModel.activeTheme.collectAsStateWithLifecycle()
  val hasPersistedTheme by themeViewModel.hasPersistedTheme.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  // Local state management for Landing Page + 6 top-level workspace views
  var showLandingPage by rememberSaveable { mutableStateOf(true) }
  var selectedMode by rememberSaveable { mutableStateOf(KromdeckMode.DISCOVER) }
  var restoredInitialTheme by rememberSaveable { mutableStateOf(false) }

  val navigateToWorkspace: (KromdeckMode) -> Unit = { mode ->
    selectedMode = mode
    showLandingPage = false
  }

  // Restore saved KromdeckTheme from DataStore once on startup
  LaunchedEffect(hasPersistedTheme, persistedTheme) {
    if (hasPersistedTheme && !restoredInitialTheme) {
      vm.selectPreset(persistedTheme.toThemeDeck())
      restoredInitialTheme = true
    }
  }

  // Keep ThemeViewModel in sync whenever activeDeck changes in KromdeckViewModel
  LaunchedEffect(state.activeDeck) {
    themeViewModel.setActiveTheme(state.activeDeck.toKromdeckTheme(), autoPersist = false)
  }

  LaunchedEffect(state.statusMessage) {
    val msg = state.statusMessage
    if (!msg.isNullOrBlank()) {
      snackbarHostState.showSnackbar(msg)
      vm.clearStatusMessage()
    }
  }

  if (!showLandingPage && selectedMode != KromdeckMode.DISCOVER) {
    BackHandler {
      selectedMode = KromdeckMode.DISCOVER
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useNavRail = maxWidth >= 840.dp

    Scaffold(
      containerColor = KromdeckCreamCanvas,
      contentWindowInsets = WindowInsets.safeDrawing,
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
      bottomBar = {
        if (!useNavRail) {
          KromdeckNavigationBar(
            selectedMode = if (showLandingPage) null else selectedMode,
            savedCount = state.shortlist.size,
            onSelectMode = navigateToWorkspace,
          )
        }
      },
    ) { innerPadding ->
      Row(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
      ) {
        if (useNavRail) {
          KromdeckNavigationRail(
            selectedMode = if (showLandingPage) null else selectedMode,
            savedCount = state.shortlist.size,
            onSelectMode = navigateToWorkspace,
          )
        }

        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
          KromdeckHeader(
            activeDeck = state.activeDeck,
            isGuideActive = showLandingPage,
            canUndo = state.canUndo,
            canRedo = state.canRedo,
            randomizerLocks = state.randomizerLocks,
            onToggleGuide = { showLandingPage = !showLandingPage },
            onOpenStudio = { navigateToWorkspace(KromdeckMode.STUDIO) },
            onToggleCompanion = vm::toggleCompanionDarkLight,
            onUndo = vm::undo,
            onRedo = vm::redo,
            onRandomize = vm::randomizeDeck,
            onToggleLockPalette = vm::toggleLockPalette,
            onToggleLockTypography = vm::toggleLockTypography,
            onToggleLockSurface = vm::toggleLockSurface,
            onOpenShareBackup = vm::openShareBackupModal,
          )

          val activeTarget: ActiveScreen =
            if (showLandingPage) ActiveScreen.Landing else ActiveScreen.Workspace(selectedMode)

          Crossfade(
            targetState = activeTarget,
            modifier = Modifier.weight(1f),
            label = "kromdeckModeCrossfade",
          ) { target ->
            when (target) {
              ActiveScreen.Landing ->
                LandingPageScreen(
                  onNavigateToMode = navigateToWorkspace,
                  onOpenImageExtractor = {
                    showLandingPage = false
                    selectedMode = KromdeckMode.DISCOVER
                    vm.openExtractorModal()
                  },
                  onOpenShareBackup = { vm.openShareBackupModal() },
                  onOpenStudioExports = {
                    vm.setStudioDrawerTab(StudioDrawerTab.WCAG_EXPORTS)
                    navigateToWorkspace(KromdeckMode.STUDIO)
                  },
                )

              is ActiveScreen.Workspace ->
                when (target.mode) {
                KromdeckMode.DISCOVER ->
                  DiscoverScreen(
                    state = state,
                    onSearchChange = vm::setDiscoverSearch,
                    onCategorySelect = vm::setDiscoverCategory,
                    onSurfaceFilterSelect = vm::setDiscoverSurfaceFilter,
                    onToggleCardFlip = vm::toggleDiscoverCardFlip,
                    onToggleSave = vm::toggleSaveDeck,
                    onOpenInStudio = { deck ->
                      vm.openInStudio(deck)
                      navigateToWorkspace(KromdeckMode.STUDIO)
                    },
                    onOpenExtractor = vm::openExtractorModal,
                    onNotify = vm::notifyMessage,
                  )

                KromdeckMode.STUDIO ->
                  StudioView(
                    state = state,
                    isThemePersisted = hasPersistedTheme && persistedTheme == state.activeDeck.toKromdeckTheme(),
                    onPersistThemeToDataStore = {
                      themeViewModel.persistTheme(state.activeDeck.toKromdeckTheme())
                      vm.notifyMessage("Persisted \"${state.activeDeck.name}\" to DataStore")
                    },
                    onSelectTemplate = vm::setTemplate,
                    onSelectViewport = vm::setViewportMode,
                    onSelectColorBlindness = vm::setColorBlindnessMode,
                    onUpdateBrandIdentity = vm::setBrandIdentity,
                    onToggleSplitWipe = vm::toggleSplitWipe,
                    onSetSplitBaseline = vm::setSplitWipeBaseline,
                    onTogglePresentationShot = vm::togglePresentationShotMode,
                    onToggleSaveActive = { vm.toggleSaveDeck(state.activeDeck) },
                    onOpenExtractor = vm::openExtractorModal,
                    onSelectDrawerTab = vm::setStudioDrawerTab,
                    onOpenColorPicker = vm::openColorPicker,
                    onToggleCompanion = vm::toggleCompanionDarkLight,
                    onHarmonize = vm::harmonizePalette,
                    onSwapAccents = vm::swapAccents,
                    onAutoFixContrast = vm::autoFixContrast,
                    onUpdateGradient = vm::updateGradient,
                    onSelectPhysics = vm::setPhysics,
                    onUpdateSurfaceGeometry = vm::updateSurfaceGeometry,
                    onSelectFontPairing = vm::setFontPairing,
                    onUpdateTypographyScale = vm::updateTypographyScale,
                    onUpdateLayoutConfig = vm::updateLayoutConfig,
                    onSelectMotionFeel = vm::setMotionFeel,
                    onSelectExportFormat = vm::setExportFormat,
                    onNotify = vm::notifyMessage,
                  )

                KromdeckMode.COMPARE ->
                  CompareTournamentScreen(
                    state = state,
                    onSelectTemplate = vm::setTemplate,
                    onSelectLeftDeck = vm::setCompareLeft,
                    onSelectRightDeck = vm::setCompareRight,
                    onSwapCompareDecks = vm::swapCompareDecks,
                    onStartTournament = vm::startTournament,
                    onPickTournamentWinner = vm::pickTournamentWinner,
                    onResetTournament = vm::resetTournament,
                    onClearTournamentRecords = vm::clearTournamentRecords,
                    onSaveDeck = vm::pinDeckToShortlist,
                    onOpenInStudio = { deck ->
                      vm.openInStudio(deck)
                      navigateToWorkspace(KromdeckMode.STUDIO)
                    },
                  )

                KromdeckMode.COMPONENTS ->
                  ComponentsScreen(state = state)

                KromdeckMode.TOKENS ->
                  TokensScaleScreen(
                    state = state,
                    onNotify = vm::notifyMessage,
                  )

                KromdeckMode.SAVED ->
                  SavedScreen(
                    state = state,
                    onSaveCurrentActive = {
                      vm.pinDeckToShortlist(state.activeDeck)
                      themeViewModel.persistTheme(state.activeDeck.toKromdeckTheme())
                    },
                    onOpenShareBackup = vm::openShareBackupModal,
                    onOpenInStudio = { deck ->
                      vm.openInStudio(deck)
                      navigateToWorkspace(KromdeckMode.STUDIO)
                    },
                    onSetCompareLeft = { deck ->
                      vm.setCompareLeft(deck)
                      navigateToWorkspace(KromdeckMode.COMPARE)
                    },
                    onSetWipeBaseline = { deck ->
                      vm.setSplitWipeBaseline(deck)
                      navigateToWorkspace(KromdeckMode.STUDIO)
                    },
                    onUpdateNote = vm::updateShortlistNote,
                    onRemoveSaved = vm::removeFromShortlist,
                  )
              }
            }
          }
        }
      }
    }
  }

  // Hex Color Picker Modal
  state.editingColorRole?.let { role ->
    HexColorPickerDialog(
      role = role,
      initialHex = state.activeDeck.palette.hexFor(role),
      onDismiss = vm::closeColorPicker,
      onApply = { newHex ->
        vm.updateColorRole(role, newHex)
        vm.closeColorPicker()
      },
    )
  }

  // Share Hash & JSON Backup Modal
  if (state.showShareBackupModal) {
    ShareBackupDialog(
      activeHash = vm.getActiveThemeHash(),
      onBuildBackupJson = vm::buildJsonBackupString,
      onImportHash = { raw ->
        val ok = vm.importThemeFromHash(raw)
        if (ok) navigateToWorkspace(KromdeckMode.STUDIO)
        ok
      },
      onImportBackupJson = { raw ->
        val ok = vm.importJsonBackup(raw)
        if (ok) navigateToWorkspace(KromdeckMode.STUDIO)
        ok
      },
      onNotify = vm::notifyMessage,
      onDismiss = vm::closeShareBackupModal,
    )
  }

  // Image-to-Theme Extractor Modal
  if (state.showExtractorModal) {
    ImageExtractorDialog(
      extractedResult = state.extractedResult,
      isExtracting = state.isExtractingPalette,
      onExtractBitmap = vm::extractPaletteFromBitmap,
      onApplyExtracted = { useCompanion ->
        vm.applyExtractedDeck(useCompanion)
        navigateToWorkspace(KromdeckMode.STUDIO)
      },
      onDismiss = vm::closeExtractorModal,
    )
  }
}

@Composable
private fun KromdeckNavigationBar(
  selectedMode: KromdeckMode?,
  savedCount: Int,
  onSelectMode: (KromdeckMode) -> Unit,
) {
  Column {
    HorizontalDivider(thickness = 2.dp, color = KromdeckSlateInk)
    NavigationBar(
      containerColor = KromdeckPureSurface,
      contentColor = KromdeckSlateInk,
      windowInsets = WindowInsets.navigationBars,
      tonalElevation = 0.dp,
      modifier = Modifier.testTag("kromdeck_navigation_bar"),
    ) {
      KromdeckMode.entries.forEach { mode ->
        val selected = mode == selectedMode
        val navLabel = shortNavLabel(mode)
        NavigationBarItem(
          selected = selected,
          onClick = { onSelectMode(mode) },
          modifier = Modifier.testTag("nav_bar_${mode.name.lowercase()}"),
          icon = {
            NavDestinationIcon(mode = mode, selected = selected, savedCount = savedCount)
          },
          label = {
            Text(
              text = navLabel,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          },
          alwaysShowLabel = true,
          colors =
            NavigationBarItemDefaults.colors(
              selectedIconColor = KromdeckIndigo,
              selectedTextColor = KromdeckSlateInk,
              indicatorColor = KromdeckIndigoTint,
              unselectedIconColor = KromdeckMutedInk,
              unselectedTextColor = KromdeckMutedInk,
            ),
        )
      }
    }
  }
}

@Composable
private fun KromdeckNavigationRail(
  selectedMode: KromdeckMode?,
  savedCount: Int,
  onSelectMode: (KromdeckMode) -> Unit,
) {
  Row(modifier = Modifier.fillMaxHeight()) {
    NavigationRail(
      containerColor = KromdeckPureSurface,
      contentColor = KromdeckSlateInk,
      modifier = Modifier.statusBarsPadding().testTag("kromdeck_navigation_rail"),
    ) {
      KromdeckMode.entries.forEach { mode ->
        val selected = mode == selectedMode
        NavigationRailItem(
          selected = selected,
          onClick = { onSelectMode(mode) },
          modifier = Modifier.testTag("nav_rail_${mode.name.lowercase()}"),
          icon = {
            NavDestinationIcon(mode = mode, selected = selected, savedCount = savedCount)
          },
          label = {
            Text(
              text = shortNavLabel(mode),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
              maxLines = 1,
            )
          },
          colors =
            NavigationRailItemDefaults.colors(
              selectedIconColor = KromdeckIndigo,
              selectedTextColor = KromdeckSlateInk,
              indicatorColor = KromdeckIndigoTint,
              unselectedIconColor = KromdeckMutedInk,
              unselectedTextColor = KromdeckMutedInk,
            ),
        )
      }
    }
    VerticalDivider(thickness = 2.dp, color = KromdeckSlateInk)
  }
}

@Composable
private fun NavDestinationIcon(
  mode: KromdeckMode,
  selected: Boolean,
  savedCount: Int,
) {
  val iconVector = navModeIcon(mode, selected)
  if (mode == KromdeckMode.SAVED && savedCount > 0) {
    BadgedBox(
      badge = {
        Badge(containerColor = KromdeckCoral, contentColor = Color.White) {
          Text(savedCount.toString(), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
        }
      },
    ) {
      Icon(iconVector, contentDescription = mode.label, modifier = Modifier.size(22.dp))
    }
  } else {
    Icon(iconVector, contentDescription = mode.label, modifier = Modifier.size(22.dp))
  }
}

private fun shortNavLabel(mode: KromdeckMode): String =
  when (mode) {
    KromdeckMode.DISCOVER -> "Discover"
    KromdeckMode.STUDIO -> "Studio"
    KromdeckMode.COMPARE -> "Compare"
    KromdeckMode.COMPONENTS -> "UI Kit"
    KromdeckMode.TOKENS -> "Tokens"
    KromdeckMode.SAVED -> "Saved"
  }

private fun navModeIcon(mode: KromdeckMode, selected: Boolean): ImageVector =
  when (mode) {
    KromdeckMode.DISCOVER -> if (selected) Icons.Filled.Explore else Icons.Outlined.Explore
    KromdeckMode.STUDIO -> if (selected) Icons.Filled.Tune else Icons.Outlined.Tune
    KromdeckMode.COMPARE -> if (selected) Icons.Filled.Compare else Icons.Outlined.Compare
    KromdeckMode.COMPONENTS -> if (selected) Icons.Filled.Widgets else Icons.Outlined.Widgets
    KromdeckMode.TOKENS -> if (selected) Icons.Filled.Palette else Icons.Outlined.Palette
    KromdeckMode.SAVED -> if (selected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
  }
