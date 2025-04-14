package dev.usrmrz.searchgithub.data.db.entity

import androidx.room.Entity

@Entity(
    tableName = "user",
    primaryKeys = ["login"]
)
data class UserEntity(
    val login: String,
    val avatarUrl: String?,
    val url: String?,
    val name: String?,
    val company: String?,
    val reposUrl: String?,
    val blog: String?
)
