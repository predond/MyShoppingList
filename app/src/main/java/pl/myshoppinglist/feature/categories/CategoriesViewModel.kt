package pl.myshoppinglist.feature.categories

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.myshoppinglist.data.local.dao.CategoryDao
import pl.myshoppinglist.data.local.entity.CategoryEntity
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val dao: CategoryDao
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            dao.observeAll().collect {
                _categories.value = it
            }
        }
    }

    fun addCategory(name: String, color: Int = Color.White.toArgb()) {
        viewModelScope.launch {
            dao.upsert(CategoryEntity(name = name, color = color))
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            dao.upsert(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            dao.delete(category)
        }
    }
}
