package com.fashionassistant.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fashionassistant.app.data.model.ClothingItem
import com.fashionassistant.app.data.model.Outfit
import com.fashionassistant.app.data.repository.ClothingRepository
import com.fashionassistant.app.data.repository.OutfitRepository
import com.fashionassistant.app.domain.OutfitEvaluator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutfitViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository,
    private val clothingRepository: ClothingRepository,
    private val outfitEvaluator: OutfitEvaluator
) : ViewModel() {

    val outfits: StateFlow<List<Outfit>> = outfitRepository.getAllOutfits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedItems = MutableStateFlow<Set<Long>>(emptySet())
    val selectedItems: StateFlow<Set<Long>> = _selectedItems.asStateFlow()

    private val _currentEvaluation = MutableStateFlow<OutfitEvaluator.OutfitEvaluation?>(null)
    val currentEvaluation: StateFlow<OutfitEvaluator.OutfitEvaluation?> = 
        _currentEvaluation.asStateFlow()

    fun toggleItemSelection(itemId: Long) {
        val current = _selectedItems.value.toMutableSet()
        if (itemId in current) {
            current.remove(itemId)
        } else {
            current.add(itemId)
        }
        _selectedItems.value = current
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
        _currentEvaluation.value = null
    }

    fun evaluateCurrentSelection() {
        viewModelScope.launch {
            val items = clothingRepository.getItemsByIds(_selectedItems.value.toList())
            val evaluation = outfitEvaluator.evaluateOutfit(items)
            _currentEvaluation.value = evaluation
        }
    }

    fun createOutfit(name: String, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val items = clothingRepository.getItemsByIds(_selectedItems.value.toList())
            val evaluation = outfitEvaluator.evaluateOutfit(items)
            
            val outfit = Outfit.fromItemIds(name, _selectedItems.value.toList())
                .copy(
                    score = evaluation.score,
                    feedback = evaluation.feedback
                )
            
            val outfitId = outfitRepository.insertOutfit(outfit)
            clearSelection()
            onComplete(outfitId)
        }
    }

    suspend fun getOutfitWithItems(outfitId: Long): Pair<Outfit, List<ClothingItem>>? {
        val outfit = outfitRepository.getOutfitById(outfitId) ?: return null
        val items = clothingRepository.getItemsByIds(outfit.getItemIdsList())
        return outfit to items
    }

    fun deleteOutfit(outfit: Outfit) {
        viewModelScope.launch {
            outfitRepository.deleteOutfit(outfit)
        }
    }
}
