package dev.usrmrz.searchgithub.domain.model

data class User(
    val login: String,
    val avatarUrl: String?,
    val url: String?,
    val name: String?,
    val company: String?,
    val reposUrl: String?,
    val blog: String?
)
