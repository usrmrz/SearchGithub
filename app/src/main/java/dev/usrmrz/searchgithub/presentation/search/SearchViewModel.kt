package dev.usrmrz.searchgithub.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.usrmrz.searchgithub.data.repository.RepoRepository
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

//@Suppress("unused")
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val nextPageHandler = NextPageHandler(repoRepository, viewModelScope)

    val query: StateFlow<String> = _query.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val results: StateFlow<Resource<List<Repo>>> = _query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { search ->
            if(search.isBlank()) {
                flowOf(Resource.success(emptyList()))
            } else {
                repoRepository.search(search)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, Resource.loading(null))

    val loadMoreStatus: StateFlow<LoadMoreState> = nextPageHandler.loadMoreState

    fun setQuery(originalInput: String) {
        val input = originalInput.lowercase(Locale.getDefault()).trim()

        Log.d("vals from SearchVM", "input: $input; results: $results loadMoreStatus: $loadMoreStatus nextPageHandler: $nextPageHandler")

        if(input == _query.value) return
        nextPageHandler.reset()
        _query.value = input
    }

    fun loadNextPage() {
        val currentQuery = _query.value
        if(currentQuery.isNotBlank()) {
            nextPageHandler.queryNextPage(currentQuery)
        }
    }


    fun refresh() {
        _query.value = _query.value
    }


    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false


        val errorMessageIfNotHandled: String?
            get() = if(handledError) null else {
                handledError = true
                errorMessage
            }
    }

    class NextPageHandler(
        private val repository: RepoRepository,
        private val coroutineScope: CoroutineScope,
    ) {
        private val _loadMoreState = MutableStateFlow(LoadMoreState(false, null))
        val loadMoreState: StateFlow<LoadMoreState> = _loadMoreState.asStateFlow()

        private var query: String? = null
        private var _hasMore: Boolean = false
        val hasMore: Boolean get() = _hasMore

        private var currentJob: Job? = null

        fun queryNextPage(query: String) {
            if(this.query == query) return
            reset()
            this.query = query

            _loadMoreState.value = LoadMoreState(true, null)

            currentJob = this.coroutineScope.launch {
                repository.searchNextPage(query)
                    .catch { error ->
                        _loadMoreState.value = LoadMoreState(false, error.message)
                        _hasMore = true
                    }
//                    .collectLatest { result ->
//                        when(result.status) {
//                            Status.SUCCESS -> {
//                                _hasMore = result.data == true
//                                _loadMoreState.value = LoadMoreState(false, null)
//                            }
//
//                            Status.ERROR -> {
//                                _hasMore = true
//                                _loadMoreState.value = LoadMoreState(false, result.message)
//                            }
//
//                            Status.LOADING -> {}
//                            null -> true
//                        }
//                    }
            }
        }


        fun reset() {
            currentJob?.cancel()
            _hasMore = true
            _loadMoreState.value = LoadMoreState(false, null)
        }
    }
}
