package dev.usrmrz.searchgithub.di

import android.app.Application
import androidx.room.Room
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.usrmrz.searchgithub.data.api.GithubApi
import dev.usrmrz.searchgithub.data.database.GitDatabase
import dev.usrmrz.searchgithub.data.database.RepoDao
import dev.usrmrz.searchgithub.data.repository.RepoRepositoryImpl
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): GithubApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(GithubApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGitDatabase(app: Application): GitDatabase {
        return Room.databaseBuilder(
            app,
            GitDatabase::class.java,
            GitDatabase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: GitDatabase): RepoDao {
        return db.repoDao
    }

    @Provides
    @Singleton
    fun provideRepoRepository(
        githubApi: GithubApi,
        repoDao: RepoDao
    ): RepoRepository {
        return RepoRepositoryImpl(githubApi, repoDao)
    }
}