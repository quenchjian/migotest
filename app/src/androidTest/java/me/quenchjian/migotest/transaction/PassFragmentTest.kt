package me.quenchjian.migotest.transaction

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import me.quenchjian.migotest.MainActivity
import me.quenchjian.migotest.R
import me.quenchjian.migotest.network.MockServerRule
import me.quenchjian.migotest.utils.clickChild
import me.quenchjian.migotest.utils.hasChildCount
import me.quenchjian.migotest.utils.hasErrorText
import me.quenchjian.migotest.utils.withViewHolder
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@HiltAndroidTest
class PassFragmentTest {

  @get:Rule val rule = RuleChain.outerRule(HiltAndroidRule(this)).around(MockServerRule())

  @Before
  fun init() {
    ActivityScenario.launch(MainActivity::class.java)
  }

  @Test
  fun testPassesLoaded() {
    onView(withId(R.id.recycler_pass))
      .check(matches(isDisplayed()))
      .check(matches(hasChildCount(7)))
  }

  @Test
  fun testAddDayPass_success() {
    onView(withId(R.id.recycler_pass))
      .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickChild(R.id.button_add)))

    onView(withId(R.id.form_pass)).check(matches(isDisplayed()))
    onView(withId(R.id.edit_duration)).perform(typeText("2"))
    onView(withId(R.id.edit_rp)).perform(typeText("3"))
    onView(withId(R.id.button_add)).perform(click())

    onView(withId(R.id.recycler_pass)).check(matches(hasChildCount(8)))
  }

  @Test
  fun testAddDayPass_showEmptyError() {
    onView(withId(R.id.recycler_pass))
      .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickChild(R.id.button_add)))

    onView(withId(R.id.form_pass)).check(matches(isDisplayed()))
    onView(withId(R.id.button_add)).perform(click())

    onView(withId(R.id.editlayout_duration)).check(matches(hasErrorText(R.string.error_duration_empty)))
    onView(withId(R.id.editlayout_rp)).check(matches(hasErrorText(R.string.error_rp_empty)))
    onView(withId(R.id.button_cancel)).perform(click())
  }

  @Test
  fun testAddHourPass_showInvalidError() {
    onView(withId(R.id.recycler_pass))
      .perform(scrollToPosition<RecyclerView.ViewHolder>(4))
      .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(4, clickChild(R.id.button_add)))

    onView(withId(R.id.form_pass)).check(matches(isDisplayed()))
    onView(withId(R.id.edit_duration)).perform(replaceText("test"))
    onView(withId(R.id.edit_rp)).perform(replaceText("abc"))
    onView(withId(R.id.button_add)).perform(click())

    onView(withId(R.id.editlayout_duration)).check(matches(hasErrorText(R.string.error_duration_invalid)))
    onView(withId(R.id.editlayout_rp)).check(matches(hasErrorText(R.string.error_rp_invalid)))
    onView(withId(R.id.button_cancel)).perform(click())
  }

  @Test
  fun testBuyHourPass() {
    onView(withId(R.id.recycler_pass))
      .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(5, clickChild(R.id.button_buy)))

    onView(withViewHolder(R.id.recycler_pass, 5, R.id.button_buy)).check(matches(isNotEnabled()))
  }

  @Test
  fun testClickPass_showDetailPartial() {
    onView(withId(R.id.recycler_pass))
      .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

    onView(withId(R.id.view_pass_detail)).check(matches(isDisplayed()))
    onView(withId(R.id.text_pass_start)).check(matches(not(isDisplayed())))
    onView(withId(R.id.text_pass_end)).check(matches(not(isDisplayed())))
  }

  @Test
  fun testBuyAndClickPass_showDetailAll() {
    onView(withId(R.id.recycler_pass))
      .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, clickChild(R.id.button_buy)))

    onView(withViewHolder(R.id.recycler_pass, 1, R.id.button_buy)).check(matches(isNotEnabled()))

    onView(withId(R.id.recycler_pass))
      .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

    onView(withId(R.id.view_pass_detail)).check(matches(isDisplayed()))
    onView(withId(R.id.text_pass_start)).check(matches(isDisplayed()))
    onView(withId(R.id.text_pass_end)).check(matches(isDisplayed()))
  }
}