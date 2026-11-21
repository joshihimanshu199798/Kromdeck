package com.example.ui.components

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.PresetDecks
import com.example.model.ExtractedPaletteResult
import com.example.model.ThemePalette
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckIndigoTint
import com.example.ui.theme.KromdeckMutedInk
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.util.ColorUtils

@Composable
fun ShareBackupDialog(
  activeHash: String,
  onBuildBackupJson: () -> String,
  onImportHash: (String) -> Boolean,
  onImportBackupJson: (String) -> Boolean,
  onNotify: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  val context = LocalContext.current
  val clipboard = LocalClipboardManager.current
  var hashInput by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  val exportJsonLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
      if (uri != null) {
        try {
          val json = onBuildBackupJson()
          context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray(Charsets.UTF_8)) }
          onNotify("Saved Kromdeck JSON backup")
        } catch (_: Exception) {
          onNotify("Could not write backup file")
        }
      }
    }

  val importJsonLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
      if (uri != null) {
        try {
          val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText().orEmpty()
          if (!onImportBackupJson(text)) {
            errorText = "Invalid Kromdeck JSON backup file."
          }
        } catch (_: Exception) {
          errorText = "Failed to read selected file."
        }
      }
    }

  Dialog(onDismissRequest = onDismiss) {
    TactileCard(
      containerColor = KromdeckPureSurface,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = "Share & JSON Backup",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckSlateInk,
            )
            Text(
              text = "Encode active theme into a #theme hash or backup moodboard",
              style = MaterialTheme.typography.bodySmall,
              color = KromdeckMutedInk,
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Filled.Close, contentDescription = "Close share modal", tint = KromdeckSlateInk)
          }
        }

        // Encoded Hash Box
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(KromdeckCreamCanvas)
              .border(BorderStroke(1.5.dp, KromdeckSlateInk), RoundedCornerShape(10.dp))
              .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = "SHAREABLE THEME HASH",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = KromdeckIndigo,
          )
          Text(
            text = "https://kromdeck.app/$activeHash",
            fontFamily = JetBrainsMonoFamily,
            style = MaterialTheme.typography.labelSmall,
            color = KromdeckSlateInk,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TactileButton(
              onClick = {
                clipboard.setText(AnnotatedString("https://kromdeck.app/$activeHash"))
                onNotify("Copied shareable #theme link")
              },
              containerColor = KromdeckIndigo,
              contentColor = Color.White,
              shadowOffset = 2.dp,
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              testTag = "copy_theme_hash_button",
            ) {
              Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Copy Hash", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
            TactileButton(
              onClick = {
                val intent =
                  Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Kromdeck Theme Link")
                    putExtra(Intent.EXTRA_TEXT, "https://kromdeck.app/$activeHash")
                  }
                context.startActivity(Intent.createChooser(intent, "Share Kromdeck Theme"))
              },
              containerColor = KromdeckPureSurface,
              contentColor = KromdeckSlateInk,
              shadowOffset = 2.dp,
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              testTag = "share_theme_hash_button",
            ) {
              Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Share", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Import #theme hash
        OutlinedTextField(
          value = hashInput,
          onValueChange = {
            hashInput = it
            errorText = null
          },
          label = { Text("Paste #theme=... link or hash") },
          singleLine = true,
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = KromdeckIndigo,
              unfocusedBorderColor = KromdeckSlateInk,
            ),
          modifier = Modifier.fillMaxWidth(),
        )
        TactileButton(
          onClick = {
            if (!onImportHash(hashInput)) {
              errorText = "Invalid #theme hash. Paste a valid Kromdeck link."
            }
          },
          enabled = hashInput.isNotBlank(),
          containerColor = KromdeckAmber,
          contentColor = KromdeckSlateInk,
          shadowOffset = 2.dp,
          modifier = Modifier.fillMaxWidth(),
          testTag = "import_theme_hash_button",
        ) {
          Icon(Icons.Filled.Link, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Load From #theme Hash", fontWeight = FontWeight.ExtraBold)
        }

        // 1-Click JSON Preset Backup / Restore
        Text(
          text = "JSON PRESET & MOODBOARD BACKUP",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.ExtraBold,
          color = KromdeckMutedInk,
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          TactileButton(
            onClick = { exportJsonLauncher.launch("kromdeck_backup.json") },
            containerColor = KromdeckEmerald,
            contentColor = Color.White,
            shadowOffset = 2.dp,
            modifier = Modifier.weight(1f),
            testTag = "export_json_backup_button",
          ) {
            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Export JSON", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          }
          TactileButton(
            onClick = { importJsonLauncher.launch(arrayOf("application/json", "text/plain")) },
            containerColor = KromdeckPureSurface,
            contentColor = KromdeckSlateInk,
            shadowOffset = 2.dp,
            modifier = Modifier.weight(1f),
            testTag = "import_json_backup_button",
          ) {
            Icon(Icons.Filled.UploadFile, contentDescription = null, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Import JSON", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          }
        }

        if (errorText != null) {
          Text(
            text = errorText.orEmpty(),
            style = MaterialTheme.typography.labelMedium,
            color = KromdeckCoral,
            fontWeight = FontWeight.Bold,
          )
        }
      }
    }
  }
}

@Composable
fun ImageExtractorDialog(
  extractedResult: ExtractedPaletteResult?,
  isExtracting: Boolean,
  onExtractBitmap: (String, Bitmap) -> Unit,
  onApplyExtracted: (Boolean) -> Unit,
  onDismiss: () -> Unit,
) {
  val context = LocalContext.current

  val photoPickerLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
      if (uri != null) {
        try {
          context.contentResolver.openInputStream(uri)?.use { stream ->
            val bmp = BitmapFactory.decodeStream(stream)
            if (bmp != null) onExtractBitmap("Custom Image", bmp)
          }
        } catch (_: Exception) {
          // Ignore malformed stream
        }
      }
    }

  Dialog(onDismissRequest = onDismiss) {
    TactileCard(
      containerColor = KromdeckPureSurface,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Image-to-Theme Extractor",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckSlateInk,
            )
            Text(
              text = "Quantize any screenshot into balanced Light & Dark Kromdeck presets",
              style = MaterialTheme.typography.bodySmall,
              color = KromdeckMutedInk,
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Filled.Close, contentDescription = "Close extractor modal", tint = KromdeckSlateInk)
          }
        }

        // Photo Picker Button
        TactileButton(
          onClick = {
            photoPickerLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
          },
          containerColor = KromdeckIndigo,
          contentColor = Color.White,
          shadowOffset = 2.dp,
          modifier = Modifier.fillMaxWidth(),
          testTag = "extractor_pick_photo_button",
        ) {
          Icon(Icons.Filled.ImageSearch, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isExtracting) "Sampling Colors..." else "Pick Screenshot / Photo",
            fontWeight = FontWeight.ExtraBold,
          )
        }

        // Built-in Sample Artworks
        Text(
          text = "OR SAMPLE BUILT-IN ARTWORKS",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.ExtraBold,
          color = KromdeckMutedInk,
        )
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          PresetDecks.sampleExtractorAssets.forEach { (label, resId) ->
            TactilePillChip(
              label = label,
              selected = extractedResult?.sourceLabel == label,
              onClick = {
                val bmp = BitmapFactory.decodeResource(context.resources, resId)
                if (bmp != null) onExtractBitmap(label, bmp)
              },
              activeColor = KromdeckCoral,
              testTag = "extractor_sample_${label.lowercase()}",
            )
          }
        }

        if (extractedResult != null) {
          Column(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(KromdeckIndigoTint)
                .border(BorderStroke(1.5.dp, KromdeckSlateInk), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = "SAMPLED SWATCHES (${extractedResult.sourceLabel.uppercase()})",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.ExtraBold,
              color = KromdeckIndigo,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              extractedResult.swatches.forEach { hex ->
                Box(
                  modifier =
                    Modifier.size(28.dp)
                      .clip(CircleShape)
                      .background(ColorUtils.parseHexColor(hex))
                      .border(1.5.dp, KromdeckSlateInk, CircleShape),
                )
              }
            }

            // Dual Light & Dark preview cards
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              val primaryIsDark =
                ColorUtils.relativeLuminance(ColorUtils.parseHexColor(extractedResult.palette.canvasHex)) < 0.25
              ExtractedPairCard(
                label = if (primaryIsDark) "Dark Pair" else "Light Pair",
                isDark = primaryIsDark,
                palette = extractedResult.palette,
                onApply = { onApplyExtracted(false) },
                modifier = Modifier.weight(1f),
              )
              ExtractedPairCard(
                label = if (primaryIsDark) "Light Pair" else "Dark Pair",
                isDark = !primaryIsDark,
                palette = extractedResult.darkCompanionPalette,
                onApply = { onApplyExtracted(true) },
                modifier = Modifier.weight(1f),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ExtractedPairCard(
  label: String,
  isDark: Boolean,
  palette: ThemePalette,
  onApply: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val bg = ColorUtils.parseHexColor(palette.canvasHex)
  val surface = ColorUtils.parseHexColor(palette.surfaceHex)
  val text = ColorUtils.parseHexColor(palette.textPrimaryHex)
  val primary = ColorUtils.parseHexColor(palette.primaryHex)
  val secondary = ColorUtils.parseHexColor(palette.secondaryHex)

  Column(
    modifier =
      modifier
        .clip(RoundedCornerShape(10.dp))
        .background(bg)
        .border(1.5.dp, KromdeckSlateInk, RoundedCornerShape(10.dp))
        .padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      Icon(
        imageVector = if (isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
        contentDescription = null,
        tint = primary,
        modifier = Modifier.size(13.dp),
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold,
        color = text,
      )
    }
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(surface)
          .padding(6.dp),
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(primary))
        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(secondary))
        Text("Aa", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = text)
      }
    }
    TactileButton(
      onClick = onApply,
      containerColor = primary,
      contentColor = ColorUtils.bestOnColor(primary),
      shadowOffset = 1.dp,
      cornerRadius = 8.dp,
      contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
      modifier = Modifier.fillMaxWidth(),
      testTag = "apply_extracted_${label.lowercase().replace(" ", "_")}",
    ) {
      Text("Load", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
    }
  }
}
