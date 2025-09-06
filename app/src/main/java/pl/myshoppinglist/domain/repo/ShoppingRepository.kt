package pl.myshoppinglist.domain.repo

import kotlinx.coroutines.flow.Flow
import pl.myshoppinglist.domain.model.ShoppingList

interface ShoppingRepository {
    fun observeLists(): Flow<List<ShoppingList>>
    suspend fun createList(name: String)
    suspend fun archiveList(id: Long)
}
