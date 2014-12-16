package com.illusivemen.reminder;

import java.util.Calendar;

import com.illusivemen.db.DBConn;
import com.illusivemen.smartwatchadministrator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class CalendarReminder extends Activity {
	
	// putExtra final strings
	private final String TITLE = "title";
	private final String ORGANISER = "organizer";
	private final String DESCRIPTION = "description";
	private final String BEGINTIME = "beginTime";
	private final String FREQUENCY = "rrule";
	private final String TYPE = "vnd.android.cursor.item/event";
	private final String RRULE = "FREQ=DAILY";
	private final String PUT_EVENT = "/putPatientReminder.php";
	
	// Calendar variables 
	private String title;
	private static String organiser;
	private String description;
	private long longBeginTime;
	private String beginTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_reminder);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.calendar_reminder, menu);
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	public static Intent makeIntent(Context context, String payload) {
        return new Intent(context, CalendarReminder.class);
    }
	
	public void setOrganiser() {
		// Gets the editable text
		EditText organiser_email = (EditText) findViewById(R.id.organiser_name);
		// Sets the organiser variable to the editable text
		organiser = organiser_email.getEditableText().toString();
	}
	
	public static String getOrganiser() {
		return organiser;
	}
	
	public void setReminderTitle() {
		// Gets the editable text
		EditText reminder_title = (EditText) findViewById(R.id.reminder_title);
		// Sets the title variable to the editable text
		this.title = reminder_title.getEditableText().toString();
	}
	
	public String getReminderTitle() {
		return this.title;
	}
	
	public void setReminderDescription() {
		// Gets the editable text
		EditText reminder_description = (EditText) findViewById(R.id.reminder_description);
		// Sets the editable text
		this.description = reminder_description.getEditableText().toString();
	}
	
	public String getReminderDescription() {
		return this.description;
	}
	
	public void setBeginTime() {
		// Creates a Calendar
		Calendar beginTime = Calendar.getInstance();
		// Gets the Time and Date picker
		TimePicker timePicker = (TimePicker) findViewById(R.id.beginning_time);
		DatePicker datePicker = (DatePicker) findViewById(R.id.beginning_date);
		// Sets the minute and hour from the Time Picker
		int minute = timePicker.getCurrentMinute();
		int hour = timePicker.getCurrentHour();
		// Sets the day, month and year from the Date Picker
		int day = datePicker.getDayOfMonth();
		int month = datePicker.getMonth() + 1;
		int year = datePicker.getYear();
		// Sets the calendar using the time and date variables
		beginTime.set(year, month, day, hour, minute);
		// Sets beginTime to the calendar time in Millis
		this.longBeginTime = beginTime.getTimeInMillis();
		this.beginTime = String.valueOf(longBeginTime);
	}
	
	public String getBeginTime() {
		return this.beginTime;
	}
	
	// Sets up connection and sends data from the application to the database
	public void setReminder(View view) {
		new UpdatePatientReminderDB().execute();
	}
	
	private class UpdatePatientReminderDB extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// Sets the current values of the EditTexts
			setOrganiser();
			setReminderTitle();
			setReminderDescription();
			setBeginTime();
			
			// Store Parameters
			String [] parameters = {"title=" + title, "organiser=" + organiser, "description=" + description, "beginTime=" + beginTime, "rrule=" + RRULE};
			
			// Creates a database connection
			DBConn conn;
			// Posts information
			conn = new DBConn(PUT_EVENT);
			conn.execute(parameters);
			
			return conn.getResult();
		}
	}
}
