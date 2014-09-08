package com.illusivemen.setting;

import com.illusivemen.smartwatchclient.R;
import com.illusivemen.smartwatchclient.R.xml;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Settings extends PreferenceFragment {
	
	// Loads the Setting view from the XML defined settings
	// using PreferenceFragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
