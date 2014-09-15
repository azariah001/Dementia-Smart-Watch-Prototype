package com.illusivemen.reminder;

import java.util.Calendar;

import com.illusivemen.smartwatchclient.R;
import com.illusivemen.smartwatchclient.R.id;
import com.illusivemen.smartwatchclient.R.layout;
import com.illusivemen.smartwatchclient.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CalendarReminder extends Activity {

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
		reminder.putExtra("beginTime", cal.getTimeInMillis());
		reminder.putExtra("allDay",  false);
		reminder.putExtra("rrule",  "FREQ=DAILY");
		reminder.putExtra("endTime",  cal.getTimeInMillis()+60*60*1000);
		reminder.putExtra("title", "a test");
		startActivity(reminder);
	}
}
