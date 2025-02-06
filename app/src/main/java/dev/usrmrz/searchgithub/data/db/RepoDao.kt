package dev.usrmrz.searchgithub.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.usrmrz.searchgithub.data.entities.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReposDb(repos: List<RepoEntity>)

    @Query("SELECT * FROM repo")
    fun getReposFromDb(): Flow<List<RepoEntity>>
}

