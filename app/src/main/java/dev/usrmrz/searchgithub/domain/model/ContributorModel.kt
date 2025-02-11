package dev.usrmrz.searchgithub.domain.model

import com.google.gson.annotations.SerializedName

data class ContributorModel(

    @field:SerializedName("login")
    val login: String,
    @field:SerializedName("contributions")
    val contributions: Int,
    @field:SerializedName("avatar_url")
    val avatarUrl: String?
)