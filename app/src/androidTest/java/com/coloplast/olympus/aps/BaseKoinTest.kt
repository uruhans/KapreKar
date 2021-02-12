package com.coloplast.olympus.aps

import org.junit.After
import org.junit.Before
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.test.KoinTest

open class BaseKoinTest : KoinTest {

  @Before
  fun setup() {
    loadKoinModules(koinModule)
  }

  @After
  fun tearDown() {
    unloadKoinModules(koinModule)
  }
}