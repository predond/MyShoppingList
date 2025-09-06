package pl.myshoppinglist.feature.storages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.myshoppinglist.data.local.dao.StorageDao
import pl.myshoppinglist.data.local.entity.CategoryEntity
import pl.myshoppinglist.data.local.entity.StorageEntity
import javax.inject.Inject

@HiltViewModel
class StoragesViewModel @Inject constructor(
    private val dao: StorageDao
) : ViewModel() {

    private val _storages = MutableStateFlow<List<StorageEntity>>(emptyList())
    val storages: StateFlow<List<StorageEntity>> = _storages.asStateFlow()

    init {
        viewModelScope.launch {
            dao.observeAll().collect {
                _storages.value = it
            }
        }
    }

    fun addStorage(name: String, color: Long = 0xFF9E9E9E) {
        viewModelScope.launch {
            dao.upsert(StorageEntity(name = name))
        }
    }

    fun updateStorage(storage: StorageEntity) {
        viewModelScope.launch {
            dao.upsert(storage)
        }
    }

    fun deleteStorage(storage: StorageEntity) {
        viewModelScope.launch {
            dao.delete(storage)
        }
    }
}
