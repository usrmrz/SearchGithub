package dev.usrmrz.searchgithub.data.db.entity

import androidx.room.Entity
import androidx.room.TypeConverters
import dev.usrmrz.searchgithub.data.db.GithubTypeConverters

@Entity(
    tableName = "reposearchresult",
    primaryKeys = ["query"]
)
@TypeConverters(GithubTypeConverters::class)
data class RepoSearchEntity(
    val query: String,
    val repoIds: List<Int>,
    val totalCount: Int,
    val next: Int?
)