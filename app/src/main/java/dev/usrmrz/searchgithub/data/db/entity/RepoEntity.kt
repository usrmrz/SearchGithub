package dev.usrmrz.searchgithub.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "repo",
    indices = [
        Index("id"),
        Index("owner_login")],
    primaryKeys = ["name", "owner_login"]
)
data class RepoEntity(
    val id: Int,
    val name: String,
    val fullName: String?,
    @field:Embedded(prefix = "owner_")
    val owner: OwnerEntity,
    val description: String?,
    val createdAt: String,
    val updatedAt: String?,
    val watchers: Int,
    val issues: Int,
    val stars: Int,
    val forks: Int,
) {
    data class OwnerEntity(
        val login: String,
        val avatarUrl: String?
    )
}
