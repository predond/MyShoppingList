package pl.myshoppinglist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Join table: powiązanie między listą zakupów (ShoppingListEntity) a produktem (ShoppingItemEntity).
 * Zawiera także nazwę/quantity które pozwalają zapisać snapshot produktu na liście.
 */
@Entity(tableName = "shopping_list_items")
data class ShoppingListItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val productId: Long,
    val name: String,
    val quantity: Double = 1.0
)
