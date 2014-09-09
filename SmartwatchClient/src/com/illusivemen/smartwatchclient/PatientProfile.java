package com.illusivemen.smartwatchclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.maps.model.LatLng;
import com.illusivemen.mapping.GoogleMapping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PatientProfile extends Activity {
	
	
	public static Intent makeIntent(Context context, String payload) {
        return new Intent(context, PatientProfile.class);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_profile);
		new RetrieveProfile().execute();
	}
	
	private void showProfile(String[] profile) {	
		
		// Initialising intermediary variables
		String name;
		String age;
		String address;
		String contact;
		String medical;
		
		// Getting previously saved Settings from SharedPreferences
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences
				(getBaseContext());
		boolean prefProfileName    = getPrefs.getBoolean("hideName", true);
		boolean prefProfileAge     = getPrefs.getBoolean("hideAge", true);
		boolean prefProfileAddress = getPrefs.getBoolean("hideAddress", true);
		boolean prefProfileMedical = getPrefs.getBoolean("hideMedicalInfo", true);
		boolean prefProfileContact = getPrefs.getBoolean("hideEmergencyContact", true);
		
		// Value for hidden Profile fields
		String hiddenInfo = "This information is hidden.";
		
		// Sets the intermediary variable to hidden or the actual Profile information
		name 	= (prefProfileName    == true) ? hiddenInfo : profile[0];
		age 	= (prefProfileAge     == true) ? hiddenInfo : profile[1];
		address = (prefProfileAddress == true) ? hiddenInfo : profile[2];
		contact = (prefProfileContact == true) ? hiddenInfo : profile[3];
		medical = (prefProfileMedical == true) ? hiddenInfo : profile[4];
		
		// Sets the EditText field to the intermediary variable
		EditText patientName = (EditText) findViewById(R.id.patientName);
		patientName.setText(name);		
		patientName.setKeyListener(null);
			
		EditText patientAge = (EditText) findViewById(R.id.patientAge);
		patientAge.setText(age);		
		patientAge.setKeyListener(null);
			
		EditText patientAddress = (EditText) findViewById(R.id.patientAddress);
		patientAddress.setText(address);		
		patientAddress.setKeyListener(null);
			
		EditText medicalInformation = (EditText) findViewById(R.id.medicalInformation);
		medicalInformation.setText(medical);		
		medicalInformation.setKeyListener(null);
			
		EditText emergencyContact = (EditText) findViewById(R.id.emergencyContact);
		emergencyContact.setText(contact);		
		emergencyContact.setKeyListener(null);
		
	}
	
	
	private class RetrieveProfile extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String strUrl = "http://agile.azarel-howard.me/retrieveProfile.php";
			URL url = null;
			StringBuffer sb = new StringBuffer();
			try {
				url = new URL(strUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.connect();
	            InputStream iStream = connection.getInputStream();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
	            String line = "";
	            while( (line = reader.readLine()) != null){
	                sb.append(line);
	            }

	            reader.close();
	            iStream.close();

	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return sb.toString();
	    }
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			String[] profile = result.split(",");
			showProfile(profile);
			
		}
	}
}