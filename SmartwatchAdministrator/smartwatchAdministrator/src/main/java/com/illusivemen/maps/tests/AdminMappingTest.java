package com.illusivemen.maps.tests;

import com.illusivemen.maps.AdminGoogleMapping;
import com.illusivemen.smartwatchadministrator.R;
import com.robotium.solo.Solo;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

public class AdminMappingTest extends ActivityInstrumentationTestCase2<AdminGoogleMapping> {
	
	private Solo solo;
	
	public AdminMappingTest() {
		super(AdminGoogleMapping.class);
	}
	
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
		final String expected = solo.getString(R.string.title_activity_admin_google_mapping);
		final String actual = getActivity().getTitle().toString();
		assertEquals(expected, actual);
	}
}
