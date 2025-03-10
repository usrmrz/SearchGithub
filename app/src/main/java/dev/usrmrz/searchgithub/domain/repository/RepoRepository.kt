package dev.usrmrz.searchgithub.domain.repository

import dev.usrmrz.searchgithub.domain.model.Contributor
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RepoRepository {

//    fun loadRepos(owner: String): Flow<List<Repo>>
    fun loadRepos(owner: String): Flow<Resource<List<Repo>>>
//    fun loadRepo(owner: String, name: String): Flow<Repo>
    fun loadRepo(owner: String, name: String): Flow<Resource<Repo>>
//    fun loadContributors(owner: String, name: String): Flow<List<Contributor>>
    fun loadContributors(owner: String, name: String): Flow<Resource<List<Contributor>>>
//    fun searchNextPage(query: String): StateFlow<Boolean>?
    fun searchNextPage(query: String): StateFlow<Resource<Boolean>?>
//    fun search(query: String): Flow<List<Repo>>
    fun search(query: String): Flow<Resource<List<Repo>>>
}