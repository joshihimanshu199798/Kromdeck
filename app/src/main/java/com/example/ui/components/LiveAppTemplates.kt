package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PresetDecks
import com.example.data.SampleWallpaper
import com.example.model.DiscoverCategory
import com.example.model.NavShellStyle
import com.example.model.PreviewTemplate
import com.example.model.ThemeDeck
import com.example.ui.theme.resolveFontPairing
import kotlin.math.cos
import kotlin.math.sin

/**
 * Live interactive preview frame rendering any of the 6 realistic app templates
 * using the active ThemeDeck's colors, surface physics, typography, gradients, and nav shell.
 */
@Composable
fun LiveTemplatePreviewFrame(
  deck: ThemeDeck,
  template: PreviewTemplate,
  modifier: Modifier = Modifier,
  compact: Boolean = false,
  brandAppName: String = "Kromdeck App",
  brandTagline: String = "Crafted tactile interface",
) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  val densityScale = deck.gridDensity.spacingMultiplier
  val outerShape = RoundedCornerShape((deck.cornerRadiusDp + 4).coerceIn(8, 28).dp)
  val rad = Math.toRadians(deck.gradientAngle.toDouble())

  val backdropModifier =
    if (deck.gradientEnabled) {
      Modifier.background(
        Brush.linearGradient(
          colors =
            listOf(
              colors.canvas,
              colors.primary.copy(alpha = if (deck.isDark) 0.16f else 0.10f),
              colors.secondary.copy(alpha = if (deck.isDark) 0.14f else 0.08f),
            ),
          start = Offset.Zero,
          end = Offset((cos(rad) * 600f).toFloat(), (sin(rad) * 600f).toFloat()),
        ),
      )
    } else {
      Modifier.background(colors.canvas)
    }

  Box(
    modifier =
      modifier
        .testTag("live_preview_frame_${template.name.lowercase()}")
        .clip(outerShape)
        .then(backdropModifier)
        .border(BorderStroke(deck.borderWidthDp.coerceAtLeast(1.5f).dp, colors.border), outerShape),
  ) {
    // Optional mesh glow overlay
    if (deck.gradientMeshGlow) {
      Canvas(modifier = Modifier.matchParentSize()) {
        drawCircle(
          brush =
            Brush.radialGradient(
              colors = listOf(colors.primary.copy(alpha = 0.22f), Color.Transparent),
              center = Offset(size.width * 0.85f, size.height * 0.15f),
              radius = size.width * 0.55f,
            ),
        )
        drawCircle(
          brush =
            Brush.radialGradient(
              colors = listOf(colors.secondary.copy(alpha = 0.18f), Color.Transparent),
              center = Offset(size.width * 0.12f, size.height * 0.82f),
              radius = size.width * 0.50f,
            ),
        )
      }
    }

    Column(
      modifier = Modifier.fillMaxWidth().padding((if (compact) 8f else 12f * densityScale).dp),
      verticalArrangement = Arrangement.spacedBy((if (compact) 8f else 10f * densityScale).dp),
    ) {
      // Top Nav Shell Bar (when TOP_TABS or MINIMAL_DRAWER is active)
      if (!compact && (deck.navShell == NavShellStyle.TOP_TABS || deck.navShell == NavShellStyle.MINIMAL_DRAWER)) {
        PreviewTopShellHeader(
          deck = deck,
          appName = brandAppName,
          tagline = brandTagline,
        )
      }

      when (template) {
        PreviewTemplate.MUSIC_PLAYER ->
          MusicRadioTemplate(deck, compact, brandAppName, brandTagline)
        PreviewTemplate.WALLPAPER_GALLERY ->
          PhotoGalleryTemplate(deck, compact, brandAppName)
        PreviewTemplate.SAAS_DASHBOARD ->
          SaasFinanceTemplate(deck, compact, brandAppName)
        PreviewTemplate.SOCIAL_FEED ->
          SocialFeedTemplate(deck, compact, brandAppName)
        PreviewTemplate.ECOMMERCE ->
          EcommerceTemplate(deck, compact, brandAppName)
        PreviewTemplate.TASKS_NOTES ->
          TasksNotesTemplate(deck, compact, brandAppName)
      }

      // Bottom Nav Shell Bar (when BOTTOM_PILL or FLOATING_DOCK is active)
      if (!compact && (deck.navShell == NavShellStyle.BOTTOM_PILL || deck.navShell == NavShellStyle.FLOATING_DOCK)) {
        PreviewBottomNavShell(deck = deck)
      }
    }
  }
}

@Composable
private fun PreviewTopShellHeader(
  deck: ThemeDeck,
  appName: String,
  tagline: String,
) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Overview", "Explore", "Activity")

  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (deck.navShell == NavShellStyle.MINIMAL_DRAWER) {
          Box(
            modifier =
              Modifier.size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.elevated)
                .border(1.dp, colors.border.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Filled.Menu, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(16.dp))
          }
        }
        Column {
          Text(
            text = appName,
            fontFamily = fonts.display,
            fontWeight = if (deck.headingWeightBold) FontWeight.ExtraBold else FontWeight.SemiBold,
            fontSize = (deck.baseFontSizeSp + 1f).sp,
            letterSpacing = deck.letterSpacingSp.sp,
            color = colors.textPrimary,
            maxLines = 1,
          )
          Text(
            text = tagline,
            fontFamily = fonts.body,
            fontSize = 10.sp,
            color = colors.textMuted,
            maxLines = 1,
          )
        }
      }
      Box(
        modifier =
          Modifier.size(26.dp)
            .clip(CircleShape)
            .background(colors.primary),
        contentAlignment = Alignment.Center,
      ) {
        Icon(Icons.Filled.Person, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(14.dp))
      }
    }

    if (deck.navShell == NavShellStyle.TOP_TABS) {
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        tabs.forEachIndexed { idx, label ->
          val active = idx == selectedTab
          Box(
            modifier =
              Modifier.clip(RoundedCornerShape(8.dp))
                .background(if (active) colors.primary else colors.elevated)
                .clickable { selectedTab = idx }
                .padding(horizontal = 10.dp, vertical = 5.dp),
          ) {
            Text(
              text = label,
              fontFamily = fonts.body,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (active) colors.onPrimary else colors.textPrimary,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PreviewBottomNavShell(deck: ThemeDeck) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  var selectedNav by remember { mutableIntStateOf(0) }
  val isDock = deck.navShell == NavShellStyle.FLOATING_DOCK
  val items = listOf("Home" to Icons.Filled.Home, "Search" to Icons.Filled.Search, "Profile" to Icons.Filled.Person)

  ThemedCard(
    deck = deck,
    backgroundOverride = if (isDock) colors.elevated else colors.surface,
    cornerRadiusOverride = if (isDock) 24.dp else deck.cornerRadiusDp.dp,
    modifier = Modifier.fillMaxWidth().padding(horizontal = if (isDock) 16.dp else 0.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      items.forEachIndexed { idx, (label, icon) ->
        val active = idx == selectedNav
        Row(
          modifier =
            Modifier.clip(RoundedCornerShape(999.dp))
              .background(if (active) colors.primary else Color.Transparent)
              .clickable { selectedNav = idx }
              .padding(horizontal = 10.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) colors.onPrimary else colors.textMuted,
            modifier = Modifier.size(14.dp),
          )
          if (active) {
            Text(
              text = label,
              fontFamily = fonts.body,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = colors.onPrimary,
            )
          }
        }
      }
    }
  }
}

/**
 * Fast, rich live mini-UI preview thumbnail rendered directly inside Discover Feed cards.
 */
@Composable
fun DiscoverMiniPreview(
  deck: ThemeDeck,
  modifier: Modifier = Modifier,
) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  val cardCorner = (deck.cornerRadiusDp * 0.7f).coerceIn(4f, 16f).dp

  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .height(142.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(
          if (deck.gradientEnabled) {
            Brush.linearGradient(
              listOf(
                colors.canvas,
                colors.primary.copy(alpha = if (deck.isDark) 0.24f else 0.14f),
                colors.secondary.copy(alpha = if (deck.isDark) 0.18f else 0.10f),
              ),
            )
          } else {
            Brush.linearGradient(listOf(colors.canvas, colors.canvas))
          },
        )
        .border(BorderStroke(1.5.dp, colors.border.copy(alpha = 0.7f)), RoundedCornerShape(10.dp))
        .padding(10.dp),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
      // Mini status + header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
          Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(colors.primary))
          Text(
            text =
              when (deck.category) {
                DiscoverCategory.MUSIC_RADIO -> "FM Studio"
                DiscoverCategory.PHOTO_GALLERY -> "Lens Roll"
                DiscoverCategory.SAAS_FINANCE -> "Ledger Pro"
                DiscoverCategory.SOCIAL_FEED -> "Pulse Feed"
                DiscoverCategory.ECOMMERCE -> "Craft Shop"
                else -> "Kanso Notes"
              },
            fontFamily = fonts.display,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            color = colors.textPrimary,
            maxLines = 1,
          )
        }
        Box(
          modifier =
            Modifier.clip(RoundedCornerShape(999.dp))
              .background(colors.secondary)
              .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
          Text(
            text = deck.physics.filterTag,
            fontFamily = fonts.mono,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSecondary,
            maxLines = 1,
          )
        }
      }

      // Mini Hero Card reflecting actual SurfacePhysics
      ThemedCard(
        deck = deck,
        backgroundOverride = colors.surface,
        cornerRadiusOverride = cardCorner,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
              text = "Aa Bb 24.8%",
              fontFamily = fonts.display,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 13.sp,
              color = colors.textPrimary,
              maxLines = 1,
            )
            Text(
              text = deck.fontPairing.label,
              fontFamily = fonts.body,
              fontSize = 9.sp,
              color = colors.textMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          Box(
            modifier =
              Modifier.clip(RoundedCornerShape(6.dp))
                .background(colors.primary)
                .padding(horizontal = 8.dp, vertical = 5.dp),
          ) {
            Text(
              text = "Action",
              fontFamily = fonts.body,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = colors.onPrimary,
            )
          }
        }
      }

      // Mini bottom row: metric bar + pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          modifier =
            Modifier.weight(1f)
              .height(24.dp)
              .clip(RoundedCornerShape(cardCorner))
              .background(colors.elevated)
              .padding(horizontal = 6.dp),
          contentAlignment = Alignment.CenterStart,
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Box(modifier = Modifier.weight(0.55f).height(6.dp).clip(CircleShape).background(colors.primary))
            Box(modifier = Modifier.weight(0.25f).height(6.dp).clip(CircleShape).background(colors.warm))
            Box(modifier = Modifier.weight(0.20f).height(6.dp).clip(CircleShape).background(colors.success))
          }
        }
        Box(
          modifier =
            Modifier.height(24.dp)
              .clip(RoundedCornerShape(cardCorner))
              .background(colors.success.copy(alpha = 0.22f))
              .border(1.dp, colors.success, RoundedCornerShape(cardCorner))
              .padding(horizontal = 8.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "LIVE",
            fontFamily = fonts.mono,
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.textPrimary,
          )
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Template 1: Music & Radio
// ---------------------------------------------------------------------------
@Composable
private fun MusicRadioTemplate(
  deck: ThemeDeck,
  compact: Boolean,
  brandAppName: String,
  brandTagline: String,
) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  val genres = listOf("All", "Synthwave", "Lo-Fi", "Podcast", "Ambient")

  var selectedGenre by remember { mutableStateOf("All") }
  var activeTrackIdx by remember { mutableIntStateOf(0) }
  var isPlaying by remember { mutableStateOf(true) }

  val filteredTracks =
    remember(selectedGenre) {
      if (selectedGenre == "All") PresetDecks.sampleTracks
      else PresetDecks.sampleTracks.filter { it.genre == selectedGenre }
    }
  val currentTrack = PresetDecks.sampleTracks.getOrElse(activeTrackIdx) { PresetDecks.sampleTracks.first() }
  val headingWeight = if (deck.headingWeightBold) FontWeight.ExtraBold else FontWeight.SemiBold

  Column(verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 10.dp)) {
    // Featured Radio Hero
    ThemedCard(
      deck = deck,
      backgroundOverride = colors.primary,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(if (compact) 9.dp else deck.cardPaddingDp.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "FEATURED MIX • ${brandAppName.uppercase()}",
            fontFamily = fonts.mono,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onPrimary.copy(alpha = 0.85f),
            maxLines = 1,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = currentTrack.title,
            fontFamily = fonts.display,
            fontWeight = headingWeight,
            fontSize = if (compact) 14.sp else (deck.baseFontSizeSp + 3f).sp,
            letterSpacing = deck.letterSpacingSp.sp,
            color = colors.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = if (compact) currentTrack.artist else "${currentTrack.artist} • $brandTagline",
            fontFamily = fonts.body,
            fontSize = if (compact) 10.sp else 12.sp,
            color = colors.onPrimary.copy(alpha = 0.88f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        currentTrack.artworkRes?.let { resId ->
          Image(
            painter = painterResource(resId),
            contentDescription = currentTrack.title,
            contentScale = ContentScale.Crop,
            modifier =
              Modifier.size(if (compact) 40.dp else 54.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.5.dp, colors.border, RoundedCornerShape(10.dp)),
          )
        }
      }
    }

    // Genre Pills
    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      genres.forEach { genre ->
        val isSel = genre == selectedGenre
        Box(
          modifier =
            Modifier.clip(RoundedCornerShape(999.dp))
              .background(if (isSel) colors.secondary else colors.surface)
              .border(1.5.dp, colors.border, RoundedCornerShape(999.dp))
              .clickable { selectedGenre = genre }
              .padding(horizontal = if (compact) 8.dp else 11.dp, vertical = if (compact) 4.dp else 6.dp),
        ) {
          Text(
            text = genre,
            fontFamily = fonts.body,
            fontSize = if (compact) 10.sp else 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSel) colors.onSecondary else colors.textPrimary,
          )
        }
      }
    }

    // Track Rows
    val visibleTracks = if (compact) filteredTracks.take(2) else filteredTracks
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      visibleTracks.forEach { track ->
        val trackIndex = PresetDecks.sampleTracks.indexOf(track).coerceAtLeast(0)
        val isActive = trackIndex == activeTrackIdx
        ThemedCard(
          deck = deck,
          backgroundOverride = if (isActive) colors.elevated else colors.surface,
          onClick = {
            activeTrackIdx = trackIndex
            isPlaying = true
          },
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(if (compact) 7.dp else 9.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            track.artworkRes?.let { resId ->
              Image(
                painter = painterResource(resId),
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier =
                  Modifier.size(if (compact) 30.dp else 38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp)),
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = track.title,
                fontFamily = fonts.display,
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 11.sp else 13.sp,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
              Text(
                text = "${track.artist} • ${track.duration}",
                fontFamily = fonts.body,
                fontSize = if (compact) 9.sp else 11.sp,
                color = colors.textMuted,
                maxLines = 1,
              )
            }
            Box(
              modifier =
                Modifier.clip(RoundedCornerShape(6.dp))
                  .background(if (isActive) colors.primary else colors.warm.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 3.dp),
            ) {
              Text(
                text = if (isActive && isPlaying) "PLAYING" else track.badge,
                fontFamily = fonts.mono,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) colors.onPrimary else colors.textPrimary,
              )
            }
          }
        }
      }
    }

    // Mini Player Bar
    ThemedCard(
      deck = deck,
      backgroundOverride = colors.elevated,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = currentTrack.title,
            fontFamily = fonts.display,
            fontWeight = FontWeight.Bold,
            fontSize = if (compact) 10.sp else 12.sp,
            color = colors.textPrimary,
            maxLines = 1,
          )
          Text(
            text = if (isPlaying) "Now Playing • 24-bit" else "Paused",
            fontFamily = fonts.mono,
            fontSize = 9.sp,
            color = colors.success,
          )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Box(
            modifier =
              Modifier.size(32.dp)
                .clip(CircleShape)
                .background(colors.primary)
                .border(1.5.dp, colors.border, CircleShape)
                .clickable { isPlaying = !isPlaying },
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
              contentDescription = "Play or pause track",
              tint = colors.onPrimary,
              modifier = Modifier.size(16.dp),
            )
          }
          Box(
            modifier =
              Modifier.size(32.dp)
                .clip(CircleShape)
                .background(colors.surface)
                .border(1.5.dp, colors.border, CircleShape)
                .clickable { activeTrackIdx = (activeTrackIdx + 1) % PresetDecks.sampleTracks.size },
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Filled.SkipNext,
              contentDescription = "Next track",
              tint = colors.textPrimary,
              modifier = Modifier.size(16.dp),
            )
          }
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Template 2: Photo & Gallery (with interactive Lightbox overlay)
// ---------------------------------------------------------------------------
@Composable
private fun PhotoGalleryTemplate(
  deck: ThemeDeck,
  compact: Boolean,
  brandAppName: String,
) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  var selectedIds by remember { mutableStateOf(setOf("w1")) }
  var filterTag by remember { mutableStateOf("All") }
  var lightboxItem by remember { mutableStateOf<SampleWallpaper?>(null) }

  val tags = listOf("All", "Minimal", "Vector", "Dark", "Photo")
  val visibleWallpapers =
    remember(filterTag, compact) {
      val list =
        if (filterTag == "All") PresetDecks.sampleWallpapers
        else PresetDecks.sampleWallpapers.filter { it.tag == filterTag }
      if (compact) list.take(2) else list
    }

  Column(verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 9.dp)) {
    // Search & Tag Filter Bar
    ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(Icons.Filled.Search, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Search $brandAppName curated shots...",
          fontFamily = fonts.body,
          fontSize = if (compact) 10.sp else 12.sp,
          color = colors.textMuted,
          modifier = Modifier.weight(1f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Box(
          modifier =
            Modifier.clip(RoundedCornerShape(6.dp))
              .background(colors.primary)
              .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
          Text(
            text = "4K RAW",
            fontFamily = fonts.mono,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onPrimary,
          )
        }
      }
    }

    if (!compact) {
      Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        tags.forEach { tag ->
          val sel = tag == filterTag
          Box(
            modifier =
              Modifier.clip(RoundedCornerShape(999.dp))
                .background(if (sel) colors.primary else colors.surface)
                .border(1.dp, colors.border.copy(alpha = 0.6f), RoundedCornerShape(999.dp))
                .clickable { filterTag = tag }
                .padding(horizontal = 10.dp, vertical = 4.dp),
          ) {
            Text(
              text = tag,
              fontFamily = fonts.body,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (sel) colors.onPrimary else colors.textPrimary,
            )
          }
        }
      }
    }

    // Lightbox Preview Banner when tapped
    if (lightboxItem != null && !compact) {
      val item = lightboxItem!!
      ThemedCard(deck = deck, backgroundOverride = colors.elevated, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Lightbox • ${item.title}",
              fontFamily = fonts.display,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = colors.textPrimary,
            )
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "Close lightbox",
              tint = colors.textPrimary,
              modifier = Modifier.size(18.dp).clickable { lightboxItem = null },
            )
          }
          item.imageRes?.let { resId ->
            Image(
              painter = painterResource(resId),
              contentDescription = item.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)),
            )
          }
        }
      }
    }

    // 2-Column Masonry/Grid
    visibleWallpapers.chunked(2).forEach { rowItems ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        rowItems.forEach { item ->
          val isSelected = item.id in selectedIds
          ThemedCard(
            deck = deck,
            onClick = { lightboxItem = item },
            modifier = Modifier.weight(1f),
          ) {
            Column {
              Box(modifier = Modifier.fillMaxWidth().height(if (compact) 68.dp else 90.dp)) {
                item.imageRes?.let { resId ->
                  Image(
                    painter = painterResource(resId),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                  )
                }
                Box(
                  modifier =
                    Modifier.align(Alignment.TopStart)
                      .padding(6.dp)
                      .clip(RoundedCornerShape(6.dp))
                      .background(colors.canvas.copy(alpha = 0.88f))
                      .border(1.dp, colors.border, RoundedCornerShape(6.dp))
                      .padding(horizontal = 5.dp, vertical = 2.dp),
                ) {
                  Text(
                    text = item.resolution,
                    fontFamily = fonts.mono,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.textPrimary,
                  )
                }
                Box(
                  modifier =
                    Modifier.align(Alignment.TopEnd)
                      .padding(6.dp)
                      .size(22.dp)
                      .clip(CircleShape)
                      .background(if (isSelected) colors.success else colors.surface.copy(alpha = 0.85f))
                      .border(1.5.dp, colors.border, CircleShape)
                      .clickable {
                        selectedIds = if (isSelected) selectedIds - item.id else selectedIds + item.id
                      },
                  contentAlignment = Alignment.Center,
                ) {
                  if (isSelected) {
                    Icon(Icons.Filled.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(13.dp))
                  }
                }
              }
              Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = item.title,
                    fontFamily = fonts.display,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 10.sp else 11.sp,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                  Text(
                    text = item.author,
                    fontFamily = fonts.body,
                    fontSize = 9.sp,
                    color = colors.textMuted,
                    maxLines = 1,
                  )
                }
                if (item.verified) {
                  Icon(Icons.Filled.Verified, contentDescription = "Verified", tint = colors.primary, modifier = Modifier.size(14.dp))
                }
              }
            }
          }
        }
      }
    }

    // Bulk Download Bar
    ThemedCard(
      deck = deck,
      backgroundOverride = colors.primary,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = "${selectedIds.size} selected • Tap image for lightbox",
          fontFamily = fonts.body,
          fontSize = if (compact) 10.sp else 11.sp,
          fontWeight = FontWeight.Bold,
          color = colors.onPrimary,
        )
        Row(
          modifier =
            Modifier.clip(RoundedCornerShape(8.dp))
              .background(colors.warm)
              .clickable { selectedIds = emptySet() }
              .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(Icons.Filled.Download, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Save", fontFamily = fonts.display, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Template 3: SaaS & Finance (Balance Card, Interactive Chart, Quick Actions, Transactions)
// ---------------------------------------------------------------------------
@Composable
private fun SaasFinanceTemplate(
  deck: ThemeDeck,
  compact: Boolean,
  brandAppName: String,
) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  var period by remember { mutableStateOf("30D") }
  val metrics = PresetDecks.sampleSaasMetrics[period].orEmpty()
  val chartPoints =
    when (period) {
      "7D" -> listOf(0.42f, 0.56f, 0.50f, 0.70f, 0.65f, 0.85f, 0.92f)
      "90D" -> listOf(0.25f, 0.35f, 0.48f, 0.52f, 0.68f, 0.78f, 0.96f)
      else -> listOf(0.32f, 0.44f, 0.60f, 0.55f, 0.74f, 0.82f, 0.90f)
    }

  Column(verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 9.dp)) {
    // Balance & Period Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = "$brandAppName Ledger",
        fontFamily = fonts.display,
        fontWeight = FontWeight.ExtraBold,
        fontSize = if (compact) 12.sp else (deck.baseFontSizeSp + 1f).sp,
        color = colors.textPrimary,
        maxLines = 1,
      )
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf("7D", "30D", "90D").forEach { p ->
          val sel = p == period
          Box(
            modifier =
              Modifier.clip(RoundedCornerShape(6.dp))
                .background(if (sel) colors.primary else colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(6.dp))
                .clickable { period = p }
                .padding(horizontal = 7.dp, vertical = 3.dp),
          ) {
            Text(
              text = p,
              fontFamily = fonts.mono,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = if (sel) colors.onPrimary else colors.textPrimary,
            )
          }
        }
      }
    }

    // KPI Cards
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      metrics.forEach { m ->
        ThemedCard(deck = deck, modifier = Modifier.weight(1f)) {
          Column(modifier = Modifier.padding(if (compact) 6.dp else 9.dp)) {
            Text(m.label, fontFamily = fonts.body, fontSize = 9.sp, color = colors.textMuted, maxLines = 1)
            Text(
              text = m.value,
              fontFamily = fonts.display,
              fontWeight = FontWeight.ExtraBold,
              fontSize = if (compact) 12.sp else 14.sp,
              color = colors.textPrimary,
              maxLines = 1,
            )
            Text(
              text = m.delta,
              fontFamily = fonts.mono,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = if (m.isPositive) colors.success else colors.secondary,
            )
          }
        }
      }
    }

    // Sparkline Chart Card
    if (!compact) {
      ThemedCard(deck = deck, backgroundOverride = colors.elevated, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text("Cashflow Velocity ($period)", fontFamily = fonts.display, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(
                modifier =
                  Modifier.clip(RoundedCornerShape(6.dp))
                    .background(colors.primary)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                  Icon(Icons.Filled.Send, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(11.dp))
                  Text("Transfer", fontFamily = fonts.body, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.onPrimary)
                }
              }
            }
          }
          Canvas(modifier = Modifier.fillMaxWidth().height(54.dp)) {
            val stepX = size.width / (chartPoints.size - 1)
            val path = Path()
            chartPoints.forEachIndexed { idx, v ->
              val x = idx * stepX
              val y = size.height * (1f - v)
              if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
              path = path,
              color = colors.primary,
              style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )
            chartPoints.forEachIndexed { idx, v ->
              drawCircle(
                color = colors.secondary,
                radius = 4.dp.toPx(),
                center = Offset(idx * stepX, size.height * (1f - v)),
              )
            }
          }
        }
      }
    }

    // Transactions List
    val rows = if (compact) PresetDecks.sampleSaasRows.take(2) else PresetDecks.sampleSaasRows
    ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { row ->
          val statusColor =
            when (row.status) {
              "Settled" -> colors.success
              "Pending" -> colors.warm
              else -> colors.secondary
            }
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = row.workspace,
                fontFamily = fonts.display,
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 10.sp else 12.sp,
                color = colors.textPrimary,
                maxLines = 1,
              )
              Text(
                text = "${row.plan} • ${row.usagePct}% quota",
                fontFamily = fonts.body,
                fontSize = 9.sp,
                color = colors.textMuted,
              )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(
                modifier =
                  Modifier.clip(RoundedCornerShape(999.dp))
                    .background(statusColor.copy(alpha = 0.2f))
                    .border(1.dp, statusColor, RoundedCornerShape(999.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
              ) {
                Text(row.status, fontFamily = fonts.mono, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
              }
              Text(
                text = row.mrr,
                fontFamily = fonts.mono,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (compact) 10.sp else 11.sp,
                color = colors.textPrimary,
              )
            }
          }
        }
      }
    }
  }
}
