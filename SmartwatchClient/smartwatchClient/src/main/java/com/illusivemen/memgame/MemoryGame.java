package com.illusivemen.memgame;

import java.util.ArrayList;

import com.illusivemen.smartwatchclient.R;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MemoryGame extends Activity implements OnClickListener, OnCompletionListener {
	
	private MediaPlayer mp = null;
	private CardTable cardTable = null;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memory_game);
		
		cardTable = new CardTable(this);
		ArrayList<PlayingCard> cards = cardTable.getCards();
		for (int card = 0; card < cards.size(); card++) {
			cards.get(card).setOnClickListener(this);
			
			if (card < 4) {
				((LinearLayout) findViewById(R.id.card_row1)).addView(cards.get(card));
			} else if (card < 8) {
				((LinearLayout) findViewById(R.id.card_row2)).addView(cards.get(card));
			} else if (card < 12) {
				((LinearLayout) findViewById(R.id.card_row3)).addView(cards.get(card));
			} else {
				((LinearLayout) findViewById(R.id.card_row4)).addView(cards.get(card));
			}
		}
		
		// initial score
		TextView scoreDisplay = (TextView) findViewById(R.id.valueScore);
		scoreDisplay.setText(String.valueOf(cardTable.getScore()));
		
		// feedback sounds
		mp = new MediaPlayer();
	}
	
	/**
	 * On screen rotations and other actions, the activity may be reset.
	 * This saves important values to re-load upon re-creating the activity.
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		
		// TODO: implement parcelable on cardtable/playingcard
		// http://stackoverflow.com/questions/12503836/how-to-save-custom-arraylist-on-android-screen-rotate
	}
	
	/**
	 * Process card selection logic.
	 */
	@Override
	public void onClick(View view) {
		PlayingCard card = (PlayingCard) findViewById(view.getId());
		// flip card
		card.setVisible(!card.getVisible());
		if (card.isLocked()) {
			// play a bad selection sound
		} else if (card.getVisible()) {
			playSound(R.raw.card_pickup);
		} else {
			playSound(R.raw.card_down);
		}
		
		// process table
		if (cardTable.processFlippedCards()) {
			// successful match
			if (cardTable.finishedGame()) {
				System.out.println("congratulations! you have completed the mini-game!");
			}
		}
		
		// show score
		TextView scoreDisplay = (TextView) findViewById(R.id.valueScore);
		scoreDisplay.setText(String.valueOf(cardTable.getScore()));
	}
	
	private void playSound(int sound) {
		if (!mp.isPlaying()) {
			mp = MediaPlayer.create(getApplicationContext(), sound);
			mp.setOnCompletionListener(this);
			mp.start();
		} else {
			mp.stop();
			mp.reset();
			playSound(sound);
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer arg0) {
		mp.reset();
	}
}
