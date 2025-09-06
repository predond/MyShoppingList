package pl.myshoppinglist.feature.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import pl.myshoppinglist.data.local.entity.ShoppingItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(viewModel: ItemsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    var isGrid by remember { mutableStateOf(true) }
    var grouping by remember { mutableStateOf(GroupingMode.NONE) }
    var showDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ShoppingItemEntity?>(null) }

    Scaffold(
        topBar = {
            // prosty topBar (tytuł może być pusty — tytuł wyświetlamy poniżej jako część content)
            TopAppBar(title = { Text("") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editingItem = null; showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // TYTUŁ + akcje w jednym rzędzie, tytuł po lewej, akcje po prawej
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Produkty (wszystkie)", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { isGrid = !isGrid }) {
                        Icon(imageVector = if (isGrid) Icons.Default.ViewList else Icons.Default.GridView, contentDescription = "Toggle layout")
                    }
                    IconButton(onClick = { editingItem = null; showDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Dodaj produkt")
                    }
                }
            }

            // PASEK GRUPOWANIA pod tytułem
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = { grouping = GroupingMode.NONE }) { Text("Brak") }
                TextButton(onClick = { grouping = GroupingMode.CATEGORY }) { Text("Kategoria") }
                TextButton(onClick = { grouping = GroupingMode.STORAGE }) { Text("Magazyn") }
                Spacer(modifier = Modifier.weight(1f))
            }

            // TREŚĆ — lista / siatka
            val items = when (grouping) {
                GroupingMode.NONE -> state.items
                GroupingMode.CATEGORY -> state.items.sortedBy { it.categoryId ?: Long.MAX_VALUE }
                GroupingMode.STORAGE -> state.items.sortedBy { it.storageId ?: Long.MAX_VALUE }
            }

            if (isGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    gridItems(items) { item ->
                        ItemRow(
                            item = item,
                            onEdit = { editingItem = it; showDialog = true },
                            onDelete = { viewModel.deleteItem(it) },
                            onToggleChecked = { viewModel.toggleChecked(it) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    lazyItems(items) { item ->
                        ItemRow(
                            item = item,
                            onEdit = { editingItem = it; showDialog = true },
                            onDelete = { viewModel.deleteItem(it) },
                            onToggleChecked = { viewModel.toggleChecked(it) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // DIALOG ADD / EDIT
    if (showDialog) {
        AddEditItemDialog(
            initialItem = editingItem,
            categories = state.categories,
            storages = state.storages,
            onDismiss = { showDialog = false },

            // dialog potrzebuje funkcji suspend do tworzenia nowych kategorii/magazynów
            createCategory = viewModel::createCategory,
            createStorage = viewModel::createStorage,

            onSave = { entity ->
                if (entity.id == 0L) viewModel.insertItem(entity) else viewModel.updateItem(entity)
                showDialog = false
            }
        )
    }
}

enum class GroupingMode { NONE, CATEGORY, STORAGE }

@Composable
private fun ItemRow(
    item: ShoppingItemEntity,
    onEdit: (ShoppingItemEntity) -> Unit,
    onDelete: (ShoppingItemEntity) -> Unit,
    onToggleChecked: (ShoppingItemEntity) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${item.quantity} ${item.unit}", style = MaterialTheme.typography.bodySmall)
            }
            Column {
                IconButton(onClick = { onToggleChecked(item) }) {
                    Checkbox(checked = item.checked, onCheckedChange = { onToggleChecked(item) })
                }
                TextButton(onClick = { onEdit(item) }) { Text("Edytuj") }
                TextButton(onClick = { onDelete(item) }) { Text("Usuń") }
            }
        }
    }
}
