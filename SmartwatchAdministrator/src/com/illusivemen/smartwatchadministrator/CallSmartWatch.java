package com.illusivemen.smartwatchadministrator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class CallSmartWatch extends Activity {

	public static Intent makeIntent(Context context, String payload) {
        return new Intent(context, CallSmartWatch.class);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		makeCall();
	}
	
	// TODO: retrieve smartwatch number from db based on patient id
	private void makeCall() {
		String url = "tel:0123456789";
		Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
		startActivity(callIntent);
	}
}
