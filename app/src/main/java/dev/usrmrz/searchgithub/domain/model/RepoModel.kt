package dev.usrmrz.searchgithub.domain.model

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

data class RepoModel(
    val id: Int,
    val name: String,
    @field:SerializedName("full_name")
    val fullName: String,
    @field:Embedded(prefix = "owner_")
    val owner: OwnerModel,
    val description: String?,
    @field:SerializedName("updated_at")
    val updatedAt: String,
    @field:SerializedName("stargazers_count")
    val stars: Int
)

data class OwnerModel(
    val login: String,
    val url: String?
)

