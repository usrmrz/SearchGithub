package dev.usrmrz.searchgithub.presentation.searchrepo

import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse

sealed interface GitUiState {
    object Loading : GitUiState
    data class Success(val data: RepoSearchResponse) : GitUiState
    object Error : GitUiState
    }
