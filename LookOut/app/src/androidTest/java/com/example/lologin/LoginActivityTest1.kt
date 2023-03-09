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
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest1 {
// Tests SkipLogin Button, Logout Button, clicks register, sign in, and forgot password buttons (Doesn't sign in or register an account)

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginActivityTest1() {
        val materialButton = onView(
            allOf(
                withId(R.id.RegisterButton), withText("Register"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val materialButton2 = onView(
            allOf(
                withId(R.id.LoginButton), withText("Sign in"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val materialTextView = onView(
            allOf(
                withId(R.id.ForgotPasswordButton), withText("Forgot Password"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
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
        materialTextView.perform(click())

        val materialButton3 = onView(
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
        materialButton3.perform(click())

        val materialButton4 = onView(
            allOf(
                withId(R.id.logout), withText("Log Out"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton4.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.inputPassword),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
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
        appCompatEditText.perform(replaceText("noemail"), closeSoftKeyboard())

        val materialButton5 = onView(
            allOf(
                withId(R.id.RegisterButton), withText("Register"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton5.perform(click())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.inputEmail),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("noemail"), closeSoftKeyboard())

        val materialButton6 = onView(
            allOf(
                withId(R.id.RegisterButton), withText("Register"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton6.perform(click())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.inputPassword), withText("noemail"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
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
        appCompatEditText3.perform(replaceText("noemail"))

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.inputPassword), withText("noemail"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
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
        appCompatEditText4.perform(closeSoftKeyboard())

        val materialButton7 = onView(
            allOf(
                withId(R.id.LoginButton), withText("Sign in"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        materialButton7.perform(click())

        val materialButton8 = onView(
            allOf(
                withId(R.id.RegisterButton), withText("Register"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton8.perform(click())

        val materialTextView2 = onView(
            allOf(
                withId(R.id.ForgotPasswordButton), withText("Forgot Password"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
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
        materialTextView2.perform(click())

        val materialButton9 = onView(
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
        materialButton9.perform(click())

        val materialButton10 = onView(
            allOf(
                withId(R.id.logout), withText("Log Out"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton10.perform(click())

        val materialButton11 = onView(
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
        materialButton11.perform(click())

        val materialButton12 = onView(
            allOf(
                withId(R.id.logout), withText("Log Out"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton12.perform(click())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.inputEmail),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(replaceText("nopassword"), closeSoftKeyboard())

        val materialButton13 = onView(
            allOf(
                withId(R.id.LoginButton), withText("Sign in"),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        materialButton13.perform(click())
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
