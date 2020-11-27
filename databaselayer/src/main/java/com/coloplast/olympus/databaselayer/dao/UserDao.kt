package com.coloplast.olympus.databaselayer.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coloplast.olympus.databaselayer.entities.DbUser
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

@Dao
interface UserDao {

  @Query("DELETE FROM DbUser")
  fun deleteUser() : Single<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertOrUpdate(dbUser: DbUser): Single<Long>

  @Query("SELECT * FROM DbUser WHERE id = :userId")
  fun getById(userId: UUID) : Observable<DbUser>

  @Query("SELECT * FROM DbUser WHERE id = :userId")
  fun getSingleUserById(userId: UUID) : Single<DbUser>

}