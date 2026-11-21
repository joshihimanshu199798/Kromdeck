package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.DiscoverCategory
import com.example.model.FontPairing
import com.example.model.GridDensity
import com.example.model.MotionFeel
import com.example.model.NavShellStyle
import com.example.model.KromdeckTheme
import com.example.model.SurfacePhysics
import com.example.model.ThemeDeck
import com.example.model.ThemePalette
import com.example.model.ThemeWinLossRecord
import com.example.model.toKromdeckTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.kromdeckDataStore by preferencesDataStore(name = "kromdeck_prefs")

class ShortlistRepository(private val context: Context) {

  private val shortlistKey = stringPreferencesKey("shortlist_decks_json")
  private val tournamentRecordsKey = stringPreferencesKey("tournament_records_json")
  private val activeThemeKey = stringPreferencesKey("active_kromdeck_theme_json")

  val activeThemeFlow: Flow<KromdeckTheme?> =
    context.kromdeckDataStore.data.map { prefs ->
      val raw = prefs[activeThemeKey]
      if (raw.isNullOrBlank()) null
      else runCatching { jsonToDeck(JSONObject(raw))?.toKromdeckTheme() }.getOrNull()
    }

  val shortlistFlow: Flow<List<ThemeDeck>> =
    context.kromdeckDataStore.data.map { prefs ->
      val raw = prefs[shortlistKey]
      if (raw.isNullOrBlank()) {
        listOf(
          PresetDecks.all[0].copy(note = "Crisp default — strong visual hierarchy"),
          PresetDecks.all[1].copy(note = "Warm editorial botanical option"),
          PresetDecks.all[5].copy(note = "High-contrast AMOLED dark option"),
        )
      } else {
        decodeDecks(raw)
      }
    }

  val tournamentRecordsFlow: Flow<List<ThemeWinLossRecord>> =
    context.kromdeckDataStore.data.map { prefs ->
      val raw = prefs[tournamentRecordsKey]
      if (raw.isNullOrBlank()) emptyList() else decodeRecords(raw)
    }

  suspend fun saveActiveTheme(theme: KromdeckTheme) {
    context.kromdeckDataStore.edit { prefs ->
      prefs[activeThemeKey] = deckToJson(theme.toThemeDeck()).toString()
    }
  }

  suspend fun clearActiveTheme() {
    context.kromdeckDataStore.edit { prefs ->
      prefs.remove(activeThemeKey)
    }
  }

  suspend fun saveShortlist(decks: List<ThemeDeck>) {
    context.kromdeckDataStore.edit { prefs ->
      prefs[shortlistKey] = encodeDecks(decks)
    }
  }

  suspend fun saveTournamentRecords(records: List<ThemeWinLossRecord>) {
    context.kromdeckDataStore.edit { prefs ->
      prefs[tournamentRecordsKey] = encodeRecords(records)
    }
  }

  suspend fun clearTournamentRecords() {
    context.kromdeckDataStore.edit { prefs ->
      prefs.remove(tournamentRecordsKey)
    }
  }

  fun exportBackupJson(activeDeck: ThemeDeck, savedDecks: List<ThemeDeck>): String {
    val root =
      JSONObject()
        .put("kromdeckVersion", 2)
        .put("activeDeck", deckToJson(activeDeck))
        .put("savedDecks", JSONArray().apply { savedDecks.forEach { put(deckToJson(it)) } })
    return root.toString(2)
  }

  fun importBackupJson(rawJson: String): Pair<ThemeDeck?, List<ThemeDeck>>? {
    return try {
      val trimmed = rawJson.trim()
      if (trimmed.startsWith("[")) {
        val list = decodeDecks(trimmed)
        Pair(list.firstOrNull(), list)
      } else {
        val root = JSONObject(trimmed)
        if (root.has("activeDeck") || root.has("savedDecks")) {
          val active = root.optJSONObject("activeDeck")?.let { jsonToDeck(it) }
          val saved =
            root.optJSONArray("savedDecks")?.let { arr ->
              buildList {
                for (i in 0 until arr.length()) {
                  jsonToDeck(arr.getJSONObject(i))?.let { add(it) }
                }
              }
            } ?: emptyList()
          Pair(active, saved)
        } else {
          // Single deck object
          val single = jsonToDeck(root) ?: return null
          Pair(single, listOf(single))
        }
      }
    } catch (_: Exception) {
      null
    }
  }

  private fun encodeRecords(records: List<ThemeWinLossRecord>): String {
    val array = JSONArray()
    for (rec in records) {
      val obj =
        JSONObject()
          .put("deckId", rec.deckId)
          .put("deckName", rec.deckName)
          .put("physics", rec.physics.name)
          .put("fontPairing", rec.fontPairing.name)
          .put("isDark", rec.isDark)
          .put("primaryHex", rec.primaryHex)
          .put("canvasHex", rec.canvasHex)
          .put("wins", rec.wins)
          .put("losses", rec.losses)
          .put("championships", rec.championships)
      array.put(obj)
    }
    return array.toString()
  }

  private fun decodeRecords(raw: String): List<ThemeWinLossRecord> {
    return try {
      val array = JSONArray(raw)
      buildList {
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          add(
            ThemeWinLossRecord(
              deckId = obj.getString("deckId"),
              deckName = obj.getString("deckName"),
              physics = enumValueOrDefault(obj.optString("physics"), SurfacePhysics.HARD_INK),
              fontPairing = enumValueOrDefault(obj.optString("fontPairing"), FontPairing.GEOMETRIC_SANS),
              isDark = obj.optBoolean("isDark", false),
              primaryHex = obj.optString("primaryHex", "#4F46E5"),
              canvasHex = obj.optString("canvasHex", "#FAF9F6"),
              wins = obj.optInt("wins", 0),
              losses = obj.optInt("losses", 0),
              championships = obj.optInt("championships", 0),
            ),
          )
        }
      }
    } catch (_: Exception) {
      emptyList()
    }
  }

  private fun deckToJson(deck: ThemeDeck): JSONObject {
    val p = deck.palette
    val pObj =
      JSONObject()
        .put("canvasHex", p.canvasHex)
        .put("surfaceHex", p.surfaceHex)
        .put("elevatedSurfaceHex", p.elevatedSurfaceHex)
        .put("borderHex", p.borderHex)
        .put("textPrimaryHex", p.textPrimaryHex)
        .put("textMutedHex", p.textMutedHex)
        .put("primaryHex", p.primaryHex)
        .put("secondaryHex", p.secondaryHex)
        .put("warmHex", p.warmHex)
        .put("successHex", p.successHex)

    return JSONObject()
      .put("id", deck.id)
      .put("name", deck.name)
      .put("tagline", deck.tagline)
      .put("isDark", deck.isDark)
      .put("category", deck.category.name)
      .put("physics", deck.physics.name)
      .put("fontPairing", deck.fontPairing.name)
      .put("motionFeel", deck.motionFeel.name)
      .put("note", deck.note)
      .put("gradientEnabled", deck.gradientEnabled)
      .put("gradientAngle", deck.gradientAngle.toDouble())
      .put("gradientMeshGlow", deck.gradientMeshGlow)
      .put("cornerRadiusDp", deck.cornerRadiusDp)
      .put("cardPaddingDp", deck.cardPaddingDp)
      .put("borderWidthDp", deck.borderWidthDp.toDouble())
      .put("shadowIntensity", deck.shadowIntensity.toDouble())
      .put("glassBlurAlpha", deck.glassBlurAlpha.toDouble())
      .put("baseFontSizeSp", deck.baseFontSizeSp.toDouble())
      .put("headingWeightBold", deck.headingWeightBold)
      .put("letterSpacingSp", deck.letterSpacingSp.toDouble())
      .put("lineHeightMultiplier", deck.lineHeightMultiplier.toDouble())
      .put("navShell", deck.navShell.name)
      .put("gridDensity", deck.gridDensity.name)
      .put("iconStrokeWeight", deck.iconStrokeWeight.toDouble())
      .put("palette", pObj)
  }

  private fun jsonToDeck(obj: JSONObject): ThemeDeck? {
    return try {
      val pObj = obj.getJSONObject("palette")
      val surfaceHex = pObj.getString("surfaceHex")
      val palette =
        ThemePalette(
          canvasHex = pObj.getString("canvasHex"),
          surfaceHex = surfaceHex,
          elevatedSurfaceHex = pObj.optString("elevatedSurfaceHex", surfaceHex),
          borderHex = pObj.getString("borderHex"),
          textPrimaryHex = pObj.getString("textPrimaryHex"),
          textMutedHex = pObj.getString("textMutedHex"),
          primaryHex = pObj.getString("primaryHex"),
          secondaryHex = pObj.getString("secondaryHex"),
          warmHex = pObj.getString("warmHex"),
          successHex = pObj.getString("successHex"),
        )
      val id = obj.optString("id", "imported_${System.currentTimeMillis() % 10000}")
      val matchingIcon =
        PresetDecks.all.firstOrNull { preset ->
          id == preset.id || id.startsWith("${preset.id}_") || id.startsWith("custom_${preset.id}")
        }?.swatchIconRes
      ThemeDeck(
        id = id,
        name = obj.optString("name", "Imported Deck"),
        tagline = obj.optString("tagline", "Saved design system"),
        isDark = obj.optBoolean("isDark", false),
        category = enumValueOrDefault(obj.optString("category"), DiscoverCategory.MINIMAL_NOTES),
        palette = palette,
        physics = enumValueOrDefault(obj.optString("physics"), SurfacePhysics.HARD_INK),
        fontPairing = enumValueOrDefault(obj.optString("fontPairing"), FontPairing.GEOMETRIC_SANS),
        motionFeel = enumValueOrDefault(obj.optString("motionFeel"), MotionFeel.HARD_SNAP),
        note = obj.optString("note", ""),
        swatchIconRes = matchingIcon,
        gradientEnabled = obj.optBoolean("gradientEnabled", false),
        gradientAngle = obj.optDouble("gradientAngle", 135.0).toFloat(),
        gradientMeshGlow = obj.optBoolean("gradientMeshGlow", false),
        cornerRadiusDp = obj.optInt("cornerRadiusDp", 14).coerceIn(0, 32),
        cardPaddingDp = obj.optInt("cardPaddingDp", 12).coerceIn(6, 24),
        borderWidthDp = obj.optDouble("borderWidthDp", 2.0).toFloat().coerceIn(0f, 3f),
        shadowIntensity = obj.optDouble("shadowIntensity", 0.85).toFloat().coerceIn(0f, 1.5f),
        glassBlurAlpha = obj.optDouble("glassBlurAlpha", 0.78).toFloat().coerceIn(0.45f, 0.96f),
        baseFontSizeSp = obj.optDouble("baseFontSizeSp", 14.0).toFloat().coerceIn(11f, 18f),
        headingWeightBold = obj.optBoolean("headingWeightBold", true),
        letterSpacingSp = obj.optDouble("letterSpacingSp", 0.0).toFloat().coerceIn(-0.6f, 2f),
        lineHeightMultiplier = obj.optDouble("lineHeightMultiplier", 1.35).toFloat().coerceIn(1.1f, 1.8f),
        navShell = enumValueOrDefault(obj.optString("navShell"), NavShellStyle.BOTTOM_PILL),
        gridDensity = enumValueOrDefault(obj.optString("gridDensity"), GridDensity.COMFORTABLE),
        iconStrokeWeight = obj.optDouble("iconStrokeWeight", 1.8).toFloat().coerceIn(1f, 3f),
      )
    } catch (_: Exception) {
      null
    }
  }

  private fun encodeDecks(decks: List<ThemeDeck>): String {
    val array = JSONArray()
    decks.forEach { array.put(deckToJson(it)) }
    return array.toString()
  }

  private fun decodeDecks(raw: String): List<ThemeDeck> {
    return try {
      val array = JSONArray(raw)
      buildList {
        for (i in 0 until array.length()) {
          jsonToDeck(array.getJSONObject(i))?.let { add(it) }
        }
      }
    } catch (_: Exception) {
      emptyList()
    }
  }

  private inline fun <reified T : Enum<T>> enumValueOrDefault(name: String, default: T): T {
    return try {
      enumValueOf<T>(name)
    } catch (_: Exception) {
      default
    }
  }
}
