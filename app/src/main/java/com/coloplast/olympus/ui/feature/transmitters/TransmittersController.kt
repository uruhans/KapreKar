package com.coloplast.olympus.ui.feature.transmitters

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.databinding.ControllerTransmittersBinding
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.DrawerStateDispatcher
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.common.extensions.toggle
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterFlowController
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
  private val retryConnectionRelay = PublishRelay.create<Unit>()

  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(SmallHeaderItem::class.java, BR.item, R.layout.item_small_header)
    .map(SectionDivider::class.java, BR.item, R.layout.item_recyclerview_section_divider)
    .map(TransmitterItem::class.java, BR.transmitterItem, R.layout.item_transmitter_cardview_wrapper)

  val hasTransmittersAdded = ObservableBoolean(false)
  val canAddMoreTransmitters = ObservableBoolean(true)
  val searchEnabled = ObservableBoolean(true)
  val retryPressed = ObservableBoolean(false)
  val editEnabled = ObservableBoolean(false)

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

          // TODO - we need to change this into a list of Attached and / not attached list
          val list = mutableListOf<Any>(
            SmallHeaderItem(R.string.transmitters_not_attached)
          )

          uiModel.devices.map { TransmitterItem(it, editEnabled, this) }
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
          // state updates are coming from the Service (StateDispatcher)
          uiModel.stateChange.bleDevice.peripheral?.powerLevel?.let { powerLevel ->
            items.filterIsInstance<TransmitterItem>()
              .first { it.transmitter.macAddress == uiModel.stateChange.bleDevice.macAddress }
              .setPowerLevel(powerLevel)
          }
        }
        is TransmittersUiModel.TransmitterConnectionState -> {
          // TODO - here we need to update the state of the transmitters - and move them around in the list (put into attached / not attached)

          items.filterIsInstance<TransmitterItem>()
            .first { it.transmitter.macAddress == uiModel.connectionState.macAddress }
            .setConnectionState(uiModel.connectionState.connectionState)

          searchEnabled.set(!allConnected())

          if(retryPressed.get()) {
            val allConnected = items.filterIsInstance<TransmitterItem>()
              .any { it.connectionState.get() == BleConnectionState.CONNECTED }
            retryPressed.set(!allConnected)
          }
        }
        is TransmittersUiModel.TransmitterDeleted -> {

        }
        is TransmittersUiModel.Empty -> {
          hasTransmittersAdded.set(false)
        }
        is TransmittersUiModel.RetryConnectToTransmitters -> {
          retryPressed.set(true)
          searchEnabled.set(false)
        }
        is TransmittersUiModel.RetryConnectComplete -> {
          retryPressed.set(false)
          searchEnabled.set(!allConnected())
        }
      }
    }
  }

  fun onEditClicked(){
    editEnabled.toggle()
  }

  private fun allConnected() : Boolean {
    // no reason to run through each transmitter if the current uiModel state is already *not* connected
    return items.filterIsInstance<TransmitterItem>().all { it.connectionState.get() == BleConnectionState.CONNECTED }
  }

  override fun onDeleteTransmitter(transmitter: Transmitter) {
    transmitterToDelete = transmitter

    dialogController = DialogController.create(
      this,
      REQUEST_CODE_1000,
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
      REQUEST_CODE_1000 -> {
        dialogController?.dismissDialog(true)
      }
    }
  }

  override fun onNeutralButtonClicked(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_1000 -> {
        dialogController?.dismissDialog(false)
      }
    }
  }

  override fun onDialogDismissed(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_1000 -> {
        transmitterToDelete?.let {
          deleteTransmitterRelay.accept(it.macAddress)
        }
      }
    }
  }

  fun onSearchClicked() {
    retryConnectionRelay.accept(Unit)
  }

  fun onAddTransmitterClicked() {
    router.pushController(
      AddTransmitterFlowController.routerTransaction(2)
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }

  override fun requestDeleteTransmitter(): Observable<String> {
    return deleteTransmitterRelay
      .publish()
      .refCount()
  }

  override fun retryConnection() : Observable<Unit> {
    return retryConnectionRelay
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
    private const val REQUEST_CODE_1000 = 1000

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(TransmittersController())
        .tag(TAG_CONTROLLER)
    }
  }
}