package com.erkprog.madtaxi.ui.main;

import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;


import com.erkprog.madtaxi.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainTest {

  @Rule
  public ActivityTestRule<MainActivity> mMainActivityActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Rule
  public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


  @Test
  public void testAddressTextOnMapMoved() {
    onView(withId(R.id.map))
        .perform(swipeLeft());
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(
        () ->
            onView(withId(R.id.address))
                .check(matches(not(withText(""))))
    );
  }

  @Test
  public void testGpsProgressBarOnGettingLocation() {

    onView(withId(R.id.main_gps_progress))
        .check(matches(not(isDisplayed())));
    onView(withId(R.id.get_location_img))
        .perform(click());
    onView(withId(R.id.get_location_img))
        .check(matches(not(isDisplayed())));

    await().atMost(5, TimeUnit.SECONDS).untilAsserted(
        () -> onView(withId(R.id.main_gps_progress))
            .check(matches(isDisplayed()))
    );
    onView(withId(R.id.main_gps_textinfo))
        .check(matches(isDisplayed()));

    await().atMost(5, TimeUnit.SECONDS).untilAsserted(
        () -> onView(withId(R.id.get_location_img))
        .check(matches(isDisplayed()))
    );

    onView(withId(R.id.main_gps_progress))
        .check(matches(not(isDisplayed())));
    onView(withId(R.id.main_gps_textinfo))
        .check(matches(not(isDisplayed())));
  }
}
