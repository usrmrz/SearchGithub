package dev.usrmrz.searchgithub.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.model.Status
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val nextPageHandler = NextPageHandler(repoRepository, viewModelScope)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val results = _query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { search ->
            if(search.isBlank()) {
                flowOf(Resource.Success(emptyList())) // Возвращаем пустой список
            } else {
                repoRepository.search(search)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    val loadMoreStatus: StateFlow<LoadMoreState> = nextPageHandler.loadMoreState

    fun setQuery(input: String) {
        val newQuery = input.lowercase(Locale.getDefault()).trim()
        if(newQuery != _query.value) {
            nextPageHandler.reset()
            _query.value = newQuery
        }
    }

    fun loadNextPage() {
        val currentQuery = _query.value
        Log.d(
            "SVM",
            "fun loadNextPage() {val currentQuery = _query.value;;currentQuery: $currentQuery; _query.value: ${_query.value};"
        )
        if(currentQuery.isNotBlank()) {
            Log.d(
                "SVM",
                "if(currentQuery.isNotBlank());;currentQuery: $currentQuery; _query.value: ${_query.value};"
            )
            nextPageHandler.queryNextPage(currentQuery)
        }
    }

//    fun refresh() {
//        _query.value = _query.value
//    }

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if(handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }
//}

    //    inner class NextPageHandler(
    class NextPageHandler(
        private val repository: RepoRepository,
        private val scope: CoroutineScope
    ) {
        private var nextPageFlow: Flow<Resource<Boolean>>? = null
        private val _loadMoreState = MutableStateFlow(LoadMoreState(false, null))
        val loadMoreState: StateFlow<LoadMoreState> = _loadMoreState.asStateFlow()

        private var query: String? = null
        private var _hasMore: Boolean = true

        fun queryNextPage(query: String) {

            reset()
            if(this.query == query) {
                Log.d("SVM", "if(this.query == query);;query: $query")
                return
            }
            unregister()
            this.query = query
            Log.d("SVM", "this.query = query;;query: $query")
            nextPageFlow = repository.searchNextPage(query)

            scope.launch {
                nextPageFlow?.collectLatest { result ->
                    when(result.status) {
                        Status.SUCCESS -> {
                            _hasMore = result.data == true
                            _loadMoreState.value = LoadMoreState(false, null)
                        }

                        Status.ERROR -> {
                            _hasMore = true
                            _loadMoreState.value = LoadMoreState(false, result.message)
                        }

                        Status.LOADING -> {
                            _loadMoreState.value = LoadMoreState(true, null)
                        }
                    }
                }
            }
        }

        private fun unregister() {
            nextPageFlow = null
            if(_hasMore) {
                query = null
            }
        }

        fun reset() {
            unregister()
            _hasMore = true
            _loadMoreState.value = LoadMoreState(false, null)
        }
    }
}


