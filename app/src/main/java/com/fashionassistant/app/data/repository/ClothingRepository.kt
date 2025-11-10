package com.fashionassistant.app.data.repository

import com.fashionassistant.app.data.dao.ClothingItemDao
import com.fashionassistant.app.data.model.ClothingItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClothingRepository @Inject constructor(
    private val clothingItemDao: ClothingItemDao
) {
    fun getAllItems(): Flow<List<ClothingItem>> = clothingItemDao.getAllItems()

    suspend fun getItemById(id: Long): ClothingItem? = clothingItemDao.getItemById(id)

    suspend fun getItemsByIds(ids: List<Long>): List<ClothingItem> = 
        clothingItemDao.getItemsByIds(ids)

    suspend fun insertItem(item: ClothingItem): Long = clothingItemDao.insertItem(item)

    suspend fun updateItem(item: ClothingItem) = clothingItemDao.updateItem(item)

    suspend fun deleteItem(item: ClothingItem) = clothingItemDao.deleteItem(item)

    suspend fun deleteItemById(id: Long) = clothingItemDao.deleteItemById(id)
}
