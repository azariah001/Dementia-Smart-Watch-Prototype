package com.illusivemen.patientprofile;

import com.illusivemen.db.DBConn;
import com.illusivemen.smartwatchadministrator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdatePatientProfile extends Activity {
	
	private String name;
	private String age;
	private String address;
	private String medical;
	private String contact;
	private String DEFAULT_PATIENT = "1";
	
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
            	updateProfile();
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
		
		// update in database
		new UpdatePatientProfileDB().execute(new String[]{DEFAULT_PATIENT,name,age,address,medical,contact});
	}
	
	/**
	 * Background process which retrieves the profile information.
	 */
	private class RetrieveProfile extends AsyncTask<Void, Void, String> {
		
		private DBConn conn;
		
		protected String doInBackground(Void... params) {
						
			// prepare parameters for query
			String[] parameters = {"patient_id=2"};			
			
			conn = new DBConn("/retrieveProfile.php");
			conn.execute(parameters);
			return conn.getResult();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (result != null && !result.isEmpty()) {				
	
				String[] profile = result.split(",");
				showProfile(profile);				
				
			} else {
				Context context = getApplicationContext();
				CharSequence text = "DB Error";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();	
			}		
		}
	}
	
	/**
	 * Background process which updates the profile information.
	 */
	private class UpdatePatientProfileDB extends AsyncTask<String[], Void, String> {
		
		private DBConn conn;
		String id;
		String name;
		String age;
		String address;		
		String medical;
		String contact;
		
		@Override
		protected String doInBackground(String[]... params) {
			id = params[0][0];
			name = params[0][1];
			age = params[0][2];
			address = params[0][3];
			medical = params[0][4];
			contact = params[0][5];
			
			// prepare parameters for query
			String[] parameters = {"patient_id=" + id,
					"patient_name=" + name,					
					"patient_age=" + age,
					"patient_address=" + address,
					"medical_information=" + medical,
					"emergency_contact=" + contact};
			
			conn = new DBConn("/updatePatientProfile.php");
			conn.execute(parameters);
			return conn.getResult();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (result.equals("1")) {
				// database update success
				Context context = getApplicationContext();
				CharSequence text = "Success";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();				
			}
		}
	}
}
