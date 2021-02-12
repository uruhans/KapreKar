package com.coloplast.olympus.patchsplit.models

import com.coloplast.olympus.ui.common.Stack

class CompromisedVault {

  private val vault = Stack<CompromisedPackage>(5)

  fun add(compromisedPackage: CompromisedPackage) {
    vault.push(compromisedPackage)
  }

  fun last() : CompromisedPackage? {
    return vault.peek()
  }

  fun previous() : CompromisedPackage? {
    return vault.peekPrevious()
  }

}