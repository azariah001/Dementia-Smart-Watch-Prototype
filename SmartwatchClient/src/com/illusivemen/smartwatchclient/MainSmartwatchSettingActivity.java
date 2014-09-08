package com.illusivemen.smartwatchclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainSmartwatchSettingActivity extends Activity {
	
	private static final int RESULT_SETTINGS = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		showUserSettings();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode,  resultCode, data);
		
		switch (requestCode) {
		case RESULT_SETTINGS:
			showUserSettings();
			break;
		}
	}
	
	private void showUserSettings() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("\n Username: "
				+ sharedPrefs.getString("prefUsername",  "NULL"));
		
		builder.append("\n Send report:"
				+ sharedPrefs.getBoolean("prefSendReport", false));
		
		builder.append("\n Sync Frequency: "
				+ sharedPrefs.getString("prefSyncFrequency", "NULL"));
	}
	
}