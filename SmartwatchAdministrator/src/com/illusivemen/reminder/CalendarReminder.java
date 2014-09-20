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
	
	// Calendar variables 
	private String type = "vnd.android.cursor.item/event";
	private String organiser;
	private String title;
	private String description;
	private long beginTime;
	private String rrule;
	
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
	
	public void setTitle() {
		// Gets the editable text
		EditText reminder_title = (EditText) findViewById(R.id.reminder_title);
		// Sets the title variable to the editable text
		this.title = reminder_title.getEditableText().toString();
	}
	
	public void setDescription() {
		// Gets the editable text
		EditText reminder_description = (EditText) findViewById(R.id.reminder_description);
		// Sets the editable text
		this.description = reminder_description.getEditableText().toString();
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
		this.beginTime = beginTime.getTimeInMillis();
	}
	
	public void setFrequency() {
		// Gets the editable text
		EditText frequency = (EditText) findViewById(R.id.frequency);
		// Sets the frequency rule
		this.rrule = "FREQ=DAILY;COUNT=" + frequency.getText();
	}
	// Does not work
	public void sendReminder() {
		
	}
	
	public void setReminder() {
		Calendar cal = Calendar.getInstance();
		Intent reminder = new Intent(Intent.ACTION_EDIT);
		reminder.setType(this.type);
		reminder.putExtra("beginTime", this.beginTime);
		reminder.putExtra("allDay",  false);
		reminder.putExtra("rrule",  this.rrule);
		reminder.putExtra("endTime",  cal.getTimeInMillis()+60*60*1000);
		reminder.putExtra("title", this.title);
		reminder.putExtra("organizer", this.organiser);
		reminder.putExtra("description", this.description);
		startActivity(reminder);
	}
	

}
