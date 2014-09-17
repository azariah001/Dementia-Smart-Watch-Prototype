package com.illusivemen.checkin;


import com.illusivemen.smartwatchadministrator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;

// WIP
public class PatientCheckIn extends Activity {
	
	String phoneNumber = "0411794760"; // TODO: should not be static
	String message = "Checkin Request";
	
	/**
	 * Factory method for creating a launch intent.
	 * @param context
	 * @param payload extra string input
	 * @return
	 */
	public static Intent makeIntent(Context context, String payload) {
		return new Intent(context, PatientCheckIn.class);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_check_in);
	}
	
	// sends a checkin request the client instantly via SMS
	public void sendRequest(View view) {		
		SmsManager manager = SmsManager.getDefault();
		manager.sendTextMessage(phoneNumber, null, message, null, null);		
	}
	
	/**
	 * Settings (frequency, patient id?)
	 */
		
	
	/**
	 * provide a list of available patients
	 */
}
