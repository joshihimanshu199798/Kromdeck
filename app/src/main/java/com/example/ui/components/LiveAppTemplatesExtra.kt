package com.example.ui.components

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PresetDecks
import com.example.model.ThemeDeck
import com.example.ui.theme.resolveFontPairing

// ---------------------------------------------------------------------------
// Template 4: Social & Feed (Stories Row, Interactive Posts, Composer Pill)
// ---------------------------------------------------------------------------
@Composable
fun SocialFeedTemplate(
  deck: ThemeDeck,
  compact: Boolean,
  brandAppName: String,
) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  var likedIds by remember { mutableStateOf(setOf("p1")) }
  val stories = listOf("Maya", "Arjun", "Elena", "Kenji", "Sora")

  Column(verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 10.dp)) {
    // Stories Row
    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      stories.forEachIndexed { idx, name ->
        val ringColor = if (idx % 2 == 0) colors.primary else colors.secondary
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
          Box(
            modifier =
              Modifier.size(if (compact) 34.dp else 44.dp)
                .clip(CircleShape)
                .border(2.dp, ringColor, CircleShape)
                .padding(3.dp)
                .clip(CircleShape)
                .background(colors.elevated),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = name.take(1),
              fontFamily = fonts.display,
              fontWeight = FontWeight.ExtraBold,
              fontSize = if (compact) 11.sp else 14.sp,
              color = colors.textPrimary,
            )
          }
          Text(name, fontFamily = fonts.body, fontSize = 9.sp, color = colors.textMuted)
        }
      }
    }

    // Feed Posts
    val visiblePosts = if (compact) PresetDecks.sampleSocialPosts.take(1) else PresetDecks.sampleSocialPosts
    visiblePosts.forEach { post ->
      val liked = post.id in likedIds
      ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(if (compact) 8.dp else 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(
                modifier = Modifier.size(26.dp).clip(CircleShape).background(colors.primary),
                contentAlignment = Alignment.Center,
              ) {
                Text(post.author.take(1), color = colors.onPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
              Column {
                Text(post.author, fontFamily = fonts.display, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.textPrimary)
                Text("${post.handle} • ${post.timeAgo}", fontFamily = fonts.body, fontSize = 9.sp, color = colors.textMuted)
              }
            }
          }
          Text(
            text = post.body,
            fontFamily = fonts.body,
            fontSize = if (compact) 10.sp else 12.sp,
            color = colors.textPrimary,
            maxLines = if (compact) 2 else 3,
            overflow = TextOverflow.Ellipsis,
          )
          post.imageRes?.let { resId ->
            Image(
              painter = painterResource(resId),
              contentDescription = post.author,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxWidth().height(if (compact) 68.dp else 96.dp).clip(RoundedCornerShape(8.dp)),
            )
          }
          Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(
              modifier =
                Modifier.clip(RoundedCornerShape(999.dp))
                  .clickable { likedIds = if (liked) likedIds - post.id else likedIds + post.id }
                  .padding(vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Like post",
                tint = if (liked) colors.secondary else colors.textMuted,
                modifier = Modifier.size(16.dp),
              )
              Text(
                text = "${post.likes + if (liked) 1 else 0}",
                fontFamily = fonts.mono,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
              )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(15.dp))
              Text("${post.comments}", fontFamily = fonts.mono, fontSize = 10.sp, color = colors.textMuted)
            }
          }
        }
      }
    }

    // Composer Pill
    ThemedCard(deck = deck, backgroundOverride = colors.elevated, modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text("Share a design update on $brandAppName...", fontFamily = fonts.body, fontSize = 10.sp, color = colors.textMuted, maxLines = 1)
        Box(
          modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(colors.primary).padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
          Text("Post", fontFamily = fonts.display, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.onPrimary)
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Template 5: E-Commerce (Product Showcase, Size/Color Picker, Add to Cart CTA)
// ---------------------------------------------------------------------------
@Composable
fun EcommerceTemplate(
  deck: ThemeDeck,
  compact: Boolean,
  brandAppName: String,
) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  var selectedProductIdx by remember { mutableIntStateOf(0) }
  var selectedSizeIdx by remember { mutableIntStateOf(1) }
  var cartCount by remember { mutableIntStateOf(1) }
  val product = PresetDecks.sampleProducts[selectedProductIdx]

  Column(verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 10.dp)) {
    // Storefront Header + Bag Counter
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        PresetDecks.sampleProducts.forEachIndexed { idx, p ->
          val active = idx == selectedProductIdx
          Box(
            modifier =
              Modifier.clip(RoundedCornerShape(8.dp))
                .background(if (active) colors.primary else colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                .clickable {
                  selectedProductIdx = idx
                  selectedSizeIdx = 0
                }
                .padding(horizontal = 8.dp, vertical = 4.dp),
          ) {
            Text(
              text = p.name.split(" ").first(),
              fontFamily = fonts.body,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (active) colors.onPrimary else colors.textPrimary,
            )
          }
        }
      }
      Row(
        modifier =
          Modifier.clip(RoundedCornerShape(999.dp))
            .background(colors.elevated)
            .border(1.dp, colors.border, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = colors.primary, modifier = Modifier.size(13.dp))
        Text("$cartCount", fontFamily = fonts.mono, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = colors.textPrimary)
      }
    }

    // Showcase Product Card
    ThemedCard(deck = deck, modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(if (compact) 8.dp else 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(if (compact) 82.dp else 118.dp)) {
          Image(
            painter = painterResource(product.imageRes),
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize().clip(RoundedCornerShape(10.dp)),
          )
          Box(
            modifier =
              Modifier.align(Alignment.TopStart)
                .padding(6.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(colors.secondary)
                .padding(horizontal = 7.dp, vertical = 3.dp),
          ) {
            Text(product.badge, fontFamily = fonts.mono, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = colors.onSecondary)
          }
          Box(
            modifier =
              Modifier.align(Alignment.BottomEnd)
                .padding(6.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(colors.surface.copy(alpha = 0.92f))
                .padding(horizontal = 6.dp, vertical = 2.dp),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
              Icon(Icons.Filled.Star, contentDescription = null, tint = colors.warm, modifier = Modifier.size(11.dp))
              Text(product.rating, fontFamily = fonts.mono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = product.name,
              fontFamily = fonts.display,
              fontWeight = FontWeight.ExtraBold,
              fontSize = if (compact) 12.sp else (deck.baseFontSizeSp + 1f).sp,
              color = colors.textPrimary,
              maxLines = 1,
            )
            Text(
              text = "$brandAppName • ${product.subtitle}",
              fontFamily = fonts.body,
              fontSize = 10.sp,
              color = colors.textMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          Text(
            text = product.price,
            fontFamily = fonts.mono,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (compact) 14.sp else 16.sp,
            color = colors.primary,
          )
        }

        // Size & Variant Selectors
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          product.sizes.forEachIndexed { idx, sizeLabel ->
            val sel = idx == selectedSizeIdx
            Box(
              modifier =
                Modifier.clip(RoundedCornerShape(8.dp))
                  .background(if (sel) colors.primary else colors.elevated)
                  .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                  .clickable { selectedSizeIdx = idx }
                  .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
              Text(
                text = sizeLabel,
                fontFamily = fonts.body,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (sel) colors.onPrimary else colors.textPrimary,
              )
            }
          }
        }

        // Add to Cart CTA
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(colors.primary)
              .border(1.5.dp, colors.border, RoundedCornerShape(10.dp))
              .clickable { cartCount++ }
              .padding(vertical = 9.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "Add to Bag — ${product.price}",
            fontFamily = fonts.display,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = colors.onPrimary,
          )
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Template 6: Tasks & Notes (Progress Ring, Priority Tags, Interactive Checkboxes, FAB)
// ---------------------------------------------------------------------------
@Composable
fun TasksNotesTemplate(
  deck: ThemeDeck,
  compact: Boolean,
  brandAppName: String,
) {
  val colors = deck.resolveColors()
  val fonts = resolveFontPairing(deck.fontPairing)
  var completedIds by remember {
    mutableStateOf(PresetDecks.sampleTasks.filter { it.done }.map { it.id }.toSet())
  }
  val total = PresetDecks.sampleTasks.size
  val completedCount = completedIds.size
  val progress = completedCount.toFloat() / total.toFloat()

  Column(verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 9.dp)) {
    // Progress Ring Summary Card
    ThemedCard(deck = deck, backgroundOverride = colors.elevated, modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(if (compact) 8.dp else 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.matchParentSize()) {
              drawArc(
                color = colors.border.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round),
              )
              drawArc(
                color = colors.success,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round),
              )
            }
            Text(
              text = "${(progress * 100).toInt()}%",
              fontFamily = fonts.mono,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = colors.textPrimary,
            )
          }
          Column {
            Text(
              text = "$brandAppName Sprint",
              fontFamily = fonts.display,
              fontWeight = FontWeight.ExtraBold,
              fontSize = if (compact) 12.sp else 14.sp,
              color = colors.textPrimary,
            )
            Text(
              text = "$completedCount of $total deliverables shipped",
              fontFamily = fonts.body,
              fontSize = 10.sp,
              color = colors.textMuted,
            )
          }
        }
        Box(
          modifier =
            Modifier.size(32.dp)
              .clip(CircleShape)
              .background(colors.primary)
              .border(1.5.dp, colors.border, CircleShape)
              .clickable {
                completedIds =
                  if (completedIds.size == total) emptySet()
                  else PresetDecks.sampleTasks.map { it.id }.toSet()
              },
          contentAlignment = Alignment.Center,
        ) {
          Icon(Icons.Filled.Add, contentDescription = "Toggle all tasks", tint = colors.onPrimary, modifier = Modifier.size(16.dp))
        }
      }
    }

    // Interactive Task Checkboxes
    val visibleTasks = if (compact) PresetDecks.sampleTasks.take(3) else PresetDecks.sampleTasks
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      visibleTasks.forEach { task ->
        val checked = task.id in completedIds
        val prioColor =
          when (task.priority) {
            "High" -> colors.secondary
            "Medium" -> colors.warm
            else -> colors.success
          }
        ThemedCard(
          deck = deck,
          onClick = { completedIds = if (checked) completedIds - task.id else completedIds + task.id },
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Box(
              modifier =
                Modifier.size(20.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (checked) colors.primary else colors.canvas)
                  .border(1.5.dp, colors.border, RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center,
            ) {
              if (checked) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(13.dp))
              }
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = task.title,
                fontFamily = fonts.body,
                fontWeight = FontWeight.SemiBold,
                fontSize = if (compact) 10.sp else 12.sp,
                color = if (checked) colors.textMuted else colors.textPrimary,
                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
              Text(task.dueLabel, fontFamily = fonts.mono, fontSize = 9.sp, color = colors.textMuted)
            }
            Box(
              modifier =
                Modifier.clip(RoundedCornerShape(6.dp))
                  .background(prioColor.copy(alpha = 0.2f))
                  .border(1.dp, prioColor, RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
              Text(task.priority, fontFamily = fonts.mono, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            }
          }
        }
      }
    }
  }
}
