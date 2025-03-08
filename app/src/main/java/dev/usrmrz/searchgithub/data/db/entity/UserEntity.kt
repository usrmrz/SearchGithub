package dev.usrmrz.searchgithub.data.db.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "user",
    primaryKeys = ["login"]
)
data class UserEntity(
    @field:SerializedName("login")
    val login: String,
    @field:SerializedName("avatar_url")
    val avatarUrl: String?,
    @field:SerializedName("url")
    val url: String?,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("company")
    val company: String?,
    @field:SerializedName("repos_url")
    val reposUrl: String?,
    @field:SerializedName("blog")
    val blog: String?
)
