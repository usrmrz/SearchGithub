package dev.usrmrz.searchgithub.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.usrmrz.searchgithub.data.database.entity.RepoEntity

@Database(entities = [RepoEntity::class], version = 1)
abstract class GithubDb : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}
