package com.illusivemen.smartwatchclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainMenu extends Activity {

	public final static String ACTIVITY_MESSAGE = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
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
	
	public void showLocation(View view) {
		Intent map = new Intent(this, GoogleMapping.class);
		map.putExtra(ACTIVITY_MESSAGE, "LIVE_MAP");
		startActivity(map);
	}
	
	public void showProfile(View view) {
		Toast.makeText(this,
				"You are John Smith",
				Toast.LENGTH_SHORT).show();
	}
}
