package com.fashionassistant.app.data.database

import androidx.room.TypeConverter
import com.fashionassistant.app.data.model.ClothingCategory
import com.fashionassistant.app.data.model.Season

class Converters {
    @TypeConverter
    fun fromClothingCategory(category: ClothingCategory): String {
        return category.name
    }

    @TypeConverter
    fun toClothingCategory(value: String): ClothingCategory {
        return ClothingCategory.valueOf(value)
    }

    @TypeConverter
    fun fromSeason(season: Season): String {
        return season.name
    }

    @TypeConverter
    fun toSeason(value: String): Season {
        return Season.valueOf(value)
    }
}
