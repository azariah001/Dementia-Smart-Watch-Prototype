package com.illusivemen.login;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.illusivemen.smartwatchclient.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * login to the app so that we can track multiple patients
 * TODO - Password validation
 * @author Daniel Luckhurst
 *
 */
public class PatientLogIn extends Activity {
	
	LogIn login;
	
	/**
	 * Factory method for creating a launch intent.
	 * @param context
	 * @param payload extra string input
	 * @return
	 */
	public static Intent makeIntent(Context context) {
		return new Intent(context, PatientLogIn.class);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		final Button loginButton = (Button) findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	login();
            }
        });
        
        final Button retrieveButton = (Button) findViewById(R.id.btnRetrieve);
        retrieveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	retrieve();
            }
        });
	}
	
	/**
	 * save the patient id to the device's internal storage
	 * can be accessed by any class
	 * private to this application
	 */
	
	private void login() {		
		
		String patientId = ((EditText) findViewById(R.id.patientIdField)).getText().toString();
		Context context = getApplicationContext();		
		int duration = Toast.LENGTH_LONG;
		Toast toast = null;
		
		
		if(patientId != null && !patientId.isEmpty()) {
			try {
				
				login = new LogIn();				
				login.LogInToApp(context, patientId);
				
				CharSequence text = "Login Successful";
				toast = Toast.makeText(context, text, duration);
			}
			catch(Exception e) {
				e.printStackTrace();
			}		
		} else {
			//warning message, missing patient id
			CharSequence text = "Patient ID field cannot be empty";
			toast = Toast.makeText(context, text, duration);
		}
		
		toast.show();
	}
	
	//TODO - add this to smartwatch-common_lib
	private void retrieve() {
		
		LogIn login = new LogIn();
		String id = login.GetId(this.getApplicationContext());
		
		((EditText) findViewById(R.id.patientIdField)).setText(id);		
	}
}
