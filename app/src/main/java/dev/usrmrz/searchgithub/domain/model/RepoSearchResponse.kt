package dev.usrmrz.searchgithub.domain.model

import com.google.gson.annotations.SerializedName

data class RepoSearchResponse(

    @field:SerializedName("total_count")
    val total: Int = 0,
    val items: List<Repo>
)

