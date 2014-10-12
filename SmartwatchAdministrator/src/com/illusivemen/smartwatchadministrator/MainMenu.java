package com.illusivemen.smartwatchadministrator;

//import com.illusivemen.checkin.PatientCheckIn;
import com.illusivemen.checkin.PatientCheckIn;
import com.illusivemen.login.AdminLogIn;
import com.illusivemen.maps.AdminGoogleMapping;
import com.illusivemen.patients.MyPatients;
import com.illusivemen.reminder.CalendarReminder;
import com.illusivemen.service.NotificationService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainMenu extends Activity {

	public final static String TRACK_MESSAGE = "TrackPatients";
	public final static String CALL_MESSAGE = "CallSmartWatch";
	public final static String SET_REMINDER = "SetReminder";
	public final static String PATIENT_CHECKIN = "PatientCheckIn";
	public final static String PROFILE_SELECT = "ProfileSelect";
	public final static String ADMIN_LOGIN = "AdminLogin";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		// start background service
		this.startService(new Intent(this, NotificationService.class));
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
	
	//IM-21 - Call SmartWatch
	public void callSmartWatch(View view) {
		startActivity(CallSmartWatch.makeIntent(MainMenu.this, CALL_MESSAGE));
	}
	
	//IM-27 - Activity Reminder
	public void setReminder(View view) {
		startActivity(CalendarReminder.makeIntent(MainMenu.this, SET_REMINDER));
	}
	
	//IM-25 - Patient Check In
	public void patientCheckIn(View view) {
		startActivity(PatientCheckIn.makeIntent(MainMenu.this, PATIENT_CHECKIN));
	}
	
	//IM-19 - Patient Profile Update
	public void selectPatientProfile(View view) {
		startActivity(MyPatients.makeIntent(MainMenu.this, PROFILE_SELECT));
	}
	
	//IM-15
	public void loginPrompt(View view) {
		startActivity(new Intent(this, AdminLogIn.class));
	}
}
	
	
	

