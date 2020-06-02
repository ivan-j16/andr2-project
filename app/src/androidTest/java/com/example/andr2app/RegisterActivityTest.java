package com.example.andr2app;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class RegisterActivityTest {

    @Rule
    public ActivityScenarioRule<RegisterActivity> activityScenarioRule = new ActivityScenarioRule(RegisterActivity.class);

    @Test
    public void testActivityInView() {
        Espresso.onView(withId(R.id.registerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility() {
        Espresso.onView(withId(R.id.textEmail))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(withId(R.id.textPwrd))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(withId(R.id.textConfirmPwrd))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        Espresso.onView(withId(R.id.progress_circular))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void testNavigationToLoginActivity() {
        Espresso.onView(withId(R.id.textGoBackToLogin)).perform(click());

        // Check if new activity is in view
        Espresso.onView(withId(R.id.loginView)).check(matches(isDisplayed()));
    }
}