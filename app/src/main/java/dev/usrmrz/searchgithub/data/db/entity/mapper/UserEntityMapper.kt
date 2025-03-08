package dev.usrmrz.searchgithub.data.db.entity.mapper

import dev.usrmrz.searchgithub.data.db.entity.UserEntity
import dev.usrmrz.searchgithub.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        login,
        avatarUrl,
        url,
        name,
        company,
        reposUrl,
        blog
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        login,
        avatarUrl,
        url,
        name,
        company,
        reposUrl,
        blog
    )
}