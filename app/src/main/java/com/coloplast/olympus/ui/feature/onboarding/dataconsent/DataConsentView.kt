package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import io.reactivex.Observable

interface DataConsentView: UiView<GenericUiModel> {
  fun onUpdateDataConsent(): Observable<Boolean>
}
