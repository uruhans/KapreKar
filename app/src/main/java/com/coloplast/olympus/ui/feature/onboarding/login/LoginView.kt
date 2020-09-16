package com.coloplast.olympus.ui.feature.onboarding.login

import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import io.reactivex.Observable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 14/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
interface LoginView: UiView<GenericUiModel> {
  fun onLogin(): Observable<Credentials>
}