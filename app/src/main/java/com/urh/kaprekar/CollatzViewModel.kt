package com.urh.kaprekar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CollatzState(
    val calculationResult: String = ""
)

class CollatzViewModel : ViewModel() {
    private val _state = MutableStateFlow(CollatzState())
    val state = _state.asStateFlow()

    fun getCollatzSequence(n: Long) {
        require(n in 1L..1_000_000L) {
            "Collatz input must be between 1 and 1,000,000"
        }
        _state.update {
            it.copy(calculationResult = calculate(n))
        }
    }

    private fun collatzSteps(n: Long): List<Long> {
        require(n > 0) { "Collatz is defined for positive integers" }
        val sequence = mutableListOf(n)
        var current = n
        while (current != 1L) {
            current = if (current % 2 == 0L) current / 2 else 3 * current + 1
            sequence.add(current)
        }
        return sequence
    }

    private fun calculate(start: Long): String {
        val seq = collatzSteps(start)
        println("Steps: ${seq.size - 1}")
        println("Peak:  ${seq.max()}")
        println(seq.joinToString(" → ") + " ...")
        return "Start: $start\nSteps: ${seq.size - 1}\nPeak: ${seq.max()}\n${seq.joinToString(" → ")} ..."
    }
}
