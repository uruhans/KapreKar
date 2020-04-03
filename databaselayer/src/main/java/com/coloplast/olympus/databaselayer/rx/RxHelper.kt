package com.coloplast.olympus.databaselayer.rx

import com.coloplast.olympus.databaselayer.exceptions.DatabaseException
import io.reactivex.Completable

object RxHelper {
  
  fun completeOrError(columnsChanged: Int, exceptionToThrow: DatabaseException) : Completable {
    return Completable.create { emitter ->
      if(columnsChanged > 0) {
        emitter.onComplete()
      }
      else {
        emitter.onError(exceptionToThrow)
      }
    }
  }
}