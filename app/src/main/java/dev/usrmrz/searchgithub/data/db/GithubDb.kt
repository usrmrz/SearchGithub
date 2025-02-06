package dev.usrmrz.searchgithub.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.usrmrz.searchgithub.data.entities.ContributorEntity
import dev.usrmrz.searchgithub.data.entities.RepoEntity
import dev.usrmrz.searchgithub.data.entities.RepoSearchResultEntity
import dev.usrmrz.searchgithub.data.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        RepoEntity::class,
        ContributorEntity::class,
        RepoSearchResultEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class GithubDb : RoomDatabase() {
    abstract val repoDao: RepoDao

    companion object {
        const val DATABASE_NAME = "github_db"
    }
}