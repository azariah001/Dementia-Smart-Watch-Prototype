package com.illusivemen.memgame;

import com.illusivemen.smartwatchclient.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;

public class PlayingCard extends ImageView {
	
	// whether the card is flipped so that you can see the value
	private boolean visible = false;
	// whether the card is allowed to flip, shouldn't flip if matched
	private boolean locked = false;
	// the two faces of the card
	private Drawable back;
	private Drawable front;
	private int card;

	public PlayingCard(Context context, int cardFace) {
		super(context);
		
		// clicks will cause logic to run
		this.setClickable(true);
		
		// set weight to 1 for equal distribution
		this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		
		// set card image (show back initially)
		back = getResources().getDrawable(R.drawable.card_back);
		front = getResources().getDrawable(cardFace);
		this.card = cardFace;
		this.setImageDrawable(back);
		
		// image of card should fit within whatever size the image view is sized to
		this.setAdjustViewBounds(true);
		
		// set a unique id
		this.setId(generateViewId());
	}
	
	/**
	 * Gets the drawable resource for the side not currently displayed.
	 * @return drawable not active
	 */
	private Drawable otherFace() {
		if (visible) {
			return back;
		} else {
			return front;
		}
	}
	
	private Drawable face() {
		if (visible) {
			return front;
		} else {
			return back;
		}
	}
	
	public void slowFade() {
		if (this.locked) {
			return;
		}
		
		TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{front, back});
		this.setImageDrawable(transitionDrawable);
		transitionDrawable.startTransition(2000);
		this.visible = false;
	}
	
	// GETTERS AND SETTERS BELOW THIS POINT
	
	public void setVisible(boolean visible) {
		if (this.locked) {
			return;
		}
		
		// change shown side if necessary
		if (this.visible != visible) {
			TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{face(), otherFace()});
			this.setImageDrawable(transitionDrawable);
			transitionDrawable.startTransition(200);
		}
		
		// set new side
		this.visible = visible;
	}
	
	public boolean getVisible() {
		return this.visible;
	}
	
	public int getCard() {
		return this.card;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public boolean isLocked() {
		return this.locked;
	}

}
