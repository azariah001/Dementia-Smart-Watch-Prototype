package com.illusivemen.smartwatchadministrator;

//import com.illusivemen.checkin.PatientCheckIn;
import com.illusivemen.checkin.PatientCheckIn;
import com.illusivemen.maps.AdminGoogleMapping;
import com.illusivemen.reminder.CalendarReminder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

public class MainMenu extends Activity {

	public final static String TRACK_MESSAGE = "TrackPatients";
	public final static String CALL_MESSAGE = "CallSmartWatch";
	public final static String SET_REMINDER = "SetReminder";
	public final static String PATIENT_CHECKIN = "PatientCheckIn";
	
	private boolean patientPanic = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void showLocations(View view) {
		startActivity(AdminGoogleMapping.makeIntent(MainMenu.this, TRACK_MESSAGE));
	}
	
	//Dummy method, emulates the 
	public void changePanic(View view) {
		
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	        patientPanic = true;
	        
	        //Open dialogBox, option to see patients current location

	    } else {
	        patientPanic = false;
	    }
		
	}
	
	//IM-21 - Call SmartWatch
	public void callSmartWatch(View view) {
		startActivity(CallSmartWatch.makeIntent(MainMenu.this, CALL_MESSAGE));
	}
	
	//IM-27 - Activity Reminder
	public void setReminder(View view) {
		startActivity(CalendarReminder.makeIntent(MainMenu.this, SET_REMINDER));
	}
	
	// - Patient Check In
	public void patientCheckIn(View view) {
		startActivity(PatientCheckIn.makeIntent(MainMenu.this, PATIENT_CHECKIN));
	}	
}
	
	
	

