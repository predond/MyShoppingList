package pl.myshoppinglist.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_items",
    indices = [Index("name"), Index("categoryId"), Index("storageId")]
)
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val quantity: Double = 1.0,
    val unit: String = "",
    val checked: Boolean = false,
    val categoryId: Long? = null,
    val storageId: Long? = null
)