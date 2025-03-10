package dev.usrmrz.searchgithub.data.db.entity.mapper

import dev.usrmrz.searchgithub.data.db.entity.ContributorEntity
import dev.usrmrz.searchgithub.domain.model.Contributor

fun ContributorEntity.toDomain(): Contributor {
    return Contributor(
        login = login,
        contributions = contributions,
        avatarUrl = avatarUrl,
    )
}

fun Contributor.toEntity(repoName: String, repoOwner: String): ContributorEntity {
    return ContributorEntity(
        login = login,
        contributions = contributions,
        avatarUrl = avatarUrl,
        repoName = repoName,
        repoOwner = repoOwner
    )
}