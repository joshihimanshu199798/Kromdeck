package com.example.ui.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ColorHarmonyRule
import com.example.model.ColorRole
import com.example.model.ExportFormat
import com.example.model.FontPairing
import com.example.model.GridDensity
import com.example.model.MotionFeel
import com.example.model.NavShellStyle
import com.example.model.PaletteMoodFilter
import com.example.model.StudioDrawerTab
import com.example.model.SurfacePhysics
import com.example.model.ThemeDeck
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCoralTint
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckEmeraldTint
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckIndigoTint
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.ui.theme.resolveFontPairing
import com.example.util.ColorUtils
import com.example.util.ExportGenerator
import com.example.viewmodel.KromdeckUiState
import java.util.Locale

@Composable
fun StudioCustomizationDrawer(
  state: KromdeckUiState,
  onSelectTab: (StudioDrawerTab) -> Unit,
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
  onNotify: (String) -> Unit,
) {
  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = Modifier.fillMaxWidth().testTag("studio_customization_drawer"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      // 6-Tab Strip
      Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        StudioDrawerTab.entries.forEach { tab ->
          TactilePillChip(
            label = tab.label,
            selected = state.activeStudioTab == tab,
            onClick = { onSelectTab(tab) },
            activeColor = KromdeckIndigo,
            testTag = "studio_tab_${tab.name.lowercase()}",
          )
        }
      }

      when (state.activeStudioTab) {
        StudioDrawerTab.COLORS ->
          ColorsDrawerTab(
            state = state,
            onOpenColorPicker = onOpenColorPicker,
            onToggleCompanion = onToggleCompanion,
            onHarmonize = onHarmonize,
            onSwapAccents = onSwapAccents,
            onAutoFixContrast = onAutoFixContrast,
            onUpdateGradient = onUpdateGradient,
          )
        StudioDrawerTab.MIXER_LAB ->
          InfiniteThemeLabTab(
            state = state,
            onRenameDeck = onRenameDeck,
            onGenerateHarmony = onGenerateHarmony,
            onBlendDecks = onBlendDecks,
            onApplyGlobalGrade = onApplyGlobalGrade,
            onApplyMoodTransform = onApplyMoodTransform,
          )
        StudioDrawerTab.SURFACE ->
          SurfaceDrawerTab(
            state = state,
            onSelectPhysics = onSelectPhysics,
            onUpdateSurfaceGeometry = onUpdateSurfaceGeometry,
          )
        StudioDrawerTab.TYPOGRAPHY ->
          TypographyDrawerTab(
            state = state,
            onSelectFontPairing = onSelectFontPairing,
            onUpdateTypographyScale = onUpdateTypographyScale,
          )
        StudioDrawerTab.LAYOUT ->
          LayoutDrawerTab(
            state = state,
            onUpdateLayoutConfig = onUpdateLayoutConfig,
          )
        StudioDrawerTab.MOTION ->
          MotionDrawerTab(
            state = state,
            onSelectMotionFeel = onSelectMotionFeel,
          )
        StudioDrawerTab.WCAG_EXPORTS ->
          WcagExportsDrawerTab(
            state = state,
            onAutoFixContrast = onAutoFixContrast,
            onSelectExportFormat = onSelectExportFormat,
            onNotify = onNotify,
          )
      }
    }
  }
}

@Composable
private fun ColorsDrawerTab(
  state: KromdeckUiState,
  onOpenColorPicker: (ColorRole) -> Unit,
  onToggleCompanion: () -> Unit,
  onHarmonize: () -> Unit,
  onSwapAccents: () -> Unit,
  onAutoFixContrast: () -> Unit,
  onUpdateGradient: (Boolean, Float, Boolean) -> Unit,
) {
  val deck = state.activeDeck
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    // Quick Color Engine Actions
    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      TactileButton(
        onClick = onHarmonize,
        containerColor = KromdeckIndigo,
        contentColor = Color.White,
        shadowOffset = 2.dp,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        testTag = "colors_harmonize_btn",
      ) {
        Icon(Icons.Filled.Palette, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Harmonize", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
      }
      TactileButton(
        onClick = onSwapAccents,
        containerColor = KromdeckAmber,
        contentColor = KromdeckSlateInk,
        shadowOffset = 2.dp,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        testTag = "colors_swap_accents_btn",
      ) {
        Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Swap Accents", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
      }
      TactileButton(
        onClick = onAutoFixContrast,
        containerColor = KromdeckEmerald,
        contentColor = Color.White,
        shadowOffset = 2.dp,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        testTag = "colors_autofix_wcag_btn",
      ) {
        Icon(Icons.Filled.AutoFixHigh, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Auto-Fix WCAG", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
      }
      TactileButton(
        onClick = onToggleCompanion,
        containerColor = KromdeckSlateInk,
        contentColor = Color.White,
        shadowOffset = 2.dp,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        testTag = "colors_flip_pair_btn",
      ) {
        Text(
          text = if (deck.isDark) "Switch to Light" else "Switch to Dark",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
        )
      }
    }

    // 2-Column Color Role Pickers
    ColorRole.entries.chunked(2).forEach { pair ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        pair.forEach { role ->
          val hex = deck.palette.hexFor(role)
          Row(
            modifier =
              Modifier.weight(1f)
                .testTag("studio_color_role_${role.name.lowercase()}")
                .clip(RoundedCornerShape(10.dp))
                .background(KromdeckCreamCanvas)
                .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(10.dp))
                .clickable { onOpenColorPicker(role) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Box(
              modifier =
                Modifier.size(28.dp)
                  .clip(RoundedCornerShape(7.dp))
                  .background(ColorUtils.parseHexColor(hex))
                  .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(7.dp)),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(role.label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = KromdeckSlateInk, maxLines = 1)
              Text(hex, fontFamily = JetBrainsMonoFamily, style = MaterialTheme.typography.labelSmall, color = KromdeckMutedInk)
            }
          }
        }
      }
    }

    // Gradient & Mesh Glow Engine
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(KromdeckCreamCanvas)
          .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(12.dp))
          .padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Gradient Backdrop", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
        Switch(
          checked = deck.gradientEnabled,
          onCheckedChange = { onUpdateGradient(it, deck.gradientAngle, deck.gradientMeshGlow) },
        )
      }
      if (deck.gradientEnabled) {
        Text("Gradient Angle: ${deck.gradientAngle.toInt()}°", style = MaterialTheme.typography.labelSmall, color = KromdeckMutedInk)
        Slider(
          value = deck.gradientAngle,
          onValueChange = { onUpdateGradient(true, it, deck.gradientMeshGlow) },
          valueRange = 0f..360f,
          colors = SliderDefaults.colors(thumbColor = KromdeckSlateInk, activeTrackColor = KromdeckIndigo),
        )
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Ambient Mesh Glow", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
        Switch(
          checked = deck.gradientMeshGlow,
          onCheckedChange = { onUpdateGradient(deck.gradientEnabled, deck.gradientAngle, it) },
        )
      }
    }
  }
}

@Composable
private fun SurfaceDrawerTab(
  state: KromdeckUiState,
  onSelectPhysics: (SurfacePhysics) -> Unit,
  onUpdateSurfaceGeometry: (Int?, Int?, Float?, Float?, Float?) -> Unit,
) {
  val deck = state.activeDeck
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    SurfacePhysics.entries.chunked(2).forEach { pair ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        pair.forEach { physics ->
          val sel = deck.physics == physics
          Column(
            modifier =
              Modifier.weight(1f)
                .testTag("studio_physics_${physics.name.lowercase()}")
                .clip(RoundedCornerShape(10.dp))
                .background(if (sel) KromdeckIndigoTint else KromdeckCreamCanvas)
                .border(BorderStroke(if (sel) 2.5.dp else 1.5.dp, if (sel) KromdeckIndigo else KromdeckSlateInk), RoundedCornerShape(10.dp))
                .clickable { onSelectPhysics(physics) }
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
          ) {
            Text(physics.label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
            Text(physics.description, style = MaterialTheme.typography.labelSmall, color = KromdeckMutedInk, maxLines = 2)
          }
        }
      }
    }

    SliderControlRow(
      label = "Corner Radius",
      valueLabel = "${deck.cornerRadiusDp}dp",
      value = deck.cornerRadiusDp.toFloat(),
      range = 0f..32f,
      onValueChange = { onUpdateSurfaceGeometry(it.toInt(), null, null, null, null) },
    )
    SliderControlRow(
      label = "Card Padding",
      valueLabel = "${deck.cardPaddingDp}dp",
      value = deck.cardPaddingDp.toFloat(),
      range = 6f..24f,
      onValueChange = { onUpdateSurfaceGeometry(null, it.toInt(), null, null, null) },
    )
    SliderControlRow(
      label = "Border Width",
      valueLabel = String.format(Locale.US, "%.1fdp", deck.borderWidthDp),
      value = deck.borderWidthDp,
      range = 0f..3f,
      onValueChange = { onUpdateSurfaceGeometry(null, null, it, null, null) },
    )
    SliderControlRow(
      label = "Shadow Intensity",
      valueLabel = "${(deck.shadowIntensity * 100).toInt()}%",
      value = deck.shadowIntensity,
      range = 0f..1.4f,
      onValueChange = { onUpdateSurfaceGeometry(null, null, null, it, null) },
    )
    if (deck.physics == SurfacePhysics.FROSTED_GLASS) {
      SliderControlRow(
        label = "Glass Opacity",
        valueLabel = "${(deck.glassBlurAlpha * 100).toInt()}%",
        value = deck.glassBlurAlpha,
        range = 0.45f..0.95f,
        onValueChange = { onUpdateSurfaceGeometry(null, null, null, null, it) },
      )
    }
  }
}

@Composable
private fun TypographyDrawerTab(
  state: KromdeckUiState,
  onSelectFontPairing: (FontPairing) -> Unit,
  onUpdateTypographyScale: (Float?, Boolean?, Float?, Float?) -> Unit,
) {
  val deck = state.activeDeck
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    FontPairing.entries.chunked(2).forEach { pair ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        pair.forEach { fp ->
          val sel = deck.fontPairing == fp
          val resolved = resolveFontPairing(fp)
          Column(
            modifier =
              Modifier.weight(1f)
                .testTag("studio_font_${fp.name.lowercase()}")
                .clip(RoundedCornerShape(10.dp))
                .background(if (sel) KromdeckIndigoTint else KromdeckCreamCanvas)
                .border(BorderStroke(if (sel) 2.5.dp else 1.5.dp, if (sel) KromdeckIndigo else KromdeckSlateInk), RoundedCornerShape(10.dp))
                .clickable { onSelectFontPairing(fp) }
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
          ) {
            Text(
              text = "Aa Bb 123",
              fontFamily = resolved.display,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 16.sp,
              color = KromdeckSlateInk,
            )
            Text(
              text = fp.label,
              fontFamily = resolved.body,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = KromdeckMutedInk,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
      }
    }

    SliderControlRow(
      label = "Base Font Size",
      valueLabel = "${deck.baseFontSizeSp.toInt()}sp",
      value = deck.baseFontSizeSp,
      range = 12f..18f,
      onValueChange = { onUpdateTypographyScale(it, null, null, null) },
    )
    SliderControlRow(
      label = "Letter Spacing",
      valueLabel = String.format(Locale.US, "%.1fsp", deck.letterSpacingSp),
      value = deck.letterSpacingSp,
      range = -1f..2.5f,
      onValueChange = { onUpdateTypographyScale(null, null, it, null) },
    )
    SliderControlRow(
      label = "Line Height",
      valueLabel = String.format(Locale.US, "%.2fx", deck.lineHeightMultiplier),
      value = deck.lineHeightMultiplier,
      range = 1.15f..1.65f,
      onValueChange = { onUpdateTypographyScale(null, null, null, it) },
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text("Extra-Bold Display Headings", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
      Switch(
        checked = deck.headingWeightBold,
        onCheckedChange = { onUpdateTypographyScale(null, it, null, null) },
      )
    }
  }
}

@Composable
private fun LayoutDrawerTab(
  state: KromdeckUiState,
  onUpdateLayoutConfig: (NavShellStyle?, GridDensity?, Float?) -> Unit,
) {
  val deck = state.activeDeck
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text("Navigation Shell Style", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
    NavShellStyle.entries.chunked(2).forEach { pair ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        pair.forEach { shell ->
          val sel = deck.navShell == shell
          TactilePillChip(
            label = shell.label,
            selected = sel,
            onClick = { onUpdateLayoutConfig(shell, null, null) },
            activeColor = KromdeckIndigo,
            modifier = Modifier.weight(1f),
            testTag = "studio_navshell_${shell.name.lowercase()}",
          )
        }
      }
    }

    Text("Grid & Content Density", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      GridDensity.entries.forEach { density ->
        TactilePillChip(
          label = density.label,
          selected = deck.gridDensity == density,
          onClick = { onUpdateLayoutConfig(null, density, null) },
          activeColor = KromdeckCoral,
          modifier = Modifier.weight(1f),
          testTag = "studio_density_${density.name.lowercase()}",
        )
      }
    }

    SliderControlRow(
      label = "Icon Stroke Weight",
      valueLabel = String.format(Locale.US, "%.1fpx", deck.iconStrokeWeight),
      value = deck.iconStrokeWeight,
      range = 1.0f..2.8f,
      onValueChange = { onUpdateLayoutConfig(null, null, it) },
    )
  }
}

@Composable
private fun MotionDrawerTab(
  state: KromdeckUiState,
  onSelectMotionFeel: (MotionFeel) -> Unit,
) {
  val deck = state.activeDeck
  var tapCounter by remember { mutableIntStateOf(0) }

  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    MotionFeel.entries.forEach { feel ->
      val sel = deck.motionFeel == feel
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .testTag("studio_motion_${feel.name.lowercase()}")
            .clip(RoundedCornerShape(10.dp))
            .background(if (sel) KromdeckIndigoTint else KromdeckCreamCanvas)
            .border(BorderStroke(if (sel) 2.5.dp else 1.5.dp, if (sel) KromdeckIndigo else KromdeckSlateInk), RoundedCornerShape(10.dp))
            .clickable { onSelectMotionFeel(feel) }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(feel.label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
          Text(feel.description, style = MaterialTheme.typography.labelSmall, color = KromdeckMutedInk)
        }
        Text("${feel.durationMs}ms", fontFamily = JetBrainsMonoFamily, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = KromdeckIndigo)
      }
    }

    // Live Interactive Press & Transition Feel Tester
    val colors = deck.resolveColors()
    ThemedCard(
      deck = deck,
      backgroundOverride = colors.primary,
      onClick = { tapCounter++ },
      modifier = Modifier.fillMaxWidth().testTag("motion_tester_card"),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Filled.TouchApp, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(20.dp))
          Column {
            Text("Press & Hold Feel Tester", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = colors.onPrimary)
            Text("Active curve: ${deck.motionFeel.label}", fontSize = 11.sp, color = colors.onPrimary.copy(alpha = 0.85f))
          }
        }
        Text(
          text = "#$tapCounter",
          fontFamily = JetBrainsMonoFamily,
          fontWeight = FontWeight.ExtraBold,
          color = colors.onPrimary,
        )
      }
    }
  }
}

@Composable
private fun WcagExportsDrawerTab(
  state: KromdeckUiState,
  onAutoFixContrast: () -> Unit,
  onSelectExportFormat: (ExportFormat) -> Unit,
  onNotify: (String) -> Unit,
) {
  val context = LocalContext.current
  val clipboard = LocalClipboardManager.current
  val audit = state.wcagAudit
  val deck = state.activeDeck
  val format = state.selectedExportFormat
  val generated = remember(deck, format) { ExportGenerator.generate(deck, format) }

  val saveFileLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(format.mimeType)) { uri ->
      if (uri != null) {
        try {
          context.contentResolver.openOutputStream(uri)?.use { it.write(generated.toByteArray(Charsets.UTF_8)) }
          onNotify("Downloaded ${ExportGenerator.suggestedFileName(deck, format)}")
        } catch (_: Exception) {
          onNotify("Failed to save file")
        }
      }
    }

  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    // Live WCAG 2.1 Validator Card
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(if (audit.passesAA) KromdeckEmeraldTint else KromdeckCoralTint)
          .border(2.dp, if (audit.passesAA) KromdeckEmerald else KromdeckCoral, RoundedCornerShape(12.dp))
          .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Icon(
            imageVector = if (audit.passesAA) Icons.Filled.CheckCircle else Icons.Filled.Warning,
            contentDescription = null,
            tint = if (audit.passesAA) KromdeckEmerald else KromdeckCoral,
            modifier = Modifier.size(18.dp),
          )
          Text(
            text = if (audit.passesAA) "WCAG 2.1 AA PASS" else "WCAG CONTRAST WARNING",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckSlateInk,
          )
        }
        if (!audit.passesAA) {
          TactileButton(
            onClick = onAutoFixContrast,
            containerColor = KromdeckCoral,
            contentColor = Color.White,
            shadowOffset = 2.dp,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            testTag = "wcag_drawer_autofix_btn",
          ) {
            Text("Auto-Fix", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
          }
        }
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Text/Bg: ${String.format(Locale.US, "%.1f:1", audit.canvasTextRatio)}",
          fontFamily = JetBrainsMonoFamily,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = KromdeckSlateInk,
        )
        Text(
          text = "Text/Card: ${String.format(Locale.US, "%.1f:1", audit.surfaceTextRatio)}",
          fontFamily = JetBrainsMonoFamily,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = KromdeckSlateInk,
        )
        Text(
          text = "Pri/Bg: ${String.format(Locale.US, "%.2f:1", audit.worstPrimaryRatio)}",
          fontFamily = JetBrainsMonoFamily,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = if (audit.primaryPassesUI) KromdeckEmerald else KromdeckCoral,
        )
      }
      audit.warnings.forEach { warn ->
        Text("• $warn", style = MaterialTheme.typography.labelSmall, color = KromdeckCoral)
      }
    }

    // 6 Export Format Chips
    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      ExportFormat.entries.forEach { f ->
        TactilePillChip(
          label = f.label,
          selected = format == f,
          onClick = { onSelectExportFormat(f) },
          activeColor = KromdeckSlateInk,
          testTag = "export_format_${f.name.lowercase()}",
        )
      }
    }

    // Action Buttons: Copy / Download / Share
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      TactileButton(
        onClick = {
          clipboard.setText(AnnotatedString(generated))
          onNotify("Copied ${format.label} to clipboard")
        },
        containerColor = KromdeckIndigo,
        contentColor = Color.White,
        shadowOffset = 2.dp,
        modifier = Modifier.weight(1f),
        testTag = "export_copy_btn",
      ) {
        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Copy", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
      }
      TactileButton(
        onClick = { saveFileLauncher.launch(ExportGenerator.suggestedFileName(deck, format)) },
        containerColor = KromdeckEmerald,
        contentColor = Color.White,
        shadowOffset = 2.dp,
        modifier = Modifier.weight(1f),
        testTag = "export_download_btn",
      ) {
        Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(".${format.fileExtension.uppercase()}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
      }
      TactileButton(
        onClick = {
          val intent =
            Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_SUBJECT, "Kromdeck ${deck.name} (${format.label})")
              putExtra(Intent.EXTRA_TEXT, generated)
            }
          context.startActivity(Intent.createChooser(intent, "Share Design Tokens"))
        },
        containerColor = KromdeckPureSurface,
        contentColor = KromdeckSlateInk,
        shadowOffset = 2.dp,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        testTag = "export_share_btn",
      ) {
        Icon(Icons.Filled.Share, contentDescription = "Share code", modifier = Modifier.size(15.dp))
      }
    }

    // Code Preview Block
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(KromdeckSlateInk)
          .padding(12.dp),
    ) {
      Text(
        text = generated,
        fontFamily = JetBrainsMonoFamily,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        color = Color(0xFFE2E8F0),
        maxLines = 18,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
private fun SliderControlRow(
  label: String,
  valueLabel: String,
  value: Float,
  range: ClosedFloatingPointRange<Float>,
  onValueChange: (Float) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
      Text(valueLabel, fontFamily = JetBrainsMonoFamily, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = KromdeckIndigo)
    }
    Slider(
      value = value.coerceIn(range.start, range.endInclusive),
      onValueChange = onValueChange,
      valueRange = range,
      colors = SliderDefaults.colors(thumbColor = KromdeckSlateInk, activeTrackColor = KromdeckIndigo),
    )
  }
}
