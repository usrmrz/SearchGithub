package dev.usrmrz.searchgithub.presentation.search

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import dev.usrmrz.searchgithub.domain.model.Resource


@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onRepoClick: (String, String) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val loadMoreState by viewModel.loadMoreState.collectAsState()

    Column {
        SearchBar(
            query = query,
            onQueryChange = { viewModel.setQuery(it) }
        )

        when (results) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Success -> RepoList(
                repos = results.data ?: emptyList(),
                onRepoClick = onRepoClick,
                onLoadMore = { viewModel.loadNextPage() }
            )
            is Resource.Error -> Text("Error: ${results.message}")
        }

        if (loadMoreState.isRunning) {
            CircularProgressIndicator()
        }
    }
}
