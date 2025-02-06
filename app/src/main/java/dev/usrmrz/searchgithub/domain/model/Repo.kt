package dev.usrmrz.searchgithub.domain.model

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

data class Repo(
    val id: Int,
    val name: String,
    @field:SerializedName("full_name")
    val fullName: String,
    val description: String?,
    @field:Embedded(prefix = "owner_")
    val owner: Owner,
    @field:SerializedName("stargazers_count")
    val stars: Int
)

data class Owner(
    val login: String,
    val url: String?
)

