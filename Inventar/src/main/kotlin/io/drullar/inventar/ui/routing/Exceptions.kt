package io.drullar.inventar.ui.routing

internal class MissingRouteException(screen: Screen) :
    Exception("Route $screen is not defined in the routing table")