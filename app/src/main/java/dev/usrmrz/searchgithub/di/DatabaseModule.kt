package dev.usrmrz.searchgithub.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.usrmrz.searchgithub.data.database.GithubDb
import dev.usrmrz.searchgithub.data.database.RepoDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GithubDb {
        return Room.databaseBuilder(
            context,
            GithubDb::class.java,
            "github.db"
        ).build()

    }

    @Provides
    fun provideRepoDao(db: GithubDb): RepoDao {
        return db.repoDao()
    }
}