package com.illusivemen.checkin;

import com.illusivemen.smartwatchclient.PatientProfile;
import com.illusivemen.smartwatchclient.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

// Listens for and displays the Check-in prompt on the client device.
// Settings a provided by the Admin device


public class PatientCheckIn extends Activity {	
	
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	            //Yes button clicked
	        	//Send confirmation back to Carer
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	            //No button clicked
	        	//Send SOS Alert back to Carer
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
	    .setNegativeButton("No", dialogClickListener).show();
	}
}
