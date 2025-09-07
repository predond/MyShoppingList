package pl.myshoppinglist.feature.items

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.myshoppinglist.data.local.dao.CategoryDao
import pl.myshoppinglist.data.local.dao.ShoppingItemDao
import pl.myshoppinglist.data.local.dao.StorageDao
import pl.myshoppinglist.data.local.entity.CategoryEntity
import pl.myshoppinglist.data.local.entity.ShoppingItemEntity
import pl.myshoppinglist.data.local.entity.StorageEntity
import javax.inject.Inject

data class ItemsUiState(
    val items: List<ShoppingItemEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val storages: List<StorageEntity> = emptyList()
)

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val itemDao: ShoppingItemDao,
    private val categoryDao: CategoryDao,
    private val storageDao: StorageDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemsUiState())
    val uiState: StateFlow<ItemsUiState> = _uiState.asStateFlow()

    init {
        // łączymy flow z DAO w jeden uiState
        combine(itemDao.observeAll(), categoryDao.observeAll(), storageDao.observeAll()) { items, cats, stor ->
            ItemsUiState(items = items, categories = cats, storages = stor)
        }.onEach { _uiState.value = it }.launchIn(viewModelScope)
    }

    fun insertItem(item: ShoppingItemEntity) = viewModelScope.launch { itemDao.upsert(item) }
    fun updateItem(item: ShoppingItemEntity) = viewModelScope.launch { itemDao.upsert(item) }
    fun deleteItem(item: ShoppingItemEntity) = viewModelScope.launch { itemDao.delete(item) }

    // tworzenie niestandardowej kategorii/storage zwraca id
    suspend fun createCategory(name: String, color: Int = Color.White.toArgb()): Long = categoryDao.upsert(CategoryEntity(name = name, color = color))
    suspend fun createStorage(name: String): Long = storageDao.upsert(StorageEntity(name = name))
}
