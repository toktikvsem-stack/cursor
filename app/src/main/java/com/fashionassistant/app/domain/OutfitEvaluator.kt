package com.fashionassistant.app.domain

import com.fashionassistant.app.data.model.ClothingCategory
import com.fashionassistant.app.data.model.ClothingItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Evaluates outfits based on fashion rules and provides feedback.
 * This is a simplified rule-based AI system that can be extended with ML models later.
 */
@Singleton
class OutfitEvaluator @Inject constructor() {

    data class OutfitEvaluation(
        val score: Float, // 0-100
        val feedback: String,
        val suggestions: List<String>
    )

    fun evaluateOutfit(items: List<ClothingItem>): OutfitEvaluation {
        if (items.isEmpty()) {
            return OutfitEvaluation(
                score = 0f,
                feedback = "Добавьте вещи для создания образа",
                suggestions = emptyList()
            )
        }

        var score = 50f // Base score
        val suggestions = mutableListOf<String>()
        val feedback = mutableListOf<String>()

        // Check outfit completeness
        val categories = items.map { it.category }.toSet()
        val hasTop = categories.contains(ClothingCategory.TOP) || 
                     categories.contains(ClothingCategory.DRESS)
        val hasBottom = categories.contains(ClothingCategory.BOTTOM) || 
                        categories.contains(ClothingCategory.DRESS)
        val hasShoes = categories.contains(ClothingCategory.SHOES)

        // Completeness scoring
        when {
            hasTop && hasBottom && hasShoes -> {
                score += 30f
                feedback.add("✓ Образ полный")
            }
            hasTop && hasBottom -> {
                score += 20f
                feedback.add("✓ Верх и низ есть")
                suggestions.add("Добавьте обувь для завершения образа")
            }
            categories.contains(ClothingCategory.DRESS) && hasShoes -> {
                score += 30f
                feedback.add("✓ Платье с обувью - отличный выбор")
            }
            else -> {
                score += 10f
                suggestions.add("Образ неполный. Добавьте базовые элементы")
            }
        }

        // Color coordination check
        val colors = items.map { it.color.lowercase() }
        val colorScore = evaluateColorHarmony(colors)
        score += colorScore
        
        when {
            colorScore >= 15f -> feedback.add("✓ Отличное сочетание цветов")
            colorScore >= 10f -> feedback.add("✓ Хорошее сочетание цветов")
            else -> suggestions.add("Попробуйте скомбинировать цвета более гармонично")
        }

        // Check for duplicate categories (except accessories)
        val categoryCount = items.groupingBy { it.category }.eachCount()
        categoryCount.forEach { (category, count) ->
            if (count > 1 && category != ClothingCategory.ACCESSORIES) {
                score -= 10f
                suggestions.add("В образе несколько вещей из категории ${category.displayName}")
            }
        }

        // Bonus for accessories
        if (categories.contains(ClothingCategory.ACCESSORIES)) {
            score += 5f
            feedback.add("✓ Аксессуары добавляют стиль")
        }

        // Ensure score is in valid range
        score = score.coerceIn(0f, 100f)

        return OutfitEvaluation(
            score = score,
            feedback = buildFeedbackString(score, feedback, suggestions),
            suggestions = suggestions
        )
    }

    private fun evaluateColorHarmony(colors: List<String>): Float {
        if (colors.size <= 1) return 10f

        var harmony = 10f

        // Define color groups for harmony checking
        val neutrals = setOf("черный", "белый", "серый", "бежевый", "коричневый")
        val warm = setOf("красный", "оранжевый", "желтый", "розовый")
        val cool = setOf("синий", "голубой", "фиолетовый", "зеленый")

        val neutralCount = colors.count { it in neutrals }
        val warmCount = colors.count { it in warm }
        val coolCount = colors.count { it in cool }

        // Good combinations:
        // 1. Mostly neutrals
        if (neutralCount >= colors.size - 1) {
            harmony += 10f
        }
        // 2. Neutral + one accent color
        else if (neutralCount >= colors.size / 2) {
            harmony += 8f
        }
        // 3. All warm or all cool
        else if (warmCount == colors.size || coolCount == colors.size) {
            harmony += 7f
        }
        // 4. Mix of warm and cool (can work but more risky)
        else if (warmCount > 0 && coolCount > 0) {
            harmony += 3f
        }

        // Penalty for too many different colors
        val uniqueColors = colors.toSet().size
        if (uniqueColors > 3) {
            harmony -= 5f
        }

        return harmony.coerceIn(0f, 20f)
    }

    private fun buildFeedbackString(
        score: Float,
        feedback: List<String>,
        suggestions: List<String>
    ): String {
        val sb = StringBuilder()
        
        // Overall rating
        val rating = when {
            score >= 85 -> "Превосходно! 🌟"
            score >= 70 -> "Отлично! ✨"
            score >= 50 -> "Хорошо! 👍"
            score >= 30 -> "Неплохо"
            else -> "Нужно улучшить"
        }
        sb.appendLine(rating)
        sb.appendLine()

        // Add positive feedback
        if (feedback.isNotEmpty()) {
            sb.appendLine("Что хорошо:")
            feedback.forEach { sb.appendLine("• $it") }
            sb.appendLine()
        }

        // Add suggestions
        if (suggestions.isNotEmpty()) {
            sb.appendLine("Рекомендации:")
            suggestions.forEach { sb.appendLine("• $it") }
        }

        return sb.toString().trim()
    }

    /**
     * Get styling tips based on the items in the outfit
     */
    fun getStyleTips(items: List<ClothingItem>): List<String> {
        val tips = mutableListOf<String>()
        val categories = items.map { it.category }.toSet()

        if (categories.contains(ClothingCategory.DRESS)) {
            tips.add("Платье - универсальная основа образа")
        }

        if (categories.contains(ClothingCategory.ACCESSORIES)) {
            tips.add("Аксессуары могут полностью изменить образ")
        }

        if (items.any { it.category == ClothingCategory.OUTERWEAR }) {
            tips.add("Верхняя одежда должна сочетаться по стилю с остальным образом")
        }

        return tips
    }
}
