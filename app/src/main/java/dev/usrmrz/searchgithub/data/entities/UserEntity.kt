package dev.usrmrz.searchgithub.data.entities

import android.R.attr.data
import androidx.room.Entity

@Entity(
    tableName = "user",
    primaryKeys = ["login"],
)

data class UserEntity(
    val login: String,
    val avatarUrl: String?,
    val name: String?,
    val company: String?,
    val reposUrl: String?,
    val blog: String?
)