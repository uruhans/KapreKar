package com.urh.kaprekar

import androidx.lifecycle.ViewModel

class CalculationViewModel : ViewModel()  {

    fun findKapreKarData(inputNo: Int): List<CalculationState> {
        var result = inputNo
        val listOf = mutableListOf<CalculationState>()

        var i = 0
        while (result != 6174) {
            i++
            val calculationState = calculateKapreKar(result)
            listOf.add(calculationState)
            result = calculationState.result
            if (i > 7) {
                break
            }
        }
        return listOf
    }


    private fun calculateKapreKar(inputNo: Int): CalculationState {
        val numberDescending = if (inputNo < 1000) sortDescending(inputNo * 10) else sortDescending(inputNo)
        val numberAscending = sortAscending(inputNo)
        return CalculationState(
            numberAscending = numberAscending,
            numberDescending = numberDescending,
            result = numberDescending - numberAscending
        )
    }

    private fun sortAscending(inputNo: Int): Int {
        val digits = inputNo.toString().map { it.toString().toInt() }.sorted()
        return digits.joinToString("").toInt()
    }

    private fun sortDescending(inputNo: Int): Int {
        val digits = inputNo.toString().map { it.toString().toInt() }.sortedDescending()
        return digits.joinToString("").toInt()
    }

}