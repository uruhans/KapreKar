package com.coloplast.olympus.datalayer

import com.coloplast.olympus.shared.models.AppState
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.models.TransmitterPackage
import com.coloplast.olympus.shared.types.PackageType
import io.reactivex.Completable
import java.util.*

interface AzurePackageApi {

  fun sendPackages(userId: UUID, packageType: PackageType, packages: List<TransmitterPackage>) : Completable
  fun sendTransmitter(userId: UUID, transmitters: List<Transmitter>) : Completable
  fun sendAppStates(userId: UUID, appStates: List<AppState>) : Completable

}