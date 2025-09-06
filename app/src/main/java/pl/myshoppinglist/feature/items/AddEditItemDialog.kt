package pl.myshoppinglist.feature.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pl.myshoppinglist.data.local.entity.CategoryEntity
import pl.myshoppinglist.data.local.entity.ShoppingItemEntity
import pl.myshoppinglist.data.local.entity.StorageEntity

@Composable
fun AddEditItemDialog(
    initialItem: ShoppingItemEntity?,
    categories: List<CategoryEntity>,
    storages: List<StorageEntity>,
    onDismiss: () -> Unit,
    onSave: (ShoppingItemEntity) -> Unit,
    // suspend functions: tworzą i zwracają id nowej encji
    createCategory: suspend (name: String, colorHex: Int) -> Long,
    createStorage: suspend (name: String) -> Long
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var qty by remember { mutableStateOf(initialItem?.quantity?.toString() ?: "1") }
    var unit by remember { mutableStateOf(initialItem?.unit ?: "") }

    var selectedCategoryId by remember { mutableStateOf(initialItem?.categoryId) }
    var selectedStorageId by remember { mutableStateOf(initialItem?.storageId) }

    // control for nested dialogs
    var showNewCategory by remember { mutableStateOf(false) }
    var showNewStorage by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val qtyDouble = qty.toDoubleOrNull() ?: 1.0
                val entity = ShoppingItemEntity(
                    id = initialItem?.id ?: 0L,
                    name = name.trim(),
                    quantity = qtyDouble,
                    unit = unit.trim(),
                    checked = initialItem?.checked ?: false,
                    categoryId = selectedCategoryId,
                    storageId = selectedStorageId
                )
                onSave(entity)
            }) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
        text = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {

                TextField(value = name, onValueChange = { name = it }, label = { Text("Nazwa") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(value = qty, onValueChange = { qty = it }, label = { Text("Ilość") }, modifier = Modifier.weight(1f))
                    TextField(value = unit, onValueChange = { unit = it }, label = { Text("Jednostka") }, modifier = Modifier.width(120.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))

                // KATEGORIA: dropdown (lewa szeroka część) + button '+' (prawo)
                Text("Kategoria", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    // left: dropdown
                    DropdownSelector(
                        items = categories.map { it.name },
                        selectedIndex = categories.indexOfFirst { it.id == selectedCategoryId }.takeIf { it >= 0 } ?: -1,
                        onSelect = { idx -> if (idx >= 0) selectedCategoryId = categories[idx].id }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // right: plus tile
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { showNewCategory = true },
                        shape = RoundedCornerShape(8.dp),
                        tonalElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = "Nowa kategoria")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // MAGAZYN: dropdown + plus
                Text("Magazyn", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    DropdownSelector(
                        items = storages.map { it.name },
                        selectedIndex = storages.indexOfFirst { it.id == selectedStorageId }.takeIf { it >= 0 } ?: -1,
                        onSelect = { idx -> if (idx >= 0) selectedStorageId = storages[idx].id }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { showNewStorage = true },
                        shape = RoundedCornerShape(8.dp),
                        tonalElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = "Nowy magazyn")
                        }
                    }
                }
            }
        }
    )

    // Nested dialog: New Category
    if (showNewCategory) {
        CategoryDialog(
            onDismiss = { showNewCategory = false },
            onSave = { name, colorHex ->
                coroutineScope.launch {
                    val newId = createCategory(name, colorHex)
                    selectedCategoryId = newId
                    showNewCategory = false
                }
            }
        )
    }

    // Nested dialog: New Storage
    if (showNewStorage) {
        StorageDialog(
            onDismiss = { showNewStorage = false },
            onSave = { name ->
                coroutineScope.launch {
                    val newId = createStorage(name)
                    selectedStorageId = newId
                    showNewStorage = false
                }
            }
        )
    }
}
