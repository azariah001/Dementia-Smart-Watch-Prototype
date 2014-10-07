package com.illusivemen.smartwatchclient;

import com.illusivemen.db.DBConn;
import com.illusivemen.login.LogIn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

public class PatientProfile extends Activity {
	
	private String name;
	private String age;
	private String address;
	private String contact;
	private String medical;
	private static final String HIDDEN_MSG = "Hidden";
	private LogIn login;
	private String id;
	
	/**
	 * Factory method for creating a launch intent.
	 * @param context
	 * @param payload extra string input
	 * @return
	 */
	public static Intent makeIntent(Context context) {
		return new Intent(context, PatientProfile.class);
	}
	
	/**
	 * The activity starts with a connection to the database.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_profile);
		
		login = new LogIn();
		id = login.GetId(this.getApplicationContext());
		
		// retrieve information and insert into display
		new RetrieveProfile().execute();
	}
	
	/**
	 * Given that the profile has been retrieved,
	 * this method will display the result.
	 * @param profile
	 */
	private void showProfile(String[] profile) {	
		
		// previously saved Settings from SharedPreferences
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences
				(getBaseContext());
		boolean prefProfileName    = getPrefs.getBoolean("hideName", true);
		boolean prefProfileAge     = getPrefs.getBoolean("hideAge", true);
		boolean prefProfileAddress = getPrefs.getBoolean("hideAddress", true);
		boolean prefProfileMedical = getPrefs.getBoolean("hideMedicalInfo", true);
		boolean prefProfileContact = getPrefs.getBoolean("hideEmergencyContact", true);
		
		// retrieve values or message that information is hidden
		name 	= (prefProfileName    == true) ? HIDDEN_MSG : profile[0];
		age 	= (prefProfileAge     == true) ? HIDDEN_MSG : profile[1];
		address = (prefProfileAddress == true) ? HIDDEN_MSG : profile[2];
		medical = (prefProfileMedical == true) ? HIDDEN_MSG : profile[3];
		contact = (prefProfileContact == true) ? HIDDEN_MSG : profile[4];
		
		// display values in profile
		((EditText) findViewById(R.id.patientName)).setText(name);
		((EditText) findViewById(R.id.patientAge)).setText(age);
		((EditText) findViewById(R.id.patientAddress)).setText(address);
		((EditText) findViewById(R.id.medicalInformation)).setText(medical);
		((EditText) findViewById(R.id.emergencyContact)).setText(contact);
	}
	
	/**
	 * Background process which retrieves the profile information.
	 */
	private class RetrieveProfile extends AsyncTask<Void, Void, String> {
		
		private DBConn conn;
		
		
		@Override
		protected String doInBackground(Void... params) {
			
			// use a default if not logged in
			if (id.isEmpty()) {
				id = "1";
			}
			
			// prepare parameters for query
			String[] parameters = {"patient_id=" + id};
			
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
}