package dev.usrmrz.searchgithub.presentation.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.Status
import dev.usrmrz.searchgithub.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onRepoClick: (String, String) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val loadMoreStatus by viewModel.loadMoreStatus.collectAsState()

    var searchText by rememberSaveable { mutableStateOf(query) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    Log.d(
        "vals from SScr1",
        "query: $query; results: $results loadMoreStatus: $loadMoreStatus searchText: $searchText"
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibleItems -> visibleItems.lastOrNull()?.index }
            .distinctUntilChanged()
            .collectLatest { lastVisibleItem ->
                if(lastVisibleItem != null && lastVisibleItem >= results.data?.size.orZero() - 1) {
                    viewModel.loadNextPage()
                }
            }
    }
    Log.d(
        "vals from SScr2",
        "query: $query; results: $results loadMoreStatus: $loadMoreStatus searchText: $searchText"
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_hint)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    viewModel.setQuery(searchText)
                }
            )
        )

        if(results.status == Status.LOADING) {
            Log.d("SrchS_if", "results: $results, results.st: ${results.status}")
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        if(results.status == Status.SUCCESS && results.data.isNullOrEmpty()) {
            Text(
                text = stringResource(R.string.empty_search_result, query),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            items(results.data.orEmpty()) { repo ->
                RepoItem(repo = repo, onClick = { onRepoClick(repo.owner.login, repo.name) })
            }
        }

        if(loadMoreStatus.isRunning) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        loadMoreStatus.errorMessageIfNotHandled?.let { errorMessage ->
            LaunchedEffect(errorMessage) {
                SnackbarHostState().showSnackbar(errorMessage)
            }
        }
    }
    Log.d(
        "vals from SScr3",
        "query: $query; results: $results loadMoreStatus: $loadMoreStatus searchText: $searchText"
    )
}

@Composable
fun RepoItem(repo: Repo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = repo.fullName, fontWeight = FontWeight.Bold)
            Text(text = repo.description ?: stringResource(R.string.no_description))
        }
    }
}

private fun Int?.orZero(): Int = this ?: 0
