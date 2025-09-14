package com.example.smd_assignment_i230796

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogInActivityTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(LogInActivity::class.java)


    @Test
    fun testEmptyLoginStaysOnSameScreen() {

        onView(withId(R.id.btnLogin)).perform(click())


        onView(withId(R.id.etUsername)).check(matches(isDisplayed()))
    }


    @Test
    fun testSuccessfulLoginNavigatesToMainFeed() {
        //Enter username
        onView(withId(R.id.etUsername))
            .perform(typeText("testuser"), closeSoftKeyboard())

        //Enter password
        onView(withId(R.id.etPassword))
            .perform(typeText("12345"), closeSoftKeyboard())

        //Click login
        onView(withId(R.id.btnLogin)).perform(click())

        //taking to mainfeed
        onView(withId(R.id.main_feed_root))
            .check(matches(isDisplayed()))
    }
}
