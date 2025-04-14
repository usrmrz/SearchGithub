package dev.usrmrz.searchgithub.domain.repository

import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun loadUser(login: String): Flow<Resource<User>>
}
