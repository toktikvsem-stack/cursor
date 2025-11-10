package com.fashionassistant.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val imagePath: String,
    val category: ClothingCategory,
    val color: String,
    val season: Season = Season.ALL_SEASON,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ClothingCategory(val displayName: String) {
    TOP("Верх"),
    BOTTOM("Низ"),
    DRESS("Платье"),
    OUTERWEAR("Верхняя одежда"),
    SHOES("Обувь"),
    ACCESSORIES("Аксессуары");

    companion object {
        fun fromDisplayName(name: String): ClothingCategory {
            return entries.find { it.displayName == name } ?: TOP
        }
    }
}

enum class Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER,
    ALL_SEASON
}
