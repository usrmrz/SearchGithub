package dev.usrmrz.searchgithub.domain.model

import com.google.gson.annotations.SerializedName

data class Repo(
    val id: Int,
    val name: String,
    @field:SerializedName("full_name")
    val fullName: String,
    val owner: Owner,
    val description: String?,
    @field:SerializedName("created_at")
    val createdAt: String,
    @field:SerializedName("updated_at")
    val updatedAt: String?,
    val watchers: Int,
    @field:SerializedName("open_issues")
    val issues: Int,
    @field:SerializedName("stargazers_count")
    val stars: Int,
    val forks: Int,
) {
    data class Owner(
        val login: String,
        @field:SerializedName("avatar_url")
        val avatarUrl: String?
    )
    companion object {
        const val UNKNOWN_ID = -1
    }
}

