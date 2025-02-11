package dev.usrmrz.searchgithub.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.usrmrz.searchgithub.domain.model.RepoModel
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

//@Suppress("unused")
@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<Resource<List<RepoModel>>>(Resource.Loading())
    val results: StateFlow<Resource<List<RepoModel>>> = _results.asStateFlow()

    private val _loadMoreState = MutableStateFlow(LoadMoreState(isRunning = false, errorMessage = null))
    val loadMoreState: StateFlow<LoadMoreState> = _loadMoreState.asStateFlow()

    private var _hasMore = true
    val hasMore: Boolean get() = _hasMore

    init {
        viewModelScope.launch {
            _query
                .debounce(300) //Подождём, пока пользователь не закончит ввод
                .distinctUntilChanged()
                .collectLatest { search ->
                    if (search.isBlank()) {
                        _results.value = Resource.Success(emptyList())
                    } else {
                        repoRepository.search(search).collect {
                            _results.value = it
                        }
                    }
                }
        }
    }

    fun setQuery(originalInput: String) {
        val input = originalInput.lowercase(Locale.getDefault()).trim()
        if (input == _query.value) return
        resetPagination()
        _query.value = input
    }

    fun loadNextPage() {
        val currentQuery = _query.value
        if (currentQuery.isNotBlank() && _hasMore) {
            viewModelScope.launch {
                _loadMoreState.value = LoadMoreState(isRunning = true, errorMessage = null)
                when (val result = repoRepository.searchNextPage(currentQuery)) {
                    is Resource.Success -> {
                        _hasMore = result.data == true
                        _loadMoreState.value = LoadMoreState(isRunning = false, errorMessage = null)
                    }
                    is Resource.Error -> {
                        _hasMore = true
                        _loadMoreState.value = LoadMoreState(isRunning = false, errorMessage = result.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    fun refresh() {
        _query.value = _query.value
    }

    private fun resetPagination() {
        _hasMore = true
        _loadMoreState.value = LoadMoreState(isRunning = false, errorMessage = null)
    }

    data class LoadMoreState(val isRunning: Boolean, val errorMessage: String?)
}
