package com.illusivemen.smartwatchclient;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SmartwatchSettingActivity extends PreferenceActivity{
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
	}
	
}
