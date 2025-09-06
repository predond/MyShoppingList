package pl.myshoppinglist.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.myshoppinglist.data.local.entity.ShoppingItemEntity

@Dao
interface ShoppingItemDao {
    // wszystkie produkty w inventory (globalne)
    @Query("SELECT * FROM shopping_items ORDER BY checked, name")
    fun observeAll(): Flow<List<ShoppingItemEntity>>

    // filtrowanie
    @Query("SELECT * FROM shopping_items WHERE categoryId = :categoryId ORDER BY checked, name")
    fun observeByCategory(categoryId: Long): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE storageId = :storageId ORDER BY checked, name")
    fun observeByStorage(storageId: Long): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ShoppingItemEntity): Long

    @Update
    suspend fun update(item: ShoppingItemEntity)

    @Query("UPDATE shopping_items SET checked = :checked WHERE id = :id")
    suspend fun setChecked(id: Long, checked: Boolean)

    @Delete
    suspend fun delete(item: ShoppingItemEntity)
}
