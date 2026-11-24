package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PresetDecks
import com.example.model.AiPolarityPreference
import com.example.model.ColorBlindnessMode
import com.example.model.ColorHarmonyRule
import com.example.model.ColorRole
import com.example.model.ExportFormat
import com.example.model.FontPairing
import com.example.model.GridDensity
import com.example.model.MotionFeel
import com.example.model.NavShellStyle
import com.example.model.PaletteMoodFilter
import com.example.model.PreviewTemplate
import com.example.model.StudioDrawerTab
import com.example.model.SurfacePhysics
import com.example.model.ThemeDeck
import com.example.model.ViewportMode
import com.example.ui.components.LiveComponentPreviewFrame
import com.example.ui.components.LiveTemplatePreviewFrame
import com.example.ui.components.StudioCustomizationDrawer
import com.example.ui.components.TactileButton
import com.example.ui.components.TactileCard
import com.example.ui.components.TactilePillChip
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.util.ColorUtils
import com.example.util.ExportGenerator
import com.example.viewmodel.KromdeckUiState
import java.io.ByteArrayOutputStream

/**
 * Interactive StudioView featuring:
 * - A resizable preview frame area with Mobile / Tablet / Desktop viewport toggles and a live drag-resize handle.
 * - A collapsible Sidebar Drawer component (docked side-by-side on wide screens, slide-over drawer on compact screens)
 *   housing all 6 customization tabs and a 1-click DataStore theme persist button.
 */
@Composable
fun StudioView(
  state: KromdeckUiState,
  isThemePersisted: Boolean,
  onPersistThemeToDataStore: () -> Unit,
  onSelectTemplate: (PreviewTemplate) -> Unit,
  onSelectViewport: (ViewportMode) -> Unit,
  onSelectColorBlindness: (ColorBlindnessMode) -> Unit,
  onUpdateBrandIdentity: (String, String) -> Unit,
  onToggleSplitWipe: () -> Unit,
  onSetSplitBaseline: (ThemeDeck) -> Unit,
  onTogglePresentationShot: () -> Unit,
  onToggleSaveActive: () -> Unit,
  onOpenExtractor: () -> Unit,
  onSelectDrawerTab: (StudioDrawerTab) -> Unit,
  onOpenColorPicker: (ColorRole) -> Unit,
  onToggleCompanion: () -> Unit,
  onHarmonize: () -> Unit,
  onSwapAccents: () -> Unit,
  onAutoFixContrast: () -> Unit,
  onUpdateGradient: (Boolean, Float, Boolean) -> Unit,
  onRenameDeck: (String) -> Unit,
  onGenerateHarmony: (Float, ColorHarmonyRule, Boolean) -> Unit,
  onBlendDecks: (ThemeDeck, ThemeDeck, Float) -> Unit,
  onApplyGlobalGrade: (Float, Float, Float) -> Unit,
  onApplyMoodTransform: (PaletteMoodFilter) -> Unit,
  onSelectPhysics: (SurfacePhysics) -> Unit,
  onUpdateSurfaceGeometry: (Int?, Int?, Float?, Float?, Float?) -> Unit,
  onSelectFontPairing: (FontPairing) -> Unit,
  onUpdateTypographyScale: (Float?, Boolean?, Float?, Float?) -> Unit,
  onUpdateLayoutConfig: (NavShellStyle?, GridDensity?, Float?) -> Unit,
  onSelectMotionFeel: (MotionFeel) -> Unit,
  onSelectExportFormat: (ExportFormat) -> Unit,
  onAiPromptChange: (String) -> Unit,
  onAiPolarityChange: (AiPolarityPreference) -> Unit,
  onGenerateAiThemes: () -> Unit,
  onApplyAiSuggestion: (ThemeDeck) -> Unit,
  onSaveAiSuggestion: (ThemeDeck) -> Unit,
  onNotify: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val density = LocalDensity.current
  val deck = state.activeDeck
  val isSaved = remember(state.shortlist, deck.id) { state.shortlist.any { it.id == deck.id } }

  var showBrandEditor by remember { mutableStateOf(false) }
  var showVisionMenu by remember { mutableStateOf(false) }
  var showBaselineMenu by remember { mutableStateOf(false) }
  var wipeFraction by remember { mutableFloatStateOf(0.5f) }
  var isSidebarOpen by remember { mutableStateOf(false) }
  var customFrameWidthDp by remember { mutableFloatStateOf(state.viewportMode.maxWidthDp.toFloat()) }

  // Sync resizable frame width when user switches Mobile / Tablet / Desktop presets
  LaunchedEffect(state.viewportMode) {
    customFrameWidthDp = state.viewportMode.maxWidthDp.toFloat()
  }

  val exportPngLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
      if (uri != null) {
        try {
          val bmp = ExportGenerator.renderSpecCardBitmap(deck, state.brandAppName, state.brandTagline)
          val bytes = ByteArrayOutputStream().apply { bmp.compress(Bitmap.CompressFormat.PNG, 100, this) }.toByteArray()
          context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
          onNotify("Exported Spec Card PNG")
        } catch (_: Exception) {
          onNotify("Could not export PNG")
        }
      }
    }

  val exportSvgLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/svg+xml")) { uri ->
      if (uri != null) {
        try {
          val svg = ExportGenerator.generateSpecCardSvg(deck, state.brandAppName, state.brandTagline)
          context.contentResolver.openOutputStream(uri)?.use { it.write(svg.toByteArray(Charsets.UTF_8)) }
          onNotify("Exported Spec Card SVG")
        } catch (_: Exception) {
          onNotify("Could not export SVG")
        }
      }
    }

  val colorMatrix = remember(state.colorBlindnessMode) { ColorUtils.colorBlindnessMatrix(state.colorBlindnessMode) }

  BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("studio_view_root")) {
    val isExpandedLayout = maxWidth >= 780.dp
    // Open sidebar by default on wide screens so the split Studio + Sidebar Drawer shines immediately
    LaunchedEffect(isExpandedLayout) {
      if (isExpandedLayout) isSidebarOpen = true
    }

    val drawerContent: @Composable () -> Unit = {
      StudioSidebarDrawer(
        state = state,
        isThemePersisted = isThemePersisted,
        onPersistThemeToDataStore = onPersistThemeToDataStore,
        onClose = { isSidebarOpen = false },
        onSelectDrawerTab = onSelectDrawerTab,
        onOpenColorPicker = onOpenColorPicker,
        onToggleCompanion = onToggleCompanion,
        onHarmonize = onHarmonize,
        onSwapAccents = onSwapAccents,
        onAutoFixContrast = onAutoFixContrast,
        onUpdateGradient = onUpdateGradient,
        onRenameDeck = onRenameDeck,
        onGenerateHarmony = onGenerateHarmony,
        onBlendDecks = onBlendDecks,
        onApplyGlobalGrade = onApplyGlobalGrade,
        onApplyMoodTransform = onApplyMoodTransform,
        onSelectPhysics = onSelectPhysics,
        onUpdateSurfaceGeometry = onUpdateSurfaceGeometry,
        onSelectFontPairing = onSelectFontPairing,
        onUpdateTypographyScale = onUpdateTypographyScale,
        onUpdateLayoutConfig = onUpdateLayoutConfig,
        onSelectMotionFeel = onSelectMotionFeel,
        onSelectExportFormat = onSelectExportFormat,
        onAiPromptChange = onAiPromptChange,
        onAiPolarityChange = onAiPolarityChange,
        onGenerateAiThemes = onGenerateAiThemes,
        onApplyAiSuggestion = onApplyAiSuggestion,
        onSaveAiSuggestion = onSaveAiSuggestion,
        onNotify = onNotify,
      )
    }

    Row(modifier = Modifier.fillMaxSize()) {
      // Main Resizable Preview Stage Area
      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxHeight().testTag("studio_preview_area"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        // 1. Studio Stage Toolbar (Templates, Mobile/Tablet/Desktop Viewport Toggle, Sidebar Drawer Toggle)
        item {
          TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              // Top row: Viewport Switcher + Sidebar Drawer Toggle
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(
                  modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  ViewportMode.entries.forEach { vp ->
                    val sel = state.viewportMode == vp
                    val icon =
                      when (vp) {
                        ViewportMode.MOBILE -> Icons.Filled.PhoneAndroid
                        ViewportMode.TABLET -> Icons.Filled.TabletAndroid
                        ViewportMode.DESKTOP -> Icons.Filled.DesktopWindows
                      }
                    Row(
                      modifier =
                        Modifier.testTag("viewport_${vp.name.lowercase()}")
                          .clip(RoundedCornerShape(8.dp))
                          .background(if (sel) KromdeckIndigo else KromdeckCreamCanvas)
                          .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(8.dp))
                          .clickable {
                            onSelectViewport(vp)
                            customFrameWidthDp = vp.maxWidthDp.toFloat()
                          }
                          .padding(horizontal = 9.dp, vertical = 7.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                      Icon(icon, contentDescription = vp.label, tint = if (sel) Color.White else KromdeckSlateInk, modifier = Modifier.size(14.dp))
                      Text(vp.label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = if (sel) Color.White else KromdeckSlateInk)
                    }
                  }
                }

                Spacer(modifier = Modifier.width(8.dp))

                TactileButton(
                  onClick = { isSidebarOpen = !isSidebarOpen },
                  containerColor = if (isSidebarOpen) KromdeckSlateInk else KromdeckIndigo,
                  contentColor = Color.White,
                  shadowOffset = 2.dp,
                  cornerRadius = 10.dp,
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                  testTag = "studio_sidebar_toggle_btn",
                ) {
                  Icon(Icons.Filled.Tune, contentDescription = "Toggle customization sidebar drawer", modifier = Modifier.size(15.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (isSidebarOpen) "Hide Drawer" else "Drawer",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                  )
                }
              }

              // 6 Template Chips
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
                    testTag = "studio_tpl_${tpl.name.lowercase()}",
                  )
                }
              }

              // Stage Feature Toggles (Brand Text, Split Wipe, Vision Sim, Shot Mode, Extract, Save)
              Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                StageChipButton(
                  label = "Brand Text",
                  icon = Icons.Filled.Edit,
                  active = showBrandEditor,
                  onClick = { showBrandEditor = !showBrandEditor },
                  testTag = "stage_brand_edit_toggle",
                )
                StageChipButton(
                  label = "Split Wipe",
                  icon = Icons.Filled.Compare,
                  active = state.splitWipeEnabled,
                  onClick = onToggleSplitWipe,
                  testTag = "stage_split_wipe_toggle",
                )
                Box {
                  StageChipButton(
                    label = if (state.colorBlindnessMode == ColorBlindnessMode.NORMAL) "Vision Sim" else state.colorBlindnessMode.label,
                    icon = Icons.Filled.Visibility,
                    active = state.colorBlindnessMode != ColorBlindnessMode.NORMAL,
                    onClick = { showVisionMenu = true },
                    testTag = "stage_vision_sim_button",
                  )
                  DropdownMenu(
                    expanded = showVisionMenu,
                    onDismissRequest = { showVisionMenu = false },
                    modifier = Modifier.background(KromdeckPureSurface),
                  ) {
                    ColorBlindnessMode.entries.forEach { mode ->
                      DropdownMenuItem(
                        text = {
                          Text(
                            text = mode.label,
                            fontWeight = if (state.colorBlindnessMode == mode) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (state.colorBlindnessMode == mode) KromdeckIndigo else KromdeckSlateInk,
                          )
                        },
                        onClick = {
                          onSelectColorBlindness(mode)
                          showVisionMenu = false
                        },
                      )
                    }
                  }
                }
                StageChipButton(
                  label = "Shot Mode",
                  icon = Icons.Filled.CameraAlt,
                  active = state.presentationShotMode,
                  onClick = onTogglePresentationShot,
                  testTag = "stage_shot_mode_toggle",
                )
                StageChipButton(
                  label = "Extract Img",
                  icon = Icons.Filled.ImageSearch,
                  active = false,
                  onClick = onOpenExtractor,
                  testTag = "stage_open_extractor_btn",
                )
                StageChipButton(
                  label = if (isSaved) "Saved" else "Moodboard",
                  icon = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                  active = isSaved,
                  onClick = onToggleSaveActive,
                  testTag = "stage_save_deck_btn",
                )
              }

              // Inline Brand Identity Editor
              if (showBrandEditor) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = state.brandAppName,
                    onValueChange = { onUpdateBrandIdentity(it, state.brandTagline) },
                    label = { Text("App Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = KromdeckIndigo, unfocusedBorderColor = KromdeckSlateInk),
                    modifier = Modifier.weight(1f),
                  )
                  OutlinedTextField(
                    value = state.brandTagline,
                    onValueChange = { onUpdateBrandIdentity(state.brandAppName, it) },
                    label = { Text("Tagline") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = KromdeckIndigo, unfocusedBorderColor = KromdeckSlateInk),
                    modifier = Modifier.weight(1f),
                  )
                }
              }

              // Split Wipe Baseline Selector
              if (state.splitWipeEnabled) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(
                    text = "Left: ${state.splitWipeBaseline.name}  |  Right: ${deck.name}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = KromdeckIndigo,
                    modifier = Modifier.weight(1f),
                  )
                  Box {
                    TactileButton(
                      onClick = { showBaselineMenu = true },
                      containerColor = KromdeckCreamCanvas,
                      contentColor = KromdeckSlateInk,
                      shadowOffset = 1.dp,
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                      testTag = "wipe_baseline_picker",
                    ) {
                      Text("Change Baseline", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    DropdownMenu(
                      expanded = showBaselineMenu,
                      onDismissRequest = { showBaselineMenu = false },
                      modifier = Modifier.background(KromdeckPureSurface),
                    ) {
                      PresetDecks.all.take(10).forEach { preset ->
                        DropdownMenuItem(
                          text = { Text(preset.name, color = KromdeckSlateInk) },
                          onClick = {
                            onSetSplitBaseline(preset)
                            showBaselineMenu = false
                          },
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // 2. Resizable Preview Frame Area with Draggable Edge Handle + Live Width Indicator
        item {
          val visionModifier =
            if (colorMatrix != null) {
              Modifier.drawWithContent {
                val paint = Paint().apply { colorFilter = ColorFilter.colorMatrix(colorMatrix) }
                drawIntoCanvas { canvas ->
                  canvas.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
                  drawContent()
                  canvas.restore()
                }
              }
            } else {
              Modifier
            }

          BoxWithConstraints(modifier = Modifier.fillMaxWidth().testTag("resizable_preview_stage")) {
            val availableWidthDp = maxWidth.value.coerceAtLeast(260f)
            val effectiveWidthDp = customFrameWidthDp.coerceIn(260f, availableWidthDp)
            val activeViewportLabel =
              when {
                effectiveWidthDp <= 390f -> "Mobile"
                effectiveWidthDp <= 640f -> "Tablet"
                else -> "Desktop"
              }

            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              // Resizable Viewport Ruler Bar
              Row(
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(KromdeckPureSurface)
                    .border(BorderStroke(1.5.dp, KromdeckSlateInk), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = "$activeViewportLabel Viewport • ${effectiveWidthDp.toInt()}dp",
                  fontFamily = JetBrainsMonoFamily,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = KromdeckSlateInk,
                )
                Text(
                  text = "Drag right edge to resize",
                  style = MaterialTheme.typography.labelSmall,
                  color = KromdeckMutedInk,
                )
              }

              // Resizable Frame + Interactive Right Drag Handle
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Box(
                  modifier =
                    Modifier.widthIn(max = (effectiveWidthDp - 24f).coerceAtLeast(240f).dp)
                      .fillMaxWidth()
                      .then(visionModifier),
                ) {
                  if (state.presentationShotMode) {
                    PresentationShotFrame(
                      deck = deck,
                      state = state,
                      onExportPng = { exportPngLauncher.launch(ExportGenerator.suggestedSpecCardName(deck, "png")) },
                      onExportSvg = { exportSvgLauncher.launch(ExportGenerator.suggestedSpecCardName(deck, "svg")) },
                    )
                  } else if (state.splitWipeEnabled) {
                    SplitWipePreviewStage(
                      leftDeck = state.splitWipeBaseline,
                      rightDeck = deck,
                      template = state.activeTemplate,
                      brandAppName = state.brandAppName,
                      brandTagline = state.brandTagline,
                      wipeFraction = wipeFraction,
                      onWipeChange = { wipeFraction = it },
                    )
                  } else {
                    LiveTemplatePreviewFrame(
                      deck = deck,
                      template = state.activeTemplate,
                      brandAppName = state.brandAppName,
                      brandTagline = state.brandTagline,
                    )
                  }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Draggable Resize Handle
                Box(
                  modifier =
                    Modifier.testTag("preview_resize_handle")
                      .width(20.dp)
                      .height(72.dp)
                      .clip(RoundedCornerShape(999.dp))
                      .background(KromdeckAmber)
                      .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(999.dp))
                      .pointerInput(availableWidthDp) {
                        detectHorizontalDragGestures { change, dragAmount ->
                          change.consume()
                          val deltaDp = with(density) { (dragAmount * 2f).toDp().value }
                          customFrameWidthDp = (customFrameWidthDp + deltaDp).coerceIn(260f, availableWidthDp)
                        }
                      },
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(
                    imageVector = Icons.Filled.DragIndicator,
                    contentDescription = "Drag to resize preview width",
                    tint = KromdeckSlateInk,
                    modifier = Modifier.size(16.dp),
                  )
                }
              }
            }
          }
        }

        // 3. Live UI Component Preview Frame (Buttons, Inputs, Switches & Typography Scale)
        item {
          LiveComponentPreviewFrame(
            deck = deck,
          )
        }

        // Quick inline button to open Sidebar Drawer on compact screens
        if (!isExpandedLayout && !isSidebarOpen) {
          item {
            TactileButton(
              onClick = { isSidebarOpen = true },
              containerColor = KromdeckIndigo,
              contentColor = Color.White,
              modifier = Modifier.fillMaxWidth(),
              testTag = "studio_open_drawer_cta",
            ) {
              Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Open Customization & AI Drawer (8 Tabs)", fontWeight = FontWeight.ExtraBold)
            }
          }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
      }

      // Docked Sidebar Drawer on Expanded Screens (>= 780.dp)
      if (isExpandedLayout) {
        AnimatedVisibility(
          visible = isSidebarOpen,
          enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
          exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        ) {
          Box(
            modifier =
              Modifier.width(360.dp)
                .fillMaxHeight()
                .background(KromdeckCreamCanvas)
                .border(BorderStroke(2.dp, KromdeckSlateInk)),
          ) {
            drawerContent()
          }
        }
      }
    }

    // Slide-over Sidebar Drawer Overlay on Compact/Medium Screens (< 780.dp)
    if (!isExpandedLayout) {
      AnimatedVisibility(
        visible = isSidebarOpen,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        Box(
          modifier =
            Modifier.fillMaxSize()
              .background(Color.Black.copy(alpha = 0.42f))
              .clickable { isSidebarOpen = false },
        )
      }

      AnimatedVisibility(
        visible = isSidebarOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = Modifier.align(Alignment.CenterEnd),
      ) {
        Box(
          modifier =
            Modifier.fillMaxWidth(0.90f)
              .widthIn(max = 390.dp)
              .fillMaxHeight()
              .background(KromdeckCreamCanvas)
              .border(BorderStroke(2.dp, KromdeckSlateInk))
              .testTag("studio_sidebar_drawer"),
        ) {
          drawerContent()
        }
      }
    }
  }
}

@Composable
private fun StudioSidebarDrawer(
  state: KromdeckUiState,
  isThemePersisted: Boolean,
  onPersistThemeToDataStore: () -> Unit,
  onClose: () -> Unit,
  onSelectDrawerTab: (StudioDrawerTab) -> Unit,
  onOpenColorPicker: (ColorRole) -> Unit,
  onToggleCompanion: () -> Unit,
  onHarmonize: () -> Unit,
  onSwapAccents: () -> Unit,
  onAutoFixContrast: () -> Unit,
  onUpdateGradient: (Boolean, Float, Boolean) -> Unit,
  onRenameDeck: (String) -> Unit,
  onGenerateHarmony: (Float, ColorHarmonyRule, Boolean) -> Unit,
  onBlendDecks: (ThemeDeck, ThemeDeck, Float) -> Unit,
  onApplyGlobalGrade: (Float, Float, Float) -> Unit,
  onApplyMoodTransform: (PaletteMoodFilter) -> Unit,
  onSelectPhysics: (SurfacePhysics) -> Unit,
  onUpdateSurfaceGeometry: (Int?, Int?, Float?, Float?, Float?) -> Unit,
  onSelectFontPairing: (FontPairing) -> Unit,
  onUpdateTypographyScale: (Float?, Boolean?, Float?, Float?) -> Unit,
  onUpdateLayoutConfig: (NavShellStyle?, GridDensity?, Float?) -> Unit,
  onSelectMotionFeel: (MotionFeel) -> Unit,
  onSelectExportFormat: (ExportFormat) -> Unit,
  onAiPromptChange: (String) -> Unit,
  onAiPolarityChange: (AiPolarityPreference) -> Unit,
  onGenerateAiThemes: () -> Unit,
  onApplyAiSuggestion: (ThemeDeck) -> Unit,
  onSaveAiSuggestion: (ThemeDeck) -> Unit,
  onNotify: (String) -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    // Sidebar Header + DataStore Persist Action
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Theme Inspector Drawer",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold,
          color = KromdeckSlateInk,
        )
        Text(
          text = if (isThemePersisted) "Synced to DataStore" else "Unsaved changes",
          style = MaterialTheme.typography.labelSmall,
          color = if (isThemePersisted) KromdeckEmerald else KromdeckMutedInk,
          fontWeight = FontWeight.Bold,
        )
      }
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        TactileButton(
          onClick = onPersistThemeToDataStore,
          containerColor = KromdeckEmerald,
          contentColor = Color.White,
          shadowOffset = 2.dp,
          cornerRadius = 8.dp,
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
          testTag = "persist_theme_datastore_btn",
        ) {
          Icon(Icons.Filled.Save, contentDescription = "Persist theme to DataStore", modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Persist", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
        }
        IconButton(onClick = onClose, modifier = Modifier.testTag("close_sidebar_drawer_btn")) {
          Icon(Icons.Filled.Close, contentDescription = "Close sidebar drawer", tint = KromdeckSlateInk)
        }
      }
    }

    StudioCustomizationDrawer(
      state = state,
      onSelectTab = onSelectDrawerTab,
      onOpenColorPicker = onOpenColorPicker,
      onToggleCompanion = onToggleCompanion,
      onHarmonize = onHarmonize,
      onSwapAccents = onSwapAccents,
      onAutoFixContrast = onAutoFixContrast,
      onUpdateGradient = onUpdateGradient,
      onRenameDeck = onRenameDeck,
      onGenerateHarmony = onGenerateHarmony,
      onBlendDecks = onBlendDecks,
      onApplyGlobalGrade = onApplyGlobalGrade,
      onApplyMoodTransform = onApplyMoodTransform,
      onSelectPhysics = onSelectPhysics,
      onUpdateSurfaceGeometry = onUpdateSurfaceGeometry,
      onSelectFontPairing = onSelectFontPairing,
      onUpdateTypographyScale = onUpdateTypographyScale,
      onUpdateLayoutConfig = onUpdateLayoutConfig,
      onSelectMotionFeel = onSelectMotionFeel,
      onSelectExportFormat = onSelectExportFormat,
      onAiPromptChange = onAiPromptChange,
      onAiPolarityChange = onAiPolarityChange,
      onGenerateAiThemes = onGenerateAiThemes,
      onApplyAiSuggestion = onApplyAiSuggestion,
      onSaveAiSuggestion = onSaveAiSuggestion,
      onNotify = onNotify,
    )
  }
}

@Composable
private fun PresentationShotFrame(
  deck: ThemeDeck,
  state: KromdeckUiState,
  onExportPng: () -> Unit,
  onExportSvg: () -> Unit,
) {
  val p = deck.palette
  Box(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .background(
          Brush.linearGradient(
            listOf(
              ColorUtils.parseHexColor(p.primaryHex).copy(alpha = 0.25f),
              KromdeckCreamCanvas,
              ColorUtils.parseHexColor(p.secondaryHex).copy(alpha = 0.22f),
            ),
          ),
        )
        .border(2.dp, KromdeckSlateInk, RoundedCornerShape(20.dp))
        .padding(14.dp),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(deck.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
          Text("${deck.physics.label} • ${deck.fontPairing.label}", style = MaterialTheme.typography.labelSmall, color = KromdeckMutedInk)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          TactileButton(
            onClick = onExportPng,
            containerColor = KromdeckIndigo,
            contentColor = Color.White,
            shadowOffset = 2.dp,
            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 5.dp),
            testTag = "export_spec_png_btn",
          ) {
            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("PNG", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
          }
          TactileButton(
            onClick = onExportSvg,
            containerColor = KromdeckEmerald,
            contentColor = Color.White,
            shadowOffset = 2.dp,
            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 5.dp),
            testTag = "export_spec_svg_btn",
          ) {
            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("SVG", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
          }
        }
      }

      LiveTemplatePreviewFrame(
        deck = deck,
        template = state.activeTemplate,
        brandAppName = state.brandAppName,
        brandTagline = state.brandTagline,
      )

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        listOf("BG" to p.canvasHex, "SURF" to p.surfaceHex, "PRI" to p.primaryHex, "SEC" to p.secondaryHex, "WARM" to p.warmHex).forEach { (lbl, hex) ->
          Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(
              modifier =
                Modifier.size(26.dp)
                  .clip(CircleShape)
                  .background(ColorUtils.parseHexColor(hex))
                  .border(1.5.dp, KromdeckSlateInk, CircleShape),
            )
            Text(lbl, fontFamily = JetBrainsMonoFamily, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
          }
        }
      }
    }
  }
}

@Composable
fun SplitWipePreviewStage(
  leftDeck: ThemeDeck,
  rightDeck: ThemeDeck,
  template: PreviewTemplate,
  brandAppName: String,
  brandTagline: String,
  wipeFraction: Float,
  onWipeChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
) {
  BoxWithConstraints(
    modifier =
      modifier
        .fillMaxWidth()
        .testTag("split_wipe_stage")
        .pointerInput(Unit) {
          detectHorizontalDragGestures { change, _ ->
            change.consume()
            val pct = (change.position.x / size.width.toFloat()).coerceIn(0.08f, 0.92f)
            onWipeChange(pct)
          }
        },
  ) {
    val totalWidth = maxWidth
    LiveTemplatePreviewFrame(
      deck = rightDeck,
      template = template,
      brandAppName = brandAppName,
      brandTagline = brandTagline,
      modifier = Modifier.fillMaxWidth(),
    )

    Box(
      modifier =
        Modifier.matchParentSize().drawWithContent {
          clipRect(right = size.width * wipeFraction) {
            this@drawWithContent.drawContent()
          }
        },
    ) {
      LiveTemplatePreviewFrame(
        deck = leftDeck,
        template = template,
        brandAppName = brandAppName,
        brandTagline = brandTagline,
        modifier = Modifier.fillMaxWidth(),
      )
    }

    Box(
      modifier =
        Modifier.matchParentSize().padding(start = (totalWidth * wipeFraction - 14.dp).coerceAtLeast(0.dp)),
      contentAlignment = Alignment.CenterStart,
    ) {
      Box(
        modifier =
          Modifier.width(28.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(KromdeckAmber)
            .border(2.dp, KromdeckSlateInk, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Icon(Icons.Filled.Compare, contentDescription = "Drag split wipe divider", tint = KromdeckSlateInk, modifier = Modifier.size(16.dp))
      }
    }
  }
}

@Composable
private fun StageChipButton(
  label: String,
  icon: ImageVector,
  active: Boolean,
  onClick: () -> Unit,
  testTag: String,
) {
  Row(
    modifier =
      Modifier.testTag(testTag)
        .clip(RoundedCornerShape(8.dp))
        .background(if (active) KromdeckCoral else KromdeckCreamCanvas)
        .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(8.dp))
        .clickable(onClick = onClick)
        .padding(horizontal = 9.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Icon(icon, contentDescription = null, tint = if (active) Color.White else KromdeckSlateInk, modifier = Modifier.size(14.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Bold,
      color = if (active) Color.White else KromdeckSlateInk,
    )
  }
}
