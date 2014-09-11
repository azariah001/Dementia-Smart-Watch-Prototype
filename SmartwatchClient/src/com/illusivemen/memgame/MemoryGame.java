package com.illusivemen.memgame;

import com.illusivemen.smartwatchclient.R;
import com.illusivemen.smartwatchclient.R.id;
import com.illusivemen.smartwatchclient.R.layout;
import com.illusivemen.smartwatchclient.R.menu;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;

public class MemoryGame extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memory_game);
		
		((TableRow) findViewById(R.id.tableRow1)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow1)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow1)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow1)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow2)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow2)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow2)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow2)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow3)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow3)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow3)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow3)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow4)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow4)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow4)).addView(new PlayingCard(this));
		((TableRow) findViewById(R.id.tableRow4)).addView(new PlayingCard(this));
	}
	
	public void testClick(View view) {
		System.out.println(view.getId());
	}
}
