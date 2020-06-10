package com.example.andr2app;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class AddItemActivityTest {

    @Rule
    public ActivityTestRule<AddItemActivity> mActivityTestRule = new ActivityTestRule<AddItemActivity>(AddItemActivity.class);
    private AddItemActivity mActivity = null;
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View view = mActivity.findViewById(R.id.btnPostProduct);
        assertNotNull(view);
    }

    @Test
    public void testAddData(){
        String name = "Product test name";
        double price = 35.99;
        String url = "none";
        Random rand = new Random();
        int product_id = rand.nextInt(1000000);

        Product p = new Product(name, price, url, String.valueOf(product_id));

        assertEquals(p.getName(), mActivity.addData(name, price, url, String.valueOf(product_id)).getName());
    }


    @Test
    public void testSentToMainActivity(){
        mActivity.sentToMainActivity();
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(mainActivity);

        mainActivity.finish();
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}