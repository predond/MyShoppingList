package pl.myshoppinglist.feature.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import pl.myshoppinglist.feature.lists.ColorPickerRow

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, color: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Transparent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nowa kategoria") },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim(), selectedColor.toArgb())
                    }
                }
            ) { Text("Zapisz") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nazwa kategorii") }
                )

                Text("Wybierz kolor:")
                ColorPickerRow(
                    selected = selectedColor,
                    onSelect = { selectedColor = it }
                )
            }
        }
    )
}

@Composable
fun StorageDialog(
    onDismiss: () -> Unit,
    onSave: (name: String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onSave(name.trim()) }) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Nazwa magazynu") })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    )
}