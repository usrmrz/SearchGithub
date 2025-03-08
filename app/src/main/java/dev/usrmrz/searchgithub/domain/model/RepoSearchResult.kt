package dev.usrmrz.searchgithub.domain.model

data class RepoSearchResult(
    val query: String,
    val repoIds: List<Int>,
    val totalCount: Int,
    val next: Int?
)

