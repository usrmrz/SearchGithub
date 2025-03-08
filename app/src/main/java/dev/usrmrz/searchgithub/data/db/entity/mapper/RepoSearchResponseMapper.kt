package dev.usrmrz.searchgithub.data.db.entity.mapper

import dev.usrmrz.searchgithub.data.db.entity.RepoSearchEntity
import dev.usrmrz.searchgithub.domain.model.RepoSearchResult

fun RepoSearchEntity.toDomain(): RepoSearchResult {
    return RepoSearchResult(
        query,
        repoIds,
        totalCount,
        next
    )
}

fun RepoSearchResult.toEntity(): RepoSearchEntity {
    return RepoSearchEntity(
        query,
        repoIds,
        totalCount,
        next
    )
}