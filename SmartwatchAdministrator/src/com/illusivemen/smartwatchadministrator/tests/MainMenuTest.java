package com.illusivemen.smartwatchadministrator.tests;

import com.illusivemen.maps.AdminGoogleMapping;
import com.illusivemen.smartwatchadministrator.MainMenu;
import com.illusivemen.smartwatchadministrator.R;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

public class MainMenuTest extends ActivityUnitTestCase<MainMenu> {

	public MainMenuTest() {
		super(MainMenu.class);
	}
	
	private Intent aLaunchIntent;
	private MainMenu aMenu;
	private Button btnTrack;
	private Button btnCall;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
        
        // create the activity
        aLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), MainMenu.class);
        startActivity(aLaunchIntent, null, null);
        
        // pointers for components
        aMenu = getActivity();
        btnTrack = (Button) aMenu.findViewById(R.id.btnTrack);
        btnCall = (Button) aMenu.findViewById(R.id.callSmartWatch);
	}
	
	@Override
	public void tearDown() {
		
	}
	
	/**
     * Test if the test fixture has been set up correctly.
     */
    public void testPreconditions() {
        assertNotNull("cMenu is null", aMenu);
        assertNotNull("btnTrack is null", btnTrack);
        assertNotNull("btnCall is null", btnCall);
    }
    
    // All Tests Should Be Below This Point
    
    /**
     * Test that the layout is correct.
     */
    public void testButtonText_openMap() {
        final String expected =
                aMenu.getString(R.string.patient_tracking);
        final String actual = btnTrack.getText().toString();
        assertEquals(expected, actual);
    }
	
    
    // -------------------------------- Map Activity Tests
    
    /**
     * Test that the button opens the map activity.
     */
    @MediumTest
    public void testActivityIntent_openMap() {
	    btnTrack.performClick();
	    final Intent launchIntent = getStartedActivityIntent();
	    assertNotNull("MapActivity was null", launchIntent);
	}
    
    /**
     * Test that closing the map activity doesn't close the entire application.
     */
    @MediumTest
	public void testActivityLaunchReturns_openMap() {
	    btnTrack.performClick();
	    
	    // if finish() is called, this will exit the entire application, not just the map activity
	    assertFalse(isFinishCalled());
	}
    
    /**
     * Test that the intent payload is as expected.
     */
    @MediumTest
    public void testActivityLaunch_openMap() {
	    btnTrack.performClick();
	    final Intent launchIntent = getStartedActivityIntent();
	    final String payload =
	            launchIntent.getStringExtra(AdminGoogleMapping.MAP_PURPOSE);
	    assertEquals("Payload is empty", MainMenu.TRACK_MESSAGE, payload);
	}
    
    /**
  	* Test that the layout is correct.
  	*/     
    public void testButtonText_callSmartWatch() {
        final String expected =
                aMenu.getString(R.string.call_smartwatch);
        final String actual = btnCall.getText().toString();
        assertEquals(expected, actual);
    }
	
    /**
    * Test that the button opens the call smart watch activity
    */
    @MediumTest
	public void testActivityIntent_callSmartWatch() {
	    btnCall.performClick();

	    final Intent launchIntent = getStartedActivityIntent();
	    assertNotNull("CallActivity was null", launchIntent);
	}
}
