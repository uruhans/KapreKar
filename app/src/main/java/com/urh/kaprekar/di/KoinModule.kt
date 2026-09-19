package com.urh.kaprekar.di

import com.urh.kaprekar.NumberViewModel
import com.urh.kaprekar.CalculationViewModel
import com.urh.kaprekar.CollatzViewModel
import com.urh.kaprekar.PalindromViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModules = module {
    viewModelOf(::NumberViewModel)
    viewModelOf(::CalculationViewModel)
    viewModelOf(::CollatzViewModel)
    viewModelOf(::PalindromViewModel)
    //viewModel { NumberViewModel() }
}