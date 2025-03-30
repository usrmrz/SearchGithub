package dev.usrmrz.searchgithub.presentation.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ForkLeft
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.usrmrz.searchgithub.R
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.Status
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
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
        "SScr",
        "1_isEqual query&results: query: $query; results: $results loadMoreStatus: $loadMoreStatus"
    )
    Log.d(
        "SScr",
        "2_searchText: $searchText keyboardController: $keyboardController listState: $listState"
    )

    LaunchedEffect(listState) {
//        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
//            .map { visibleItems -> visibleItems.lastOrNull()?.index }
//            .collectLatest { lastVisibleItem ->
//                if(lastVisibleItem != null && lastVisibleItem >= results.data!!.size - 1) {

        Log.d("SScr", "LaunchedEffect(key1: listState);;listState: $listState")
        snapshotFlow { listState.firstVisibleItemIndex to listState.layoutInfo.totalItemsCount }
            .distinctUntilChanged()
            .collectLatest { (firstIndex, totalItems) ->
                if(totalItems > 0 && firstIndex + listState.layoutInfo.visibleItemsInfo.size >= totalItems - 1) {
                    Log.d(
                        "SScr",
                        "if(totalItems > 0 && firstIndex + listState.layoutInfo.visibleItemsInfo.size >= totalItems - 1);;listState: $listState; firstIndex: $firstIndex; totalItems: $totalItems;  size: ${listState.layoutInfo.visibleItemsInfo.size}; results: $results results.data: ${results.data?.size?.minus(1)}"
                    )
                    viewModel.loadNextPage()
                }
            }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        TopAppBar(
            expandedHeight = 20.dp,
            title = { Text("GitHub Repositories", style = MaterialTheme.typography.titleLarge) },
        )
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text(stringResource(R.string.search_hint)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.setQuery(searchText)
                }
            )
        )

        Log.d("SScr", "OutlinedTextField(value = searchText;;listState: $listState")

        if(results.status == Status.LOADING) {
            Log.d(
                "SScr",
                "results.status == Status.LOADING;;results: $results, results.st: ${results.status}"
            )
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
            Log.d(
                "SScr",
                "results.status == Status.SUCCESS && results.data.isNullOrEmpty();;results: $results, results.st: ${results.status}"
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
            Log.d(
                "SScr",
                "if(loadMoreStatus.isRunning) {;;results: $results, results.st: ${results.status}"
            )
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

//        val snackbarHostState = remember { SnackbarHostState() }
//        val snackbarCoroutineScope = rememberCoroutineScope()

//        loadMoreStatus.errorMessageIfNotHandled.collectAsState().value?.let { errorMessage ->
        loadMoreStatus.errorMessageIfNotHandled?.let { errorMessage ->
            LaunchedEffect(errorMessage) {
                SnackbarHostState().showSnackbar(errorMessage)
//                snackbarCoroutineScope.launch {
//                    snackbarHostState.showSnackbar(errorMessage)
//                    loadMoreStatus.errorMessageIfNotHandled.value = null
//                }

            }
        }
    }
    Log.d(
        "SScr",
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
            Row(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = repo.owner.login + "/",
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = repo.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = repo.description ?: stringResource(R.string.no_description),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) { DateSection(repo) }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) { StatsSection(repo) }

        }
    }
}

@Composable
fun DateSection(repo: Repo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DateItem(
            icon = Icons.Filled.AccessTime,
            label = stringResource(R.string.created_at),
            value = repo.createdAt.toString()
        )
        DateItem(
            icon = Icons.Filled.AccessTime,
            label = stringResource(R.string.update_at),
            value = "${repo.updatedAt}"
        )
    }
}

@Composable
fun DateItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(imageVector = icon, contentDescription = label)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth()
//        ) {
//            HeaderSection()
//            Spacer(modifier = Modifier.height(16.dp))
//            DescriptionSection()
//            Spacer(modifier = Modifier.height(16.dp))
//            StatsSection()
//        }
//    }
//}
//@Composable
//fun HeaderSection() {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.Top
//    ) {
//        Column {
//            Text(
//                text = "HITsz-TMG/",
//                style = MaterialTheme.typography.titleLarge
//            )
//            Text(
//                text = "FilmAgent",
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold
//            )
//        }
//        Image(
//            painter = rememberAsyncImagePainter("https://path-to-your-image.com/image.png"),
//            contentDescription = null,
//            modifier = Modifier
//                .size(60.dp)
//                .padding(4.dp),
//            contentScale = ContentScale.Crop
//        )
//    }
//}
//@Composable
//fun DescriptionSection() {
//    Text(
//        text = "Resources of our paper \"FilmAgent: A Multi-Agent Framework for End-to-End Film Automation in Virtual 3D Spaces\".",
//        style = MaterialTheme.typography.bodyMedium
//    )
//}
@Composable
fun StatsSection(repo: Repo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            icon = Icons.Filled.Group,
            label = stringResource(R.string.watchers),
            value = repo.watchers.toString()
        )
        StatItem(
            icon = Icons.Filled.Error,
            label = stringResource(R.string.issues),
            value = "${repo.issues}"
        )
        StatItem(
            icon = Icons.Filled.Star,
            label = stringResource(R.string.stars),
            value = repo.stars.toString()
        )
        StatItem(
            icon = Icons.Filled.ForkLeft,
            label = stringResource(R.string.forks),
            value = repo.forks.toString()
        )
    }
}

@Composable
fun StatItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(imageVector = icon, contentDescription = label)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}