package com.example.lologin


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NewPresetButtonTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun newPresetButtonTest() {
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

        val appCompatEditText = onView(
            allOf(
                withId(R.id.TimerHours),
                childAtPosition(
                    allOf(
                        withId(R.id.activity_timers),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(pressImeActionButton())

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

        val appCompatEditText2 = onView(
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
        appCompatEditText2.perform(replaceText("2"), closeSoftKeyboard())

        val appCompatEditText3 = onView(
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
        appCompatEditText3.perform(replaceText("6"), closeSoftKeyboard())

        val appCompatEditText4 = onView(
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
        appCompatEditText4.perform(replaceText("42"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
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
        appCompatEditText5.perform(replaceText("new preset"), closeSoftKeyboard())

        val appCompatEditText6 = onView(
            allOf(
                withId(R.id.name_text_box), withText("new preset"),
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
        appCompatEditText6.perform(pressImeActionButton())

        val appCompatEditText7 = onView(
            allOf(
                withId(R.id.name_text_box), withText("new preset"),
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
        appCompatEditText7.perform(pressImeActionButton())

        val appCompatEditText8 = onView(
            allOf(
                withId(R.id.name_text_box), withText("new preset"),
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
        appCompatEditText8.perform(pressImeActionButton())

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
                withId(R.id.existing_timer_time), withText("02:06:42"),
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

        val appCompatEditText9 = onView(
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
        appCompatEditText9.perform(replaceText("1"), closeSoftKeyboard())

        val appCompatEditText10 = onView(
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
        appCompatEditText10.perform(replaceText("2"), closeSoftKeyboard())

        val appCompatEditText11 = onView(
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
        appCompatEditText11.perform(replaceText("3"), closeSoftKeyboard())

        val appCompatEditText12 = onView(
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
        appCompatEditText12.perform(replaceText("second"), closeSoftKeyboard())

        val materialButton3 = onView(
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
        materialButton3.perform(click())

        val floatingActionButton3 = onView(
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
        floatingActionButton3.perform(click())

        val appCompatEditText13 = onView(
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
        appCompatEditText13.perform(replaceText("10"), closeSoftKeyboard())

        val appCompatEditText14 = onView(
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
        appCompatEditText14.perform(replaceText("third"), closeSoftKeyboard())

        val appCompatEditText15 = onView(
            allOf(
                withId(R.id.name_text_box), withText("third"),
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
        appCompatEditText15.perform(pressImeActionButton())

        val materialButton4 = onView(
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
        materialButton4.perform(click())

        val floatingActionButton4 = onView(
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
        floatingActionButton4.perform(click())

        val appCompatEditText16 = onView(
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
        appCompatEditText16.perform(replaceText("5"), closeSoftKeyboard())

        val appCompatEditText17 = onView(
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
        appCompatEditText17.perform(replaceText("fourth"), closeSoftKeyboard())

        val appCompatEditText18 = onView(
            allOf(
                withId(R.id.name_text_box), withText("fourth"),
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
        appCompatEditText18.perform(pressImeActionButton())

        val materialButton5 = onView(
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
        materialButton5.perform(click())

        val materialTextView2 = onView(
            allOf(
                withId(R.id.deletion_button), withText("X"),
                childAtPosition(
                    allOf(
                        withId(R.id.timer_item),
                        childAtPosition(
                            withId(R.id.activity_timers),
                            13
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialTextView2.perform(click())

        val materialTextView3 = onView(
            allOf(
                withId(R.id.deletion_button), withText("X"),
                childAtPosition(
                    allOf(
                        withId(R.id.timer_item),
                        childAtPosition(
                            withId(R.id.activity_timers),
                            12
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialTextView3.perform(click())

        val materialTextView4 = onView(
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
        materialTextView4.perform(click())

        val floatingActionButton5 = onView(
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
        floatingActionButton5.perform(click())

        val appCompatEditText19 = onView(
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
        appCompatEditText19.perform(replaceText("3"), closeSoftKeyboard())

        val appCompatEditText20 = onView(
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
        appCompatEditText20.perform(replaceText("6"), closeSoftKeyboard())

        val appCompatEditText21 = onView(
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
        appCompatEditText21.perform(replaceText("fail"), closeSoftKeyboard())

        val appCompatEditText22 = onView(
            allOf(
                withId(R.id.timer_pop_seconds), withText("6"),
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
        appCompatEditText22.perform(replaceText("60"))

        val appCompatEditText23 = onView(
            allOf(
                withId(R.id.timer_pop_seconds), withText("60"),
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
        appCompatEditText23.perform(closeSoftKeyboard())

        val appCompatEditText24 = onView(
            allOf(
                withId(R.id.timer_pop_seconds), withText("60"),
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
        appCompatEditText24.perform(pressImeActionButton())

        val appCompatEditText25 = onView(
            allOf(
                withId(R.id.name_text_box), withText("fail"),
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
        appCompatEditText25.perform(pressImeActionButton())

        val materialButton6 = onView(
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
        materialButton6.perform(click())

        val appCompatEditText26 = onView(
            allOf(
                withId(R.id.timer_pop_seconds), withText("60"),
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
        appCompatEditText26.perform(replaceText(""))

        val appCompatEditText27 = onView(
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
        appCompatEditText27.perform(closeSoftKeyboard())

        val appCompatEditText28 = onView(
            allOf(
                withId(R.id.timer_pop_minutes), withText("3"),
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
        appCompatEditText28.perform(replaceText("60"))

        val appCompatEditText29 = onView(
            allOf(
                withId(R.id.timer_pop_minutes), withText("60"),
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
        appCompatEditText29.perform(closeSoftKeyboard())

        val appCompatEditText30 = onView(
            allOf(
                withId(R.id.timer_pop_minutes), withText("60"),
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
        appCompatEditText30.perform(pressImeActionButton())

        val materialButton7 = onView(
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
        materialButton7.perform(click())

        val appCompatEditText31 = onView(
            allOf(
                withId(R.id.timer_pop_minutes), withText("60"),
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
        appCompatEditText31.perform(click())

        val appCompatEditText32 = onView(
            allOf(
                withId(R.id.timer_pop_minutes), withText("60"),
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
        appCompatEditText32.perform(replaceText(""))

        val appCompatEditText33 = onView(
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
        appCompatEditText33.perform(closeSoftKeyboard())

        val appCompatEditText34 = onView(
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
        appCompatEditText34.perform(replaceText("60"), closeSoftKeyboard())

        val materialButton8 = onView(
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
        materialButton8.perform(click())

        val materialTextView5 = onView(
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
        materialTextView5.perform(click())

        val floatingActionButton6 = onView(
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
        floatingActionButton6.perform(click())

        val appCompatEditText35 = onView(
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
        appCompatEditText35.perform(replaceText("5"), closeSoftKeyboard())

        val materialButton9 = onView(
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
        materialButton9.perform(click())
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
