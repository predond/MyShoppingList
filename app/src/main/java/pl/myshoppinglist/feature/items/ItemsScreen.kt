package pl.myshoppinglist.feature.items

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.myshoppinglist.data.local.entity.ShoppingItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(viewModel: ItemsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    var isGrid by remember { mutableStateOf(false) }
    var grouping by remember { mutableStateOf(GroupingMode.NONE) }
    var showDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ShoppingItemEntity?>(null) }

    /*var expandedGroups by remember { mutableStateOf(setOf<Long>()) }
    fun toggleExpanded(id: Long) {
        expandedGroups = if (expandedGroups.contains(id)) expandedGroups - id else expandedGroups + id
    }*/
    var expandedGroup by remember { mutableStateOf<Long?>(null) }
    fun toggleExpanded(id: Long) {
        expandedGroup = if (expandedGroup == id) null else id
    }

    Scaffold(
        topBar = {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(36.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(18.dp))
            ) {
                GroupingMode.values().forEach { mode ->
                    val selected = grouping == mode
                    TextButton(
                        onClick = { grouping = mode },
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(18.dp)
                            )
                    ) {
                        Text(
                            text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            val items = when (grouping) {
                GroupingMode.NONE -> state.items
                GroupingMode.CATEGORY -> state.items.sortedBy { it.categoryId ?: Long.MAX_VALUE }
                GroupingMode.STORAGE -> state.items.sortedBy { it.storageId ?: Long.MAX_VALUE }
            }

            if (grouping == GroupingMode.NONE) {
                // zachowujemy płaskie wyświetlanie (twój oryginalny kod)
                if (isGrid) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        gridItems(items) { item ->
                            GridItemCard(
                                item = item,
                                onEdit = { editingItem = it; showDialog = true },
                                onDelete = { viewModel.deleteItem(it) },
                                onQuantityChange = { newQ -> viewModel.updateItem(item.copy(quantity = newQ)) }
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
                                onQuantityChange = { newQ -> viewModel.updateItem(item.copy(quantity = newQ)) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            } else {
                // grouping == CATEGORY or STORAGE -> przygotuj listę grup (pair: id -> lista produktów)
                val groups: List<Pair<Long, List<ShoppingItemEntity>>> = when (grouping) {
                    GroupingMode.CATEGORY -> {
                        val allCategories = state.categories
                        val mapped = allCategories.map { cat -> cat.id to state.items.filter { it.categoryId == cat.id } }.toMutableList()
                        val without = state.items.filter { it.categoryId == null || allCategories.none { c -> c.id == it.categoryId } }
                        if (without.isNotEmpty()) mapped.add(0L to without) // 0L = brak kategorii
                        mapped
                    }
                    GroupingMode.STORAGE -> {
                        val allStorages = state.storages
                        val mapped = allStorages.map { s -> s.id to state.items.filter { it.storageId == s.id } }.toMutableList()
                        val without = state.items.filter { it.storageId == null || allStorages.none { st -> st.id == it.storageId } }
                        if (without.isNotEmpty()) mapped.add(0L to without) // 0L = brak magazynu
                        mapped
                    }
                    else -> emptyList()
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    lazyItems(groups) { (groupId, groupItems) ->
                        val title = when (grouping) {
                            GroupingMode.CATEGORY -> state.categories.find { it.id == groupId }?.name ?: "Brak kategorii"
                            GroupingMode.STORAGE -> state.storages.find { it.id == groupId }?.name ?: "Brak magazynu"
                            else -> ""
                        }

                        GroupSection(
                            title = title,
                            groupId = groupId,
                            items = groupItems,
                            /*expanded = expandedGroups.contains(groupId),*/
                            expanded = expandedGroup == groupId,
                            onToggle = { toggleExpanded(groupId) },
                            isGrid = isGrid,
                            onEdit = { editingItem = it; showDialog = true },
                            onDelete = { viewModel.deleteItem(it) },
                            onItemQuantityChange = { item, newQ -> viewModel.updateItem(item.copy(quantity = newQ)) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddEditItemDialog(
            initialItem = editingItem,
            categories = state.categories,
            storages = state.storages,
            onDismiss = { showDialog = false },
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
    onQuantityChange: (Double) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = {}, // normalne kliknięcie nic
                onLongClick = { onEdit(item) }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (item.quantity > 1) onQuantityChange(item.quantity - 1) }) {
                Icon(Icons.Default.Remove, contentDescription = "Zmniejsz ilość")
            }
            Text("${item.quantity} ${item.unit}", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                Icon(Icons.Default.Add, contentDescription = "Zwiększ ilość")
            }
        }

        IconButton(onClick = { onDelete(item) }) {
            Icon(Icons.Default.Delete, contentDescription = "Usuń")
        }
    }
}

@Composable
private fun GridItemCard(
    item: ShoppingItemEntity,
    onEdit: (ShoppingItemEntity) -> Unit,
    onDelete: (ShoppingItemEntity) -> Unit,
    onQuantityChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { onEdit(item) }
            )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 4.dp).weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Ilość i kosz w jednym rzędzie na dole
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (item.quantity > 1) onQuantityChange(item.quantity - 1) }) {
                        Icon(Icons.Default.Remove, contentDescription = "−")
                    }
                    Text("${item.quantity} ${item.unit}")
                    IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                        Icon(Icons.Default.Add, contentDescription = "+")
                    }
                }
                IconButton(onClick = { onDelete(item) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Usuń")
                }
            }
        }
    }
}

@Composable
private fun GroupSection(
    title: String,
    groupId: Long,
    items: List<ShoppingItemEntity>,
    expanded: Boolean,
    onToggle: () -> Unit,
    isGrid: Boolean,
    onEdit: (ShoppingItemEntity) -> Unit,
    onDelete: (ShoppingItemEntity) -> Unit,
    onItemQuantityChange: (ShoppingItemEntity, Double) -> Unit
) {
    val dividerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    Card(modifier = Modifier.fillMaxWidth()) {
        // Usunąć animationSpec, żeby było ładogne przejście zamiast odbicia !!!!!
        Column(modifier = Modifier.fillMaxWidth().animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))) {
            // Nagłówek grupy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    Text(text = "${items.size} produktów", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Zwiń" else "Rozwiń"
                    )
                }
            }

            // kreska oddzielająca nagłówek od listy
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = dividerColor)

            if (expanded) {
                if (isGrid) {
                    // grid + dolna kreska
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 140.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 0.dp, max = 400.dp)
                            .padding(8.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        gridItems(items) { item ->
                            GridItemCard(
                                item = item,
                                onEdit = onEdit,
                                onDelete = onDelete,
                                onQuantityChange = { newQ -> onItemQuantityChange(item, newQ) }
                            )
                        }
                    }
                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = dividerColor)
                } else {
                    // lista: kreski między elementami oraz kreska na końcu grupy
                    Column(modifier = Modifier.fillMaxWidth()) {
                        items.forEachIndexed { index, item ->
                            // odstęp/padding wewnętrzny
                            Column(modifier = Modifier.fillMaxWidth()) {
                                ItemRow(
                                    item = item,
                                    onEdit = onEdit,
                                    onDelete = onDelete,
                                    onQuantityChange = { newQ -> onItemQuantityChange(item, newQ) }
                                )
                            }

                            // kreska między elementami (nie dodajemy dodatkowego odstępu)
                            if (index < items.lastIndex) {
                                Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = dividerColor)
                            }
                        }
                        // dolna kreska grupy (oddziela od następnej sekcji)
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = dividerColor)
                    }
                }
            }
        }
    }
}
