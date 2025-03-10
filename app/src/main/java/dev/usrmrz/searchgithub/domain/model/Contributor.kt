package dev.usrmrz.searchgithub.domain.model

data class Contributor(
    val login: String,
    val contributions: Int,
    val avatarUrl: String?,
//    var repoName: String,
//    var repoOwner: String
) //{
//    lateinit var repoName: String
//    lateinit var repoOwner: String
//}