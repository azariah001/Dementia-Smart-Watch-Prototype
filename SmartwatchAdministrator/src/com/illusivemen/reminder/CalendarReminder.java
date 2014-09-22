package com.illusivemen.reminder;

import java.util.Calendar;

import com.illusivemen.smartwatchadministrator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class CalendarReminder extends Activity {
	
	// putExtra final strings
	private final String TITLE = "title";
	private final String ORGANISER = "organizer";
	private final String DESCRIPTION = "description";
	private final String BEGINTIME = "beginTime";
	private final String FREQUENCY = "rrule";
	private final String TYPE = "vnd.android.cursor.item/event";
	
	// Calendar variables 
	private String title;
	private String organiser;
	private String description;
	private long longBeginTime;
	private String beginTime;
	private String rrule;
	private String[] calendarVariables;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_reminder);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
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
	
	public static Intent makeIntent(Context context, String payload) {
        return new Intent(context, CalendarReminder.class);
    }
	
	public void setOrganiser() {
		// Gets the editable text
		EditText organiser_email = (EditText) findViewById(R.id.organiser_email);
		// Sets the organiser variable to the editable text
		this.organiser = organiser_email.getEditableText().toString();
	}
	
	public String getOrganiser() {
		return this.organiser;
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
	
	public void setbeginTime() {
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
	
	public void setFrequency() {
		// Gets the editable text
		EditText frequency = (EditText) findViewById(R.id.frequency);
		// Sets the frequency rule
		this.rrule = "FREQ=DAILY;COUNT=" + frequency.getText();
	}
	
	public String getFrequency() {
		return this.rrule;
	}
	// Does not work
	public void sendReminder() {
		// Sets the parameters 
		calendarVariables = new String[]{title, organiser, beginTime, description, rrule};
	}
	
	public void setReminder() {
		Calendar cal = Calendar.getInstance();
		Intent reminder = new Intent(Intent.ACTION_EDIT);
		reminder.setType(TYPE);
		reminder.putExtra(TITLE, getReminderTitle());
		reminder.putExtra(ORGANISER, getOrganiser());
		reminder.putExtra(DESCRIPTION, getReminderDescription());
		reminder.putExtra(BEGINTIME, getBeginTime());
		reminder.putExtra(FREQUENCY, getFrequency());
		startActivity(reminder);
	}
	

}
