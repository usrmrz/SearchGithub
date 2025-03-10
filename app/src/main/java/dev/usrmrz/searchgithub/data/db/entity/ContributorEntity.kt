package dev.usrmrz.searchgithub.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.google.gson.annotations.SerializedName

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
    @field:SerializedName("login")
    val login: String,
    @field:SerializedName("contributions")
    val contributions: Int,
    @field:SerializedName("avatar_url")
    val avatarUrl: String?,
    @field:SerializedName("repo_name")
    val repoName: String,
    @field:SerializedName("repo_owner")
    val repoOwner: String,
) //{
//    lateinit var repoName: String
//    lateinit var repoOwner: String
//}
