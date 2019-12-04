package com.coloplast.olympus.ui.common.rx

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
class DelayedLoadingTransformer<T>
/**
 * @param delayInMillis
 * *
 * @param scheduler the scheduler used for debounce
 */
@JvmOverloads constructor(
    private val delayInMillis: Long = 500, private val scheduler: Scheduler = Schedulers.io()
) : ObservableTransformer<T, DelayedLoadingModel<T>> {
  
  override fun apply(
      upstream: Observable<T>
  ): ObservableSource<DelayedLoadingModel<T>> {
    return upstream.map { DelayedLoadingModel(it, false) }
        .startWith(DelayedLoadingModel<T>(null, false))
        .publish<DelayedLoadingModel<T>> { delayedLoadingModelObservable ->
          delayedLoadingModelObservable
              /*
               * Ensure that this inner stream will not throw an exception, but silently complete.
               * The source will also get the throwable and can handle it as they please.
               */.onErrorResumeNext(Observable.empty<DelayedLoadingModel<T>>())
              /*
               * Uses debounce to emit isLoading=true,
               * if upstream does not emmit anything before delayInMillis.
               *
               * Source inspiration for more information:
               * http://www.nurkiewicz.com/2017/09/detecting-and-testing-stalled-stream.html
               */.mergeWith(delayedLoadingModelObservable.debounce(
            delayInMillis,
            TimeUnit.MILLISECONDS,
            scheduler
          ).takeWhile { (result) ->
                /*
                 * Terminate if we get an actual network response.
                 * takeWhile must be called after debounce,
                 * to ensure any previously delayed items won't
                 * cause emit after the "timeout"
                 */
                result == null
              }.map { DelayedLoadingModel<T>(null, true) })
        }
  }
}