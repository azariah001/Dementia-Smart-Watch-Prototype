package com.illusivemen.smartwatchclient;

import java.util.Timer;
import java.util.TimerTask;

import com.illusivemen.mapping.GoogleMapping;
//import com.illusivemen.mapping.GoogleMapping.SaveTask;
import com.illusivemen.memgame.MemoryGame;
import com.illusivemen.reminder.CalendarReminder;
import com.illusivemen.service.BackgroundServices;
import com.illusivemen.setting.ShowSettings;
import com.illusivemen.panic.UtilityClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainMenu extends Activity {

	public final static String ACTIVITY_MESSAGE = "ClientStart";
	private static final long CHECK_BATTERY_STATE = 250;
	
	public UtilityClass utility;
	
	private boolean lowBatteryAlert = false;
	private float batteryPct;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		// start background service
		this.startService(new Intent(this, BackgroundServices.class));
		
		new Timer().scheduleAtFixedRate(lowBattery, 0, CHECK_BATTERY_STATE);
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
	
	public void startMemoryGame(View view) {
		startActivity(new Intent(this, MemoryGame.class));
	}
	
	public void showProfile(View view) {
		startActivity(PatientProfile.makeIntent(MainMenu.this));
	}
	
	public void panicSOS(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("SOS Beacon");
	    builder.setMessage("Are you sure?");
	    
	    builder.setPositiveButton("ON", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {            
	        								
	        								//Sets to panic mode
	        								new UtilityClass().sendRequest();
	        								dialog.dismiss(); } } );

	    builder.setNegativeButton("OFF", new DialogInterface.OnClickListener() {

	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	
	        								//Sets to chill mode

											dialog.dismiss(); } } );

	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	// for testing purposes
	public void showBattery(View view) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		String alertText;
		if (lowBatteryAlert == true) {
			alertText = "Low Battery Alert";
		} else {
			alertText = "Battery is " + batteryPct + "% charged";
		}
		Toast toast = Toast.makeText(context, alertText, duration);
		toast.show();
	}
	
	TimerTask lowBattery = new TimerTask() {
		
		public void run() {
		
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent battery = registerReceiver(null, ifilter);
		
			int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

			float percent = level / (float)scale * 100;
			batteryPct = percent; 
			
			if (percent < 0.10) {
				lowBatteryAlert = true;
			}			
		}
	};
	
	// Shows the Settings menu
	public void showSettings(View view) {
		Intent showSettings = new Intent(this, ShowSettings.class);
		startActivity(showSettings);
	}
	// opens reminder view
	public void setReminder(View view) {
		Intent setReminder = new Intent(this, CalendarReminder.class);
		startActivity(setReminder);
	}
}
