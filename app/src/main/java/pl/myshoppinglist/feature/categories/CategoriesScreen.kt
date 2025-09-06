package pl.myshoppinglist.feature.categories

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import pl.myshoppinglist.data.local.entity.CategoryEntity

@Composable
fun CategoriesScreen(viewModel: CategoryViewModel) {
    val categories by viewModel.categories.collectAsState()

    var newCategoryName by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var editName by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                placeholder = { Text("Nowa kategoria") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                viewModel.addCategory(newCategoryName)
                                newCategoryName = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Dodaj kategorię")
                    }
                }
            )

        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(categories) { category ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // delikatny cień
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // neutralne tło
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            category.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f) // zajmuje całą dostępną szerokość
                        )

                        IconButton(onClick = {
                            editCategory = category
                            editName = category.name
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                        }

                        IconButton(onClick = { viewModel.deleteCategory(category) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń")
                        }
                    }
                }
            }
        }
    }

    // Dialog edycji nazwy
    if (editCategory != null) {
        AlertDialog(
            onDismissRequest = { editCategory = null },
            title = { Text("Edytuj kategorię") },
            text = {
                TextField(
                    value = editName,
                    onValueChange = { editName = it },
                    placeholder = { Text("Nazwa kategorii") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    editCategory?.let { cat ->
                        if (editName.isNotBlank()) {
                            viewModel.updateCategory(cat.copy(name = editName))
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
}
