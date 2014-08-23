package com.illusivemen.smartwatchadministrator.test;

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
	
    @MediumTest
	public void testActivityLaunch_openMap() {
	    btnTrack.performClick();

	    final Intent launchIntent = getStartedActivityIntent();
	    assertNotNull("MapActivity was null", launchIntent);
	    assertTrue(isFinishCalled());
	    
	    final String payload =
	            launchIntent.getStringExtra(AdminGoogleMapping.MAP_PURPOSE);
	    assertEquals("Payload is empty", MainMenu.TRACK_MESSAGE, payload);
	}
	
}
