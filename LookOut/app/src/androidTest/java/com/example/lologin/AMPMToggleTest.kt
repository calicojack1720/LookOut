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
class AMPMToggleTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun aMPMToggleTest() {
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
        appCompatEditText2.perform(replaceText("00"), closeSoftKeyboard())

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

        val appCompatToggleButton2 = onView(
            allOf(
                withId(R.id.toggleAMPM), withText("PM"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    13
                ),
                isDisplayed()
            )
        )
        appCompatToggleButton2.perform(click())

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
        appCompatEditText3.perform(replaceText("AMTEST"), closeSoftKeyboard())

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

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.hours),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(replaceText("5"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.minutes),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(replaceText("00"), closeSoftKeyboard())

        val appCompatToggleButton3 = onView(
            allOf(
                withId(R.id.toggleAMPM), withText("AM"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    13
                ),
                isDisplayed()
            )
        )
        appCompatToggleButton3.perform(click())

        val appCompatEditText6 = onView(
            allOf(
                withId(R.id.name_text_box),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText6.perform(replaceText("PMTEST"), closeSoftKeyboard())

        val materialButton4 = onView(
            allOf(
                withId(R.id.submitbutton), withText("Add"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    11
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
