package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.R

class NumberedCard(order: Order = Order.InBetween, val textRes: Int, val enabled: Boolean = false, val showCheckmark: Boolean = false) {

  val isFirst = order == Order.First
  val isLast = order == Order.Last

  val firstLineColor = if(enabled) R.color.turquoise else R.color.statusBarV21
  val dotColor = if(enabled) R.color.turquoise else R.color.statusBarV21
  val secondLineColor = if(enabled && showCheckmark) R.color.turquoise else R.color.statusBarV21

  enum class Order {
    First,
    InBetween,
    Last,
  }

}