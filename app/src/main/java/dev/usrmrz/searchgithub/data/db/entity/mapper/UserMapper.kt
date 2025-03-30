package dev.usrmrz.searchgithub.data.db.entity.mapper

import dev.usrmrz.searchgithub.data.db.entity.UserEntity
import dev.usrmrz.searchgithub.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        login = login,
        avatarUrl = avatarUrl,
        url = url,
        name = name,
        company = company,
        reposUrl = reposUrl,
        blog = blog,
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        login = login,
        avatarUrl = avatarUrl,
        url = url,
        name = name,
        company = company,
        reposUrl = reposUrl,
        blog = blog,
    )
}