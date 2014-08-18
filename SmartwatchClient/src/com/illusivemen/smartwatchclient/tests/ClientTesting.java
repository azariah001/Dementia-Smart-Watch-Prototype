package com.illusivemen.smartwatchclient;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

public class ClientTesting extends ActivityUnitTestCase<MainMenu> {
	
	private Intent mapLaunchIntent;
	
	public ClientTesting(Class<MainMenu> activityClass) {
		super(activityClass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        mapLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), GoogleMapping.class);
        startActivity(mapLaunchIntent, null, null);
        final Button btnMap =
                (Button) getActivity()
                .findViewById(R.id.btnMap);
    }
	
	@MediumTest
	public void mapLaunchedWithIntent() {
	    startActivity(mapLaunchIntent, null, null);
	    final Button launchNextButton =
	            (Button) getActivity()
	            .findViewById(R.id.btnMap);
	    launchNextButton.performClick();

	    final Intent launchIntent = getStartedActivityIntent();
	    assertNotNull("Intent was null", launchIntent);
	    assertTrue(isFinishCalled());

	    final String payload =
	            launchIntent.getStringExtra(MainMenu.ACTIVITY_MESSAGE);
	    assertEquals("LIVE_MAP", MainMenu.ACTIVITY_MESSAGE, payload);
	}


	

}
