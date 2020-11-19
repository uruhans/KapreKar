package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.ui.common.models.flow.FlowFactoryModel

class ApplyingFlowModel : FlowFactoryModel {
  var transmitterCheckerSelectionType = TransmitterCheckerSelectionType.NOT_SET
  var connectionSuccessful = false
  var SetupGuideContinueSGATMH6_Call_Support = false
  var SetupGuideAttachedFailedSGA13_2_Call_Support = false

}