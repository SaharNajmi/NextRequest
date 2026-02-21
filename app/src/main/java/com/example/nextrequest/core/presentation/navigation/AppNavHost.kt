package com.example.nextrequest.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nextrequest.collection.presentation.CollectionScreen
import com.example.nextrequest.collection.presentation.CollectionViewModel
import com.example.nextrequest.history.presentation.HistoryScreen
import com.example.nextrequest.history.presentation.HistoryViewModel
import com.example.nextrequest.home.presentation.HomeScreen
import com.example.nextrequest.home.presentation.HomeViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel(),
    collectionViewModel: CollectionViewModel = hiltViewModel(),
) {
    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route
    ) {
        composable(
            Screens.HomeScreen.route,
            arguments = listOf(navArgument(Screens.ARG_REQUEST_ID) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }, navArgument(Screens.ARG_SOURCE) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }, navArgument(Screens.ARG_COLLECTION_ID) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            var requestId =
                backStackEntry.arguments?.getString(Screens.ARG_REQUEST_ID)?.toIntOrNull()
            var source = backStackEntry.arguments?.getString(Screens.ARG_SOURCE)
            val collectionId =
                backStackEntry.arguments?.getString((Screens.ARG_COLLECTION_ID))
            HomeScreen(
                homeViewModel,
                requestId,
                source,
                collectionId,
                onNavigateToHistory = {
                    navController.navigate(Screens.HistoryScreen.route)
                },
                onNavigateToCollection = {
                    navController.navigate(Screens.CollectionScreen.route)
                }
            )
        }

        composable(Screens.HistoryScreen.route) {
            HistoryScreen(
                navController,
                historyViewModel,
                onHistoryItemClick = { requestId ->
                    navController.navigate(
                        Screens.HomeScreen.createRoute(
                            requestId,
                            Screens.ROUTE_HISTORY_SCREEN
                        )
                    ) {
                        popUpTo(navController.graph.id) { inclusive = false }
                    }
                }
            )
        }
        composable(Screens.CollectionScreen.route) {
            CollectionScreen(
                navController,
                collectionViewModel,
                onCollectionItemClick = { requestId, collectionId ->
                    navController.navigate(
                        Screens.HomeScreen.createRoute(
                            requestId,
                            Screens.ROUTE_COLLECTION_SCREEN,
                            collectionId
                        )
                    ){
                        popUpTo(navController.graph.id) { inclusive = false }
                    }
                })
        }
    }
}