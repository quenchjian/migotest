package me.quenchjian.migotest.utils

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.PerformException
import androidx.test.espresso.util.HumanReadables
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun hasChildCount(expected: Int): Matcher<View> = object : TypeSafeMatcher<View>() {
  private var actual = -1

  override fun describeTo(description: Description) {
    if (actual == -1) {
      description.appendText("check has $expected items")
    } else {
      description.appendText("should has $expected items, but has $actual items")
    }
  }

  override fun matchesSafely(item: View): Boolean {
    actual = when (item) {
      is RecyclerView -> item.adapter?.itemCount ?: 0
      is ViewGroup -> item.childCount
      else -> 0
    }
    return actual == expected
  }
}

fun hasErrorText(@StringRes expected: Int): Matcher<View> = object : TypeSafeMatcher<View>() {
  private var actual = ""
  private var resources: Resources? = null

  override fun describeTo(description: Description) {
    val text = resources?.getResourceName(expected) ?: expected.toString()
    if (actual.isEmpty()) {
      description.appendText("check error text with $text")
    } else {
      description.appendText("check error text with $text, but got $actual")
    }
  }

  override fun matchesSafely(item: View): Boolean {
    val res = item.context.resources.also { resources = it }
    val actual = when (item) {
      is TextInputLayout -> item.error?.toString() ?: ""
      is TextInputEditText -> item.error?.toString() ?: ""
      is EditText -> item.error?.toString() ?: ""
      else -> ""
    }
    return res.getString(expected) == actual
  }
}

fun withViewHolder(
  @IdRes parentId: Int,
  position: Int = 0,
  @IdRes childId: Int = View.NO_ID,
): Matcher<View> = object : TypeSafeMatcher<View>() {
  private var resources: Resources? = null
  override fun describeTo(description: Description) {
    if (resources == null) {
      if (childId != View.NO_ID) {
        description.appendText("find child inside ViewHolder at $position")
      } else {
        description.appendText("find ViewHolder at $position")
      }
    } else {
      description.appendText("cannot find ")
      if (childId != View.NO_ID)
        description.appendText("${resources!!.getResourceName(childId)} inside ViewHolder at ")
      else
        description.appendText(" ViewHolder at ")
      description.appendText("position $position of ${resources!!.getResourceName(parentId)}")
    }
  }

  override fun matchesSafely(item: View): Boolean {
    resources = item.context.resources
    val parent = item.rootView.findViewById<View>(parentId)
    if (parent !is RecyclerView) {
      return false
    }
    val holder = parent.findViewHolderForAdapterPosition(position) ?: throw PerformException.Builder()
      .withActionDescription(this.toString())
      .withViewDescription(HumanReadables.describe(item))
      .withCause(IllegalStateException("No view holder at position: $position"))
      .build()
    return when (childId) {
      View.NO_ID -> holder.itemView == item
      else -> holder.itemView.findViewById<View>(childId) == item
    }
  }
}