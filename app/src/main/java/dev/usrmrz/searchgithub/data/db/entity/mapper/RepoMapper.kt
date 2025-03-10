package dev.usrmrz.searchgithub.data.db.entity.mapper

import dev.usrmrz.searchgithub.data.db.entity.RepoEntity
import dev.usrmrz.searchgithub.domain.model.Repo

fun RepoEntity.toDomain(): Repo {
    return Repo(
        id = id,
        name = name,
        fullName = fullName ?: "",
        owner = Repo.Owner(login = owner.login, avatarUrl = owner.avatarUrl),
        description = description ?: "",
        watchers = watchers,
        issues = issues,
        stars = stars,
        forks = forks,
    )
}

fun Repo.toEntity(): RepoEntity {
    return RepoEntity(
        id = id,
        name = name,
        fullName = fullName,
        owner = RepoEntity.OwnerEntity(owner.login, owner.avatarUrl),
        description = description,
        watchers = watchers,
        issues = issues,
        stars = stars,
        forks = forks,
    )
}
