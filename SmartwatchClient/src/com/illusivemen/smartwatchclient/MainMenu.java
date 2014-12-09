package com.illusivemen.smartwatchclient;

import java.util.Timer;
import java.util.TimerTask;

import com.illusivemen.db.DBConn;
import com.illusivemen.login.LogIn;
import com.illusivemen.login.PatientLogIn;
import com.illusivemen.mapping.GoogleMapping;
import com.illusivemen.memgame.MemoryGame;
import com.illusivemen.reminder.CalendarReminder;
import com.illusivemen.service.LocationPush;
import com.illusivemen.setting.ShowSettings;
import com.illusivemen.panic.UtilityClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//testing

public class MainMenu extends Activity {

	public final static String ACTIVITY_MESSAGE = "ClientStart";
	private static final long CHECK_BATTERY_STATE = 25000;
	
	public UtilityClass utility;
	
	private boolean lowBatteryAlert = false;
	private float batteryPct;
	LogIn login;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		// start background service
		this.startService(new Intent(this, LocationPush.class));		
		new Timer().scheduleAtFixedRate(lowBattery, 0, CHECK_BATTERY_STATE);
		// select the default patient at startup
		login = new LogIn();
		login.LogInToApp(getApplicationContext(), "0");
		
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
	
	public void loginPrompt(View view) {
		startActivity(new Intent(this, PatientLogIn.class));
	}
	
	public void panicSOS(View view) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Emergency Carer Alert");
		builder.setMessage("Set Panic State?");
		
		builder.setPositiveButton("ON", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				//Sets to panic mode
				new UtilityClass().sendRequest();
				// push new location to database
				new PanicTask().execute("1");
				((Button) findViewById(R.id.btnPanic)).setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
				dialog.dismiss();
			}
		});
		
		builder.setNegativeButton("OFF", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				//Sets to chill mode
				new PanicTask().execute("0");
				((Button) findViewById(R.id.btnPanic)).setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
				dialog.dismiss();
			}
		});
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Background thread to set patient panic state.
	 */
	private class PanicTask extends AsyncTask<String, Void, Void> {
		
		private static final String PANIC_SET_SCRIPT = "/updatePatientState.php";
		
		@Override
		protected Void doInBackground(String... params) {
			
			// which patient
			login = new LogIn();
			String patientId = login.GetId(getApplicationContext());
			
			// store parameters
			String[] parameters = {"patient_id=" + patientId,
					"panic_state=" + params[0]};
			
			// post information
			DBConn conn = new DBConn(PANIC_SET_SCRIPT);
			conn.execute(parameters);
			return null;
		}
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
	public void showReminders(View view) {
		Intent showReminders = new Intent(this, CalendarReminder.class);
		startActivity(showReminders);
	}
}
