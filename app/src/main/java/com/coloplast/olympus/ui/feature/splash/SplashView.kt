package com.coloplast.olympus.ui.feature.splash

import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import io.reactivex.Observable
import java.util.*

interface SplashView : UiView<GenericUiModel> {
  fun userIdForLoggedInUser(): Observable<UUID>
}