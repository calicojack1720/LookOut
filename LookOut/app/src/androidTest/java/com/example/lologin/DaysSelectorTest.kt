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
class DaysSelectorTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun daysSelectorTest() {
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

        val materialButton2 = onView(
            allOf(
                withId(android.R.id.button2), withText("Cancel"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    2
                )
            )
        )
        materialButton2.perform(scrollTo(), click())

        val floatingActionButton = onView(
            allOf(
                withId(R.id.addalarm), withContentDescription("New Alarm"),
                childAtPosition(
                    allOf(
                        withId(R.id.activity_alarms),
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
        floatingActionButton.perform(click())

        val appCompatToggleButton = onView(
            allOf(
                withId(R.id.toggleAMPM), withText("AM"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    13
                ),
                isDisplayed()
            )
        )
        appCompatToggleButton.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.hours),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("5"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.minutes),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("55"), closeSoftKeyboard())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.name_text_box),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText("test"), closeSoftKeyboard())

        val materialTextView = onView(
            allOf(
                withId(R.id.tuesday_button), withText("Tues"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    5
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())

        val materialButton3 = onView(
            allOf(
                withId(R.id.submitbutton), withText("Add"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    11
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.addalarm), withContentDescription("New Alarm"),
                childAtPosition(
                    allOf(
                        withId(R.id.activity_alarms),
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
        floatingActionButton2.perform(click())

        val materialTextView2 = onView(
            allOf(
                withId(R.id.sunday_button), withText("Sun"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    3
                ),
                isDisplayed()
            )
        )
        materialTextView2.perform(click())

        val materialTextView3 = onView(
            allOf(
                withId(R.id.monday_button), withText("Mon"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    4
                ),
                isDisplayed()
            )
        )
        materialTextView3.perform(click())

        val materialTextView4 = onView(
            allOf(
                withId(R.id.tuesday_button), withText("Tues"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    5
                ),
                isDisplayed()
            )
        )
        materialTextView4.perform(click())

        val materialTextView5 = onView(
            allOf(
                withId(R.id.wednesday_button), withText("Wed"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    6
                ),
                isDisplayed()
            )
        )
        materialTextView5.perform(click())

        val materialTextView6 = onView(
            allOf(
                withId(R.id.thursday_button), withText("Thurs"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    7
                ),
                isDisplayed()
            )
        )
        materialTextView6.perform(click())

        val materialTextView7 = onView(
            allOf(
                withId(R.id.friday_button), withText("Fri"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    8
                ),
                isDisplayed()
            )
        )
        materialTextView7.perform(click())

        val materialTextView8 = onView(
            allOf(
                withId(R.id.saturday_button), withText("Sat"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    9
                ),
                isDisplayed()
            )
        )
        materialTextView8.perform(click())

        val materialTextView9 = onView(
            allOf(
                withId(R.id.sunday_button), withText("Sun"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    3
                ),
                isDisplayed()
            )
        )
        materialTextView9.perform(click())

        val materialTextView10 = onView(
            allOf(
                withId(R.id.monday_button), withText("Mon"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    4
                ),
                isDisplayed()
            )
        )
        materialTextView10.perform(click())

        val materialTextView11 = onView(
            allOf(
                withId(R.id.tuesday_button), withText("Tues"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    5
                ),
                isDisplayed()
            )
        )
        materialTextView11.perform(click())

        val materialTextView12 = onView(
            allOf(
                withId(R.id.wednesday_button), withText("Wed"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    6
                ),
                isDisplayed()
            )
        )
        materialTextView12.perform(click())

        val materialTextView13 = onView(
            allOf(
                withId(R.id.thursday_button), withText("Thurs"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    7
                ),
                isDisplayed()
            )
        )
        materialTextView13.perform(click())

        val materialTextView14 = onView(
            allOf(
                withId(R.id.friday_button), withText("Fri"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    8
                ),
                isDisplayed()
            )
        )
        materialTextView14.perform(click())

        val materialTextView15 = onView(
            allOf(
                withId(R.id.saturday_button), withText("Sat"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    9
                ),
                isDisplayed()
            )
        )
        materialTextView15.perform(click())

        val materialButton4 = onView(
            allOf(
                withId(R.id.cancel_button), withText("Cancel"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    10
                ),
                isDisplayed()
            )
        )
        materialButton4.perform(click())
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
