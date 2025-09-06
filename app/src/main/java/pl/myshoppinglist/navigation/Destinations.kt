package pl.myshoppinglist.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Dest(val route: String, val label: String, val icon: ImageVector) {
    data object Lists : Dest("lists", "Listy", Icons.Outlined.ListAlt)
    data object Items : Dest("items", "Pozycje", Icons.Outlined.ShoppingCart)
    data object Categories : Dest("categories", "Kategorie", Icons.Outlined.Category)
    data object Settings : Dest("settings", "Ustawienia", Icons.Outlined.Settings)
}

val TopDestinations = listOf(Dest.Lists, Dest.Items, Dest.Categories, Dest.Settings)
