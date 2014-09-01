package com.illusivemen.smartwatchclient;

import com.illusivemen.mapping.GoogleMapping;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainMenu extends Activity {

	public final static String ACTIVITY_MESSAGE = "ClientStart";
	
	
	//For SOS Beacon.//0 = not panicked.//1 = very panicked
	private int panic = 0;
	
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
	
	public void showLocation(View view) {
		startActivity(GoogleMapping.makeIntent(MainMenu.this, ACTIVITY_MESSAGE));
	}
	
	public void showProfile(View view) {
		startActivity(PatientProfile.makeIntent(MainMenu.this, ACTIVITY_MESSAGE));
	}
	
	//Panic notification dialogbox, creates on click.
	public void panicSOS (View view) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("SOS Beacon");
	    builder.setMessage("Are you sure?");
	    
	    builder.setPositiveButton("ON", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {            
	        								
	        								//Sets to panic mode
	        								panic = 1;
	        								dialog.dismiss(); } } );

	    builder.setNegativeButton("OFF", new DialogInterface.OnClickListener() {

	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	
	        								//Sets to chill mode
											panic = 0;
											dialog.dismiss(); } } );

	    AlertDialog alert = builder.create();
	    alert.show();

	}
	
	//Panic getter
	public int getPanic() {
		
		return this.panic;
	}
	
	//Panic sender
	private void sendPanicServer(int panic) {
		
		panic = getPanic();
		
		//TODO update server db with current panic state;
		
	}	
}
