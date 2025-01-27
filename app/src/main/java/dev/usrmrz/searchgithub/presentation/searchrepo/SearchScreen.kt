package dev.usrmrz.searchgithub.presentation.searchrepo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.usrmrz.searchgithub.data.database.entity.RepoEntity

@Composable
fun SearchScreen(viewModel: RepoViewModel) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        var query by remember { mutableStateOf("") }

        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Введите запрос") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { viewModel.onEvent(Event.Search(query)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Find")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            is State.Loading -> Text("Loading...")
            is State.Success -> {
                val repos = (state as State.Success<List<RepoEntity>>).data
                LazyColumn {
                    items(repos) { repo ->
                        Text(repo.name, style = MaterialTheme.typography.titleMedium)
                        Text(repo.description ?: "No Description", style = MaterialTheme.typography.body2)
                        Text("⭐ ${repo.stars}", style = MaterialTheme.typography.caption)
                        Divider()
                    }
                }
            }
            is State.Error -> Text("Ошибка: ${(state as State.Error).message}")
        }
    }
}
