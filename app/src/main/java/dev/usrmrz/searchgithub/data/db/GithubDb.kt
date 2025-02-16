package dev.usrmrz.searchgithub.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.usrmrz.searchgithub.domain.model.Contributor
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResult
import dev.usrmrz.searchgithub.domain.model.User

@Database(
    entities = [
        User::class,
        Repo::class,
        Contributor::class,
        RepoSearchResult::class],
    version = 2,
    exportSchema = false,
)
abstract class GithubDb : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "github_db"
    }
}