package com.example.model

enum class AiPolarityPreference(val label: String) {
  AUTO("Auto"),
  LIGHT("Light Mode"),
  DARK("Dark Mode"),
}

data class AiTypographyScaleSpec(
  val displaySp: Int,
  val h1Sp: Int,
  val h2Sp: Int,
  val bodySp: Int,
  val captionSp: Int,
  val scaleRatioLabel: String,
)

data class AiThemeSuggestion(
  val deck: ThemeDeck,
  val designRationale: String,
  val scaleSpec: AiTypographyScaleSpec,
  val wcagAudit: WcagAudit,
)

sealed interface AiThemeUiState {
  data object Idle : AiThemeUiState
  data object Loading : AiThemeUiState
  data class Success(
    val prompt: String,
    val suggestions: List<AiThemeSuggestion>,
  ) : AiThemeUiState
  data class Error(val message: String) : AiThemeUiState
}
