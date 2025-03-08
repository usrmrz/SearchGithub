package dev.usrmrz.searchgithub.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.usrmrz.searchgithub.presentation.repo.RepoScreen
import dev.usrmrz.searchgithub.presentation.search.SearchScreen
import dev.usrmrz.searchgithub.presentation.user.UserScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "search") {

        composable("search") {
            SearchScreen(
                onRepoClick = { owner, name ->
                    navController.navigate("repo/$owner/$name")
                }
            )
        }

        composable(
            route = "repo/{owner}/{name}",
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: return@composable
            val name = backStackEntry.arguments?.getString("name") ?: return@composable
            RepoScreen(
                name = name,
                owner = owner,
                onUserClick = { login, avatarUrl ->
                    val avatarPart = avatarUrl.let { "/$it" }
                    navController.navigate("user/$login$avatarPart")
                }
            )
        }

        composable(
            route = "user/{login}/{avatarUrl}?",
            arguments = listOf(
                navArgument("login") { type = NavType.StringType },
                navArgument("avatarUrl") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val login = backStackEntry.arguments?.getString("login") ?: return@composable
            val avatarUrl = backStackEntry.arguments?.getString("avatarUrl")
            UserScreen(
                login = login,
                avatarUrl = avatarUrl.toString(),
                onRepoClick = { owner, name ->
                    navController.navigate("repo/$owner/$name")
                }
            )
        }
    }
}
