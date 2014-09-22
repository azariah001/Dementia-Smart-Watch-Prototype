package com.illusivemen.panic;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

// WIP
public class UtilityClass {
	
	String phoneNumber = "0468625740"; // TODO: should not be static
	String message = "YOU ARE FUCKING PANICKING DAWG";
	
	/**
	 * Factory method for creating a launch intent.
	 * @param context
	 * @param payload extra string input
	 * @return
	 */
	public static Intent makeIntent(Context context, String payload) {
		return new Intent(context, UtilityClass.class);
	}
	
	// sends a checkin request the client instantly via SMS
	public void sendRequest() {		
		SmsManager manager = SmsManager.getDefault();
		manager.sendTextMessage(phoneNumber, null, message, null, null);		
	}
	


}