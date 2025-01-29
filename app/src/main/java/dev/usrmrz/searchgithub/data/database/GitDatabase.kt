package dev.usrmrz.searchgithub.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.usrmrz.searchgithub.data.entities.RepoEntity

@Database(
    entities = [RepoEntity::class],
    version = 1
)
abstract class GitDatabase : RoomDatabase() {
    abstract val repoDao: RepoDao

    companion object {
        const val DATABASE_NAME = "git_db"
    }
}