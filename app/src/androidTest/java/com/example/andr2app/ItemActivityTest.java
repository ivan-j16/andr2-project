package com.example.andr2app;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.*;

public class ItemActivityTest {

    @Rule
    public ActivityTestRule<ItemActivity> mActivityTestRule = new ActivityTestRule<ItemActivity>(ItemActivity.class);

    private ItemActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

    @Test
    public void testLoadProducts() {
        assertNotNull(mActivity.loadProducts());
    }
}