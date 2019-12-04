package com.coloplast.olympus.ui.common.rx

data class DelayedLoadingModel<T>(var result: T?, var isLoading: Boolean)