package com.fashionassistant.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fashionassistant.app.data.dao.ClothingItemDao
import com.fashionassistant.app.data.dao.OutfitDao
import com.fashionassistant.app.data.model.ClothingItem
import com.fashionassistant.app.data.model.Outfit

@Database(
    entities = [ClothingItem::class, Outfit::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clothingItemDao(): ClothingItemDao
    abstract fun outfitDao(): OutfitDao
}
