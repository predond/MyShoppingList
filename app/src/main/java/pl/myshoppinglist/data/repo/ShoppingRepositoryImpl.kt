package pl.myshoppinglist.data.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.myshoppinglist.data.local.dao.ShoppingListDao
import pl.myshoppinglist.data.local.entity.ShoppingListEntity
import pl.myshoppinglist.domain.model.ShoppingList
import pl.myshoppinglist.domain.repo.ShoppingRepository
import java.time.Instant
import javax.inject.Inject

class ShoppingRepositoryImpl @Inject constructor(
    private val listDao: ShoppingListDao
) : ShoppingRepository {

    override fun observeLists(): Flow<List<ShoppingList>> =
        listDao.observeActive().map { list ->
            list.map { e -> ShoppingList(e.id, e.name, e.createdAt, e.archived) }
        }

    override suspend fun createList(name: String) {
        listDao.upsert(ShoppingListEntity(name = name, createdAt = Instant.now()))
    }

    override suspend fun archiveList(id: Long) {
        listDao.archive(id)
    }
}
