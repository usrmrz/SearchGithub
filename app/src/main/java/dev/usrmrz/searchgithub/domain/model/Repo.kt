package dev.usrmrz.searchgithub.domain.model

data class Repo(
    
    val id: Int,
    val name: String,
    val description: String?,
    val stars: Int,
)