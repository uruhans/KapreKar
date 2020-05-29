package com.coloplast.olympus.shared.moshi

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiUtils {
  
  private var moshi: Moshi? = null
  
  private fun moshiInstance(): Moshi {
    if (moshi == null) {
      moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        //.add(JodaDateTimeAdapter())
        .build()
    }
    return moshi!!
  }
  
  fun <T> convertJsonToList(clazz: Class<*>, jsonString: String): MutableList<T> {
    if (jsonString.isBlank()) {
      return mutableListOf()
    }
    
    val type = Types.newParameterizedType(MutableList::class.java, clazz)
    val listAdapter = moshiInstance().adapter<MutableList<T>>(type)
    
    return listAdapter!!.fromJson(jsonString) ?: mutableListOf()
  }
  
  fun <T> convertListToJson(clazz: Class<*>, value: List<T>): String {
    val type = Types.newParameterizedType(List::class.java, clazz)
    val listAdapter = moshiInstance().adapter<List<T>>(type)
    return listAdapter!!.toJson(value)
  }
  
  fun <T> convertJsonToObject(clazz: Class<*>, jsonString: String): T? {
    val adapter = moshiInstance().adapter<T>(clazz)
    return adapter!!.fromJson(jsonString)
  }
  
  fun <T> convertObjectToJson(clazz: Class<*>, value: T): String {
    val adapter = moshiInstance().adapter<T>(clazz)
    return adapter!!.toJson(value)
  }
}

inline fun <reified T : MoshiSerializable> T.serialize(): String {
  return MoshiUtils.convertObjectToJson(T::class.java, this)
}

inline fun <reified T : MoshiSerializable> List<T>.serializeList(): String {
  return MoshiUtils.convertListToJson(T::class.java, this)
}

inline fun <reified T : MoshiSerializable> String.deserialize(): T? {
  return MoshiUtils.convertJsonToObject<T>(T::class.java, this)
}

inline fun <reified T : MoshiSerializable> String.deserializeList(): List<T>? {
  return MoshiUtils.convertJsonToList(T::class.java, this)
}

fun String.clearTabsAndNewlines(): String {
  return this.replace("\n", "")
    .replace("\t", "")
}