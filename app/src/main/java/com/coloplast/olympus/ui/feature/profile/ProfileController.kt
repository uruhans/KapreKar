package com.coloplast.olympus.ui.feature.profile

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerProfileBinding
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.onboarding.LandingController
import com.coloplast.olympus.ui.feature.onboarding.createprofile.types.HealthInsurance
import com.coloplast.olympus.ui.feature.onboarding.createprofile.types.StomaSurgeryDate
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 07/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ProfileController : CustomToolbarController<ControllerProfileBinding>(),
  ProfileView {

  private val viewModel: ProfileViewModel by inject()
  private val logoutRelay: PublishRelay<Unit> = PublishRelay.create()

  val firstName = ObservableField("")
  val lastName = ObservableField("")
  val phoneNumber = ObservableField("")
  val email = ObservableField("")

  val stomaType = ObservableInt(R.string.empty)
  val stomaSurgeryDate = ObservableInt(R.string.empty)
  val healthInsurance = ObservableInt(R.string.empty)
  val showHealthInsurance = ObservableBoolean(false)

  override fun attachVariables(viewBinding: ControllerProfileBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerProfileBinding) {
    super.onViewBound(viewBinding)
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      if(uiModel is GenericUiModel.Success<*> && uiModel.data is CurrentUser){
        when(uiModel.data){
          is CurrentUser.LoggedIn -> {
            val user = uiModel.data.user
            firstName.set(user.firstName)
            lastName.set(user.lastName)
            phoneNumber.set(user.phoneNumber)
            email.set(user.email)

            //TODO: Remove one of the two StomaType enums.
            user.stomaType?.let { stomaType.set(com.coloplast.olympus.ui.feature.onboarding.createprofile.types.StomaType.fromId(it.id).stringId) }
            user.surgeryDateInterval?.let { stomaSurgeryDate.set(StomaSurgeryDate.fromId(it).stringId) }
            user.insuranceType?.let {
              showHealthInsurance.set(true)
              healthInsurance.set(HealthInsurance.fromId(it).stringId)
            }
          }
          is CurrentUser.LoggedOut -> {
            router.setRoot(LandingController.routerTransaction()
              .pushChangeHandler(VerticalChangeHandler())
              .popChangeHandler(VerticalChangeHandler()))
          }
        }
      }
    }
  }

  fun onLogoutClicked(){
    logoutRelay.accept(Unit)
  }

  override fun onLogoutRequested(): Observable<Unit> =
    logoutRelay
      .publish()
      .refCount()

  override fun getLayout(): Int = R.layout.controller_profile

  override fun getNavigationIconResId(): Int = R.drawable.ic_arrow

  companion object {
    const val TAG_CONTROLLER = "profile_controller"

    fun routerTransaction() = RouterTransaction.with(ProfileController())
      .tag(HomeController.TAG_CONTROLLER)
  }
}