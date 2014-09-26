package com.illusivemen.patientprofile;

import com.illusivemen.db.DBConn;
import com.illusivemen.smartwatchadministrator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UpdatePatientProfile extends Activity {
	
	private String name;
	private String age;
	private String address;
	private String contact;
	private String medical;	
	
	/**
	 * Factory method for creating a launch intent.
	 * @param context
	 * @param payload extra string input
	 * @return
	 */
	public static Intent makeIntent(Context context, String payload) {
		return new Intent(context, UpdatePatientProfile.class);
	}
	
	/**
	 * The activity starts with a connection to the database.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_patient_profile);
		
		final Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//saveProfile();
            }
        });
		
		// retrieve information and insert into display
		new RetrieveProfile().execute();
	}
	
	/**
	 * Given that the profile has been retrieved,
	 * this method will display the result.
	 * @param profile
	 */
	private void showProfile(String[] profile) {	
				
		// retrieve values
		name = profile[0];
		age	= profile[1];
		address = profile[2];
		medical = profile[3];
		contact = profile[4];
		
		// display values in profile
		((EditText) findViewById(R.id.patientName)).setText(name);
		((EditText) findViewById(R.id.patientAge)).setText(age);
		((EditText) findViewById(R.id.patientAddress)).setText(address);
		((EditText) findViewById(R.id.medicalInformation)).setText(medical);
		((EditText) findViewById(R.id.emergencyContact)).setText(contact);
	}
	
	/**
	 * Determines if the values have been changed
	 * Overwrites them if they have
	 */
	private void updateProfile() {
		
		String name = ((EditText) findViewById(R.id.patientName)).getText().toString();
		String age = ((EditText) findViewById(R.id.patientAge)).getText().toString();
		String address = ((EditText) findViewById(R.id.patientAddress)).getText().toString();
		String medical = ((EditText) findViewById(R.id.medicalInformation)).getText().toString();
		String contact = ((EditText) findViewById(R.id.emergencyContact)).getText().toString();
		
		if (this.name != name) {
			this.name = name;
		}
		if (this.age != age) {
			this.age = age;
		}
		if (this.address != address) {
			this.address = address;
		}
		if (this.medical != medical) {
			this.medical = medical;
		}
		if (this.contact != contact) {
			this.contact = contact;
		}
	}
	
	/**
	 * Background process which retrieves the profile information.
	 */
	private class RetrieveProfile extends AsyncTask<Void, Void, String> {
		
		private DBConn conn;
		
		@Override
		protected String doInBackground(Void... params) {
			conn = new DBConn("/retrieveProfile.php");
			conn.execute();
			return conn.getResult();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			String[] profile = result.split(",");
			showProfile(profile);
			
		}		
	}
	
	private class DBSave extends AsyncTask<String[], Void, String> {
		
		private DBConn conn;		
		
		@Override
		protected String doInBackground(String[]... params) {
			conn = new DBConn("/updateProfile.php");
			conn.execute();
			return conn.getResult();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
}
