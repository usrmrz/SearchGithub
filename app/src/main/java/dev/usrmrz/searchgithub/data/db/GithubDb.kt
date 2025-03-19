package dev.usrmrz.searchgithub.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.usrmrz.searchgithub.data.db.entity.ContributorEntity
import dev.usrmrz.searchgithub.data.db.entity.RepoEntity
import dev.usrmrz.searchgithub.data.db.entity.RepoSearchEntity
import dev.usrmrz.searchgithub.data.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        RepoEntity::class,
        ContributorEntity::class,
        RepoSearchEntity::class
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(value = [GithubTypeConverters::class])
abstract class GithubDb : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "github_db"
    }
}
