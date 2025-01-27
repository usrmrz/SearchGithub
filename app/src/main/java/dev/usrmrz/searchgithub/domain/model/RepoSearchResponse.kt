package dev.usrmrz.searchgithub.domain.model

import com.google.gson.annotations.SerializedName
import dev.usrmrz.searchgithub.data.database.entity.RepoEntity

data class RepoSearchResponse(
    @SerializedName("total_count")
    val total: Int = 0,
    @SerializedName("items")
    val items: List<RepoEntity>
)

