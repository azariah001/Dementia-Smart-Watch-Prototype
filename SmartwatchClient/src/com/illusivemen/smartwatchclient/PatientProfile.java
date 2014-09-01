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
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class PatientProfile extends Activity {
	
	
	public static Intent makeIntent(Context context, String payload) {
        return new Intent(context, PatientProfile.class);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new RetrieveProfile().execute();
	}
	
	private void showProfile(String[] profile) {
		
		String name = profile[0];
		String age = profile[1];
		String address = profile[2];
		String medicalInfo = profile[3];
		String emergencyContact = profile[4];
		
		Context context = getApplicationContext();
		CharSequence text = "Name: " + name +"\n"
				+ "Age: " + age + "\n"
						+ "Address: " + address + "\n"
								+ "Medical: " + medicalInfo + "\n"
										+ "Emergency Contact: " + emergencyContact;
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
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