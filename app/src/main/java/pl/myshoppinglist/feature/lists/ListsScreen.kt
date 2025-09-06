package pl.myshoppinglist.feature.lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun ListsScreen(
    navController: NavController,
    vm: ListsViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    var dialogOpen by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { dialogOpen = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Nowa lista")
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            if (state.lists.isEmpty()) {
                EmptyState(onAdd = { dialogOpen = true })
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.lists, key = { it.id }) { item ->
                        ElevatedCard(
                            onClick = { /* TODO: przejście do pozycji listy */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                TextButton(onClick = { vm.archive(item.id) }) { Text("Archiwizuj") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (dialogOpen) {
        AlertDialog(
            onDismissRequest = { dialogOpen = false },
            confirmButton = {
                TextButton(
                    enabled = newName.isNotBlank(),
                    onClick = {
                        vm.addList(newName.trim())
                        newName = ""
                        dialogOpen = false
                    }
                ) { Text("Dodaj") }
            },
            dismissButton = { TextButton(onClick = { dialogOpen = false }) { Text("Anuluj") } },
            title = { Text("Nowa lista") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Np. Weekend, Obiady, Impreza…") }
                )
            }
        )
    }
}

@Composable
private fun EmptyState(onAdd: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Nie masz jeszcze żadnych list.",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = onAdd) { Text("Utwórz pierwszą listę") }
    }
}
