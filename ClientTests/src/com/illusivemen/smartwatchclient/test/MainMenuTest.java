package com.illusivemen.smartwatchclient.test;

import com.illusivemen.mapping.GoogleMapping;
import com.illusivemen.smartwatchclient.MainMenu;
import com.illusivemen.smartwatchclient.R;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

public class MainMenuTest extends ActivityUnitTestCase<MainMenu> {
	
	public MainMenuTest() {
		super(MainMenu.class);
	}
	
	private Intent cLaunchIntent;
	private MainMenu cMenu;
	private Button btnMap;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
        
        // create the activity
        cLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), MainMenu.class);
        startActivity(cLaunchIntent, null, null);
        
        // pointers for components
        cMenu = getActivity();
        btnMap = (Button) cMenu.findViewById(R.id.btnMap);
	}
	
	@Override
	public void tearDown() {
		
	}
	
	/**
     * Test if the test fixture has been set up correctly.
     */
    public void testPreconditions() {
        assertNotNull("cMenu is null", cMenu);
        assertNotNull("btnMap is null", btnMap);
    }
    
    // All Tests Should Be Below This Point
    
    /**
     * Test that the layout is correct.
     */
    public void testButtonText_openMap() {
        final String expected =
                cMenu.getString(R.string.show_location);
        final String actual = btnMap.getText().toString();
        assertEquals(expected, actual);
    }
	
    @MediumTest
	public void testActivityLaunch_openMap() {
	    btnMap.performClick();

	    final Intent launchIntent = getStartedActivityIntent();
	    assertNotNull("MapActivity was null", launchIntent);
	    // if finish() is called, this will exit the entire application, not just the activity
	    assertFalse(isFinishCalled());
	    
	    final String payload =
	            launchIntent.getStringExtra(GoogleMapping.MAP_PURPOSE);
	    assertEquals("Payload is empty", MainMenu.ACTIVITY_MESSAGE, payload);
	}
}
