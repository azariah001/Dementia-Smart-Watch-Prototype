package com.illusivemen.smartwatchadministrator.test;

import com.illusivemen.smartwatchadministrator.MainMenu;
import com.illusivemen.smartwatchadministrator.R;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class MainMenuTest extends ActivityUnitTestCase<MainMenu> {

	public MainMenuTest() {
		super(MainMenu.class);
	}
	
	private Intent aLaunchIntent;
	private MainMenu aMenu;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
        
        // create the activity
        aLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), MainMenu.class);
        startActivity(aLaunchIntent, null, null);
        
        // pointers for components
        aMenu = getActivity();
	}
	
	@Override
	public void tearDown() {
		
	}
	
	/**
     * Test if the test fixture has been set up correctly.
     */
    public void testPreconditions() {
        assertNotNull("cMenu is null", aMenu);
    }
    
    // All Tests Should Be Below This Point
	
}
