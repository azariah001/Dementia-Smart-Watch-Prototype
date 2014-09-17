package com.illusivemen.checkin;


import com.illusivemen.smartwatchadministrator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

// WIP
public class PatientCheckIn extends Activity {
	
	/**
	 * Factory method for creating a launch intent.
	 * @param context
	 * @param payload extra string input
	 * @return
	 */
	public static Intent makeIntent(Context context, String payload) {
		return new Intent(context, PatientCheckIn.class);
	}
	
	/**
	 * The activity starts with a connection to the database.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_check_in);
		
		// retrieve information and insert into display
		//new RetrieveProfile().execute();
	}
	
	/**
	 * Settings (frequency, patient id?)
	 */
		
	
	/**
	 * provide a list of available patients
	 */
}
