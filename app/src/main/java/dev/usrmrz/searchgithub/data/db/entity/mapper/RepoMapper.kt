package dev.usrmrz.searchgithub.data.db.entity.mapper

import dev.usrmrz.searchgithub.data.db.entity.RepoEntity
import dev.usrmrz.searchgithub.domain.model.Repo
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun RepoEntity.toDomain(): Repo {
    return Repo(
        id = id,
        name = name,
        fullName = fullName ?: "",
        owner = Repo.Owner(login = owner.login, avatarUrl = owner.avatarUrl),
        description = description ?: "",
        createdAt = createdAt,
        updatedAt = updatedAt ?: "",
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
        createdAt = formatDate(createdAt).toString(),
        updatedAt = formatDate(updatedAt).toString(),
        watchers = watchers,
        issues = issues,
        stars = stars,
        forks = forks,
    )
}

private fun formatDate(originalDateStr: String?): String? {

    // val originalDateStr = "2012-02-13T17:29:58Z"
    // Parse the original date string to a ZonedDateTime object
    val originalDate = ZonedDateTime.parse(originalDateStr)
    // Define the desired format
    val desiredFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    // Convert the ZonedDateTime object to the desired format
    val formattedDateStr = originalDate.format(desiredFormat)
    return formattedDateStr
}
