package dev.usrmrz.searchgithub.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.usrmrz.searchgithub.data.api.GithubApi
import dev.usrmrz.searchgithub.data.repository.RepoRepositoryImpl
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepoRepository(githubApi: GithubApi): RepoRepository {
        return RepoRepositoryImpl(githubApi)
    }
}