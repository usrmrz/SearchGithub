package dev.usrmrz.searchgithub.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "repo",
    indices = [
        Index("id"),
        Index("owner_login")],
    primaryKeys = ["name", "owner_login"]
)
data class RepoEntity(
    val id: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("full_name")
    val fullName: String?,
    @field:SerializedName("owner")
    @field:Embedded(prefix = "owner_")
    val owner: OwnerEntity,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("watchers_count")
    val watchers: Int,
    @field:SerializedName("open_issues_count")
    val issues: Int,
    @field:SerializedName("stargazers_count")
    val stars: Int,
    @field:SerializedName("forks_count")
    val forks: Int,
) {
    data class OwnerEntity(
        @field:SerializedName("login")
        val login: String,
        @field:SerializedName("avatar_url")
        val avatarUrl: String?
    )

//    companion object {
//        const val UNKNOWN_ID = -1
//    }
}
