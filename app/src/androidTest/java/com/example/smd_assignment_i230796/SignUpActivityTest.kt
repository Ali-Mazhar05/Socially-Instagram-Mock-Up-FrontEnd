package com.example.smd_assignment_i230796

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpActivityTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(SignUpActivity::class.java)

    @Test
    fun testUserSignUpNavigation() {
        onView(withId(R.id.etUsername)).perform(typeText("johndoe"), closeSoftKeyboard())
        onView(withId(R.id.etFirstName)).perform(typeText("John"), closeSoftKeyboard())
        onView(withId(R.id.etLastName)).perform(typeText("Doe"), closeSoftKeyboard())
        onView(withId(R.id.etDob)).perform(typeText("01/01/2000"), closeSoftKeyboard())
        onView(withId(R.id.etEmail)).perform(typeText("john@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("password123"), closeSoftKeyboard())


        onView(withId(R.id.btnCreateAccount)).perform(click())


        onView(withId(R.id.etUsername)).check(matches(isDisplayed()))
    }


    @Test
    fun testBackArrowClosesActivity() {
        val scenario = ActivityScenario.launch(SignUpActivity::class.java)

        onView(withId(R.id.backArrow)).perform(click())

        scenario.onActivity { activity -> assertTrue(activity.isFinishing)
        }
    }

}
