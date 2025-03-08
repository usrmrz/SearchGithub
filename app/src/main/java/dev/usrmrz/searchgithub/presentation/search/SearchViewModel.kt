package dev.usrmrz.searchgithub.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.model.Status
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
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
    private val _query = MutableStateFlow<String>("")
    private val nextPageHandler = NextPageHandler(repoRepository, viewModelScope)

    val query: StateFlow<String> = _query.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val results: StateFlow<Resource<List<Repo>>> = _query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { search ->
            if(search.isBlank()) {
                Log.d(
                    "SVM",
                    ".flatMapLatest { search -> if(search.isBlank()) { flowOf(Resource.Success(emptyList()));;results: $results; results.v: ${results.value} search: $search"
                )
                flowOf(Resource.Success(emptyList()))
            } else {
                Log.d(
                    "SVM",
                    ".flatMapLatest { search -> else(!search.isBlank()) { repoRepository.search(search);;results: $results; results.v: ${results.value} search: $search"
                )
                repoRepository.search(search)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading(null))

    val loadMoreStatus: StateFlow<LoadMoreState> = nextPageHandler.loadMoreState

    fun setQuery(originalInput: String) {
        val input = originalInput.lowercase(Locale.getDefault()).trim()
        Log.d(
            "SVM",
            "fun setQuery(originalInput: String) { val input = originalInput.lowercase(Locale.getDefault()).trim();;originalInput: $originalInput; input: $input"
        )
        if(input == _query.value) return
        nextPageHandler.reset()
        Log.d(
            "SVM",
            "if(input == _query.value) return::nextPageHandler.reset();;input: $input _query.v: ${_query.value}"
        )
        _query.value = input
    }

    fun loadNextPage() {
        val currentQuery = _query.value
        Log.d(
            "SVM",
            "fun loadNextPage() { val currentQuery = _query.value;;currentQuery: $currentQuery"
        )
        if(currentQuery.isNotBlank()) {
            nextPageHandler.queryNextPage(currentQuery)
        }
    }

    fun refresh() {
        _query.value.let {
            _query.value = it
        }
    }

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
//        val errorMessageIfNotHandled = MutableStateFlow(errorMessage)

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
//        val hasMore: Boolean get() = _hasMore

        private var currentJob: Job? = null

        fun queryNextPage(query: String) {
            Log.d(
                "SVM",
                "fun queryNextPage(query: String) {;;this.query: ${this.query}; query: $query"
            )
            if(this.query == query) return
            reset()
            this.query = query

            _loadMoreState.value = LoadMoreState(true, null)
            Log.d("SVM", "_loadMoreState.value = LoadMoreState(true, null);;query: $query; this.query: ${this.query}")
//            currentJob = this.coroutineScope.launch {
            currentJob = coroutineScope.launch {
                Log.d("SVM", "currentJob = coroutineScope.launch {;;currentJob: $currentJob")
                repository.searchNextPage(query)
                    .catch { error ->
                        _loadMoreState.value = LoadMoreState(false, error.message)
                        _hasMore = false
                    }
                    .collectLatest { result ->
                        Log.d(
                            "SVM",
                            ".collectLatest { result ->;;result: $result; result.st: ${result?.status}"
                        )
                        if(result == null) {
                            Log.d(
                                "SVM",
                                "result == null;;result: $result"
                            )
                            reset()
                            Log.d(
                                "SVM",
                                "result == null reset();;result: $result"
                            )
                        } else {
                            when(result.status) {
                                Status.SUCCESS -> {
                                    Log.d(
                                        "SVM",
                                        "when(result.status) { Status.SUCCESS -> {;;result: $result; result.dt: ${result.data}"
                                    )
                                    _hasMore = result.data == true
                                    _loadMoreState.value = LoadMoreState(false, null)
                                }

                                Status.ERROR -> {
                                    Log.d(
                                        "SVM",
                                        "Status.ERROR -> {;;result: $result; result.dt: ${result.data}"
                                    )
                                    _hasMore = false
                                    _loadMoreState.value = LoadMoreState(false, result.message)
                                }

                                Status.LOADING -> {
                                    _hasMore = false
                                    _loadMoreState.value = LoadMoreState(true, result.message)
                                    //Nothing
                                    Log.d(
                                        "SVM",
                                        "Status.LOADING -> {;;result: $result; result.dt: ${result.data}"
                                    )
                                }
                            }
                        }
                    }
            }
        }

        fun reset() {
            Log.d(
                "SVM",
                "begin fun reset()"
            )
            currentJob?.cancel()
            _hasMore = true
            _loadMoreState.value = LoadMoreState(false, null)
            Log.d(
                "SVM",
                "fun reset();;_loadMoreState.value: ${_loadMoreState.value}"
            )
        }
    }
}
