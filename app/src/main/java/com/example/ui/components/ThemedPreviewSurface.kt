package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.MotionFeel
import com.example.model.SurfacePhysics
import com.example.model.ThemeDeck
import com.example.util.ColorUtils

data class ResolvedDeckColors(
  val canvas: Color,
  val surface: Color,
  val elevated: Color,
  val border: Color,
  val textPrimary: Color,
  val textMuted: Color,
  val primary: Color,
  val secondary: Color,
  val warm: Color,
  val success: Color,
  val onPrimary: Color,
  val onSecondary: Color,
)

fun ThemeDeck.resolveColors(): ResolvedDeckColors {
  val primaryColor = ColorUtils.parseHexColor(palette.primaryHex)
  val secondaryColor = ColorUtils.parseHexColor(palette.secondaryHex)
  return ResolvedDeckColors(
    canvas = ColorUtils.parseHexColor(palette.canvasHex),
    surface = ColorUtils.parseHexColor(palette.surfaceHex),
    elevated = ColorUtils.parseHexColor(palette.elevatedSurfaceHex, ColorUtils.parseHexColor(palette.surfaceHex)),
    border = ColorUtils.parseHexColor(palette.borderHex),
    textPrimary = ColorUtils.parseHexColor(palette.textPrimaryHex),
    textMuted = ColorUtils.parseHexColor(palette.textMutedHex),
    primary = primaryColor,
    secondary = secondaryColor,
    warm = ColorUtils.parseHexColor(palette.warmHex),
    success = ColorUtils.parseHexColor(palette.successHex),
    onPrimary = ColorUtils.bestOnColor(primaryColor),
    onSecondary = ColorUtils.bestOnColor(secondaryColor),
  )
}

@Composable
fun ThemedCard(
  deck: ThemeDeck,
  modifier: Modifier = Modifier,
  backgroundOverride: Color? = null,
  onClick: (() -> Unit)? = null,
  cornerRadiusOverride: Dp? = null,
  content: @Composable BoxScope.() -> Unit,
) {
  val colors = deck.resolveColors()
  val bgColor = backgroundOverride ?: colors.surface
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()

  val corner = cornerRadiusOverride ?: deck.cornerRadiusDp.dp
  val shape = RoundedCornerShape(corner)
  val bw = deck.borderWidthDp.coerceIn(0f, 3f).dp
  val shadowScale = deck.shadowIntensity.coerceIn(0f, 1.5f)

  val animShift by
    animateDpAsState(
      targetValue =
        if (pressed && onClick != null && deck.physics == SurfacePhysics.HARD_INK) 2.dp else 0.dp,
      animationSpec =
        when (deck.motionFeel) {
          MotionFeel.HARD_SNAP -> tween(65)
          MotionFeel.SPRING_BOUNCE -> spring(dampingRatio = Spring.DampingRatioMediumBouncy)
          MotionFeel.SOFT_GLOW -> tween(220)
          MotionFeel.SILKY_SMOOTH -> tween(350)
        },
      label = "themedShift",
    )

  val animScale by
    animateFloatAsState(
      targetValue =
        when {
          !pressed || onClick == null -> 1f
          deck.motionFeel == MotionFeel.SPRING_BOUNCE -> 0.96f
          deck.motionFeel == MotionFeel.SILKY_SMOOTH -> 0.98f
          deck.motionFeel == MotionFeel.SOFT_GLOW -> 0.985f
          else -> 0.99f
        },
      animationSpec =
        when (deck.motionFeel) {
          MotionFeel.HARD_SNAP -> tween(75)
          MotionFeel.SPRING_BOUNCE ->
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
          MotionFeel.SOFT_GLOW -> tween(220)
          MotionFeel.SILKY_SMOOTH -> tween(350)
        },
      label = "themedScale",
    )

  val clickModifier: Modifier.() -> Modifier = {
    if (onClick != null) {
      clickable(
        interactionSource = interactionSource,
        indication = null,
        role = Role.Button,
        onClick = onClick,
      )
    } else {
      this
    }
  }

  when (deck.physics) {
    SurfacePhysics.HARD_INK -> {
      val offsetDp = (3.5f * shadowScale).coerceIn(0f, 6f).dp
      Box(modifier = modifier.padding(end = offsetDp, bottom = offsetDp)) {
        if (offsetDp > 0.dp) {
          Box(
            modifier =
              Modifier.matchParentSize()
                .offset(x = offsetDp, y = offsetDp)
                .clip(shape)
                .background(colors.border),
          )
        }
        Box(
          modifier =
            Modifier.offset(x = animShift, y = animShift)
              .graphicsLayer { scaleX = animScale; scaleY = animScale }
              .clip(shape)
              .background(bgColor)
              .let { m -> if (bw > 0.dp) m.border(BorderStroke(bw, colors.border), shape) else m }
              .clickModifier(),
          content = content,
        )
      }
    }

    SurfacePhysics.SOFT_NEUMORPHIC -> {
      Box(
        modifier =
          modifier
            .graphicsLayer { scaleX = animScale; scaleY = animScale }
            .shadow(elevation = (7f * shadowScale).dp, shape = shape, ambientColor = colors.border.copy(alpha = 0.25f))
            .clip(shape)
            .background(Brush.linearGradient(listOf(bgColor, bgColor.copy(alpha = 0.92f))))
            .let { m -> if (bw > 0.dp) m.border(BorderStroke(bw.coerceAtMost(1.2.dp), colors.border.copy(alpha = 0.16f)), shape) else m }
            .clickModifier(),
        content = content,
      )
    }

    SurfacePhysics.FROSTED_GLASS -> {
      val glassColor = bgColor.copy(alpha = deck.glassBlurAlpha.coerceIn(0.45f, 0.95f))
      val glowHighlight =
        if (pressed) colors.primary.copy(alpha = 0.8f)
        else colors.border.copy(alpha = 0.48f)
      Box(
        modifier =
          modifier
            .graphicsLayer { scaleX = animScale; scaleY = animScale }
            .shadow(elevation = (8f * shadowScale).dp, shape = shape, spotColor = colors.primary.copy(alpha = 0.28f))
            .clip(shape)
            .background(Brush.verticalGradient(listOf(glassColor, glassColor.copy(alpha = glassColor.alpha * 0.84f))))
            .let { m -> if (bw > 0.dp) m.border(BorderStroke(bw.coerceAtMost(1.8.dp), glowHighlight), shape) else m }
            .clickModifier(),
        content = content,
      )
    }

    SurfacePhysics.ULTRA_THIN -> {
      Box(
        modifier =
          modifier
            .graphicsLayer { scaleX = animScale; scaleY = animScale }
            .clip(shape)
            .background(bgColor)
            .border(BorderStroke(bw.coerceAtLeast(1.dp), colors.border.copy(alpha = 0.42f)), shape)
            .clickModifier(),
        content = content,
      )
    }

    SurfacePhysics.PUFFY_CLAY -> {
      Box(
        modifier =
          modifier
            .graphicsLayer { scaleX = animScale; scaleY = animScale }
            .shadow(elevation = (10f * shadowScale).dp, shape = shape, ambientColor = colors.border.copy(alpha = 0.28f))
            .clip(shape)
            .background(bgColor)
            .let { m -> if (bw > 0.dp) m.border(BorderStroke(bw, colors.border.copy(alpha = 0.16f)), shape) else m }
            .clickModifier(),
        content = content,
      )
    }

    SurfacePhysics.FLAT -> {
      Box(
        modifier =
          modifier
            .graphicsLayer { scaleX = animScale; scaleY = animScale }
            .clip(shape)
            .background(bgColor)
            .clickModifier(),
        content = content,
      )
    }
  }
}
