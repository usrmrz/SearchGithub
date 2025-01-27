package dev.usrmrz.searchgithub.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.usrmrz.searchgithub.presentation.searchrepo.RepoViewModel
import dev.usrmrz.searchgithub.presentation.searchrepo.SearchScreen
import dev.usrmrz.searchgithub.presentation.ui.theme.SearchGithubTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SearchGithubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val repoViewModel: RepoViewModel = viewModel()
                    SearchScreen(
                        gitUiState = repoViewModel.gitUiState
                    )
                    
                    
//                    SearchScreen(
//                        gitUiState = GitUiState.Loading
//                    )
                }
            }
        }
    }
}

