package pl.myshoppinglist.feature.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropdownSelector(items: List<String>, selectedIndex: Int = -1, onSelect: (index: Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val label = if (selectedIndex >= 0 && selectedIndex < items.size) items[selectedIndex] else "Wybierz..."
    Surface(modifier = Modifier.padding(4.dp).clickable { expanded = !expanded }) {
        Box(modifier = Modifier.padding(8.dp)) {
            Text(text = label)
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEachIndexed { idx, s ->
                    DropdownMenuItem(text = { Text(s) }, onClick = {
                        expanded = false
                        onSelect(idx)
                    })
                }
            }
        }
    }
}
