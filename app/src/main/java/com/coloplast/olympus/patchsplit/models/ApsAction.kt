package com.coloplast.olympus.patchsplit.models

sealed class ApsAction {
  data class Reset(val reset: Boolean = true) : ApsAction()
  data class Stitch(val stitch: Boolean = true) : ApsAction()
  data class Split(val split: Boolean = true) : ApsAction()
  data class Nothing(val nothing: Boolean = true) : ApsAction()
}