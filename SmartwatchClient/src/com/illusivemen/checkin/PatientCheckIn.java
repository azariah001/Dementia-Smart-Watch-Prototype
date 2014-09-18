package com.illusivemen.checkin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;

// Listens for and displays the Check-in prompt on the client device.
// Settings provided by the Admin device


public class PatientCheckIn extends Activity {
	
	String phoneNumber = "0411794760";
	String message;
	SmsManager manager = SmsManager.getDefault();
	
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	            //Yes button clicked
	        	message = "Check-in: Patient OK";
	    		manager.sendTextMessage(phoneNumber, null, message, null, null);
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	            //No button clicked
	        	message = "Check-in: Patient has responded NOT OK";
	    		manager.sendTextMessage(phoneNumber, null, message, null, null);
	            break;
	        }
	    }
	};
	
	/**
	 * Factory method for creating a launch intent.
	 * @param context
	 * @param payload extra string input
	 * @return
	 */
	public static Intent makeIntent(Context context) {
		return new Intent(context, PatientCheckIn.class);
	}
	
	/**
	 * Comment.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Check-in Request sent by Carer: Are you OK?").setPositiveButton("Yes", dialogClickListener)
	    .setNegativeButton("No", dialogClickListener);
		AlertDialog dlg = builder.create();
		dlg.show();
		timerDelayRemoveDialog(20000, dlg);
	}
	
	public void timerDelayRemoveDialog(long time, final AlertDialog d){
	    new Handler().postDelayed(new Runnable() {
	        public void run() {
	        	message = "Check-in timeout: Patient did not respond";
	    		manager.sendTextMessage(phoneNumber, null, message, null, null);
	            d.dismiss();         
	        }
	    }, time); 
	}
}
