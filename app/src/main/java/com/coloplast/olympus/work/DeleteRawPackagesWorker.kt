package com.coloplast.olympus.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.types.PackageType
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class DeleteRawPackagesWorker(applicationContext: Context, workerParams: WorkerParameters) :
  Worker(applicationContext, workerParams), KoinComponent {

  private val transmitterComponent: TransmitterComponent by inject()

  override fun doWork(): Result {
    // default set to failure
    var result = Result.failure()

    // delete packages and wait for it to complete

    // return result
    return result
  }


}