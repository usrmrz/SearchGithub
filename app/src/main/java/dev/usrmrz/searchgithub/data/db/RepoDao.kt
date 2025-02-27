package dev.usrmrz.searchgithub.data.db

import android.util.SparseIntArray
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.usrmrz.searchgithub.domain.model.Contributor
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
//@Suppress("unused")
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg repos: Repo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContributors(contributors: List<Contributor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repositories: List<Repo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createRepoIfNotExists(repo: Repo): Long

    @Query("SELECT * FROM repo WHERE owner_login = :ownerLogin AND name = :name")
    fun load(ownerLogin: String, name: String): Flow<Repo>

    @Query(
        """
       SELECT login, avatarUrl, repoName, repoOwner, contributions FROM contributor
       WHERE repoName = :name AND repoOwner = :owner
       ORDER BY contributions DESC
    """
    )
    fun loadContributors(owner: String, name: String): Flow<List<Contributor>>

    @Query(
        """
       SELECT * FROM repo
       WHERE owner_login = :owner
       ORDER BY stars DESC
    """
    )
    fun loadRepositories(owner: String): Flow<List<Repo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: RepoSearchResult)

    @Query("SELECT * FROM RepoSearchResult WHERE `query` = :query")
    fun search(query: String): Flow<RepoSearchResult?>

    fun loadOrdered(repoIds: List<Int>): Flow<List<Repo>> {
        val order = SparseIntArray()
        repoIds.withIndex().forEach { order.put(it.value, it.index) }
        return loadById(repoIds).map { list ->
            list.sortedWith(compareBy { order.get(it.id) })
        }
    }

    @Query("SELECT * FROM repo WHERE id in (:repoIds)")
    fun loadById(repoIds: List<Int>): Flow<List<Repo>>

    @Query("SELECT * FROM RepoSearchResult WHERE `query` = :query")
    fun findSearchResult(query: String): RepoSearchResult?
}
