package dev.usrmrz.searchgithub.data.db

import android.util.SparseIntArray
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.usrmrz.searchgithub.data.entities.ContributorEntity
import dev.usrmrz.searchgithub.data.entities.RepoEntity
import dev.usrmrz.searchgithub.data.entities.RepoSearchResultEntity
import dev.usrmrz.searchgithub.domain.model.RepoModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
@Suppress("unused")
abstract class RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(vararg repos: RepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertContributors(contributors: List<ContributorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRepos(repositories: List<RepoEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun createRepoIfNotExists(repo: RepoEntity): Long

    @Query("SELECT * FROM repo WHERE owner_login = :ownerLogin AND name = :name")
    abstract fun load(ownerLogin: String, name: String): Flow<RepoModel?>

    @Query(
        """
       SELECT login, avatarUrl, repoName, repoOwner, contributions FROM contributor
       WHERE repoName = :name AND repoOwner = :owner
       ORDER BY contributions DESC
    """
    )
    abstract fun loadContributors(owner: String, name: String): Flow<List<ContributorEntity>>

    @Query(
        """
       SELECT * FROM repo
       WHERE owner_login = :owner
       ORDER BY stars DESC
    """
    )
    abstract fun loadRepositories(owner: String): Flow<List<RepoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(result: RepoSearchResultEntity)

    @Query("SELECT * FROM RepoSearchResult WHERE `query` = :query")
    abstract fun search(query: String): Flow<RepoSearchResultEntity?>

    @Query("SELECT * FROM repo WHERE id in (:repoIds)")
    protected abstract fun loadById(repoIds: List<Int>): Flow<List<RepoEntity>>

    suspend fun loadOrdered(repoIds: List<Int>): List<RepoEntity> {
        val order = SparseIntArray()
        repoIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        val repositories = loadById(repoIds).firstOrNull().orEmpty()
        return repositories.sortedWith(compareBy { order.get(it.id) })
    }

    @Query("SELECT * FROM RepoSearchResult WHERE `query` = :query")
    abstract suspend fun findSearchResult(query: String): RepoSearchResultEntity?
}


