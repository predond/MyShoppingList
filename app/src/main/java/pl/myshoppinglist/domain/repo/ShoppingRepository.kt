package pl.myshoppinglist.domain.repo

import kotlinx.coroutines.flow.Flow
import pl.myshoppinglist.domain.model.ShoppingList

interface ShoppingRepository {
    fun observeLists(): Flow<List<ShoppingList>>
    suspend fun createList(name: String)
    suspend fun updateList(id: Long, newName: String, textColor: Int, backgroundColor: Int)
    suspend fun deleteList(id: Long)
}
