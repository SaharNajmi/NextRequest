package com.example.nextrequest.core.presentation.navigation

sealed class Screens(val route: String) {
    object HomeScreen :
        Screens("$ROUTE_HOME_SCREEN?$ARG_REQUEST_ID={$ARG_REQUEST_ID}&$ARG_SOURCE={$ARG_SOURCE}&$ARG_COLLECTION_ID={$ARG_COLLECTION_ID}") {
        fun createRoute(requestId: Int, source: String) =
            "$ROUTE_HOME_SCREEN?$ARG_REQUEST_ID=$requestId&$ARG_SOURCE=$source"

        fun createRoute(requestId: Int, source: String, collectionId: String) =
            "$ROUTE_HOME_SCREEN?$ARG_REQUEST_ID=$requestId&$ARG_SOURCE=$source&$ARG_COLLECTION_ID=$collectionId"
    }

    object HistoryScreen : Screens(ROUTE_HISTORY_SCREEN)
    object CollectionScreen : Screens(ROUTE_COLLECTION_SCREEN)


    companion object {
        //routs
        const val ROUTE_HISTORY_SCREEN = "history_Screen"
        const val ROUTE_COLLECTION_SCREEN = "collection_screen"
        const val ROUTE_HOME_SCREEN = "home_screen"

        //args
        const val ARG_REQUEST_ID = "request_id"
        const val ARG_SOURCE = "source"
        const val ARG_COLLECTION_ID = "collection_id"
    }
}