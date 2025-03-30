package dev.usrmrz.searchgithub.domain.repository

import dev.usrmrz.searchgithub.domain.model.Contributor
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface RepoRepository {

    fun loadRepos(owner: String): Flow<Resource<List<Repo>>>

    fun loadRepo(owner: String, name: String): Flow<Resource<Repo>>

    fun loadContributors(owner: String, name: String): Flow<Resource<List<Contributor>>>

    fun searchNextPage(query: String): Flow<Resource<Boolean>>

    fun search(query: String): Flow<Resource<List<Repo>>>
}