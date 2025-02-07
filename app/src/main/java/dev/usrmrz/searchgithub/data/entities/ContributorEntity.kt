package dev.usrmrz.searchgithub.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "contributor",
    primaryKeys = ["repoName", "repoOwner", "login"],
    foreignKeys = [ForeignKey(
        entity = RepoEntity::class,
        parentColumns = ["name", "owner_login"],
        childColumns = ["repoName", "repoOwner"],
        onUpdate = ForeignKey.CASCADE,
        deferred = true
    )]
)

data class ContributorEntity(

    val login: String,
    val contributions: Int,
    val avatarUrl: String?,
    val repoName: String,
    val repoOwner: String,
)
