package dev.usrmrz.searchgithub.domain.repository

import dev.usrmrz.searchgithub.domain.model.ContributorModel
import dev.usrmrz.searchgithub.domain.model.RepoModel
import dev.usrmrz.searchgithub.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface RepoRepository {

    fun loadRepos(owner: String): Flow<Resource<List<RepoModel>>>
    fun loadRepo(owner: String, name: String): Flow<Resource<RepoModel>>
    fun loadContributors(owner: String, name: String): Flow<Resource<List<ContributorModel>>>
    suspend fun searchNextPage(query: String): Resource<Boolean>
    fun search(query: String): Flow<Resource<List<RepoModel>>>
}

