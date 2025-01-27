package dev.usrmrz.searchgithub.domain.model

import com.google.gson.annotations.SerializedName

data class Repo(
    
    val id: Int,
    val name: String,
    val description: String?,
    @SerializedName("stargazers_count")
    val stars: Int,
)