package com.example.lenovo.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by LENOVO on 7/30/2018.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest {

    public static final String ITEM_NAME = "Brownies";

    @Rule
    public ActivityTestRule<MainActivity> mMainActivity =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickRecyclerViewItem_CheckBrownies(){

        // Click on the list item at position 1 i.e. Brownies
        onView(withId(R.id.rv_main_layout)).perform(RecyclerViewActions.actionOnItem(
        hasDescendant(withText(ITEM_NAME)), click()));

        // click on the RecyclerView position number 1
        onView(withId(R.id.rv_detail_layout)).perform(RecyclerViewActions.actionOnItemAtPosition(
        1, click()));

        // Check that the video is not available
        onView(withText("Video Not Available")).check(matches(isDisplayed()));
    }
}
