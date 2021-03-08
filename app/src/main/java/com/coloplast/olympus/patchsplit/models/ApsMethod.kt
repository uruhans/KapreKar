package com.coloplast.olympus.patchsplit.models

sealed class ApsMethod {
  data class A(val from: Int, val to: Int) : ApsMethod()
  data class B(val to: Int) : ApsMethod()
  data class C(val method: Boolean = true) : ApsMethod()
  data class D(val method: Boolean = true) : ApsMethod()
}