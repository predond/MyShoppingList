package pl.myshoppinglist.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.myshoppinglist.feature.categories.CategoriesScreen
import pl.myshoppinglist.feature.items.ItemsScreen
import pl.myshoppinglist.feature.lists.ListsScreen
import pl.myshoppinglist.feature.lists.details.ShoppingListDetailScreen
import pl.myshoppinglist.feature.settings.SettingsScreen
import pl.myshoppinglist.feature.storages.StoragesScreen

@Composable
fun MyShoppingListApp() {
    val navController = rememberNavController()
    val backstack by navController.currentBackStackEntryAsState()
    val currentRoute = backstack?.destination?.route

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TopDestinations.forEach { dest ->
                item(
                    selected = currentRoute == dest.route,
                    onClick = {
                        if (currentRoute != dest.route) {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                    label = { Text(dest.label) }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Dest.Lists.route,
            modifier = Modifier // <- usuń paddingValues
        ) {
            composable(Dest.Lists.route) { ListsScreen(navController) }
            composable(
                route = "shopping_list/{listId}",
                arguments = listOf(navArgument("listId") { type = NavType.LongType })
            ) { backStackEntry ->
                val navController = navController // używasz istniejącego navController
                ShoppingListDetailScreen(navController = navController)
            }
            composable(Dest.Items.route) { ItemsScreen() }
            composable(Dest.Categories.route) { CategoriesScreen() }
            composable(Dest.Storages.route) { StoragesScreen() }
            composable(Dest.Settings.route) { SettingsScreen() }
        }
    }
}
