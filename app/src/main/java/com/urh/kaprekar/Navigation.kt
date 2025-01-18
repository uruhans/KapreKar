package com.urh.kaprekar

import kotlinx.serialization.Serializable

@Serializable
data object MainRoute

@Serializable
data class CalculationRoute(val movieId: Int)
