package dev.usrmrz.searchgithub.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "repo",
    indices = [
        Index("id"),
        Index("owner_login")
    ],
    primaryKeys = ["name", "owner_login"],
)

data class RepoEntity(

    val id: Int,
    val name: String,
    val fullName: String,
    @field:Embedded(prefix = "owner_")
    val owner: OwnerEntity,
    val description: String?,
    val updatedAt: String,
    val stars: Int,
)

data class OwnerEntity(
    val login: String,
    val url: String?
)

