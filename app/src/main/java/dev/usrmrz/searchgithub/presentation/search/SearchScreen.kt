package dev.usrmrz.searchgithub.presentation.search

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.usrmrz.searchgithub.R
import dev.usrmrz.searchgithub.domain.model.RepoModel
import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse

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
            Text("There is ${data.total} repositories with query \"query\"")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = modifier,
        ) {
            val sampleData = RepoSearchResponse(
                query = "kotlin",
                total = 123456,
                repoIds = listOf(1,2,3,4,5),
                next = 2
            )
//            val sample = listOf(
//                RepoModel(1, "Repo1", "Name1/Repo1", , "Name1", ),
//                RepoModel(2, "Repo2", "Name2/Repo2", "Description1", "Name1"),
//                Repo(3, "Repo3", "Name3/Repo3", "Description1", "Name1),
//                Repo(4, "Repo4", "Name4/Repo4", "Description1",),
//                RepoModel(5, "Repo5", "Name5/Repo5", "Description1",),
//            )
            LazyColumn {
//                items(data.repoIds) { repo ->
//                    RepoItem(sample.repoIds)
//                }
            }
        }
    }
}

@Composable
fun RepoItem(repoModel: RepoModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = repoModel.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = repoModel.description ?: "No description",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "⭐ ${repoModel.stars}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

//    Column(modifier = Modifier.padding(8.dp)) {
//        Text("ID: ${repoModel.id}", style = MaterialTheme.typography.bodyMedium)
//        Text("Name: ${repoModel.name}", style = MaterialTheme.typography.titleMedium)
//        Text("Description: ${repoModel.description ?: "No description"}")
//        Text("Stars: ${repoModel.stars}")
//    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoadingScreenPreview() {
//    SearchGithubTheme {
//        LoadingScreen()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ErrorScreenPreview() {
//    SearchGithubTheme {
//        ErrorScreen()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ResultScreenPreview() {
//    SearchGithubTheme {
//        val sampleData = RepoSearchResponse(
//            total = 123456,
//            items = listOf(
//                RepoModel(101, "Repo1", "Description1", 100),
//                RepoModel(102, "Repo2", "Description2", 150),
//                RepoModel(103, "Repo3", "Description3", 200),
//                RepoModel(104, "Repo4", "Description4", 300),
//                RepoModel(105, "Repo5", "Description5", 500),
//            )
//        )
//        ResultScreen(
//            data = sampleData,
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}
