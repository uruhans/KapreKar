package com.coloplast.olympus.shared.types

enum class PackageType(val id: String) {
  Status("S"),
  Raw("R"),
  Power("P");

  companion object {
    fun fromId(id: String): PackageType {
      return values().first { it.id == id }
    }
  }
}