package dev.usrmrz.searchgithub.presentation.searchrepo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.usrmrz.searchgithub.R
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse
import dev.usrmrz.searchgithub.presentation.ui.theme.SearchGithubTheme

@Composable
fun SearchScreen(
    gitUiState: GitUiState,
    modifier: Modifier = Modifier,
) {
    when(gitUiState) {
        is GitUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is GitUiState.Success -> ResultScreen(
            gitUiState.data, modifier = modifier.fillMaxWidth()
        )
        is GitUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun ResultScreen(data: RepoSearchResponse, modifier: Modifier = Modifier) {
    Column {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Text("There is ${data.total} repositories")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = modifier,
//            horizontalArrangement = Arrangement.Center
        ) {
            LazyColumn {
                items(data.items) { repo ->
                    RepoItem(repo)
                }
            }
        }
    }
}

@Composable
fun RepoItem(repo: Repo) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text("ID: ${repo.id}", style = MaterialTheme.typography.bodyMedium)
        Text("Name: ${repo.name}", style = MaterialTheme.typography.titleMedium)
        Text("Description: ${repo.description ?: "No description"}")
        Text("Stars: ${repo.stars}")
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    SearchGithubTheme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    SearchGithubTheme {
        ErrorScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    SearchGithubTheme {
        val sampleData = RepoSearchResponse(
            total = 123456,
            items = listOf(
                Repo(101, "Repo1", "Description1", 100),
                Repo(102, "Repo2", "Description2", 150),
                Repo(103, "Repo3", "Description3", 200),
                Repo(104, "Repo4", "Description4", 300),
                Repo(105, "Repo5", "Description5", 500),
            )
        )
        ResultScreen(
            data = sampleData,
            modifier = Modifier.fillMaxSize()
        )
    }
}
