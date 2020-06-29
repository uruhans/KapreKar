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
import com.coloplast.olympus.shared.models.DiffListItem
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.utils.DiffUtilsImpl
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.DrawerStateDispatcher
import com.coloplast.olympus.ui.common.extensions.toggle
import com.coloplast.olympus.ui.dialogs.ImageDialogController
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
  TransmitterItem.Listener, ImageDialogController.Listener {

  private val viewModel: TransmittersViewModel by inject()
  private val deleteTransmitterRelay = PublishRelay.create<String>()
  private val retryConnectionRelay = PublishRelay.create<Unit>()

  val itemBinding: OnItemBindClass<DiffListItem> = OnItemBindClass<DiffListItem>()
    .map(TransmitterItem::class.java, BR.item, R.layout.item_transmitter_cardview_wrapper)
    .map(SmallHeaderItem::class.java, BR.item, R.layout.item_small_header)
    .map(SectionDivider::class.java, BR.item, R.layout.item_recyclerview_section_divider)

  val items = DiffObservableList(DiffUtilsImpl())

  private val transmitterList = ObservableArrayList<TransmitterItem>()

  val hasTransmittersAdded = ObservableBoolean(false)
  val searchEnabled = ObservableBoolean(true)
  val retryPressed = ObservableBoolean(false)
  val editEnabled = ObservableBoolean(false)

  private var dialogController: ImageDialogController? = null
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
        is TransmittersUiModel.TransmitterList -> {
          hasTransmittersAdded.set(true)

          transmitterList.clear()
          transmitterList.addAll(uiModel.devices.map { TransmitterItem(it, editEnabled, this) })

          if(items.isEmpty()) {
            updateDiffList()
          }
        }
        is TransmittersUiModel.StateUpdate -> {
          transmitterList
            .firstOrNull { uiModel.stateChange.bleDevice.macAddress == it.transmitter.macAddress }
            ?.setStateChange(uiModel.stateChange)

          updateDiffList()
        }
        is TransmittersUiModel.TransmitterConnectionState -> {
          transmitterList
            .find { uiModel.connectionState.macAddress == it.transmitter.macAddress }
            ?.setConnectionState(uiModel.connectionState.connectionState)

          searchEnabled.set(!allConnected())

          if (retryPressed.get()) {
            retryPressed.set(!allConnected())
          }
        }
        is TransmittersUiModel.TransmitterDeleted -> {
          compositeDisposable.clear()
          compositeDisposable.add(viewModel.bind(this))
          dialogController?.dismissDialog(false)
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

  fun onEditClicked() {
    editEnabled.toggle()
  }

  private fun allConnected(): Boolean {
    // no reason to run through each transmitter if the current uiModel state is already *not* connected
    return items.filterIsInstance<TransmitterItem>()
      .all { it.connectionState.get() == BleConnectionState.CONNECTED }
  }

  override fun onDeleteTransmitter(transmitter: Transmitter) {
    transmitterToDelete = transmitter

    dialogController = ImageDialogController.create(
      this,
      REQUEST_CODE_DELETE_TRANSMITTER,
      R.string.dialog_delete_transmitter_title,
      R.string.empty,
      R.string.generic_yes,
      R.string.generic_no,
      R.drawable.ic_info_circle,
      verticalButtonLayout = true,
      dismissAllowed = true
    ).apply { show() }
  }

  override fun onCloseClicked(requestCode: Int){
    dialogController?.dismissDialog(false)
    dialogController = null
  }

  override fun onPositiveButtonClicked(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_DELETE_TRANSMITTER -> {
        transmitterToDelete?.let {
          deleteTransmitterRelay.accept(it.macAddress)
        }
      }
      REQUEST_CODE_ADD_TRANSMITTER -> {
        dialogController?.dismissDialog(false)
        dialogController = null
      }
    }
  }

  override fun onNegativeButtonClicked(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_DELETE_TRANSMITTER -> {
        dialogController?.dismissDialog(false)
        dialogController = null
      }
    }
  }

  fun onSearchClicked() {
    retryConnectionRelay.accept(Unit)
  }

  fun onAddTransmitterClicked() {
    when(canAddMoreTransmitters()){
      true -> addTransmitterFlow()
      false -> {
        dialogController = ImageDialogController.create(
          this,
          REQUEST_CODE_ADD_TRANSMITTER,
          R.string.dialog_transmitters_cannot_add_more_transmitters_title,
          R.string.dialog_transmitters_cannot_add_more_transmitters_message,
          R.string.generic_ok,
          topImage = R.drawable.ic_info_circle,
          verticalButtonLayout = true,
          dismissAllowed = true
        ).apply { show() }
      }
    }
  }

  private fun addTransmitterFlow(){
    router.pushController(
      AddTransmitterFlowController.routerTransaction(2)
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }

  private fun canAddMoreTransmitters() = transmitterList.size < 2

  override fun requestDeleteTransmitter(): Observable<String> {
    return deleteTransmitterRelay
      .publish()
      .refCount()
  }

  override fun retryConnection(): Observable<Unit> {
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

  private fun updateDiffList(){
    val newList = mutableListOf<DiffListItem>()

    // Add attached transmitters and Title header
    transmitterList.filter { it.attached.get() }.let {
      if(it.isNotEmpty()){
        newList.add(SmallHeaderItem(R.string.transmitters_attached))
        newList.addAll(it)
      }
    }

    // Add divider if necessary
    if (newList.isNotEmpty()  && newList.size != transmitterList.size+1) newList.add(SectionDivider())

    // Add non-attached transmitters and Title header
    transmitterList.filter { !it.attached.get() }.let {
      if(it.isNotEmpty()){
        newList.add(SmallHeaderItem(R.string.transmitters_not_attached))
        newList.addAll(it)
      }
    }

    items.update(newList)
  }

  companion object {
    private const val TAG_CONTROLLER = "transmitters_controller"
    private const val REQUEST_CODE_DELETE_TRANSMITTER = 1000
    private const val REQUEST_CODE_ADD_TRANSMITTER = 1001

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(TransmittersController())
        .tag(TAG_CONTROLLER)
    }
  }
}