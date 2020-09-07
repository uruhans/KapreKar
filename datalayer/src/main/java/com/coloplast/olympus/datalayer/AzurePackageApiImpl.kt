package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.PatchDTO
import com.coloplast.olympus.datalayer.models.RawPackageDTO
import com.coloplast.olympus.datalayer.models.TransmitterStreamDTO
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

    val rawStreamDTO = mutableListOf<TransmitterStreamDTO>()

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
      rawStreamDTO.add(transmitterStreamDTOs)
    }

    return sendRawPackages(userId, packageType, rawStreamDTO)
  }

  private fun sendRawPackages(userId: UUID, packageType: PackageType, rawStream: List<TransmitterStreamDTO>): Completable {
    return packageApi.syncRawPackages(userId.toString(), packageType.toString(), rawStream)
  }

  private fun convertValueToEntities(packageType: PackageType, packages: List<TransmitterPackage>): List<RawPackageDTO> {
    return packages.map { tPackage ->
      when (packageType) {
        PackageType.Status -> {
          // TODO - change to STATUS package conversion
          RawPackageDTO.fromRawDataPackage(
            tPackage.id!!,
            tPackage.timestamp,
            OlympusWirelessProtocol.rawDataPackageTransformer(tPackage.value)
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
          // TODO - change to POWER package conversion
          RawPackageDTO.fromRawDataPackage(
            tPackage.id!!,
            tPackage.timestamp,
            OlympusWirelessProtocol.rawDataPackageTransformer(tPackage.value)
          )
        }
      }
    }
  }

}