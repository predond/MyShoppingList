package pl.myshoppinglist.feature.storages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.myshoppinglist.data.local.entity.StorageEntity

@Composable
fun StoragesScreen(viewModel: StoragesViewModel = hiltViewModel()) {
    val state by viewModel.storages.collectAsState()
    var text by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row {
            OutlinedTextField(value = text, onValueChange = { text = it }, placeholder = { Text("Nowy magazyn") }, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                if (text.isNotBlank()) {
                    viewModel.addStorage(text.trim())
                    text = ""
                }
            }) { Icon(Icons.Default.Add, contentDescription = "Dodaj") }
        }

        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn {
            items(state) { s ->
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = s.name)
                    IconButton(onClick = { viewModel.deleteStorage(s) }) { Icon(Icons.Default.Delete, contentDescription = "Usuń") }
                }
                Divider()
            }
        }
    }
}
