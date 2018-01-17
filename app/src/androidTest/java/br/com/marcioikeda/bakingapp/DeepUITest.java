package br.com.marcioikeda.bakingapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.marcioikeda.bakingapp.R;
import br.com.marcioikeda.bakingapp.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DeepUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void deepUITest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        // Wait for data loading
        sleep(10000);

        //Start first recipe
        ViewInteraction appCompatTextView = onView(
                first(allOf(withId(R.id.tv_action1), withText("START"), isDisplayed())));
        appCompatTextView.perform(click());

        sleep(700);

        //It should display Nutella Pie
        ViewInteraction textView = onView(allOf(withText("Nutella Pie")));
        textView.check(matches(isDisplayed()));

        //It should show ingredient
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tv_ingredient), withText("Graham Cracker crumbs 2.0 CUP"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.item_list),
                                        2),
                                1)
                        ));
        textView2.check(matches(isDisplayed()));

        sleep(700);

        //Click on first step
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.item_list),
                        childAtPosition(
                                withId(R.id.frameLayout),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(12, click()));

        sleep(700);

        //It should show Recipe Introduction
        ViewInteraction appCompatTextView2 = onView(
                first(allOf(withId(R.id.item_detail))));
        appCompatTextView2.check(matches(withText("Recipe Introduction")));

        sleep(5000);

        //It should show videoplayer
        ViewInteraction frameLayout = onView(
                first(allOf(withId(R.id.playerView))));
        frameLayout.check(matches(isDisplayed()));

    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private static <T> Matcher<T> first(final Matcher<T> matcher) {
        return new BaseMatcher<T>() {
            boolean isFirst = true;

            @Override
            public boolean matches(final Object item) {
                if (isFirst && matcher.matches(item)) {
                    isFirst = false;
                    return true;
                }

                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("should return first matching item");
            }
        };
    }
}
