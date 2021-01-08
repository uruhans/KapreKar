package com.coloplast.olympus.ui.feature.onboarding.setup

import android.widget.Toast
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSensorLayerSelectorBinding
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.onboarding.setup.model.NumberedCard
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SensorLayerSizeType
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideSGC4SensorLayerSizeUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.koin.core.inject

class SetupGuideSG4SensorLayerSize : BaseController<ControllerSensorLayerSelectorBinding>(), TopMenuVisibilityListener, SetupGuideSG4SensorLayerSizeView {

  val selectedSize = ObservableInt(SensorLayerSizeType.UNITIALIZED.id)

  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(NumberedCard::class.java, BR.item, R.layout.item_numbered_cardview)

  private val updateUserRelay: PublishRelay<User> = PublishRelay.create()
  private val viewModel: SetupGuideSG4SensorLayerSizeViewModel by inject()
  private lateinit var user: User

  override fun attachVariables(viewBinding: ControllerSensorLayerSelectorBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSensorLayerSelectorBinding) {
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun onUpdateUser(): Observable<User> {
    return updateUserRelay
      .publish()
      .refCount()
  }

  override fun render(): Function<Observable<SetupGuideSGC4SensorLayerSizeUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      when (uiModel) {
        is SetupGuideSGC4SensorLayerSizeUiModel.FetchedUser -> {
          user = uiModel.user
          user.sensorPatchSize?.let { selectedSize.set(it) }
        }
        is SetupGuideSGC4SensorLayerSizeUiModel.UserUpdated -> {
          getListener(FlowListener::class.java)?.onNextClicked()
        }
        is SetupGuideSGC4SensorLayerSizeUiModel.UpdateFailed -> {
          Toast.makeText(activity!!, "Could not update server", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  fun onSensorLayerClicked(sensorLayerSizeType: SensorLayerSizeType) {
    selectedSize.set(sensorLayerSizeType.ordinal)
  }

  override fun showBackButton(): Boolean = true

  override fun showCloseButton(): Boolean = false

  fun onNextClicked() {
    user.let {
      user.sensorPatchSize = selectedSize.get()
      updateUserRelay.accept(user)
    }
  }

  override fun getLayout(): Int {
    return R.layout.controller_sensor_layer_selector
  }

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_sg_4_sensor_layer_size_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuideSG4SensorLayerSize())
        .tag(TAG_CONTROLLER)
    }
  }
}
