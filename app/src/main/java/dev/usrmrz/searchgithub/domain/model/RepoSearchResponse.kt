package dev.usrmrz.searchgithub.domain.model

import com.google.gson.annotations.SerializedName

data class RepoSearchResponse(

    val query: String,
    val repoIds: List<Int>,
    @field:SerializedName("total_count")
    val total: Int = 0,
    val next: Int,
)

