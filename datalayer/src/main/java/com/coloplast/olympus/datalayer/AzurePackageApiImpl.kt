package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.*
import com.coloplast.olympus.shared.models.AppState
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.models.TransmitterPackage
import com.coloplast.olympus.shared.transformers.OlympusWirelessProtocol
import com.coloplast.olympus.shared.types.PackageType
import io.reactivex.Completable
import java.util.*

class AzurePackageApiImpl : AzurePackageApi {

  private val packageApi = AzureRetrofit.packageApi

  override fun sendPackages(userId: UUID, packageType: PackageType, packages: List<TransmitterPackage>): Completable {
    // first group the list into a map of transmitterId, list of packages
    val packagesByTransmitterId: Map<String, List<TransmitterPackage>> =
      packages.groupBy { it.transmitterId }

    val streamDTO = mutableListOf<TransmitterStreamDTO>()

    // run through each of the transmitterIds
    packagesByTransmitterId.forEach {
      // now group the list of packages into map of patchIds, and packages
      val packagesByPatchId = it.value.groupBy { transmitterPackage -> transmitterPackage.patchId }

      // take the first deviceId (we can do this because all packages that are in the local storage
      // are all created by the same device)
      val deviceId = it.value.first().deviceId
      val patchDTOs = mutableListOf<PatchDTO>()

      // run through each patch
      packagesByPatchId.forEach { patchEntry ->
        // create a patchDTO and convert the value of the package to
        val patchDTO = PatchDTO("$deviceId-${patchEntry.key}", convertValueToEntities(packageType, patchEntry.value))
        patchDTOs.add(patchDTO)
      }

      // final list containing transmitters, patches and packages
      val transmitterStreamDTOs = TransmitterStreamDTO(it.key, patchDTOs)
      streamDTO.add(transmitterStreamDTOs)
    }

    return sendRawPackages(userId, packageType, streamDTO)
  }

  private fun sendRawPackages(userId: UUID, packageType: PackageType, streamDTO: List<TransmitterStreamDTO>): Completable {
    return packageApi.syncRawPackages(userId.toString(), packageType.toString(), streamDTO)
  }

  private fun convertValueToEntities(packageType: PackageType, packages: List<TransmitterPackage>): List<TransmitterValue> {
    return packages.map { tPackage ->
      when (packageType) {
        PackageType.Status -> {
          StatusPackageDTO.fromStatusPackage(
            tPackage.id!!,
            tPackage.timestamp,
            OlympusWirelessProtocol.statusPackageTransformer(tPackage.value, false)
          )
        }
        PackageType.Raw -> {
          RawPackageDTO.fromRawDataPackage(
            tPackage.id!!,
            tPackage.timestamp,
            OlympusWirelessProtocol.rawDataPackageTransformer(tPackage.value)
          )
        }
        PackageType.Power -> {
          PowerPackageDTO.fromPowerPackage(tPackage.id!!, tPackage.timestamp, tPackage.value.first().toInt())
        }
      }
    }
  }

  override fun sendTransmitter(userId: UUID, transmitters: List<Transmitter>): Completable {
    return packageApi.syncTransmitter(
      transmitters.map { TransmitterDTO.fromTransmitter(userId, it) }
    )
  }

  override fun sendAppStates(userId: UUID, appStates: List<AppState>): Completable {
    return packageApi.syncAppState(userId.toString(), getAppStateListDTO(appStates))
  }

  private fun getAppStateListDTO(appStates: List<AppState>): List<AppStateDTO> {
    return appStates.map { AppStateDTO.fromAppState(it.appState, it.internalAppState, it.timestamp) }
  }
}