package dev.usrmrz.searchgithub.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class RepoEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String?,
    val stars: Int
)
