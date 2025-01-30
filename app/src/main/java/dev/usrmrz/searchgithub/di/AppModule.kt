package dev.usrmrz.searchgithub.di

import android.app.Application
import androidx.room.Room
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.usrmrz.searchgithub.data.api.GithubApi
import dev.usrmrz.searchgithub.data.database.GithubDatabase
import dev.usrmrz.searchgithub.data.database.RepoDao
import dev.usrmrz.searchgithub.data.repository.RepoRepositoryImpl
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().create()
                )
            ).client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideGithubApi(retrofit: Retrofit): GithubApi {
        return retrofit.create(GithubApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGithubDatabase(app: Application): GithubDatabase {
        return Room.databaseBuilder(
            app,
            GithubDatabase::class.java,
            GithubDatabase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: GithubDatabase): RepoDao {
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