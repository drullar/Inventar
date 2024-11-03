package io.drullar.inventar.ui.routing

import androidx.compose.runtime.Composable


@Composable
fun ScreenManager(renderedScreen: Screen, routingTable: RoutingTable) {
    routingTable[renderedScreen]?.invoke() ?: throw MissingRouteException(renderedScreen)
}