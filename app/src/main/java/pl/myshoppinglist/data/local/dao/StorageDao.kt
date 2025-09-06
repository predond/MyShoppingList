package pl.myshoppinglist.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.myshoppinglist.data.local.entity.StorageEntity

@Dao
interface StorageDao {
    @Query("SELECT * FROM storages ORDER BY name")
    fun observeAll(): Flow<List<StorageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(s: StorageEntity): Long

    @Delete
    suspend fun delete(s: StorageEntity)
}
