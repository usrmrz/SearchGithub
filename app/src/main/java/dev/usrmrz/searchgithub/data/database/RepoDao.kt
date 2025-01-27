package dev.usrmrz.searchgithub.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.usrmrz.searchgithub.data.database.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<RepoEntity>)

    @Query("SELECT * FROM repositories")
    fun getAllRepos(): Flow<List<RepoEntity>>
}
