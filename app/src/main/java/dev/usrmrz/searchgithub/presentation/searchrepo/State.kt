package dev.usrmrz.searchgithub.presentation.searchrepo

import dev.usrmrz.searchgithub.domain.model.Repo

sealed interface GitUiState {
    data class Success(val repos: List<Repo>) : GitUiState
    object Error : GitUiState
    object Loading : GitUiState
}
