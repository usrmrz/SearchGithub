package dev.usrmrz.searchgithub.presentation.searchrepo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.usrmrz.searchgithub.data.database.entity.RepoEntity
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val repository: RepoRepository
) : ViewModel() {

    private val _state = MutableStateFlow<State<List<Repo>>>(State.Loading)
    val state: StateFlow<State<List<Repo>>> = _state

    fun onEvent(event: Event) {
        when (event) {
            is Event.Search -> searchRepositories(event.query)
        }
    }

    private fun searchRepositories(query: String) {
        viewModelScope.launch {
            repository.searchRepositories(query).collect { resource ->
                _state.value = when (resource) {
                    is Resource.Loading -> State.Loading
                    is Resource.Success -> State.Success(resource.data)
                    is Resource.Error -> State.Error(resource.message ?: "Неизвестная ошибка")
                }
            }
        }
    }
}
