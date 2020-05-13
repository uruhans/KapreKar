package com.coloplast.olympus.ui.feature.transmitters

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerTransmittersBinding
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.DrawerStateDispatcher
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterController
import com.coloplast.olympus.ui.feature.transmitters.models.SectionDivider
import com.coloplast.olympus.ui.feature.transmitters.models.SmallHeaderItem
import com.coloplast.olympus.ui.feature.transmitters.models.TransmitterItem
import com.coloplast.olympus.ui.feature.transmitters.models.TransmittersUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.koin.core.inject
import timber.log.Timber

class TransmittersController(args: Bundle? = null) :
  CustomToolbarController<ControllerTransmittersBinding>(args), TransmittersView,
  TransmitterItem.Listener, BaseDialogController.Listener {

  private val viewModel: TransmittersViewModel by inject()

  private val deleteTransmitterRelay = PublishRelay.create<String>()

  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(SmallHeaderItem::class.java, BR.item, R.layout.item_small_header)
    .map(SectionDivider::class.java, BR.item, R.layout.item_recyclerview_section_divider)
    .map(TransmitterItem::class.java, BR.item, R.layout.item_transmitter)

  val hasTransmittersAdded = ObservableBoolean(false)
  val canAddMoreTransmitters = ObservableBoolean(true)

  private var dialogController: DialogController? = null
  private var transmitterToDelete: Transmitter? = null

  override fun attachVariables(viewBinding: ControllerTransmittersBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerTransmittersBinding) {
    super.onViewBound(viewBinding)
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<TransmittersUiModel>, Disposable> {
    return RxUi.ui { uiModel ->
      Timber.d("$uiModel")

      when (uiModel) {
        is TransmittersUiModel.ConnectedDevices -> {
          hasTransmittersAdded.set(true)

          val list = mutableListOf<Any>(
            SmallHeaderItem(R.string.guide_wiping_title)
          )

          uiModel.devices.map { TransmitterItem(it, this) }
            .also { list.addAll(it) }

          list.filterIsInstance(TransmitterItem::class.java).forEach { transmitterItem ->
            if (items.filterIsInstance(TransmitterItem::class.java)
                .none { it.transmitter.macAddress == transmitterItem.transmitter.macAddress }
            ) {
              items.add(transmitterItem)
            }
          }

          items.filterIsInstance(TransmitterItem::class.java).forEach { transmitterItem ->
            if (list.filterIsInstance(TransmitterItem::class.java)
                .none { it.transmitter.macAddress == transmitterItem.transmitter.macAddress }
            ) {
              items.remove(transmitterItem)
            }
          }

          canAddMoreTransmitters.set(items.filterIsInstance(TransmitterItem::class.java).size < 2)
        }
        is TransmittersUiModel.StateUpdate -> {
          uiModel.stateChange.bleDevice.peripheral?.powerLevel?.let { powerLevel ->
            items.filterIsInstance<TransmitterItem>()
              .first { it.transmitter.macAddress == uiModel.stateChange.bleDevice.macAddress }
              .setPowerLevel(powerLevel)
          }
        }
        is TransmittersUiModel.TransmitterConnectionState -> {
          items.filterIsInstance<TransmitterItem>()
            .first { it.transmitter.macAddress == uiModel.connectionState.macAddress }
            .setConnectionState(uiModel.connectionState.connectionState)
        }
        is TransmittersUiModel.TransmitterDeleted -> {

        }
        is TransmittersUiModel.Empty -> {
          hasTransmittersAdded.set(false)
        }
      }
    }
  }

  override fun onTransmitterClicked(transmitter: Transmitter) {
    transmitterToDelete = transmitter

    dialogController = DialogController.create(
      this,
      1000,
      R.string.dialog_delete_transmitter_title,
      R.string.dialog_delete_transmitter_title,
      R.string.generic_ok,
      R.string.generic_cancel,
      dismissAllowed = true
    )

    dialogController?.show()
  }

  override fun onPositiveButtonClicked(requestCode: Int) {
    when (requestCode) {
      1000 -> {
        dialogController?.dismissDialog(true)
      }
    }
  }

  override fun onNeutralButtonClicked(requestCode: Int) {
    when (requestCode) {
      1000 -> {
        dialogController?.dismissDialog(false)
      }
    }
  }

  override fun onDialogDismissed(requestCode: Int) {
    when (requestCode) {
      1000 -> {
        transmitterToDelete?.let {
          deleteTransmitterRelay.accept(it.macAddress)
        }
      }
    }
  }

  fun onAddTransmitterClicked() {
    router.pushController(
      AddTransmitterController.routerTransaction()
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }

  override fun requestDeleteTransmitter(): Observable<String> {
    return deleteTransmitterRelay
      .publish()
      .refCount()
  }

  override fun getNavigationIconResId(): Int {
    return R.drawable.ic_arrow
  }

  override fun onCloseClicked() {
    DrawerStateDispatcher.closeNavigationDrawerOnHome()
    super.onCloseClicked()
  }

  override fun getLayout(): Int {
    return R.layout.controller_transmitters
  }

  companion object {
    private const val TAG_CONTROLLER = "transmitters_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(TransmittersController())
        .tag(TAG_CONTROLLER)
    }
  }
}