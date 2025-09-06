package pl.myshoppinglist.feature.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.myshoppinglist.data.local.entity.CategoryEntity
import pl.myshoppinglist.feature.items.CategoryDialog
import pl.myshoppinglist.feature.lists.ColorPickerRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(viewModel: CategoriesViewModel = hiltViewModel()) {
    val categories by viewModel.categories.collectAsState()
    var editCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var editName by remember { mutableStateOf("") }

    // kontrolka do otwierania dialogu dodawania
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kategorie") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj kategorię",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // zostaw miejsce na FAB
            ) {
                items(categories) { category ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(category.color).copy(alpha = 0.9f),
                                            Color(category.color).copy(alpha = 0.5f)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                category.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = {
                                editCategory = category
                                editName = category.name
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edytuj", tint = MaterialTheme.colorScheme.onPrimary)
                            }

                            IconButton(onClick = { viewModel.deleteCategory(category) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog edycji nazwy
    if (editCategory != null) {
        var editedColor by remember { mutableStateOf(Color(editCategory!!.color)) }

        AlertDialog(
            onDismissRequest = { editCategory = null },
            title = { Text("Edytuj kategorię") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextField(
                        value = editName,
                        onValueChange = { editName = it },
                        placeholder = { Text("Nazwa kategorii") }
                    )

                    Text("Wybierz kolor:")
                    ColorPickerRow(
                        selected = editedColor,
                        onSelect = { editedColor = it }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    editCategory?.let { cat ->
                        if (editName.isNotBlank()) {
                            viewModel.updateCategory(cat.copy(name = editName.trim(), color = editedColor.toArgb()))
                        }
                    }
                    editCategory = null
                }) {
                    Text("Zapisz")
                }
            },
            dismissButton = {
                TextButton(onClick = { editCategory = null }) {
                    Text("Anuluj")
                }
            }
        )
    }

    // Dialog dodawania nowej kategorii (otwierany z FAB)
    if (showAddDialog) {
        CategoryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, color ->
                if (name.isNotBlank()) {
                    // jeśli viewModel.addCategory ma inny podpis (np. z kolorem) - dopasuj
                    viewModel.addCategory(name.trim(), color)
                }
                showAddDialog = false
            }
        )
    }
}
