package pl.myshoppinglist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.myshoppinglist.core.util.InstantConverters
import pl.myshoppinglist.data.local.dao.CategoryDao
import pl.myshoppinglist.data.local.dao.ShoppingItemDao
import pl.myshoppinglist.data.local.dao.ShoppingListDao
import pl.myshoppinglist.data.local.dao.StorageDao
import pl.myshoppinglist.data.local.entity.CategoryEntity
import pl.myshoppinglist.data.local.entity.ShoppingItemEntity
import pl.myshoppinglist.data.local.entity.ShoppingListEntity
import pl.myshoppinglist.data.local.entity.ShoppingListItemEntity
import pl.myshoppinglist.data.local.entity.StorageEntity

@Database(
    entities = [ShoppingListEntity::class, ShoppingItemEntity::class, CategoryEntity::class, StorageEntity::class, ShoppingListItemEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(InstantConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun storageDao(): StorageDao
}
