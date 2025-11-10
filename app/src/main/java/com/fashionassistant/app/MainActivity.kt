package com.fashionassistant.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fashionassistant.app.ui.navigation.Screen
import com.fashionassistant.app.ui.screens.*
import com.fashionassistant.app.ui.theme.FashionAssistantTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FashionAssistantTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    FashionAssistantApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FashionAssistantApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Wardrobe.route,
        Screen.Outfits.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { },
                        label = { Text("Гардероб") },
                        selected = currentRoute == Screen.Wardrobe.route,
                        onClick = {
                            navController.navigate(Screen.Wardrobe.route) {
                                popUpTo(Screen.Wardrobe.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { },
                        label = { Text("Образы") },
                        selected = currentRoute == Screen.Outfits.route,
                        onClick = {
                            navController.navigate(Screen.Outfits.route) {
                                popUpTo(Screen.Wardrobe.route)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Wardrobe.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Wardrobe.route) {
                WardrobeScreen(
                    onAddItemClick = { navController.navigate(Screen.AddItem.route) }
                )
            }

            composable(Screen.Outfits.route) {
                OutfitsScreen(
                    onCreateOutfitClick = { navController.navigate(Screen.CreateOutfit.route) },
                    onOutfitClick = { outfitId ->
                        navController.navigate(Screen.OutfitDetail.createRoute(outfitId))
                    }
                )
            }

            composable(Screen.AddItem.route) {
                AddItemScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CreateOutfit.route) {
                CreateOutfitScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onOutfitCreated = { outfitId ->
                        navController.popBackStack()
                        navController.navigate(Screen.OutfitDetail.createRoute(outfitId))
                    }
                )
            }

            composable(
                route = Screen.OutfitDetail.route,
                arguments = listOf(navArgument("outfitId") { type = NavType.LongType })
            ) { backStackEntry ->
                val outfitId = backStackEntry.arguments?.getLong("outfitId") ?: 0L
                OutfitDetailScreen(
                    outfitId = outfitId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
