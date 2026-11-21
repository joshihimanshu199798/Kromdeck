package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ShortlistRepository
import com.example.model.ColorRole
import com.example.model.FontPairing
import com.example.model.KromdeckTheme
import com.example.model.SurfacePhysics
import com.example.model.SurfaceStyleTokens
import com.example.model.ThemePalette
import com.example.model.TypographyTokens
import com.example.util.ColorUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Dedicated ViewModel managing the active [KromdeckTheme] (color palette, typography settings,
 * and surface style tokens) and persisting it to Jetpack DataStore.
 */
class ThemeViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = ShortlistRepository(application.applicationContext)

  private val _activeTheme = MutableStateFlow(KromdeckTheme.Default)
  val activeTheme: StateFlow<KromdeckTheme> = _activeTheme.asStateFlow()

  private val _hasPersistedTheme = MutableStateFlow(false)
  val hasPersistedTheme: StateFlow<Boolean> = _hasPersistedTheme.asStateFlow()

  private val _lastPersistedAtMillis = MutableStateFlow<Long?>(null)
  val lastPersistedAtMillis: StateFlow<Long?> = _lastPersistedAtMillis.asStateFlow()

  init {
    viewModelScope.launch {
      repository.activeThemeFlow.collect { saved ->
        if (saved != null) {
          _activeTheme.value = saved
          _hasPersistedTheme.value = true
        }
      }
    }
  }

  fun setActiveTheme(theme: KromdeckTheme, autoPersist: Boolean = false) {
    _activeTheme.value = theme
    if (autoPersist) {
      persistTheme(theme)
    }
  }

  fun updateColorPalette(palette: ThemePalette, autoPersist: Boolean = true) {
    val isDark = ColorUtils.relativeLuminance(ColorUtils.parseHexColor(palette.canvasHex)) < 0.25
    val updated = _activeTheme.value.copy(palette = palette, isDark = isDark)
    _activeTheme.value = updated
    if (autoPersist) persistTheme(updated)
  }

  fun updateColorRole(role: ColorRole, hex: String, autoPersist: Boolean = true) {
    val normalized = ColorUtils.normalizeHex(hex) ?: return
    val nextPalette = _activeTheme.value.palette.withRoleHex(role, normalized)
    updateColorPalette(nextPalette, autoPersist = autoPersist)
  }

  fun updateTypography(typography: TypographyTokens, autoPersist: Boolean = true) {
    val updated = _activeTheme.value.copy(typography = typography)
    _activeTheme.value = updated
    if (autoPersist) persistTheme(updated)
  }

  fun updateFontPairing(pairing: FontPairing, autoPersist: Boolean = true) {
    updateTypography(_activeTheme.value.typography.copy(fontPairing = pairing), autoPersist = autoPersist)
  }

  fun updateSurfaceStyle(surface: SurfaceStyleTokens, autoPersist: Boolean = true) {
    val updated = _activeTheme.value.copy(surface = surface)
    _activeTheme.value = updated
    if (autoPersist) persistTheme(updated)
  }

  fun updateSurfacePhysics(physics: SurfacePhysics, autoPersist: Boolean = true) {
    updateSurfaceStyle(_activeTheme.value.surface.copy(physics = physics), autoPersist = autoPersist)
  }

  /**
   * Persists the active (or provided) [KromdeckTheme] to Jetpack DataStore.
   */
  fun persistTheme(theme: KromdeckTheme = _activeTheme.value) {
    _activeTheme.value = theme
    viewModelScope.launch {
      repository.saveActiveTheme(theme)
      _hasPersistedTheme.value = true
      _lastPersistedAtMillis.update { System.currentTimeMillis() }
    }
  }

  /**
   * Clears the persisted theme in DataStore and resets to [KromdeckTheme.Default].
   */
  fun resetPersistedTheme() {
    _activeTheme.value = KromdeckTheme.Default
    viewModelScope.launch {
      repository.clearActiveTheme()
      _hasPersistedTheme.value = false
      _lastPersistedAtMillis.value = null
    }
  }
}
