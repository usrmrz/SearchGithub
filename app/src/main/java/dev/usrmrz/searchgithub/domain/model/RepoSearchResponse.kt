package dev.usrmrz.searchgithub.domain.model

import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import dev.usrmrz.searchgithub.data.db.GithubTypeConverters

@TypeConverters(GithubTypeConverters::class)
data class RepoSearchResponse(

    val query: String,
    val repoIds: List<Int>,
    @field:SerializedName("total_count")
    val total: Int = 0,
    val next: Int,
)

