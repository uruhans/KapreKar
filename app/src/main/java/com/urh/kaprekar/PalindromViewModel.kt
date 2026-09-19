package com.urh.kaprekar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigInteger

data class PalindromState(
    val calculationResult: String = ""
)

class PalindromViewModel : ViewModel() {
    private val _state = MutableStateFlow(PalindromState())
    val state = _state.asStateFlow()

    fun getPalindromSequence(n: Long) {
        require(n in 1L..1_000_000L) {
            "Palindrom input must be between 1 and 1,000,000"
        }
        _state.update {
            it.copy(calculationResult = evaluerLychrel(startTal = n, maxIterationer = 50).toString())
        }
    }

    private fun evaluerLychrel(startTal: Long, maxIterationer: Int = 50): String {
        var nuvaerende = BigInteger(startTal.toString())
        val beregningstrin = mutableListOf("Starter Palindrom-processen for: $nuvaerende")

        for (i in 1..maxIterationer) {
            val omvendt = BigInteger(nuvaerende.toString().reversed())
            val resultat = nuvaerende + omvendt

            beregningstrin += "Skridt $i: $nuvaerende + $omvendt = $resultat"

            if (erPalindrom(resultat)) {
                return beregningstrin.joinToString("\n") +
                    "\n\n🎉 Palindrom fundet efter $i skridt: $resultat"
            }

            nuvaerende = resultat
        }

        return beregningstrin.joinToString("\n") +
            "\n\n🛑 Grænsen på $maxIterationer iterationer nået. " +
            "$startTal er sandsynligvis et Lychrel-tal."
    }

    fun erPalindrom(tal: BigInteger): Boolean {
        val str = tal.toString()
        return str == str.reversed()
    }
}
