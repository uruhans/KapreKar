package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class PatchType(val id: Int) {
  PATCH_0(0),
  PATCH_1(1),
  PATCH_2(2),
  PATCH_3(3),
  PATCH_4(4),
  PATCH_5(5),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: UByte): PatchType {
      return values().associateBy { patchType: PatchType -> patchType.id }[id.toInt()]
        ?: UNKNOWN
    }
  }
}