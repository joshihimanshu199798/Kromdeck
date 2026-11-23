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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExportFormat
import com.example.model.TonalShade
import com.example.ui.components.TactileCard
import com.example.ui.components.TokenExportCenterCard
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.ui.theme.resolveFontPairing
import com.example.util.ColorUtils
import com.example.viewmodel.KromdeckUiState
import java.util.Locale

@Composable
fun TokensScaleScreen(
  state: KromdeckUiState,
  onNotify: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val clipboard = LocalClipboardManager.current
  val deck = state.activeDeck
  val p = deck.palette
  val fonts = resolveFontPairing(deck.fontPairing)

  val primaryRamp = remember(p.primaryHex) { ColorUtils.generateTonalRamp(p.primaryHex) }
  val secondaryRamp = remember(p.secondaryHex) { ColorUtils.generateTonalRamp(p.secondaryHex) }
  val neutralRamp = remember(p.borderHex) { ColorUtils.generateTonalRamp(p.borderHex) }

  LazyColumn(
    modifier = modifier.fillMaxSize().testTag("tokens_scale_screen"),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    // 0. Active Theme Token Exporter (1-Tap JSON & XML + Compose/CSS/Tailwind/Flutter/SwiftUI)
    item {
      TokenExportCenterCard(
        deck = deck,
        initialFormat = ExportFormat.JSON_TOKENS,
        onNotify = onNotify,
      )
    }

    // 1. Auto-Generated 50-950 Tonal Shade Ramps
    item {
      TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("50–950 Tonal Shade Ramps", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
          Text("Tap any shade chip to copy its hex token immediately.", style = MaterialTheme.typography.bodySmall, color = KromdeckMutedInk)

          TonalRampRow(
            title = "Primary (${p.primaryHex})",
            shades = primaryRamp,
            onCopy = { hex ->
              clipboard.setText(AnnotatedString(hex))
              onNotify("Copied Primary $hex")
            },
          )
          TonalRampRow(
            title = "Secondary (${p.secondaryHex})",
            shades = secondaryRamp,
            onCopy = { hex ->
              clipboard.setText(AnnotatedString(hex))
              onNotify("Copied Secondary $hex")
            },
          )
          TonalRampRow(
            title = "Neutral / Ink (${p.borderHex})",
            shades = neutralRamp,
            onCopy = { hex ->
              clipboard.setText(AnnotatedString(hex))
              onNotify("Copied Neutral $hex")
            },
          )
        }
      }
    }

    // 2. Live Type Hierarchy Specimen
    item {
      val base = deck.baseFontSizeSp
      TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Typography Scale (${deck.fontPairing.label})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
          val specs =
            listOf(
              Triple("Display", fonts.display, (base + 14f).sp),
              Triple("H1 Heading", fonts.display, (base + 8f).sp),
              Triple("H2 Section", fonts.display, (base + 4f).sp),
              Triple("H3 Subhead", fonts.display, (base + 1f).sp),
              Triple("Body Copy", fonts.body, base.sp),
              Triple("Caption", fonts.body, (base - 2f).coerceAtLeast(10f).sp),
              Triple("Mono Token", fonts.mono, (base - 2f).coerceAtLeast(10f).sp),
            )
          specs.forEach { (label, family, sizeSp) ->
            Column(
              modifier =
                Modifier.fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(KromdeckCreamCanvas)
                  .border(1.dp, KromdeckSlateInk.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label.uppercase(), fontFamily = JetBrainsMonoFamily, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = KromdeckIndigo)
                Text("${sizeSp.value.toInt()}sp", fontFamily = JetBrainsMonoFamily, fontSize = 9.sp, color = KromdeckMutedInk)
              }
              Text(
                text = if (label == "Mono Token") "--kd-radius: ${deck.cornerRadiusDp}px; /* ${p.primaryHex} */" else "The tactile craft of Kromdeck.",
                fontFamily = family,
                fontSize = sizeSp,
                fontWeight = if (label.startsWith("H") || label == "Display") FontWeight.ExtraBold else FontWeight.Normal,
                letterSpacing = deck.letterSpacingSp.sp,
                color = KromdeckSlateInk,
              )
            }
          }
        }
      }
    }

    // 3. Spacing & Radius Scale Blocks
    item {
      TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Spacing & Radius Tokens", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
          val mult = deck.gridDensity.spacingMultiplier
          listOf(4, 8, 12, 16, 24, 32, 48).forEach { unit ->
            val scaled = (unit * mult).toInt().coerceAtLeast(2)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Text("space-$unit", fontFamily = JetBrainsMonoFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KromdeckSlateInk, modifier = Modifier.width(68.dp))
              Box(
                modifier =
                  Modifier.width((scaled * 3).coerceAtMost(220).dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ColorUtils.parseHexColor(p.primaryHex))
                    .border(1.dp, KromdeckSlateInk, RoundedCornerShape(4.dp)),
              )
              Text("${scaled}dp", fontFamily = JetBrainsMonoFamily, fontSize = 10.sp, color = KromdeckMutedInk)
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(
              "sm" to (deck.cornerRadiusDp / 2).coerceAtLeast(2),
              "md" to deck.cornerRadiusDp,
              "lg" to (deck.cornerRadiusDp + 8).coerceAtMost(32),
            ).forEach { (lbl, r) ->
              Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                  modifier =
                    Modifier.size(46.dp)
                      .clip(RoundedCornerShape(r.dp))
                      .background(KromdeckCreamCanvas)
                      .border(2.dp, KromdeckSlateInk, RoundedCornerShape(r.dp)),
                  contentAlignment = Alignment.Center,
                ) {
                  Text("${r}dp", fontFamily = JetBrainsMonoFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
                }
                Text("radius-$lbl", fontFamily = JetBrainsMonoFamily, fontSize = 9.sp, color = KromdeckMutedInk)
              }
            }
          }
        }
      }
    }

    // 4. WCAG Contrast Matrix
    item {
      val fgList =
        listOf(
          "Text" to p.textPrimaryHex,
          "Muted" to p.textMutedHex,
          "Primary" to p.primaryHex,
          "Secondary" to p.secondaryHex,
        )
      val bgList =
        listOf(
          "Background" to p.canvasHex,
          "Surface" to p.surfaceHex,
          "Elevated" to p.elevatedSurfaceHex,
        )

      TactileCard(containerColor = KromdeckPureSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("WCAG 2.1 Contrast Matrix", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
          Text("Live ratios across all Foreground × Background token pairs.", style = MaterialTheme.typography.bodySmall, color = KromdeckMutedInk)

          // Header Row
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Spacer(modifier = Modifier.width(72.dp))
            bgList.forEach { (bgName, _) ->
              Text(bgName, fontFamily = JetBrainsMonoFamily, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk, modifier = Modifier.weight(1f))
            }
          }

          fgList.forEach { (fgName, fgHex) ->
            val fgColor = ColorUtils.parseHexColor(fgHex)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(fgName, fontFamily = JetBrainsMonoFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KromdeckSlateInk, modifier = Modifier.width(72.dp))
              bgList.forEach { (_, bgHex) ->
                val ratio = ColorUtils.contrastRatio(fgColor, ColorUtils.parseHexColor(bgHex))
                val grade =
                  when {
                    ratio >= 7.0 -> "AAA"
                    ratio >= 4.5 -> "AA"
                    ratio >= 3.0 -> "AA18"
                    else -> "FAIL"
                  }
                val badgeBg =
                  when (grade) {
                    "AAA", "AA" -> KromdeckEmerald
                    "AA18" -> KromdeckAmber
                    else -> KromdeckCoral
                  }
                Column(
                  modifier =
                    Modifier.weight(1f)
                      .clip(RoundedCornerShape(8.dp))
                      .background(badgeBg.copy(alpha = 0.16f))
                      .border(1.dp, badgeBg, RoundedCornerShape(8.dp))
                      .padding(vertical = 5.dp, horizontal = 4.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                  Text(String.format(Locale.US, "%.1f:1", ratio), fontFamily = JetBrainsMonoFamily, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = KromdeckSlateInk)
                  Text(grade, fontFamily = JetBrainsMonoFamily, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = if (grade == "AA18") KromdeckSlateInk else badgeBg)
                }
              }
            }
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }
}

@Composable
private fun TonalRampRow(
  title: String,
  shades: List<TonalShade>,
  onCopy: (String) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = KromdeckSlateInk)
    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      shades.forEach { shade ->
        val c = ColorUtils.parseHexColor(shade.hex)
        val onC = ColorUtils.bestOnColor(c)
        Column(
          modifier =
            Modifier.width(58.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(c)
              .border(BorderStroke(1.dp, KromdeckSlateInk), RoundedCornerShape(8.dp))
              .clickable { onCopy(shade.hex) }
              .padding(vertical = 8.dp, horizontal = 4.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          Text("${shade.step}", fontFamily = JetBrainsMonoFamily, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = onC)
          Text(shade.hex.removePrefix("#"), fontFamily = JetBrainsMonoFamily, fontSize = 8.sp, color = onC.copy(alpha = 0.9f))
        }
      }
    }
  }
}
