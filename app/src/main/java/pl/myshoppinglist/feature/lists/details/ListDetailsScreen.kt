package pl.myshoppinglist.feature.lists.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import pl.myshoppinglist.data.local.entity.ShoppingItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListDetailScreen(
    navController: NavController,
    viewModel: ListDetailsViewModel = hiltViewModel()
) {
    val items by viewModel.itemsFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.listName ?: "Lista") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Brak produktów")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items, key = { it.id }) { item ->
                        ShoppingListItemRow(item = item,
                            onCheck = { checked -> viewModel.toggleChecked(item.id, checked) },
                            onDelete = { viewModel.deleteItem(item) }
                        )
                    }
                }
            }

            // prosty formularz dodawania na dole
            AddItemRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp),
                onAdd = { name, qty ->
                    viewModel.addItem(name, qty)
                }
            )
        }
    }
}

@Composable
private fun ShoppingListItemRow(item: ShoppingItemEntity, onCheck: (Boolean) -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = item.checked, onCheckedChange = onCheck)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(text = item.name)
                if (item.unit.isNotEmpty()) Text(text = "${item.quantity} ${item.unit}", style = MaterialTheme.typography.bodySmall)
                else Text(text = "Ilość: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Usuń")
            }
        }
    }
}

@Composable
private fun AddItemRow(modifier: Modifier = Modifier, onAdd: (name: String, qty: Double) -> Unit) {
    var text by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("1") }

    Surface(modifier = modifier.fillMaxWidth()) {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Nazwa produktu") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = qty,
                onValueChange = { qty = it.filter { ch -> ch.isDigit() || ch == '.' } },
                singleLine = true,
                modifier = Modifier.width(80.dp),
                placeholder = { Text("Ilość") }
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val q = qty.toDoubleOrNull() ?: 1.0
                if (text.isNotBlank()) {
                    onAdd(text.trim(), q)
                    text = ""
                    qty = "1"
                }
            }) {
                Text("Dodaj")
            }
        }
    }
}
