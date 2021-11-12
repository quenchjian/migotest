package me.quenchjian.migotest.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun View.show() {
  visibility = View.VISIBLE
}

fun View.showIf(condition: () -> Boolean) {
  if (condition()) {
    show()
  }
}

fun View.hide() {
  visibility = View.GONE
}

fun View.hideIf(condition: () -> Boolean) {
  if (condition()) {
    hide()
  }
}

fun ViewGroup.inflate(@LayoutRes resId: Int, attachToRoot: Boolean = false): View {
  return LayoutInflater.from(context).inflate(resId, this, attachToRoot)
}