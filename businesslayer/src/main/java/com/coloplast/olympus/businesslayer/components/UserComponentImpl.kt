package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.databaselayer.DbService
import com.coloplast.olympus.databaselayer.DbServiceImpl
import com.coloplast.olympus.datalayer.ApiService
import com.coloplast.olympus.datalayer.ApiServiceImpl
import com.coloplast.olympus.datalayer.models.UserResponseDTO
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.preferences.Preferences
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import java.util.*

class UserComponentImpl(private val preferences: Preferences) : UserComponent {

  private val apiService: ApiService = ApiServiceImpl()
  private val database: DbService = DbServiceImpl()

  override fun createUser(user: User): Observable<CurrentUser> {
    return apiService.createUser(user)
      .compose(SetTokenInsertOrUpdateUserAndReturnCurrentUser(database, preferences))
  }

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

  override fun requestSecurityCode(email: String) : Completable {
    return apiService.requestSecurityCode(email)
  }

  override fun validateSecurityCode(securityCode: String, email: String) : Completable {
    return apiService.validateSecurityCode(securityCode, email)
  }

  override fun changePassword(securityCode: String, email: String, password: String): Completable {
    return apiService.changePassword(securityCode, email, password)
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
        .map<User> {
          preferences.userLoggedIn = it.user.userId!!
          preferences.userToken = it.token
          it.user.toUser()
        }
        .flatMap { database.insertOrUpdateUser(it) }
        .map<CurrentUser> { CurrentUser.LoggedIn(it) }
        .onErrorReturn { CurrentUser.LoggedOut() }
    }
  }
}