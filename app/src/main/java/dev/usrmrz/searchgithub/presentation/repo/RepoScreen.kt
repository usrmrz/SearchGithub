package dev.usrmrz.searchgithub.presentation.repo

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dev.usrmrz.searchgithub.presentation.search.SearchViewModel

@Composable
fun RepoScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onUserClick: (String, String) -> Unit,
    name: String,
    owner: String
) {
    Log.d("RepoScreen", "Name: $name Owner: $owner, viewModel: $viewModel, onUserClick: $onUserClick")
    println("$name and $owner")
}