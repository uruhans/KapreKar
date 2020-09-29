package com.coloplast.olympus.ui.feature.profile

import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import io.reactivex.Observable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 08/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
interface ProfileView: UiView<GenericUiModel> {
  fun onLogoutRequested(): Observable<Unit>
}