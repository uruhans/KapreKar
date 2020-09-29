package com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su1

import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerCreateUserSu1Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.models.SU1Model


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 01/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class CreateUserSU1Controller: BaseController<ControllerCreateUserSu1Binding>() {

  val name = ObservableField<String>()
  val validName = ObservableBoolean(true)
  private val nameListener = object: Observable.OnPropertyChangedCallback(){
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
      validName.set(name.get().isNullOrBlank().not())
    }
  }
  val surname = ObservableField<String>()
  val validSurname = ObservableBoolean(true)
  private val surnameListener = object: Observable.OnPropertyChangedCallback(){
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
      validSurname.set(surname.get().isNullOrBlank().not())
    }
  }

  override fun attachVariables(viewBinding: ControllerCreateUserSu1Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerCreateUserSu1Binding) {
    name.addOnPropertyChangedCallback(nameListener)
    surname.addOnPropertyChangedCallback(surnameListener)
  }

  override fun onDetach(view: View) {
    name.removeOnPropertyChangedCallback(nameListener)
    surname.removeOnPropertyChangedCallback(surnameListener)
    super.onDetach(view)
  }

  fun onNextClicked(){
    name.notifyChange()
    surname.notifyChange()
    if(validName.get() && validSurname.get()){
      val data = SU1Model(name.get()!!, surname.get()!!)
      getListener(FlowListener::class.java)!!.onNextClicked(data)
    }
  }

  override fun getLayout(): Int = R.layout.controller_create_user_su_1

  companion object{
    private const val TAG_CONTROLLER = "create_user_su1_controller"

    fun getRouterTransaction() = RouterTransaction.with(CreateUserSU1Controller())
      .tag(TAG_CONTROLLER)
  }
}