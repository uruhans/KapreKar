package com.coloplast.olympus.work.exceptions

class RetryException(val tuplesRemaining: Int) : Exception()