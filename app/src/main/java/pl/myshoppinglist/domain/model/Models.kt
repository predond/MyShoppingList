package pl.myshoppinglist.domain.model

import java.time.Instant

data class ShoppingList(
    val id: Long,
    val name: String,
    val textColor: Int,
    val backgroundColor: Int,
    val createdAt: Instant,
    val archived: Boolean
)
