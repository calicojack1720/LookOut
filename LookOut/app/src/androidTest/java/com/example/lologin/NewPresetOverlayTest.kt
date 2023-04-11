package com.example.lologin


import androidx.test.espresso.DataInteraction
import androidx.test.espresso.ViewInteraction
import androidx.test.filters.LargeTest
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent

import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*

import com.example.lologin.R

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.`is`

@LargeTest
@RunWith(AndroidJUnit4::class)
class NewPresetOverlayTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun newPresetOverlayTest() {
        val materialButton = onView(
            allOf(
                withId(R.id.SkipLoginButton), withText("Or, Skip Login Here"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val tabView = onView(
            allOf(
                withContentDescription("Timers"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.navigation_bar),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        tabView.perform(click())

        val floatingActionButton = onView(
            allOf(
                withId(R.id.addTimer), withContentDescription("New Alarm"),
                childAtPosition(
                    allOf(
                        withId(R.id.activity_timers),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.timer_pop_hours),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("0"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.timer_pop_minutes),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("1"), closeSoftKeyboard())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.timer_pop_seconds),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText("5"), closeSoftKeyboard())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.name_text_box),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(replaceText("testing preset"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.name_text_box), withText("testing preset"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(pressImeActionButton())

        val materialButton2 = onView(
            allOf(
                withId(R.id.submitbutton), withText("Add"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val materialTextView = onView(
            allOf(
                withId(R.id.existing_timer_time), withText("00:01:05"),
                childAtPosition(
                    allOf(
                        withId(R.id.timer_item),
                        childAtPosition(
                            withId(R.id.activity_timers),
                            11
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())

        val materialTextView2 = onView(
            allOf(
                withId(R.id.deletion_button), withText("X"),
                childAtPosition(
                    allOf(
                        withId(R.id.timer_item),
                        childAtPosition(
                            withId(R.id.activity_timers),
                            11
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialTextView2.perform(click())

        val materialButton3 = onView(
            allOf(
                withId(R.id.resetTimer), withText("Reset"),
                childAtPosition(
                    allOf(
                        withId(R.id.activity_timers),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())

        val materialButton4 = onView(
            allOf(
                withId(R.id.StartTimer), withText("Start"),
                childAtPosition(
                    allOf(
                        withId(R.id.activity_timers),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        materialButton4.perform(click())

        val materialButton5 = onView(
            allOf(
                withId(R.id.resetTimer), withText("Reset"),
                childAtPosition(
                    allOf(
                        withId(R.id.activity_timers),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton5.perform(click())

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.addTimer), withContentDescription("New Alarm"),
                childAtPosition(
                    allOf(
                        withId(R.id.activity_timers),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        floatingActionButton2.perform(click())

        val appCompatEditText6 = onView(
            allOf(
                withId(R.id.timer_pop_minutes),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText6.perform(replaceText("5"), closeSoftKeyboard())

        val appCompatEditText7 = onView(
            allOf(
                withId(R.id.timer_pop_hours),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatEditText7.perform(replaceText("0"), closeSoftKeyboard())

        val appCompatEditText8 = onView(
            allOf(
                withId(R.id.timer_pop_seconds),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText8.perform(replaceText("3"), closeSoftKeyboard())

        val appCompatEditText9 = onView(
            allOf(
                withId(R.id.name_text_box),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatEditText9.perform(replaceText("test cancek"), closeSoftKeyboard())

        val appCompatEditText10 = onView(
            allOf(
                withId(R.id.name_text_box), withText("test cancek"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatEditText10.perform(pressImeActionButton())

        val materialButton6 = onView(
            allOf(
                withId(R.id.cancel_button), withText("Cancel"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        0
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        materialButton6.perform(click())

        val tabView2 = onView(
            allOf(
                withContentDescription("Alarms"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.navigation_bar),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        tabView2.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
