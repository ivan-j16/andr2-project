package com.example.andr2app;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule(LoginActivity.class);
//    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void testActivityInView() {
        Espresso.onView(withId(R.id.loginView)).check(matches(isDisplayed()));
    }


    @Test
    public void testViewComponentVisibilityAtActivityStart() {
        Espresso.onView(withId(R.id.loginEmailPassword))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(withId(R.id.loginGoogle))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        Espresso.onView(withId(R.id.progress_circular))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void testViewComponentVisibilityOnLoginEmailAndPasswordClick() {
        // Click the login with email and password button, changing the component arrangement
        Espresso.onView(withId(R.id.loginEmailPassword)).perform(click());

        // Check if old UI elements are hidden
        Espresso.onView(withId(R.id.loginEmailPassword))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        Espresso.onView(withId(R.id.loginGoogle))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        // Check if new Registration finalizatino UI elements are visible
        Espresso.onView(withId(R.id.finalizeEmailPasswordLogin))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(withId(R.id.textRegister))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(withId(R.id.textEmail))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(withId(R.id.textPwrd))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }

    @Test
    public void testNavigationToRegisterActivity() {
        Espresso.onView(withId(R.id.loginEmailPassword)).perform(click());
        Espresso.onView(withId(R.id.textRegister)).perform(click());

        // Check if new activity is in view
        Espresso.onView(withId(R.id.registerView)).check(matches(isDisplayed()));
    }

    // Does not .getActivity() returns null
//    @Test
//    public void testToastMessage() {
//
//        Espresso.onView(withId(R.id.loginEmailPassword)).perform(click());
//        Espresso.onView(withId(R.id.finalizeEmailPasswordLogin)).perform(click());
//
//        Espresso.onView(withText("Incorrect password and/or email"))
//                .inRoot(withDecorView(not(activityTestRule.getActivity().getWindow().getDecorView())))
//                .check(matches(isDisplayed()));
//    }
}