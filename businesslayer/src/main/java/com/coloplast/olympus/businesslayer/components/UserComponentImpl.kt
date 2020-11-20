package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.databaselayer.DbService
import com.coloplast.olympus.databaselayer.DbServiceImpl
import com.coloplast.olympus.datalayer.ApiService
import com.coloplast.olympus.datalayer.ApiServiceImpl
import com.coloplast.olympus.datalayer.models.UserResponseDTO
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.*
import io.reactivex.Observable
import java.util.*

class UserComponentImpl(private val preferences: Preferences) : UserComponent {

  private val apiService: ApiService = ApiServiceImpl()
  private val database: DbService = DbServiceImpl()

  override fun createUser(user: User): Observable<CurrentUser> =
    if(user.email.isNullOrBlank()){
      apiService.createAnonymousUser()
    } else {
      apiService.createUser(user)
    }.compose(SetTokenInsertOrUpdateUserAndReturnCurrentUser(database, preferences))

  override fun login(credentials: Credentials): Observable<CurrentUser> {
    return apiService.login(credentials)
      .compose(SetTokenInsertOrUpdateUserAndReturnCurrentUser(database, preferences))
  }

  override fun automaticLogin(userId: UUID): Observable<CurrentUser> {
    return getLocalUser(userId)
      .map<CurrentUser> { CurrentUser.LoggedIn(it) }
      .onErrorReturn { CurrentUser.LoggedOut() }
  }

  override fun getLocalUser(userId: UUID): Observable<User> {
    return database.getUser(userId)
  }

  override fun getSingleLocalUser(userId: UUID): Single<User> {
    return database.getSingleUser(userId)
  }

  override fun updateUserConsent(userId: UUID, consent: Boolean): Observable<User> {
    return getLocalUser(userId)
      .map { resetUser(it) }
      .flatMap { database.insertOrUpdateUser(it) }
  }

  override fun updateUser(user: User): Observable<User> {
    return getSingleLocalUser(user.id!!)
      .toObservable()
      .flatMap { localUser ->
        // if consent goes from true to false we need to reset the user
        if(localUser.consent && !user.consent) {
          resetUser(user)
        }

        user.syncState = SyncState.Pending
        database.insertOrUpdateUser(user)
      }
  }

  override fun requestSecurityCode(email: String) : Completable {
    return apiService.requestSecurityCode(email)
  }

  override fun validateSecurityCode(securityCode: Int, email: String) : Completable {
    return apiService.validateSecurityCode(securityCode, email)
  }

  override fun changePassword(securityCode: Int, email: String, password: String): Completable {
    return apiService.changePassword(securityCode, email, password)
  }

  private fun resetUser(user: User) : User {
    if(!user.consent) {
      with(user) {
        firstName = null
        lastName = null
        insuranceType = null
        surgeryDateInterval = null
        stomaType = null
        phoneNumber = null
        email = null
        password = null
      }
    }

    user.syncState = SyncState.Pending
    return user
  }

  override fun requestPhoneValidationSMS(phoneNumber: String): Completable {
    return apiService.requestPhoneValidationSMS(phoneNumber)
  }

  override fun validatePhoneCode(phoneNumber: String, code: Int): Completable {
    return apiService.validatePhoneCode(phoneNumber, code)
  }

  override fun syncUser(userId: UUID): Observable<SyncResult> {
    return getSingleLocalUser(userId)
      .toObservable()
      .flatMap {
        if(it.syncState == SyncState.Uploaded) {
          Observable.just(SyncResult.Completed())
        }
        else {
          it.syncState = SyncState.Sending

          updateUser(it)
            .flatMap { user ->
              apiService.updateUser(user)
                .toSingle { SyncResult.Completed() }
                .toObservable()
                .flatMap<SyncResult> { syncResult ->
                  database.updateUserSyncState(userId, SyncState.Uploaded)
                    .toSingle { syncResult }
                    .toObservable()
                }
            }
        }
      }
      .onErrorResumeNext(
        database.updateUserSyncState(userId, SyncState.Pending)
          .toSingle { SyncResult.Error() }
          .toObservable()
      )
  }

  /**
   * An inner class created to prevent duplicate code when creating a new user or logging in
   *
   * 1. Sets the token
   * 2. Inserts (or updates) user in the local database
   * 3. Returns a @see CurrentUser (LoggedIn or LoggedOut)
   *
   * @Return CurrentUser.LoggedIn(user) or CurrentUser.LoggedOut
   */
  class SetTokenInsertOrUpdateUserAndReturnCurrentUser(
    private val database: DbService,
    private val preferences: Preferences
  ) : ObservableTransformer<UserResponseDTO, CurrentUser> {

    override fun apply(upstream: Observable<UserResponseDTO>): ObservableSource<CurrentUser> {
      return upstream
        .map {
          preferences.userLoggedIn = it.user.userId!!
          preferences.setToken(it.token)
          it.user.toUser()
        }
        .flatMap { database.insertOrUpdateUser(it) }
        .map<CurrentUser> { CurrentUser.LoggedIn(it) }
        .onErrorReturn { CurrentUser.LoggedOut() }
    }
  }
}