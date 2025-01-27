package dev.usrmrz.searchgithub.domain.repository

import dev.usrmrz.searchgithub.data.database.entity.RepoEntity
import dev.usrmrz.searchgithub.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface RepoRepository {
    fun searchRepositories(query: String): Flow<Resource<List<Repo>>>
}