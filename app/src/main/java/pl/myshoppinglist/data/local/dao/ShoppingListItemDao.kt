package pl.myshoppinglist.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.myshoppinglist.data.local.entity.ShoppingListItemEntity

@Dao
interface ShoppingListItemDao {
    @Query("SELECT * FROM shopping_list_items WHERE listId = :listId ORDER BY id")
    fun observeByList(listId: Long): Flow<List<ShoppingListItemEntity>>

    @Query("SELECT * FROM shopping_list_items WHERE productId = :productId ORDER BY id")
    fun observeByProduct(productId: Long): Flow<List<ShoppingListItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ShoppingListItemEntity): Long

    @Delete
    suspend fun delete(item: ShoppingListItemEntity)

    @Query("DELETE FROM shopping_list_items WHERE listId = :listId AND productId = :productId")
    suspend fun removeProductFromList(listId: Long, productId: Long)
}
