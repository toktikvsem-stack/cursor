package com.fashionassistant.app.ui.navigation

sealed class Screen(val route: String) {
    object Wardrobe : Screen("wardrobe")
    object Outfits : Screen("outfits")
    object AddItem : Screen("add_item")
    object CreateOutfit : Screen("create_outfit")
    object OutfitDetail : Screen("outfit_detail/{outfitId}") {
        fun createRoute(outfitId: Long) = "outfit_detail/$outfitId"
    }
}
