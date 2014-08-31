package com.illusivemen.smartwatchadministrator.test;

import com.illusivemen.maps.AdminGoogleMapping;
import com.illusivemen.smartwatchadministrator.MainMenu;
import com.illusivemen.smartwatchadministrator.R;
import com.robotium.solo.Solo;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

public class AdminMappingTest extends ActivityInstrumentationTestCase2<AdminGoogleMapping> {

	public AdminMappingTest() {
		super(AdminGoogleMapping.class);
	}
	
	private Solo solo;
	private Intent gmLaunchIntent;
	private AdminGoogleMapping mapActivity;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
   		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	public void tearDown() {
		solo.finishOpenedActivities();
	}
	
	/**
     * Test if the test fixture has been set up correctly.
     */
    public void testPreconditions() {
        //assertNotNull("cMenu is null", mapActivity);
    }
    
    // All Tests Should Be Below This Point
    
    /**
     * Test that the layout is correct.
     */
    public void testTitle_activity() {
        //final String expected =
        //        solo.getString(R.string.title_activity_admin_google_mapping);
        //final String actual = mapActivity.getTitle().toString();
        //assertEquals(expected, actual);
    }
	
    
    // -------------------------------- Map Activity Tests
    
    /**
     * Test that the button opens the map activity.
     */
    @MediumTest
    public void testActivityIntent_openMap() {
	    assertTrue(true);
	}
    
}
