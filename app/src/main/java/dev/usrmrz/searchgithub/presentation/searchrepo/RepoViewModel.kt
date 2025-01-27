package dev.usrmrz.searchgithub.presentation.searchrepo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val repo: RepoRepository
): ViewModel() {

    var gitUiState: GitUiState by mutableStateOf(GitUiState.Loading)
        private set

    init {
        searchRepos()
    }

    fun searchRepos() {
        viewModelScope.launch {
            gitUiState = GitUiState.Loading
            gitUiState = try {
                val listResult = repo.searchRepos(query = "kotlin")
                GitUiState.Success(
                    "Success: ${listResult.size} Mars photos retrieved"
                )
            } catch (e: IOException) {
                GitUiState.Error
            } catch (e: HttpException) {
                GitUiState.Error
            }
        }
    }
}
