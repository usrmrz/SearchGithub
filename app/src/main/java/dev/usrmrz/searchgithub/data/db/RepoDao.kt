package dev.usrmrz.searchgithub.data.db

import android.util.SparseIntArray
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.usrmrz.searchgithub.data.db.entity.ContributorEntity
import dev.usrmrz.searchgithub.data.db.entity.RepoEntity
import dev.usrmrz.searchgithub.data.db.entity.RepoSearchEntity
import dev.usrmrz.searchgithub.domain.model.RepoSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
//@Suppress("unused")
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg repos: RepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContributors(contributors: List<ContributorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repositories: List<RepoEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createRepoIfNotExists(repo: RepoEntity): Long

    @Query("SELECT * FROM repo WHERE owner_login = :ownerLogin AND name = :name")
    fun load(ownerLogin: String, name: String): Flow<RepoEntity>

    @Query(
        """
       SELECT login, avatarUrl, repoName, repoOwner, contributions FROM contributor
       WHERE repoName = :name AND repoOwner = :owner
       ORDER BY contributions DESC
    """
    )
    fun loadContributors(owner: String, name: String): Flow<List<ContributorEntity>>

    @Query(
        """
       SELECT * FROM repo
       WHERE owner_login = :owner
       ORDER BY stars DESC
    """
    )
    fun loadRepositories(owner: String): Flow<List<RepoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: RepoSearchEntity)

    @Query("SELECT * FROM reposearchresult WHERE `query` = :query")
    fun search(query: String): Flow<RepoSearchEntity?>

    fun loadOrdered(repoIds: List<Int>): Flow<List<RepoEntity>> {
        val order = SparseIntArray()
        repoIds.withIndex().forEach { order.put(it.value, it.index) }

        return loadById(repoIds).map { list ->
            list.sortedWith(compareBy { order.get(it.id) })
        }
    }

    @Query("SELECT * FROM repo WHERE id in (:repoIds)")
    fun loadById(repoIds: List<Int>): Flow<List<RepoEntity>>

    @Query("SELECT * FROM reposearchresult WHERE `query` = :query")
    fun findSearchResult(query: String): RepoSearchResult?
}
