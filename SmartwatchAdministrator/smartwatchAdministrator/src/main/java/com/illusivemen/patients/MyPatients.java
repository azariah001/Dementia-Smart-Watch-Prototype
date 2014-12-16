package com.illusivemen.patients;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import com.illusivemen.db.DBConn;
import com.illusivemen.login.LogIn;
import com.illusivemen.smartwatchadministrator.MainMenu;
import com.illusivemen.smartwatchadministrator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MyPatients extends Activity {
	
	public final static String PROFILE_UPDATE = "ProfileUpdate";
	private LogIn login;
	private String id;
	String[] patients;
	String patientList;
	String FILENAME;
	String patientId;
	ListView listView ;
	RetrievePatients retrievePatients;
	CurrentPatient currentPatient;
	
	/**
	 * Factory method for creating a launch intent.
	 * @param context
	 * @param payload extra string input
	 * @return
	 */
	public static Intent makeIntent(Context context, String payload) {
		return new Intent(context, MyPatients.class);
	}
	
	/**
	 * The activity starts with a connection to the database.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_patients);
		
		login = new LogIn();
		id = login.GetId(this.getApplicationContext());
		
		final Button updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	updateSelectedProfile(v);
            }
        });        
        
        // retrieve patients
        retrievePatients = new RetrievePatients();
     	retrievePatients.execute();
     	try {
			patientList = retrievePatients.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     	
     	formatPatientList(patientList);
     	
     	listView = (ListView) findViewById(R.id.selectPatientListView);
     	
     // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, android.R.id.text1, patients);
        
     // Assign adapter to ListView
        listView.setAdapter(adapter);
        
        // ListView Item Click Listener
        listView.setOnItemClickListener(new OnItemClickListener() {

              @Override
              public void onItemClick(AdapterView<?> parent, View view,
                 int position, long id) {
            	  
            	  view.setSelected(true);
                
               // ListView Clicked item index
               int itemPosition     = position;
               
               // ListView Clicked item value
               String  itemValue    = (String) listView.getItemAtPosition(itemPosition);
               
               // point to the selected patient
               patientId = itemValue.substring(0,1);
               currentPatient.selectPatient(getApplicationContext(), patientId);
                
                String p = currentPatient.GetId(getApplicationContext());
                Toast.makeText(getApplicationContext(),
                        p, Toast.LENGTH_LONG)
                        .show();
             
              }

         });
        
      currentPatient = new CurrentPatient();
        
      //Check for a previously selected patient and highlight the relevant patient if set
      //Doesn't work        
        //String id = currentPatient.GetId(getApplicationContext());
        //if (id != null && !id.isEmpty()) {        	
        	//String[] listPosition = id.split(",");
        	//int position = Integer.parseInt(listPosition[1]);
        	//listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        	//listView.setSelector(android.R.color.darker_gray);
        	//listView.setItemChecked(position, true);
		//}
	}
	
	/**
	 * Update the selected profile
	 */
	private void updateSelectedProfile(View view) {
		startActivity(UpdatePatientProfile.makeIntent(MyPatients.this, PROFILE_UPDATE));
		finish();
	}
	
	/** 
	 * Display the profiles in a list view
	 * @param profile
	 */
	private void formatPatientList(String patientList) {
		
		patients = patientList.split(";");
		
		for(int i = 0; i < patients.length; i++) {
			patients[i] = patients[i].replace(",", " - ");
		}
	}
	
	/**
	 * Background process which retrieves the profile information.
	 */
	private class RetrievePatients extends AsyncTask<Void, Void, String> {
		
		private DBConn conn;
		
		@Override
		protected String doInBackground(Void... params) {
			
			// use a default if not logged in
			if (id.isEmpty()) {
				id = "1";
			}
			
			// prepare parameters for query
			String[] parameters = {"carer_id=" + id};
						
			conn = new DBConn("/retrieveMyPatients.php");
			conn.execute(parameters);
			return conn.getResult();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (result != null && !result.isEmpty()) {
				
				Context context = getApplicationContext();
				CharSequence text = "Displaying Patients";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();				
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
