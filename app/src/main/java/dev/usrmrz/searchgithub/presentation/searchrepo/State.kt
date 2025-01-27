package dev.usrmrz.searchgithub.presentation.searchrepo

sealed interface GitUiState {
    data class Success(val repos: String) : GitUiState
    object Error : GitUiState
    object Loading : GitUiState
}
