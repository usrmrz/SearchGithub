package dev.usrmrz.searchgithub.domain.model

import com.google.gson.annotations.SerializedName

data class Contributor(
    val login: String,
    val contributions: Int,
    @field:SerializedName("avatar_url")
    val avatarUrl: String?,
    @field:SerializedName("repo_name")
    val repoName: String,
    @field:SerializedName("repo_owner")
    val repoOwner: String,
)
