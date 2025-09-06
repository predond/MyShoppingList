package pl.myshoppinglist.feature.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
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
    var selectedItemForEdit by remember { mutableStateOf<ListItem?>(null) }
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
                        val bgColor = Color(item.backgroundColor)
                        val gradientBrush = Brush.horizontalGradient(
                            colors = listOf(
                                bgColor.copy(alpha = 0.5f), // 70% opacity po lewej
                                bgColor.copy(alpha = 0.3f),
                                bgColor.copy(alpha = 0.05f),
                                Color.White                  // biały po prawej
                            )
                        )

                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color.Transparent
                            ),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            // krótki klik: otwórz listę zakupów
                                            navController.navigate("shopping_list/${item.id}")
                                        },
                                        onLongPress = {
                                            // przytrzymanie: otwórz edycję
                                            selectedItemForEdit = item
                                        }
                                    )
                                },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            val cardShape = MaterialTheme.shapes.medium
                            val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(cardShape)
                                    .border(width = 1.dp, color = borderColor, shape = cardShape)
                                    .background(brush = gradientBrush, shape = cardShape)
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        item.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color(item.textColor),
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(onClick = { vm.deleteList(item.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Usuń listę"
                                        )
                                    }
                                }
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

    if (selectedItemForEdit != null) {
        val item = selectedItemForEdit!!
        var editedName by remember { mutableStateOf(item.name) }
        var editedTextColor by remember { mutableStateOf(Color(item.textColor)) }
        var editedBackgroundColor by remember { mutableStateOf(Color(item.backgroundColor)) }

        AlertDialog(
            onDismissRequest = { selectedItemForEdit = null },
            title = { Text("Edytuj listę") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        singleLine = true,
                        label = { Text("Nazwa listy") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))

                    Text("Kolor tekstu")
                    ColorPickerRow(
                        selected = editedTextColor,
                        onSelect = { editedTextColor = it }
                    )

                    Spacer(Modifier.height(16.dp))
                    Text("Kolor tła")
                    ColorPickerRow(
                        selected = editedBackgroundColor,
                        onSelect = { editedBackgroundColor = it }
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            vm.deleteList(item.id)
                            selectedItemForEdit = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Usuń") }

                    Row {
                        TextButton(onClick = { selectedItemForEdit = null }) {
                            Text("Anuluj")
                        }
                        TextButton(onClick = {
                            vm.updateList(
                                item.id,
                                editedName.trim(),
                                editedTextColor.toArgb(),
                                editedBackgroundColor.toArgb()
                            )
                            selectedItemForEdit = null
                        }) {
                            Text("Zapisz")
                        }
                    }
                }
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

@Composable
fun ColorPickerRow(
    selected: Color,
    onSelect: (Color) -> Unit
) {
    val colors = listOf(
        Color.Black, Color.White, Color.Red, Color.Green, Color.Blue,
        Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (color == selected) 3.dp else 1.dp,
                        color = if (color == selected) MaterialTheme.colorScheme.primary else Color.DarkGray,
                        shape = CircleShape
                    )
                    .clickable { onSelect(color) }
            )
        }
    }
}
