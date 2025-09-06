package pl.myshoppinglist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "storages")
data class StorageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
