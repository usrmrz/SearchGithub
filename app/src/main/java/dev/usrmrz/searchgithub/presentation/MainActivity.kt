package dev.usrmrz.searchgithub.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.usrmrz.searchgithub.presentation.searchrepo.RepoViewModel
import dev.usrmrz.searchgithub.presentation.searchrepo.SearchScreen
import dev.usrmrz.searchgithub.presentation.ui.theme.SearchGithubTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchGithubTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                ) {
                    val repoViewModel: RepoViewModel = viewModel()
                    SearchScreen(
                        gitUiState = repoViewModel.gitUiState
                    )
                }
            }
        }
    }
}

