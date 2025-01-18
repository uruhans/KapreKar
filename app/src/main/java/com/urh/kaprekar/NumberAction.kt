package com.urh.kaprekar

sealed interface NumberAction {
    data class OnEnterNumber(val number: Int?, val index: Int): NumberAction
    data class OnChangeFieldFocused(val index: Int): NumberAction
    data object OnKeyboardBack: NumberAction
}