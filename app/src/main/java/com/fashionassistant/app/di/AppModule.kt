package com.fashionassistant.app.di

import android.content.Context
import androidx.room.Room
import com.fashionassistant.app.data.dao.ClothingItemDao
import com.fashionassistant.app.data.dao.OutfitDao
import com.fashionassistant.app.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fashion_assistant_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideClothingItemDao(database: AppDatabase): ClothingItemDao {
        return database.clothingItemDao()
    }

    @Provides
    @Singleton
    fun provideOutfitDao(database: AppDatabase): OutfitDao {
        return database.outfitDao()
    }
}
