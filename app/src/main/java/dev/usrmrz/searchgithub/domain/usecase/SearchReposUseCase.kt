package dev.usrmrz.searchgithub.domain.usecase

import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse
import dev.usrmrz.searchgithub.domain.repository.RepoRepository

class SearchReposUseCase(
    private val repository: RepoRepository
) {
    suspend operator fun invoke(query: String): RepoSearchResponse? {
        return repository.search(query)
    }
}