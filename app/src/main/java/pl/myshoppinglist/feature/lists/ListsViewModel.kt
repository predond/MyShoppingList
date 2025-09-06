package pl.myshoppinglist.feature.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.myshoppinglist.domain.repo.ShoppingRepository
import javax.inject.Inject

data class ListsUiState(
    val lists: List<ListItem> = emptyList()
)

data class ListItem(val id: Long, val name: String, val textColor: Int, val backgroundColor: Int)

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val repo: ShoppingRepository
) : ViewModel() {

    val uiState: StateFlow<ListsUiState> =
        repo.observeLists()
            .map { lists -> ListsUiState(lists = lists.map { ListItem(it.id, it.name, it.textColor, it.backgroundColor) }) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListsUiState())

    fun addList(name: String) {
        viewModelScope.launch { repo.createList(name) }
    }
    fun updateList(id: Long, newName: String, textColor: Int, backColor: Int) {
        viewModelScope.launch { repo.updateList(id, newName, textColor, backColor) }
    }
    fun deleteList(id: Long) {
        viewModelScope.launch { repo.deleteList(id) }
    }
}
