package dev.usrmrz.searchgithub.data.repository

import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.db.GithubDb
import dev.usrmrz.searchgithub.data.db.UserDao
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.model.User
import dev.usrmrz.searchgithub.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl(
    private val api: GithubService,
    private val dao: UserDao,
    private val db: GithubDb,
) : UserRepository {
    override fun loadUser(login: String): Flow<Resource<User>> {
        TODO("Not yet implemented")
    }
}