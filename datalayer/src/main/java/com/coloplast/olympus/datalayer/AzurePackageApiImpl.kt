package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.PatchDTO
import com.coloplast.olympus.datalayer.models.RawPackageDTO
import com.coloplast.olympus.datalayer.models.RawStreamDTO
import com.coloplast.olympus.datalayer.models.TransmitterValue
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

    val rawDTOs = mutableListOf<RawStreamDTO>()

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
        val patchDTO = PatchDTO("$deviceId-${patchEntry.key}",convertValueToEntities(packageType, patchEntry.value))
        patchDTOs.add(patchDTO)
      }

      // final list containing transmitters, patches and packages
      val rawStreamDtos = RawStreamDTO(it.key, patchDTOs)
      rawDTOs.add(rawStreamDtos)
    }

    return when (packageType) {
      PackageType.Status -> TODO()
      PackageType.Raw -> sendRawPackages(userId, rawDTOs)
      PackageType.Power -> TODO()
    }
  }

  private fun convertValueToEntities(packageType: PackageType, packages: List<TransmitterPackage>): List<TransmitterValue?> {
    return packages.map { tPackage ->
      when (packageType) {
        PackageType.Status -> null
        PackageType.Raw -> {
          RawPackageDTO.fromRawDataPackage(
            tPackage.timestamp,
            OlympusWirelessProtocol.rawDataPackageTransformer(tPackage.value)
          )
        }
        PackageType.Power -> null
      }
    }
  }

  private fun sendRawPackages(userId: UUID, streamDtos: List<RawStreamDTO>): Completable {
    return packageApi.syncRawPackages(userId.toString(), streamDtos)
  }

}