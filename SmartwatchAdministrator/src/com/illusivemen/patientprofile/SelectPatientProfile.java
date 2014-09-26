package com.illusivemen.patientprofile;

import com.illusivemen.db.DBConn;
import com.illusivemen.smartwatchadministrator.MainMenu;
import com.illusivemen.smartwatchadministrator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectPatientProfile extends Activity {
	
	public final static String PROFILE_UPDATE = "ProfileUpdate";
	
	/**
	 * Factory method for creating a launch intent.
	 * @param context
	 * @param payload extra string input
	 * @return
	 */
	public static Intent makeIntent(Context context, String payload) {
		return new Intent(context, SelectPatientProfile.class);
	}
	
	/**
	 * The activity starts with a connection to the database.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_profiles);
		
		final Button updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	updateSelectedProfile(v);
            }
        });
	}
	
	/**
	 * Update the selected profile
	 */
	private void updateSelectedProfile(View view) {
		startActivity(UpdatePatientProfile.makeIntent(SelectPatientProfile.this, PROFILE_UPDATE));
		finish();
	}
	
	/** 
	 * Display the profiles in a list view
	 * @param profile
	 */
	private void displayProfiles(String[] profile) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Background process which retrieves the profile information.
	 */
	private class RetrieveProfiles extends AsyncTask<Void, Void, String> {
		
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
			displayProfiles(profile);
			
		}		
	}

}
