package com.example.ui.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExportFormat
import com.example.model.ThemeDeck
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.util.ExportGenerator

/**
 * Dedicated Token Export Center card allowing instant JSON, XML, Compose, CSS, Tailwind,
 * Flutter, SwiftUI, and AI Prompt export of the active theme with 1-tap Copy, File Download,
 * and System Share Sheet.
 */
@Composable
fun TokenExportCenterCard(
  deck: ThemeDeck,
  initialFormat: ExportFormat = ExportFormat.JSON_TOKENS,
  onNotify: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val clipboard = LocalClipboardManager.current
  var selectedFormat by remember(initialFormat) { mutableStateOf(initialFormat) }
  var expandedPreview by remember { mutableStateOf(false) }

  val codeOutput = remember(deck, selectedFormat) { ExportGenerator.generate(deck, selectedFormat) }
  val suggestedName = remember(deck, selectedFormat) { ExportGenerator.suggestedFileName(deck, selectedFormat) }

  val jsonSaveLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(ExportFormat.JSON_TOKENS.mimeType)) { uri ->
      if (uri != null) {
        runCatching {
          val payload = ExportGenerator.generate(deck, ExportFormat.JSON_TOKENS)
          context.contentResolver.openOutputStream(uri)?.use { it.write(payload.toByteArray(Charsets.UTF_8)) }
          onNotify("Exported ${ExportGenerator.suggestedFileName(deck, ExportFormat.JSON_TOKENS)}")
        }.onFailure { onNotify("Failed to save JSON file") }
      }
    }

  val xmlSaveLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(ExportFormat.XML_TOKENS.mimeType)) { uri ->
      if (uri != null) {
        runCatching {
          val payload = ExportGenerator.generate(deck, ExportFormat.XML_TOKENS)
          context.contentResolver.openOutputStream(uri)?.use { it.write(payload.toByteArray(Charsets.UTF_8)) }
          onNotify("Exported ${ExportGenerator.suggestedFileName(deck, ExportFormat.XML_TOKENS)}")
        }.onFailure { onNotify("Failed to save XML file") }
      }
    }

  val genericSaveLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(selectedFormat.mimeType)) { uri ->
      if (uri != null) {
        runCatching {
          context.contentResolver.openOutputStream(uri)?.use { it.write(codeOutput.toByteArray(Charsets.UTF_8)) }
          onNotify("Exported $suggestedName")
        }.onFailure { onNotify("Failed to save file") }
      }
    }

  TactileCard(
    containerColor = KromdeckPureSurface,
    modifier = modifier.fillMaxWidth().testTag("token_export_center_card"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier =
              Modifier.size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(KromdeckIndigo)
                .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Filled.Code, contentDescription = "Export active theme tokens", tint = Color.White, modifier = Modifier.size(18.dp))
          }
          Column {
            Text(
              text = "Export Theme Tokens (JSON & XML)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckSlateInk,
            )
            Text(
              text = "Active theme: ${deck.name} • Ready for external projects",
              style = MaterialTheme.typography.labelSmall,
              color = KromdeckMutedInk,
            )
          }
        }
      }

      // Direct 1-Tap Primary JSON & XML Download Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        TactileButton(
          onClick = {
            selectedFormat = ExportFormat.JSON_TOKENS
            jsonSaveLauncher.launch(ExportGenerator.suggestedFileName(deck, ExportFormat.JSON_TOKENS))
          },
          containerColor = KromdeckIndigo,
          contentColor = Color.White,
          shadowOffset = 2.dp,
          modifier = Modifier.weight(1f),
          testTag = "tokens_quick_export_json_btn",
        ) {
          Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text("Export JSON", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
        }

        TactileButton(
          onClick = {
            selectedFormat = ExportFormat.XML_TOKENS
            xmlSaveLauncher.launch(ExportGenerator.suggestedFileName(deck, ExportFormat.XML_TOKENS))
          },
          containerColor = KromdeckEmerald,
          contentColor = Color.White,
          shadowOffset = 2.dp,
          modifier = Modifier.weight(1f),
          testTag = "tokens_quick_export_xml_btn",
        ) {
          Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text("Export XML", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
        }
      }

      // All 8 Export Formats Selector
      Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        ExportFormat.entries.forEach { format ->
          TactilePillChip(
            label = format.label,
            selected = selectedFormat == format,
            onClick = { selectedFormat = format },
            activeColor = KromdeckSlateInk,
            testTag = "tokens_format_chip_${format.name.lowercase()}",
          )
        }
      }

      Text(
        text = "${selectedFormat.subtitle} ($suggestedName)",
        style = MaterialTheme.typography.labelSmall,
        color = KromdeckMutedInk,
      )

      // Copy / Download Selected / Share Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        TactileButton(
          onClick = {
            clipboard.setText(AnnotatedString(codeOutput))
            onNotify("Copied ${selectedFormat.label} to clipboard")
          },
          containerColor = KromdeckAmber,
          contentColor = KromdeckSlateInk,
          shadowOffset = 2.dp,
          modifier = Modifier.weight(1f),
          testTag = "tokens_export_copy_btn",
        ) {
          Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Copy ${selectedFormat.fileExtension.uppercase()}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
        }

        TactileButton(
          onClick = { genericSaveLauncher.launch(suggestedName) },
          containerColor = KromdeckPureSurface,
          contentColor = KromdeckSlateInk,
          shadowOffset = 2.dp,
          modifier = Modifier.weight(1f),
          testTag = "tokens_export_save_file_btn",
        ) {
          Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Save File", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
        }

        TactileButton(
          onClick = {
            val intent =
              Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Kromdeck ${deck.name} (${selectedFormat.label})")
                putExtra(Intent.EXTRA_TEXT, codeOutput)
              }
            context.startActivity(Intent.createChooser(intent, "Share ${selectedFormat.label}"))
          },
          containerColor = KromdeckPureSurface,
          contentColor = KromdeckSlateInk,
          shadowOffset = 2.dp,
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          testTag = "tokens_export_share_btn",
        ) {
          Icon(Icons.Filled.Share, contentDescription = "Share exported tokens", modifier = Modifier.size(15.dp))
        }
      }

      // Live Code Preview Window
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(min = 120.dp, max = if (expandedPreview) 420.dp else 190.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(KromdeckSlateInk)
            .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(10.dp))
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
      ) {
        Text(
          text = codeOutput,
          fontFamily = JetBrainsMonoFamily,
          fontSize = 10.sp,
          lineHeight = 14.sp,
          color = Color(0xFFE2E8F0),
        )
      }

      TactileButton(
        onClick = { expandedPreview = !expandedPreview },
        containerColor = KromdeckPureSurface,
        contentColor = KromdeckSlateInk,
        shadowOffset = 1.dp,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.align(Alignment.End),
        testTag = "tokens_toggle_preview_height_btn",
      ) {
        Text(
          text = if (expandedPreview) "Compact Preview" else "Expand Full Code",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
        )
      }
    }
  }
}
