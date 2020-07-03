package com.coloplast.olympus.ui.feature.onboarding.createprofile

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.CreateUserModel
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.SU1Model
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.SU2Model
import com.coloplast.olympus.ui.feature.onboarding.createprofile.su1.CreateUserSU1Controller
import com.coloplast.olympus.ui.feature.onboarding.createprofile.su2.StomaInfoSU2Controller
import com.coloplast.olympus.ui.feature.onboarding.createprofile.su3.EmailSU3Controller

class CreateUserFactoryImpl : FlowFactory<CreateUserModel>() {

  private lateinit var model: CreateUserModel

  override fun initialize() {
    model =
      CreateUserModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when(CreateUserFlow.fromIndex(currentIndex)){
      CreateUserFlow.CreateUserSU1 -> CreateUserSU1Controller.getRouterTransaction()
      CreateUserFlow.StomaInfoSU2 -> StomaInfoSU2Controller.getRouterTransaction()
      CreateUserFlow.EmailSU3 -> EmailSU3Controller.getRouterTransaction()
      CreateUserFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    if (result == null) {
      return
    }

    when (CreateUserFlow.fromIndex(currentIndex)) {
      CreateUserFlow.CreateUserSU1 -> {
        val data = result as SU1Model
        model.firstName = data.firstname
        model.lastName = data.lastname
      }
      CreateUserFlow.StomaInfoSU2 -> {
        val data = result as SU2Model
        model.stomaType = data.stomaType
        model.stomaSurgeryDate = data.stomaSurgeryDate
      }
      CreateUserFlow.EmailSU3 -> {
        val email = result as String
        model.email = email
      }
      CreateUserFlow.Completed -> {}
    }
  }
  override fun getData(): CreateUserModel = model
}
