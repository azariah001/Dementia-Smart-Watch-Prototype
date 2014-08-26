package com.illusivemen.smartwatchadministrator;

import com.illusivemen.maps.AdminGoogleMapping;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainMenu extends Activity {

	public final static String TRACK_MESSAGE = "TrackPatients";
	
	private boolean patientPanic = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void showLocations(View view) {
		startActivity(AdminGoogleMapping.makeIntent(MainMenu.this, TRACK_MESSAGE));
		finish();
	}
	
	
	//Dummy method, emulates the 
	public void changePanic(View view) {
		
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	        patientPanic = true;
	        
	        //Open dialogBox, option to see patients current location

	    } else {
	        patientPanic = false;
	    }
		
	}
}
	
	
	

