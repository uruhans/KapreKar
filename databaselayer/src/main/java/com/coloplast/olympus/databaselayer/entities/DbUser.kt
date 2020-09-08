package com.coloplast.olympus.databaselayer.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.types.SyncState
import java.util.*

@Entity
class DbUser(
  @PrimaryKey
  @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
  val id: UUID,
  val firstName: String?,
  val lastName: String?,
  val email: String?,
  @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
  val stomaType: Int?,
  val surgeryDateInterval: Int?,
  val phoneNumber: String?,
  val insuranceType: Int?,
  val consent: Boolean?,
  @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
  val syncState: SyncState
) {

  fun toUser(): User {
    return User(
      id,
      firstName,
      lastName,
      email,
      null,
      stomaType,
      surgeryDateInterval,
      phoneNumber,
      insuranceType,
      consent,
      syncState
    )
  }

  companion object {
    fun fromUser(user: User): DbUser {
      return DbUser(
        user.id!!,
        user.firstName,
        user.lastName,
        user.email,
        user.stomaType,
        user.surgeryDateInterval,
        user.phoneNumber,
        user.insuranceType,
        user.consent,
        user.syncState
      )
    }
  }

}