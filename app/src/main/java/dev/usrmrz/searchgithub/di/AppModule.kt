package dev.usrmrz.searchgithub.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.db.GithubDb
import dev.usrmrz.searchgithub.data.db.RepoDao
import dev.usrmrz.searchgithub.data.repository.RepoRepositoryImpl
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGithubDatabase(app: Application): GithubDb {
        return Room.databaseBuilder(
            app,
            GithubDb::class.java,
            GithubDb.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideRepoRepository(api: GithubService, dao: RepoDao, db: GithubDb): RepoRepository {
        return RepoRepositoryImpl(api, dao, db)
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: GithubDb): RepoDao {
        return db.repoDao()
    }
}