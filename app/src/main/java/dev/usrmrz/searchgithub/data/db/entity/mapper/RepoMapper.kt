package dev.usrmrz.searchgithub.data.db.entity.mapper

import dev.usrmrz.searchgithub.data.db.entity.RepoEntity
import dev.usrmrz.searchgithub.domain.model.Repo

//object RepoEntityMapper : EntityMapper<List<Repo>, List<RepoEntity>> {

    fun RepoEntity.toDomain(): Repo {
        return Repo(
            id,
            name,
            fullName,
            owner,
            description,
            watchers,
            issues,
            stars,
            forks
        )
    }

    fun Repo.toEntity(): RepoEntity {
        return RepoEntity(
            id = id,
            name = name,
            fullName = fullName,
            owner = this.owner,
            description = description,
            watchers = watchers,
            issues = issues,
            stars = stars,
            forks = forks
        )
    }

    fun RepoEntity.User.toDomain(): Repo.User {
        return Repo.User(
            login,
            url
        )
    }
//
    fun Repo.owner.toEntity(): RepoEntity.UserEntity {
        return RepoEntity.UserEntity(
            login,
            url
        )
    }
}