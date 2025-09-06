package pl.myshoppinglist.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.myshoppinglist.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name") fun observeAll(): Flow<List<CategoryEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(c: CategoryEntity): Long
    @Delete suspend fun delete(c: CategoryEntity)
}
