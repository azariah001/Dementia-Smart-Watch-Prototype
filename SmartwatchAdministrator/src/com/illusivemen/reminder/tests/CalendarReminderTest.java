package com.illusivemen.reminder.tests;

import com.illusivemen.reminder.CalendarReminder;
import com.illusivemen.smartwatchadministrator.R;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.view.View;

import com.robotium.solo.Solo;

public class CalendarReminderTest extends ActivityInstrumentationTestCase2<CalendarReminder> {
	
	private Solo solo;
	
	public CalendarReminderTest() {
		super(CalendarReminder.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false);
		
   		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	public void tearDown() {
		solo.finishOpenedActivities();
	}

    public void testDisplayWhiteBox() {
    	// Defining our own values to input
    	String organiserName = "Richard Lai";
    	String reminderTitle = "Test Calendar Reminder";
    	String reminderDescription = "Reminder for testing the Calendar Reminder functionality";
    	String frequency = "420";
    	
    	solo.waitForActivity(CalendarReminder.class);
    	
    	// Access First value (edit-filed) and input Organiser Name 
    	EditText vFirstEditText = (EditText) solo.getView(R.id.organiser_name);
    	solo.clearEditText(vFirstEditText);
    	solo.enterText(vFirstEditText, organiserName);
    	
//    	// Access Second value (edit-filed) and input Reminder Title
//    	EditText vSecondEditText = (EditText) solo.getView(R.id.reminder_title);
//    	solo.clearEditText(vSecondEditText);
//    	solo.enterText(vSecondEditText, reminderTitle);
//    	
//    	EditText vThirdEditText = (EditText) solo.getView(R.id.reminder_description);
//    	solo.clearEditText(vThirdEditText);
//    	solo.enterText(vThirdEditText, reminderDescription);
//    	
//    	EditText vForthEditText = (EditText) solo.getView(R.id.frequency);
//    	solo.clearEditText(vForthEditText);
//    	solo.enterText(vForthEditText, frequency);
    	
    	assertEquals(organiserName, CalendarReminder.getOrganiser());
    }
}
