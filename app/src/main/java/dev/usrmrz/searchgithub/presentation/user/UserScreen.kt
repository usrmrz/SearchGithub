package dev.usrmrz.searchgithub.presentation.user

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dev.usrmrz.searchgithub.presentation.search.SearchViewModel

@Composable
fun UserScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onRepoClick: (String, String) -> Unit,
    login: String,
    avatarUrl: String,
) {
    Log.d("UserScreen", "login: $login avatarUrl: $avatarUrl")
    println("$login, $onRepoClick, $viewModel and $avatarUrl")
}