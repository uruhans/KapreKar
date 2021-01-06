package com.coloplast.olympus.ui.feature.profile

import SensorLayerSizeType
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
import com.coloplast.olympus.ui.dialogs.ImageDialogController
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.onboarding.signup.SignupFlowController
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
  ProfileView, ImageDialogController.Listener {

  private val viewModel: ProfileViewModel by inject()
  private val logoutRelay: PublishRelay<Unit> = PublishRelay.create()
  private val updateUserRelay: PublishRelay<User> = PublishRelay.create()

  private var imageDialogController: ImageDialogController? = null

  val hasUserProfile = ObservableBoolean()

  val sensorLayerSizeValues by lazy {
    SensorLayerSizeType.values()
      .map { applicationContext!!.getString(R.string.profile_pp_sensor_layer_size) +" "+ applicationContext!!.getString(it.stringId)}
  }

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

  val sensorLayerSizeSpinnerPosition = ObservableInt(-1)
  val stomaSpinnerPosition = ObservableInt(-1)
  val surgerySpinnerPosition = ObservableInt(-1)
  val insuranceSpinnerPosition = ObservableInt(-1)

  val dataPolicyAccepted = ObservableBoolean()
  val noUserDataPolicyAccepted = ObservableBoolean()

  private lateinit var user: User
  private var infoChanged = false
  private var healthInfoChanged = false
  private var consentChanged = false
  val userProfileChanged = ObservableBoolean(false)

  private val profileVarsChangedCallback = object : androidx.databinding.Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
      infoChanged = user.firstName != firstName.get() ||
          user.lastName != lastName.get() ||
          user.phoneNumber != phoneNumber.get() ||
          user.email != email.get()

      healthInfoChanged =
        user.sensorPatchSize != sensorLayerSizeSpinnerPosition.get() ||
        user.stomaType != stomaSpinnerPosition.get() ||
            user.surgeryDateInterval != surgerySpinnerPosition.get() ||
            user.insuranceType != insuranceSpinnerPosition.get()

      userProfileChanged.set(infoChanged || healthInfoChanged || consentChanged)
    }
  }

  private val consentChangedCallback = object : androidx.databinding.Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
      // removing consent
      if(user.consent && !dataPolicyAccepted.get()) {
        showConsentOverlay()
      }
      else if(dataPolicyAccepted.get()){
        // accepting consent - currently never being hit
        changeConsent(true)
      }
    }
  }

  private fun showConsentOverlay() {
    imageDialogController = ImageDialogController.create(
      this,
      REQUEST_CODE_CONSENT_DIALOG,
      R.string.profile_pp_withdraw_consent_modal_title,
      R.string.profile_pp_withdraw_consent_modal_description,
      R.string.profile_pp_withdraw_consent_modal_withdraw,
      R.string.generic_cancel,
      R.drawable.ic_info_circle,
      verticalButtonLayout = true,
      dismissAllowed = false)

    imageDialogController?.show()
  }

  private fun showLogOutOverlay() {
    imageDialogController = ImageDialogController.create(
      this,
      REQUEST_CODE_LOGOUT_DIALOG,
      R.string.profile_pp_log_out_modal_title,
      R.string.profile_pp_withdraw_consent_modal_description,
      R.string.profile_pp_log_out_button,
      R.string.generic_cancel,
      R.drawable.ic_info_circle,
      verticalButtonLayout = true,
      dismissAllowed = false)

    imageDialogController?.show()
  }

  private fun changeConsent(consent: Boolean) {
    consentChanged = user.consent != consent
    userProfileChanged.set(infoChanged || healthInfoChanged || consentChanged)
    if(consentChanged) {
      user.consent = dataPolicyAccepted.get()
      updateUserRelay.accept(user)
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
          editingEnabled.set(true)
          updateUserData(user)
        }
        is ProfileUiModel.UserUpdated -> {
          user = uiModel.user

          // reset changes made
          infoChanged = false
          healthInfoChanged = false
          consentChanged = false
          userProfileChanged.set(false)

          updateUserData(user)
        }
        is ProfileUiModel.UpdateFailed -> {
          Toast.makeText(activity!!, "Could not update server", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  private fun updateUserData(user: User) {
    firstName.set(user.firstName)
    lastName.set(user.lastName)
    phoneNumber.set(user.phoneNumber)
    email.set(user.email)

    user.sensorPatchSize?.let {

      sensorLayerSizeSpinnerPosition.set(it)
    }

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
    hasUserProfile.set(user.consent)

    // add callbacks again to support changes made to user profile
    addCallbacks()
  }

  fun onLogoutClicked() {
    showLogOutOverlay()
  }

  fun onCreateUserClicked() {
    userProfileChanged.set(false)
    getContentRouter().pushController(
      SignupFlowController.routerTransaction(SignupFlowController.Companion.NewUserFrom.SETTINGS_PROFIL)
        .popChangeHandler(VerticalChangeHandler())
        .pushChangeHandler(VerticalChangeHandler())
    )
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

  fun saveProfileClicked() {
    if (infoChanged) {
      user.firstName = firstName.get()
      user.lastName = lastName.get()
      user.phoneNumber = phoneNumber.get()
      user.email = email.get()
    }

    if (healthInfoChanged) {
      user.sensorPatchSize = sensorLayerSizeSpinnerPosition.get()
      user.stomaType = stomaSpinnerPosition.get()
      user.surgeryDateInterval = surgerySpinnerPosition.get()
      user.insuranceType = insuranceSpinnerPosition.get()
    }

    if (consentChanged) {
      user.consent = dataPolicyAccepted.get()
    }

    // remove callbacks so we dont get a million updates when we update user data later
    removeCallbacks()
    updateUserRelay.accept(user)
  }

  fun discardChangesClicked() {
    userProfileChanged.set(false)
    // remove callbacks so we dont get a million updates when we update user data later
    removeCallbacks()
    updateUserData(user)
  }

  override fun getLayout(): Int = R.layout.controller_profile

  override fun getNavigationIconResId(): Int = R.drawable.ic_arrow

  override fun onCloseClicked() {
    DrawerStateDispatcher.closeNavigationDrawerOnHome()
    super.onCloseClicked()
  }

  override fun handleBack(): Boolean {
    if (userProfileChanged.get()) {
      imageDialogController = ImageDialogController.create(
        this,
        REQUEST_CODE_SAVE_CHANGES_DIALOG,
        titleId = R.string.profile_pp_unsaved_changes_title,
        topImage = R.drawable.ic_info_circle,
        positiveButtonId = R.string.generic_save_changes,
        negativeButtonId = R.string.generic_cancel,
        verticalButtonLayout = true
      )

      imageDialogController?.show()
      return true
    }
    else {
      return super.handleBack()
    }
  }

  override fun onCloseClicked(requestCode: Int) {
    when(requestCode) {
      REQUEST_CODE_SAVE_CHANGES_DIALOG -> {
        imageDialogController?.dismissDialog(withCallback = true)
      }
    }
  }

  override fun onPositiveButtonClicked(requestCode: Int) {
    when(requestCode) {
      REQUEST_CODE_SAVE_CHANGES_DIALOG -> {
        imageDialogController?.dismissDialog(withCallback = true)
      }
      REQUEST_CODE_CONSENT_DIALOG -> {
        imageDialogController?.dismissDialog(withCallback = true)
      }
      REQUEST_CODE_LOGOUT_DIALOG -> {
        imageDialogController?.dismissDialog(withCallback = true)
      }
      else -> {
        super.onPositiveButtonClicked(requestCode)
      }
    }
  }

  override fun onNegativeButtonClicked(requestCode: Int) {
    when(requestCode) {
      REQUEST_CODE_LOGOUT_DIALOG,
      REQUEST_CODE_SAVE_CHANGES_DIALOG -> {
        imageDialogController?.dismissDialog(withCallback = false)
        imageDialogController = null
        super.handleBack()
      }
      REQUEST_CODE_CONSENT_DIALOG -> {
        // revert consent to true because the user has cancelled
        dataPolicyAccepted.set(true)
        imageDialogController?.dismissDialog(withCallback = false)
        imageDialogController = null
      }
      else -> {
        super.onNegativeButtonClicked(requestCode)
      }
    }
  }

  override fun onDialogDismissed(requestCode: Int) {
    when(requestCode) {
      REQUEST_CODE_SAVE_CHANGES_DIALOG -> {
        saveProfileClicked()
        imageDialogController = null
      }
      REQUEST_CODE_CONSENT_DIALOG -> {
        // set consent to false and update the user
        user.consent = false

        // remove callbacks so we dont get a million updates when we update user data later
        removeCallbacks()
        updateUserRelay.accept(user)
      }
      REQUEST_CODE_LOGOUT_DIALOG -> {
        logoutRelay.accept(Unit)
      }
      else -> {
        super.onDialogDismissed(requestCode)
      }
    }
  }

  private fun addCallbacks() {
    firstName.addOnPropertyChangedCallback(profileVarsChangedCallback)
    lastName.addOnPropertyChangedCallback(profileVarsChangedCallback)
    phoneNumber.addOnPropertyChangedCallback(profileVarsChangedCallback)
    email.addOnPropertyChangedCallback(profileVarsChangedCallback)
    sensorLayerSizeSpinnerPosition.addOnPropertyChangedCallback(profileVarsChangedCallback)
    stomaSpinnerPosition.addOnPropertyChangedCallback(profileVarsChangedCallback)
    surgerySpinnerPosition.addOnPropertyChangedCallback(profileVarsChangedCallback)
    insuranceSpinnerPosition.addOnPropertyChangedCallback(profileVarsChangedCallback)
    dataPolicyAccepted.addOnPropertyChangedCallback(consentChangedCallback)
  }

  private fun removeCallbacks() {
    firstName.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    lastName.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    phoneNumber.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    email.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    sensorLayerSizeSpinnerPosition.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    stomaSpinnerPosition.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    surgerySpinnerPosition.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    insuranceSpinnerPosition.removeOnPropertyChangedCallback(profileVarsChangedCallback)
    dataPolicyAccepted.removeOnPropertyChangedCallback(consentChangedCallback)
  }

  override fun onUnbind(viewBinding: ControllerProfileBinding) {
    super.onUnbind(viewBinding)
    removeCallbacks()
  }

  companion object {
    const val TAG_CONTROLLER = "profile_controller"

    private const val REQUEST_CODE_LOGOUT_DIALOG = 4000
    private const val REQUEST_CODE_CONSENT_DIALOG = 3000
    private const val REQUEST_CODE_SAVE_CHANGES_DIALOG = 2000
    
    fun routerTransaction() = RouterTransaction.with(ProfileController()).tag(HomeController.TAG_CONTROLLER)
  }
}