package com.coloplast.olympus.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import com.artemzin.rxui.RxUi
import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.R
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.HomeController
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), KoinComponent {
  
  private lateinit var navigation: Navigation
  
  private val currentUser: Me by inject()
  
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
      navigation.getContentRouter()!!.setRoot(HomeController.routerTransaction())
    }
    
    // code to verify that the user is logged in, and if not display the LandingPage
    compositeDisposable.add(
      currentUser.currentUserObservable()
        .filter { it is CurrentUser.LoggedOut }
        .delay(500, TimeUnit.MILLISECONDS)
        .bind(openLoginScreen())
    )
  }
  
  private fun openLoginScreen(): io.reactivex.functions.Function<Observable<CurrentUser>, Disposable> {
    return RxUi.ui {
      /*navigation.getContentRouter()
          ?.pushController(
            LandingController.routerTransaction().pushChangeHandler(VerticalChangeHandler()).popChangeHandler(
              VerticalChangeHandler()
            )
          )*/
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
