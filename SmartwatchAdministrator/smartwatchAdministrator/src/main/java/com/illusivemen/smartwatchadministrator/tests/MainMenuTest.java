package com.illusivemen.smartwatchadministrator.tests;

import com.illusivemen.login.AdminLogIn;
import com.illusivemen.maps.AdminGoogleMapping;
import com.illusivemen.smartwatchadministrator.MainMenu;
import com.illusivemen.smartwatchadministrator.R;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

public class MainMenuTest extends ActivityInstrumentationTestCase2<MainMenu> {

	private Solo solo;
	
	public MainMenuTest() {
		super(MainMenu.class);
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
	 * Test that the layout is correct.
	 */
	public void testPreconditions() {
		Button btnTrack = (Button) solo.getButton("Patient Locations");
		Button btnCall = (Button) solo.getButton("Call SmartWatch");
		Button btnLogin = (Button) solo.getButton("Login");
		assertNotNull("Login Button is null", btnLogin);
		assertNotNull("Track Button is null", btnTrack);
		assertNotNull("Call Button is null", btnCall);
	}
	
	// All Tests Should Be Below This Point
	
	/**
	 * Test that the title is correct.
	 */
	public void testButtonText_openMap() {
		final String expected = solo.getString(R.string.app_name);
		final String actual = getActivity().getTitle().toString();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test that the button opens the map activity.
	 */
	@MediumTest
	public void testActivityIntent_openMap() {
		solo.clickOnButton("Patient Locations");
		assertTrue(solo.waitForActivity(AdminGoogleMapping.class.getSimpleName()));
	}
	
	/**
	 * Test that closing the map activity doesn't close the entire application.
	 */
	@MediumTest
	public void testActivityLaunchReturns_openMap() {
		solo.clickOnButton("Patient Locations");
		
		// if finish() is called, this will exit the entire application, not just the map activity
		assertFalse(solo.getCurrentActivity().isFinishing());
	}
	
	/**
	 * Test that the button opens the call smart watch activity
	 */
	@MediumTest
	public void testActivityIntent_login() {
		solo.clickOnButton("Login");
		assertTrue(solo.waitForActivity(AdminLogIn.class.getSimpleName()));
	}
}
