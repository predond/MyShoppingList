package pl.myshoppinglist.data.local.entity

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val textColor: Int = Color.Black.toArgb(), // domyślnie czarny
    val backgroundColor: Int = Color.White.toArgb(), // domyślnie biały
    val createdAt: Instant = Instant.now(),
    val archived: Boolean = false
)
