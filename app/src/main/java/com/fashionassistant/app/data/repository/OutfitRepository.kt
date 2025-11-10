package com.fashionassistant.app.data.repository

import com.fashionassistant.app.data.dao.OutfitDao
import com.fashionassistant.app.data.model.Outfit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutfitRepository @Inject constructor(
    private val outfitDao: OutfitDao
) {
    fun getAllOutfits(): Flow<List<Outfit>> = outfitDao.getAllOutfits()

    suspend fun getOutfitById(id: Long): Outfit? = outfitDao.getOutfitById(id)

    suspend fun insertOutfit(outfit: Outfit): Long = outfitDao.insertOutfit(outfit)

    suspend fun updateOutfit(outfit: Outfit) = outfitDao.updateOutfit(outfit)

    suspend fun deleteOutfit(outfit: Outfit) = outfitDao.deleteOutfit(outfit)

    suspend fun deleteOutfitById(id: Long) = outfitDao.deleteOutfitById(id)
}
