package pl.myshoppinglist.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.myshoppinglist.data.local.entity.ShoppingListEntity

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_lists WHERE archived = 0 ORDER BY createdAt DESC")
    fun observeActive(): Flow<List<ShoppingListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ShoppingListEntity): Long

    @Query("UPDATE shopping_lists SET archived = 1 WHERE id = :id")
    suspend fun archive(id: Long)

    @Delete suspend fun delete(entity: ShoppingListEntity)
}
