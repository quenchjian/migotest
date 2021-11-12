package me.quenchjian.migotest.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.actionWithAssertions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher

fun clickChild(@IdRes resId: Int): ViewAction = actionWithAssertions(object : ViewAction {
  override fun getDescription(): String = "click view with id=$resId"
  override fun getConstraints(): Matcher<View> = isAssignableFrom(View::class.java)
  override fun perform(uiController: UiController, view: View) {
    val child = view.findViewById<View>(resId) ?: throw PerformException.Builder()
      .withActionDescription("Cannot find child view from ")
      .withViewDescription(HumanReadables.describe(view))
      .build()
    click().perform(uiController, child)
  }
})