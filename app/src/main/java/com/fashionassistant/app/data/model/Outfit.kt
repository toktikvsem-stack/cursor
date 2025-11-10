package com.fashionassistant.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfits")
data class Outfit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val itemIds: String, // Comma-separated IDs
    val score: Float = 0f,
    val feedback: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getItemIdsList(): List<Long> {
        return if (itemIds.isBlank()) emptyList()
        else itemIds.split(",").mapNotNull { it.toLongOrNull() }
    }

    companion object {
        fun fromItemIds(name: String, ids: List<Long>): Outfit {
            return Outfit(
                name = name,
                itemIds = ids.joinToString(",")
            )
        }
    }
}
