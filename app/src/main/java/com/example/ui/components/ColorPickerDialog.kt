package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.model.ColorRole
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckCreamCanvas
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk
import com.example.util.ColorUtils

@Composable
fun HexColorPickerDialog(
  role: ColorRole,
  initialHex: String,
  onDismiss: () -> Unit,
  onApply: (String) -> Unit,
) {
  val initialColor = remember(initialHex) { ColorUtils.parseHexColor(initialHex) }
  val initialHsv = remember(initialColor) { ColorUtils.rgbToHsv(initialColor) }

  var hue by remember { mutableFloatStateOf(initialHsv.first) }
  var sat by remember { mutableFloatStateOf(initialHsv.second) }
  var value by remember { mutableFloatStateOf(initialHsv.third) }
  var hexInput by remember { mutableStateOf(initialHex.uppercase()) }

  val currentColor = ColorUtils.hsvToColor(hue, sat, value)
  val currentHex = ColorUtils.colorToHex(currentColor)

  val quickSwatches =
    listOf(
      "#FAF9F6",
      "#FFFFFF",
      "#0F172A",
      "#050507",
      "#4F46E5",
      "#F43F5E",
      "#F59E0B",
      "#10B981",
      "#06B6D4",
      "#84CC16",
      "#D96B43",
      "#EC4899",
    )

  Dialog(onDismissRequest = onDismiss) {
    TactileCard(
      containerColor = KromdeckPureSurface,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Column {
            Text(
              text = "Edit ${role.label} Color",
              style = MaterialTheme.typography.titleLarge,
              color = KromdeckSlateInk,
            )
            Text(
              text = currentHex,
              fontFamily = JetBrainsMonoFamily,
              style = MaterialTheme.typography.labelMedium,
              color = KromdeckIndigo,
            )
          }
          Box(
            modifier =
              Modifier.size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(currentColor)
                .border(2.dp, KromdeckSlateInk, RoundedCornerShape(10.dp)),
          )
        }

        // Hex Input
        OutlinedTextField(
          value = hexInput,
          onValueChange = { raw ->
            hexInput = raw
            val valid = ColorUtils.normalizeHex(raw)
            if (valid != null) {
              val parsed = ColorUtils.parseHexColor(valid)
              val (h, s, v) = ColorUtils.rgbToHsv(parsed)
              hue = h
              sat = s
              value = v
            }
          },
          label = { Text("Hex Code (#RRGGBB)") },
          singleLine = true,
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = KromdeckIndigo,
              unfocusedBorderColor = KromdeckSlateInk,
            ),
          modifier = Modifier.fillMaxWidth(),
        )

        // HSV Sliders
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Hue (${hue.toInt()}°)",
            style = MaterialTheme.typography.labelMedium,
            color = KromdeckSlateInk,
          )
          Slider(
            value = hue,
            onValueChange = {
              hue = it
              hexInput = ColorUtils.colorToHex(ColorUtils.hsvToColor(hue, sat, value))
            },
            valueRange = 0f..360f,
            colors = SliderDefaults.colors(thumbColor = KromdeckSlateInk, activeTrackColor = KromdeckIndigo),
          )

          Text(
            text = "Saturation (${(sat * 100).toInt()}%)",
            style = MaterialTheme.typography.labelMedium,
            color = KromdeckSlateInk,
          )
          Slider(
            value = sat,
            onValueChange = {
              sat = it
              hexInput = ColorUtils.colorToHex(ColorUtils.hsvToColor(hue, sat, value))
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(thumbColor = KromdeckSlateInk, activeTrackColor = KromdeckCoral),
          )

          Text(
            text = "Brightness (${(value * 100).toInt()}%)",
            style = MaterialTheme.typography.labelMedium,
            color = KromdeckSlateInk,
          )
          Slider(
            value = value,
            onValueChange = {
              value = it
              hexInput = ColorUtils.colorToHex(ColorUtils.hsvToColor(hue, sat, value))
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(thumbColor = KromdeckSlateInk, activeTrackColor = KromdeckAmber),
          )
        }

        // Quick Swatches
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Quick Swatches",
            style = MaterialTheme.typography.labelMedium,
            color = KromdeckSlateInk,
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            quickSwatches.take(6).forEach { hex ->
              val c = ColorUtils.parseHexColor(hex)
              Box(
                modifier =
                  Modifier.size(30.dp)
                    .clip(CircleShape)
                    .background(c)
                    .border(BorderStroke(1.5.dp, KromdeckSlateInk), CircleShape)
                    .clickable {
                      val (h, s, v) = ColorUtils.rgbToHsv(c)
                      hue = h
                      sat = s
                      value = v
                      hexInput = hex
                    },
              )
            }
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            quickSwatches.drop(6).forEach { hex ->
              val c = ColorUtils.parseHexColor(hex)
              Box(
                modifier =
                  Modifier.size(30.dp)
                    .clip(CircleShape)
                    .background(c)
                    .border(BorderStroke(1.5.dp, KromdeckSlateInk), CircleShape)
                    .clickable {
                      val (h, s, v) = ColorUtils.rgbToHsv(c)
                      hue = h
                      sat = s
                      value = v
                      hexInput = hex
                    },
              )
            }
          }
        }

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
        ) {
          TactileButton(
            onClick = onDismiss,
            containerColor = KromdeckCreamCanvas,
            contentColor = KromdeckSlateInk,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            testTag = "color_picker_cancel",
          ) {
            Text("Cancel", fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.width(8.dp))
          TactileButton(
            onClick = { onApply(currentHex) },
            containerColor = KromdeckEmerald,
            contentColor = Color.White,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            testTag = "color_picker_apply",
          ) {
            Text("Apply", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
