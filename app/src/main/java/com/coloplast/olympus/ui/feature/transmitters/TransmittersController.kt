package com.coloplast.olympus.ui.feature.transmitters

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.DiffUtil
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.databinding.ControllerTransmittersBinding
import com.coloplast.olympus.shared.models.DiffCallback
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.DrawerStateDispatcher
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterFlowController
import com.coloplast.olympus.ui.feature.transmitters.models.SectionDivider
import com.coloplast.olympus.ui.feature.transmitters.models.SmallHeaderItem
import com.coloplast.olympus.ui.feature.transmitters.models.TransmitterItem
import com.coloplast.olympus.ui.feature.transmitters.models.TransmittersUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.koin.core.inject
import timber.log.Timber

class TransmittersController(args: Bundle? = null) :
  CustomToolbarController<ControllerTransmittersBinding>(args), TransmittersView,
  TransmitterItem.Listener, BaseDialogController.Listener {

  private val viewModel: TransmittersViewModel by inject()
  private val deleteTransmitterRelay = PublishRelay.create<String>()
  private val retryConnectionRelay = PublishRelay.create<Unit>()

  val itemBinding: OnItemBindClass<DiffCallback> = OnItemBindClass<DiffCallback>()
    .map(SmallHeaderItem::class.java, BR.item, R.layout.item_small_header)
    .map(SectionDivider::class.java, BR.item, R.layout.item_recyclerview_section_divider)
    .map(TransmitterItem::class.java, BR.item, R.layout.item_transmitter)

  private val diffUtil = object: DiffUtil.ItemCallback<DiffCallback>(){
    override fun areItemsTheSame(oldItem: DiffCallback, newItem: DiffCallback): Boolean = oldItem.areItemsTheSame(newItem)
    override fun areContentsTheSame(oldItem: DiffCallback, newItem: DiffCallback): Boolean = oldItem.areContentsTheSame(newItem)
  }
  val items = DiffObservableList(diffUtil)

  val hasTransmittersAdded = ObservableBoolean(false)
  val canAddMoreTransmitters = ObservableBoolean(true)
  val searchEnabled = ObservableBoolean(true)
  val retryPressed = ObservableBoolean(false)

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
          val list = mutableListOf<DiffCallback>(
            SmallHeaderItem(R.string.transmitters_not_attached)
          )

          uiModel.devices.map { TransmitterItem(it, this) }
            .also { list.addAll(it) }

          items.update(list)

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

  private fun allConnected() : Boolean {
    // no reason to run through each transmitter if the current uiModel state is already *not* connected
    return items.filterIsInstance<TransmitterItem>().all { it.connectionState.get() == BleConnectionState.CONNECTED }
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

  fun onSearchClicked() {
    retryConnectionRelay.accept(Unit)
  }

  fun onAddTransmitterClicked() {
    router.pushController(
      AddTransmitterFlowController.routerTransaction()
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

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(TransmittersController())
        .tag(TAG_CONTROLLER)
    }
  }
}