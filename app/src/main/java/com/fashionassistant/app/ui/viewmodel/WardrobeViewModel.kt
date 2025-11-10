package com.fashionassistant.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fashionassistant.app.data.model.ClothingItem
import com.fashionassistant.app.data.repository.ClothingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository
) : ViewModel() {

    val clothingItems: StateFlow<List<ClothingItem>> = clothingRepository.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addClothingItem(item: ClothingItem) {
        viewModelScope.launch {
            clothingRepository.insertItem(item)
        }
    }

    fun deleteClothingItem(item: ClothingItem) {
        viewModelScope.launch {
            clothingRepository.deleteItem(item)
        }
    }

    fun deleteClothingItemById(id: Long) {
        viewModelScope.launch {
            clothingRepository.deleteItemById(id)
        }
    }
}
