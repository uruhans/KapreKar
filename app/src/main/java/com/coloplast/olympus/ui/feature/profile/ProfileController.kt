package com.coloplast.olympus.ui.feature.profile

import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerProfileBinding
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.DrawerStateDispatcher
import com.coloplast.olympus.ui.common.WebViewController
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.types.HealthInsurance
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.types.StomaSurgeryDate
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.types.StomaType
import com.coloplast.olympus.ui.feature.profile.model.ProfileUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject

class ProfileController : CustomToolbarController<ControllerProfileBinding>(),
  ProfileView {

  private val viewModel: ProfileViewModel by inject()
  private val logoutRelay: PublishRelay<Unit> = PublishRelay.create()
  private val updateUserRelay: PublishRelay<User> = PublishRelay.create()

  val stomaValues by lazy {
    StomaType.values()
      .map { applicationContext!!.getString(it.stringId) }
  }

  val surgeryDateValues by lazy {
    StomaSurgeryDate.values()
      .map { applicationContext!!.getString(it.stringId) }
  }

  val insuranceValues by lazy {
    HealthInsurance.values()
      .map { applicationContext!!.getString(it.stringId) }
  }

  val editingEnabled = ObservableBoolean(false)

  val firstName = ObservableField("")
  val lastName = ObservableField("")
  val phoneNumber = ObservableField("")
  val email = ObservableField("")

  val showHealthInsurance = ObservableBoolean(false)

  val stomaSpinnerPosition = ObservableInt(-1)
  val surgerySpinnerPosition = ObservableInt(-1)
  val insuranceSpinnerPosition = ObservableInt(-1)

  val dataPolicyAccepted = ObservableBoolean()

  private lateinit var user: User

  private var infoChanged = false
  private var healthInfoChanged = false
  private var consentChanged = false
  private var userProfileChanged = false

  private val profileVarsChangedCallback = object : androidx.databinding.Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
      infoChanged =
        user.firstName != firstName.get() ||
            user.lastName != lastName.get() ||
            user.phoneNumber != phoneNumber.get() ||
            user.email != email.get()

      healthInfoChanged =
        user.stomaType != stomaSpinnerPosition.get() ||
            user.surgeryDateInterval != surgerySpinnerPosition.get() ||
            user.insuranceType != insuranceSpinnerPosition.get()

      consentChanged = user.consent != dataPolicyAccepted.get()

      userProfileChanged = infoChanged || healthInfoChanged || consentChanged

      // TODO - this is temporary until we enable changes to all fields
      if(consentChanged) {
        user.consent = dataPolicyAccepted.get()
        updateUserRelay.accept(user)
      }
    }
  }

  override fun attachVariables(viewBinding: ControllerProfileBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerProfileBinding) {
    super.onViewBound(viewBinding)
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<ProfileUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      when (uiModel) {
        is ProfileUiModel.FetchedUser -> {
          user = uiModel.user
          updateUserData(user)

          addChangeListeners()
        }
        is ProfileUiModel.UserUpdated -> {
          // TODO - show updates success
        }
        is ProfileUiModel.UpdateFailed -> {
          Toast.makeText(activity!!, "Could not update server", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  private fun addChangeListeners() {
    firstName.addOnPropertyChangedCallback(profileVarsChangedCallback)
    lastName.addOnPropertyChangedCallback(profileVarsChangedCallback)
    phoneNumber.addOnPropertyChangedCallback(profileVarsChangedCallback)
    email.addOnPropertyChangedCallback(profileVarsChangedCallback)
    stomaSpinnerPosition.addOnPropertyChangedCallback(profileVarsChangedCallback)
    surgerySpinnerPosition.addOnPropertyChangedCallback(profileVarsChangedCallback)
    insuranceSpinnerPosition.addOnPropertyChangedCallback(profileVarsChangedCallback)
    dataPolicyAccepted.addOnPropertyChangedCallback(profileVarsChangedCallback)
  }

  private fun updateUserData(user: User) {
    firstName.set(user.firstName)
    lastName.set(user.lastName)
    phoneNumber.set(user.phoneNumber)
    email.set(user.email)

    user.stomaType?.let {
      stomaSpinnerPosition.set(it)
    }

    user.surgeryDateInterval?.let {
      surgerySpinnerPosition.set(it)
    }

    user.insuranceType?.let {
      showHealthInsurance.set(true)
      insuranceSpinnerPosition.set(it)
    }

    dataPolicyAccepted.set(user.consent)
  }

  fun onLogoutClicked() {
    logoutRelay.accept(Unit)
  }

  fun onPolicyAgreementClicked() {
    val url = applicationContext!!.getString(R.string.onboarding_dc5_link1)
    getContentRouter().pushController(
      WebViewController.getRouterTransaction(url)
        .popChangeHandler(VerticalChangeHandler())
        .pushChangeHandler(VerticalChangeHandler())
    )
  }

  override fun onLogoutRequested(): Observable<Unit> =
    logoutRelay
      .publish()
      .refCount()

  override fun onUpdateUserRequest(): Observable<User> {
    return updateUserRelay
      .publish()
      .refCount()
  }

  // TODO - add this to actual buttons in the xml when ready
  fun saveProfileClicked() {
    if (infoChanged) {
      user.firstName = firstName.get()
      user.lastName = lastName.get()
      user.phoneNumber = phoneNumber.get()
      user.email = email.get()
    }

    if (healthInfoChanged) {
      user.stomaType = stomaSpinnerPosition.get()
      user.surgeryDateInterval = surgerySpinnerPosition.get()
      user.insuranceType = insuranceSpinnerPosition.get()
    }

    if (consentChanged) {
      user.consent = dataPolicyAccepted.get()
    }

    updateUserRelay.accept(user)
  }

  // TODO - add this to actual buttons in the xml when ready
  fun discardChangesClicked() {
    updateUserData(user)
  }

  override fun getLayout(): Int = R.layout.controller_profile

  override fun getNavigationIconResId(): Int = R.drawable.ic_arrow

  override fun onCloseClicked() {
    DrawerStateDispatcher.closeNavigationDrawerOnHome()
    super.onCloseClicked()
  }

  override fun onUnbind(viewBinding: ControllerProfileBinding) {
    super.onUnbind(viewBinding)

    firstName.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    lastName.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    phoneNumber.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    email.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    stomaSpinnerPosition.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    surgerySpinnerPosition.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    insuranceSpinnerPosition.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    dataPolicyAccepted.removeOnPropertyChangedCallback(profileVarsChangedCallback)
  }

  override fun onBackClicked() {
    if (userProfileChanged) {
      // TODO - show dialog box stating unsaved changes
    }
    else {
      super.onBackClicked()
    }
  }

  companion object {
    const val TAG_CONTROLLER = "profile_controller"

    fun routerTransaction() = RouterTransaction.with(ProfileController()).tag(HomeController.TAG_CONTROLLER)
  }
}