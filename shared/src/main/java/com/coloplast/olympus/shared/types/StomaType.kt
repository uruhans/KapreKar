package com.coloplast.olympus.shared.types

enum class StomaType(val id: Int) {
  Colostomy(0),
  Ileostomy(1),
  Urostomy(2),
  NA(3);

  companion object {
    fun fromId(id: Int?): StomaType? {
      return values().firstOrNull { it.id == id }
    }
  }
}