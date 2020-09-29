package com.coloplast.olympus.ui.feature.onboarding.signup.dataconsent

import com.coloplast.olympus.ui.common.models.flow.FlowFactoryModel


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 16/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DataConsentModel: FlowFactoryModel {
  var skip: Boolean = false
  var didConsent: Boolean = false
}