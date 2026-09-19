package com.urh.kaprekar

import kotlinx.serialization.Serializable

@Serializable
data object MainRoute

@Serializable
data object KaprekarRoute

@Serializable
data object CollatzRoute

@Serializable
data object PalindromRoute

@Serializable
data class CalculationRoute(val movieId: Int)
