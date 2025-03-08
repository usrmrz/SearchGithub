package dev.usrmrz.searchgithub.data.db.entity.mapper

import dev.usrmrz.searchgithub.data.db.entity.ContributorEntity
import dev.usrmrz.searchgithub.domain.model.Contributor

fun ContributorEntity.toDomain(): Contributor {
    return Contributor(
        login,
        contributions,
        avatarUrl,
        repoName,
        repoOwner
    )
}

fun Contributor.toEntity(): ContributorEntity {
    return ContributorEntity(
        login,
        contributions,
        avatarUrl
    ).apply {
        repoName = this@toEntity.repoName
        repoOwner = this@toEntity.repoOwner
    }
}