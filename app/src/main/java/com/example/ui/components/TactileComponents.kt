package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KromdeckAmber
import com.example.ui.theme.KromdeckCoral
import com.example.ui.theme.KromdeckEmerald
import com.example.ui.theme.KromdeckIndigo
import com.example.ui.theme.KromdeckPureSurface
import com.example.ui.theme.KromdeckSlateInk

/**
 * Custom fanned 4-card swatch deck logo mark for Kromdeck.
 */
@Composable
fun KromdeckLogoMark(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(36.dp)) {
    val cardW = size.width * 0.44f
    val cardH = size.height * 0.62f
    val pivot = Offset(size.width * 0.45f, size.height * 0.85f)
    val radius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
    val strokeW = 1.8.dp.toPx()

    val cards =
      listOf(
        -22f to KromdeckEmerald,
        -8f to KromdeckAmber,
        7f to KromdeckCoral,
        22f to KromdeckIndigo,
      )

    cards.forEach { (angle, fill) ->
      rotate(degrees = angle, pivot = pivot) {
        val topLeft = Offset(pivot.x - cardW / 2f, pivot.y - cardH)
        drawRoundRect(
          color = KromdeckSlateInk,
          topLeft = topLeft + Offset(1.5.dp.toPx(), 1.5.dp.toPx()),
          size = Size(cardW, cardH),
          cornerRadius = radius,
        )
        drawRoundRect(
          color = fill,
          topLeft = topLeft,
          size = Size(cardW, cardH),
          cornerRadius = radius,
        )
        drawRoundRect(
          color = KromdeckSlateInk,
          topLeft = topLeft,
          size = Size(cardW, cardH),
          cornerRadius = radius,
          style = Stroke(width = strokeW),
        )
      }
    }
  }
}

/**
 * Tactile card with crisp 2dp slate ink border and 4dp hard offset shadow.
 */
@Composable
fun TactileCard(
  modifier: Modifier = Modifier,
  containerColor: Color = KromdeckPureSurface,
  borderColor: Color = KromdeckSlateInk,
  shadowColor: Color = KromdeckSlateInk,
  cornerRadius: Dp = 14.dp,
  shadowOffset: Dp = 4.dp,
  onClick: (() -> Unit)? = null,
  testTag: String? = null,
  content: @Composable BoxScope.() -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val pressShift by
    animateDpAsState(
      targetValue = if (pressed && onClick != null) 2.dp else 0.dp,
      animationSpec = tween(durationMillis = 75),
      label = "tactileCardShift",
    )
  val shape = RoundedCornerShape(cornerRadius)

  val baseMod =
    if (testTag != null) modifier.testTag(testTag) else modifier

  Box(modifier = baseMod.padding(end = shadowOffset, bottom = shadowOffset)) {
    // Hard offset shadow layer
    Box(
      modifier =
        Modifier.matchParentSize()
          .offset(x = shadowOffset, y = shadowOffset)
          .clip(shape)
          .background(shadowColor),
    )

    val cardModifier =
      Modifier.offset(x = pressShift, y = pressShift)
        .clip(shape)
        .background(containerColor)
        .border(BorderStroke(2.dp, borderColor), shape)
        .let { m ->
          if (onClick != null) {
            m.clickable(
              interactionSource = interactionSource,
              indication = null,
              role = Role.Button,
              onClick = onClick,
            )
          } else {
            m
          }
        }

    Box(modifier = cardModifier, content = content)
  }
}

/**
 * Tactile button with 2dp slate ink border, hard offset shadow, and 2dp active press physics.
 */
@Composable
fun TactileButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  containerColor: Color = KromdeckIndigo,
  contentColor: Color = Color.White,
  borderColor: Color = KromdeckSlateInk,
  shadowOffset: Dp = 3.dp,
  cornerRadius: Dp = 12.dp,
  enabled: Boolean = true,
  contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
  testTag: String = "tactile_button",
  content: @Composable RowScope.() -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val shift by
    animateDpAsState(
      targetValue = if (pressed && enabled) 2.dp else 0.dp,
      animationSpec = tween(durationMillis = 70),
      label = "tactileBtnShift",
    )
  val shape = RoundedCornerShape(cornerRadius)
  val effectiveContainer = if (enabled) containerColor else Color(0xFFE2E8F0)
  val effectiveContent = if (enabled) contentColor else Color(0xFF94A3B8)

  Box(
    modifier =
      modifier
        .testTag(testTag)
        .defaultMinSize(minHeight = 48.dp)
        .padding(end = shadowOffset, bottom = shadowOffset),
  ) {
    Box(
      modifier =
        Modifier.matchParentSize()
          .offset(x = shadowOffset, y = shadowOffset)
          .clip(shape)
          .background(if (enabled) borderColor else Color(0xFF94A3B8)),
    )
    Surface(
      color = effectiveContainer,
      contentColor = effectiveContent,
      shape = shape,
      border = BorderStroke(2.dp, if (enabled) borderColor else Color(0xFF94A3B8)),
      modifier =
        Modifier.offset(x = shift, y = shift)
          .defaultMinSize(minHeight = 44.dp)
          .clip(shape)
          .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            role = Role.Button,
            onClick = onClick,
          ),
    ) {
      Row(
        modifier = Modifier.padding(contentPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
      )
    }
  }
}

@Composable
fun TactilePillChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  activeColor: Color = KromdeckIndigo,
  testTag: String = "tactile_chip",
) {
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val shift by
    animateDpAsState(
      targetValue = if (pressed) 1.5.dp else 0.dp,
      animationSpec = tween(durationMillis = 65),
      label = "pillShift",
    )
  val shape = RoundedCornerShape(10.dp)
  val shadow = if (selected) 2.dp else 0.dp

  Box(
    modifier =
      modifier
        .testTag(testTag)
        .defaultMinSize(minHeight = 48.dp)
        .padding(end = shadow, bottom = shadow),
    contentAlignment = Alignment.Center,
  ) {
    if (selected) {
      Box(
        modifier =
          Modifier.matchParentSize()
            .padding(vertical = 4.dp)
            .offset(x = shadow, y = shadow)
            .clip(shape)
            .background(KromdeckSlateInk),
      )
    }
    Box(
      modifier =
        Modifier.offset(x = shift, y = shift)
          .defaultMinSize(minHeight = 40.dp)
          .clip(shape)
          .background(if (selected) activeColor else KromdeckPureSurface)
          .border(BorderStroke(2.dp, KromdeckSlateInk), shape)
          .clickable(
            interactionSource = interactionSource,
            indication = null,
            role = Role.Tab,
            onClick = onClick,
          )
          .padding(horizontal = 12.dp, vertical = 8.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = if (selected) Color.White else KromdeckSlateInk,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}
