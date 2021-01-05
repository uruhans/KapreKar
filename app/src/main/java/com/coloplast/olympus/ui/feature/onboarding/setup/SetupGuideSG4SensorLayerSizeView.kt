package com.coloplast.olympus.ui.feature.onboarding.setup

import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.models.UiView
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideSGC4SensorLayerSizeUiModel
import io.reactivex.Observable

interface SetupGuideSG4SensorLayerSizeView: UiView<SetupGuideSGC4SensorLayerSizeUiModel> {
  fun onUpdateUser(): Observable<User>
}