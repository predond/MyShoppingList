package pl.myshoppinglist.feature.lists.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pl.myshoppinglist.data.local.dao.ShoppingItemDao
import pl.myshoppinglist.data.local.entity.ShoppingItemEntity
import javax.inject.Inject

@HiltViewModel
class ListDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemDao: ShoppingItemDao
) : ViewModel() {

    // savedStateHandle key musi być "listId" (tyle, ile zadeklarujesz w NavGraph)
    private val listId: Long = checkNotNull(
        savedStateHandle.get<Long>("listId") ?: savedStateHandle.get<String>("listId")?.toLongOrNull()
    )

    // (opcjonalnie) możesz wczytać nazwę listy jeśli chcesz; na razie null
    var listName: String? = null
        private set

    val itemsFlow: Flow<List<ShoppingItemEntity>> = itemDao.observeAll()

    fun toggleChecked(itemId: Long, checked: Boolean) {
        viewModelScope.launch { itemDao.setChecked(itemId, checked) }
    }

    fun deleteItem(item: ShoppingItemEntity) {
        viewModelScope.launch { itemDao.delete(item) }
    }

    fun addItem(name: String, quantity: Double) {
        viewModelScope.launch {
            val entity = ShoppingItemEntity(
                name = name,
                quantity = quantity,
                unit = "",
                checked = false,
                categoryId = null
            )
            itemDao.upsert(entity)
        }
    }
}
