package dev.usrmrz.searchgithub.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.usrmrz.searchgithub.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow


/**
 * Interface for database access for User related operations.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM user WHERE login = :login")
    fun findByLogin(login: String): Flow<UserEntity>
}