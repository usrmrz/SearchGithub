package dev.usrmrz.searchgithub.domain.model

import com.google.gson.annotations.SerializedName

data class User(

    val login: String,
    @field:SerializedName("avatar_url")
    val avatarUrl: String?,
    val name: String?,
    val company: String?,
    @field:SerializedName("repos_url")
    val reposUrl: String?,
    val blog: String?
)
