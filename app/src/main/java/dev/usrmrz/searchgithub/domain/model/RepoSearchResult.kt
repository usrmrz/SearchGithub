package dev.usrmrz.searchgithub.domain.model

import androidx.room.Entity
import androidx.room.TypeConverters
import dev.usrmrz.searchgithub.data.db.GithubTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(GithubTypeConverters::class)
data class RepoSearchResult(
    val query: String,
    val repoIds: List<Int>,
    val totalCount: Int,
    val next: Int?
)

