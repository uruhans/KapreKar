package com.coloplast.olympus.patchsplit

sealed class ApsAction {
  data class Reset(val newPatch: Boolean = false) : ApsAction()
  data class Stitch(val stitch: Boolean = true) : ApsAction()
  data class None(val nothing: Boolean = false) : ApsAction()
}