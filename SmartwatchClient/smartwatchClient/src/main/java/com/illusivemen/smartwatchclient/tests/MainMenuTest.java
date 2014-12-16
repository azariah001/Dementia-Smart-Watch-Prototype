package com.illusivemen.smartwatchclient.tests;

import com.illusivemen.mapping.GoogleMapping;
import com.illusivemen.smartwatchclient.MainMenu;
import com.illusivemen.smartwatchclient.PatientProfile;
import com.illusivemen.smartwatchclient.R;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

// change to instrumentatiton2
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
	 * Test if the layout is correctl.
	 */
	public void testPreconditions() {
		Button btnTrack = (Button) solo.getButton("My Location");
		Button btnGame = (Button) solo.getButton("Memory Game");
		Button btnLogin = (Button) solo.getButton("Login");
		assertNotNull("Login Button is null", btnLogin);
		assertNotNull("Track Button is null", btnTrack);
		assertNotNull("Game Button is null", btnGame);
	}
	
	/**
	 * Test that the title is correct.
	 */
	public void testButtonText_openMap() {
		final String expected = solo.getString(R.string.app_name);
		final String actual = getActivity().getTitle().toString();
		assertEquals(expected, actual);
	}
	
	@MediumTest
	public void testActivityLaunch_openMap() {
		solo.clickOnButton("My Location");
		assertTrue(solo.waitForActivity(GoogleMapping.class.getSimpleName()));
	}
	
	/**
	 * Test that closing the map activity doesn't close the entire application.
	 */
	@MediumTest
	public void testActivityLaunchReturns_openMap() {
		solo.clickOnButton("My Location");
		
		// if finish() is called, this will exit the entire application, not just the map activity
		assertFalse(solo.getCurrentActivity().isFinishing());
	}
	
	/**
	 * Test that the button opens the patient profile activity
	 */
	@MediumTest
	public void testActivityIntent_openProfile() {
		solo.clickOnButton("Patient Profile");
		assertTrue(solo.waitForActivity(PatientProfile.class.getSimpleName()));
	}
}
