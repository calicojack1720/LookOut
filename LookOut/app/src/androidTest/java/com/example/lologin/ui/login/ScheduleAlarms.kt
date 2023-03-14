package com.example.lologin.ui.login


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.lologin.R
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
class ScheduleAlarms {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun scheduleAlarms() {
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
                    4
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val materialButton2 = onView(
            allOf(
                withId(android.R.id.button1), withText("Ok"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
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
        appCompatEditText.perform(replaceText("20"), closeSoftKeyboard())

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
        appCompatEditText2.perform(replaceText("25"), closeSoftKeyboard())

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
        appCompatEditText3.perform(replaceText("fffff"), closeSoftKeyboard())

        val materialButton3 = onView(
            allOf(
                withId(R.id.cancel_button), withText("Cancel"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    3
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
        appCompatEditText4.perform(replaceText("12"), closeSoftKeyboard())

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
        appCompatEditText5.perform(replaceText("27"), closeSoftKeyboard())

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
        appCompatEditText6.perform(replaceText("Test"), closeSoftKeyboard())

        val appCompatEditText7 = onView(
            allOf(
                withId(R.id.name_text_box), withText("Test"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText7.perform(pressImeActionButton())

        val materialButton4 = onView(
            allOf(
                withId(R.id.submitbutton), withText("Add"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    4
                ),
                isDisplayed()
            )
        )
        materialButton4.perform(click())

        val floatingActionButton3 = onView(
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
        floatingActionButton3.perform(click())

        val appCompatEditText8 = onView(
            allOf(
                withId(R.id.hours),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText8.perform(replaceText("12"), closeSoftKeyboard())

        val appCompatEditText9 = onView(
            allOf(
                withId(R.id.minutes),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText9.perform(replaceText("28"), closeSoftKeyboard())

        val appCompatEditText10 = onView(
            allOf(
                withId(R.id.name_text_box),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText10.perform(replaceText("Test2"), closeSoftKeyboard())

        val materialButton5 = onView(
            allOf(
                withId(R.id.submitbutton), withText("Add"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    4
                ),
                isDisplayed()
            )
        )
        materialButton5.perform(click())

        val floatingActionButton4 = onView(
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
        floatingActionButton4.perform(click())

        val appCompatEditText11 = onView(
            allOf(
                withId(R.id.hours),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText11.perform(replaceText("9"), closeSoftKeyboard())

        val appCompatEditText12 = onView(
            allOf(
                withId(R.id.minutes),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText12.perform(replaceText("999"), closeSoftKeyboard())

        val appCompatEditText13 = onView(
            allOf(
                withId(R.id.name_text_box),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText13.perform(replaceText("asfasfasfafasfafs"), closeSoftKeyboard())

        val materialButton6 = onView(
            allOf(
                withId(R.id.submitbutton), withText("Add"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    4
                ),
                isDisplayed()
            )
        )
        materialButton6.perform(click())

        val materialButton7 = onView(
            allOf(
                withId(R.id.cancel_button), withText("Cancel"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton7.perform(click())

        val floatingActionButton5 = onView(
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
        floatingActionButton5.perform(click())

        val appCompatEditText14 = onView(
            allOf(
                withId(R.id.hours),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText14.perform(replaceText("1"), closeSoftKeyboard())

        val appCompatEditText15 = onView(
            allOf(
                withId(R.id.minutes),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText15.perform(replaceText("1"), closeSoftKeyboard())

        val materialButton8 = onView(
            allOf(
                withId(R.id.submitbutton), withText("Add"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    4
                ),
                isDisplayed()
            )
        )
        materialButton8.perform(click())

        val floatingActionButton6 = onView(
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
        floatingActionButton6.perform(click())

        val appCompatEditText16 = onView(
            allOf(
                withId(R.id.hours),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText16.perform(replaceText("12"), closeSoftKeyboard())

        val appCompatEditText17 = onView(
            allOf(
                withId(R.id.minutes),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText17.perform(replaceText("1"), closeSoftKeyboard())

        val appCompatEditText18 = onView(
            allOf(
                withId(R.id.name_text_box),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText18.perform(replaceText("1"), closeSoftKeyboard())

        val materialButton9 = onView(
            allOf(
                withId(R.id.submitbutton), withText("Add"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    4
                ),
                isDisplayed()
            )
        )
        materialButton9.perform(click())

        val floatingActionButton7 = onView(
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
        floatingActionButton7.perform(click())

        val appCompatEditText19 = onView(
            allOf(
                withId(R.id.hours),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText19.perform(replaceText("1"), closeSoftKeyboard())

        val appCompatEditText20 = onView(
            allOf(
                withId(R.id.minutes),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText20.perform(replaceText("12"), closeSoftKeyboard())

        val materialButton10 = onView(
            allOf(
                withId(R.id.submitbutton), withText("Add"),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    4
                ),
                isDisplayed()
            )
        )
        materialButton10.perform(click())
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
