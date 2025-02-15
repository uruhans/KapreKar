package com.urh.kaprekar.di

import com.urh.kaprekar.NumberViewModel
import com.urh.kaprekar.CalculationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModules = module {
    viewModelOf(::NumberViewModel)
    viewModelOf(::CalculationViewModel)
    //viewModel { NumberViewModel() }
}