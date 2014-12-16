package com.illusivemen.reminder;

import java.util.Calendar;

import com.illusivemen.db.DBConn;
import com.illusivemen.smartwatchclient.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CalendarReminder extends Activity {

	private String title;
	private String organiser;
	private String beginTime;
	private long longBeginTime;
	private String endTime;
	private long longEndTime;
	private String description;
	private String rrule;
	
	private String[] reminder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_reminder);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calendar_reminder, menu);
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
	
	public void handleSendText(Intent intent) {
		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (sharedText != null) {
			//createReminder();
		}
	}
	
	// Creates a reminder
	public void setReminder(View view) {
		Calendar cal = Calendar.getInstance();
		Intent reminder = new Intent(Intent.ACTION_EDIT);
		reminder.setType("vnd.android.cursor.item/event");
		reminder.putExtra("title", title);
		reminder.putExtra("beginTime", longBeginTime);
		reminder.putExtra("endTime",  longBeginTime+60*60*1000);
		reminder.putExtra("allDay",  false);
		reminder.putExtra("description", description);
		reminder.putExtra("rrule", rrule);
		startActivity(reminder);
	}
	
	public void getReminder(View view) {
		new RetrievePatientReminder().execute();
	}
	
	private void setInfo() {
		title = reminder[0];
		organiser = reminder[1];
		beginTime = reminder[2];
		description = reminder[3];
		rrule = reminder[4];
		longBeginTime = Long.valueOf(beginTime);
	}
	/**
	 * Background process which retrieves the Patient Reminder information.
	 */
	private class RetrievePatientReminder extends AsyncTask<Void, Void, String> {
		
		DBConn conn;
		
		@Override
		protected String doInBackground(Void... params) {
			conn = new DBConn("/getPatientReminder.php");
			conn.execute();
			return conn.getResult();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			reminder = new String[5];
			
			reminder = result.split(",");
			setInfo();
		}
	}
	
}
