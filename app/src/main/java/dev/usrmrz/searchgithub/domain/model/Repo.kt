package dev.usrmrz.searchgithub.domain.model

data class Repo(
    val id: Int,
    val name: String,
    val fullName: String,
    val owner: Owner,
    val description: String?,
    val watchers: Int,
    val issues: Int,
    val stars: Int,
    val forks: Int,
) {
    data class Owner(
        val login: String,
        val avatarUrl: String?
    )
    companion object {
        const val UNKNOWN_ID = -1
    }
}

