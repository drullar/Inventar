package io.drullar.inventar.ui.routing

import androidx.compose.runtime.Composable

class RoutingTable : HashMap<Screen, @Composable () -> Unit>()

fun routingTableOf(vararg pairs: Pair<Screen, @Composable () -> Unit>) =
    RoutingTable().apply { this.putAll(pairs.toMap()) }