package com.coloplast.olympus.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import com.artemzin.rxui.RxUi
import com.artemzin.rxui.kotlin.bind
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.Me
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.ServiceStateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.service.BluetoothConnectionService
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.ui.common.DrawerStateDispatcher
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.onboarding.LandingController
import com.coloplast.olympus.ui.feature.splash.SplashController
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

  private lateinit var navigation: Navigation

  private val currentUser: Me by inject()
  private val serviceStateDispatcher: ServiceStateDispatcher by inject()
  private val bluetoothManager: BluetoothManager by inject()

  private val compositeDisposable = CompositeDisposable()
  private val changeNavigationRelay: PublishRelay<Int> = PublishRelay.create()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // we find the top level of the android view hierarchy
    val contentFrameLayout: ContentFrameLayout = findViewById(android.R.id.content)
    // inflate a router and attach it to the top level view
    layoutInflater.inflate(R.layout.controller_content_overlay, contentFrameLayout, true)

    // initialize routers
    initializeRouters(savedInstanceState)

    if (!navigation.getContentRouter()!!.hasRootController()) {
      navigation.getContentRouter()!!.setRoot(SplashController.routerTransaction())
    }

    // code to verify that the user is logged in, and if not display the LandingPage
    compositeDisposable.add(
      currentUser.currentUserObservable()
        .filter { it is CurrentUser.LoggedOut }
        .delay(500, TimeUnit.MILLISECONDS)
        .bind(openLoginScreen())
    )

    // this observable listens to drawer state changes
    compositeDisposable.add(
      DrawerStateDispatcher.drawerStateObservable()
        // we delay for 500 ms because we need to have
        // controllers on top removed first
        .delay(500, TimeUnit.MILLISECONDS)
        .flatMapMaybe { drawerState ->
          // get the home controller
          Navigation.locateControllerOfType<HomeController>()
            // change the drawer state
            .map { it.changeDrawerState(drawerState) }
        }
        .subscribe()
    )

    /**
     * A service callback for managing restart of BluetoothConnectionService
     * This is only used when updating the transmitter firmware
     */
    compositeDisposable.add(
      serviceStateDispatcher.serviceStateObservable()
        .filter { it == ServiceStateDispatcher.ServiceState.START_SERVICE }
        .filter { !BluetoothConnectionService.isRunning }
        .delay(500, TimeUnit.MILLISECONDS)
        .subscribe { startService() }
    )

    compositeDisposable.add(
      bluetoothManager.bluetoothStateObservable()
        .map {
          @Suppress("NON_EXHAUSTIVE_WHEN")
          when (it) {
            BluetoothState.READY -> {
              serviceStateDispatcher.serviceRelay.accept(ServiceStateDispatcher.ServiceState.START_SERVICE)
            }
            BluetoothState.BLUETOOTH_NOT_ENABLED,
            BluetoothState.LOCATION_PERMISSION_NOT_GRANTED-> {
              serviceStateDispatcher.serviceRelay.accept(ServiceStateDispatcher.ServiceState.STOP_SERVICE)
            }
          }
        }
        .subscribe()
    )
  }

  private fun startService() {
    startService(Intent(this, BluetoothConnectionService::class.java))
  }

  private fun openLoginScreen(): io.reactivex.functions.Function<Observable<CurrentUser>, Disposable> {
    return RxUi.ui {
      navigation.getContentRouter()?.setRoot(
        LandingController.routerTransaction()
          .pushChangeHandler(VerticalChangeHandler())
          .popChangeHandler(VerticalChangeHandler())
      )
    }
  }

  private fun initializeRouters(args: Bundle?) {
    navigation = Navigation.Builder()
      .addRouter(R.id.content_overlay_controller)
      .addRouter(R.id.main_navigation_controller_container)
      .build(this, args)
  }

  override fun onBackPressed() {
    // fixed a case where a dialog controller could be shown on the overlay router, but
    // it was still possible to go back using hardware buttons
    if (navigation.getOverlayRouter()!!.handleBack()) {
      return
    }

    // if the controller on the router doesn't handle back, let the activity handle back normally
    if (!navigation.getContentRouter()!!.handleBack()) {
      super.onBackPressed()
    }
  }

  override fun onDestroy() {
    super.onDestroy()

    if (!compositeDisposable.isDisposed) {
      compositeDisposable.dispose()
    }
  }
}
