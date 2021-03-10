package com.coloplast.olympus.ui.common.rx

import com.coloplast.olympus.shared.models.DataResult
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Transforms an object of type [T] into a [DelayedLoadingModel].
 * @param <T>
 */
@Suppress("UNCHECKED_CAST", "RemoveExplicitTypeArguments")
class DelayedEmitterTransformer<T>
/**
 * @param delayInMillis
 * *
 * @param scheduler the scheduler used for debounce
 */
@JvmOverloads constructor(private val delayInMillis: Long = 500, private val scheduler: Scheduler = Schedulers.io(), val delayedEmit: T) :
    ObservableTransformer<T, DataResult<T>> {
  
  override fun apply(upstream: Observable<T>): ObservableSource<DataResult<T>> {
    return upstream.map { DataResult.Success(it) as DataResult<T> }
      .startWith(DataResult.Empty())
      .publish<DataResult<T>> { delayedLoadingModelObservable ->
        /*
         * Ensure that this inner stream will not throw an exception, but silently complete.
         * The source will also get the throwable and can handle it as they please.
         */
        delayedLoadingModelObservable
          .onErrorResumeNext(Observable.empty<DataResult<T>>())
          /*
           * Uses debounce to emit isLoading=true,
           * if upstream does not emmit anything before delayInMillis.
           *
           * Source inspiration for more information:
           * http://www.nurkiewicz.com/2017/09/detecting-and-testing-stalled-stream.html
           */
          .mergeWith(delayedLoadingModelObservable.debounce(delayInMillis, TimeUnit.MILLISECONDS, scheduler)
            .takeWhile { result: DataResult<T> ->
              /*
               * Terminate if we get an actual network response.
               * takeWhile must be called after debounce,
               * to ensure any previously delayed items won't
               * cause emit after the "timeout"
               */
              result is DataResult.Empty
            }
            .map { DataResult.Success(delayedEmit) })
      }
  }
}