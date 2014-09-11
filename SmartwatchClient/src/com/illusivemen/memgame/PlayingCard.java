package com.illusivemen.memgame;

import com.illusivemen.smartwatchclient.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TableRow.LayoutParams;
import android.widget.ImageView;

public class PlayingCard extends ImageView {

	public PlayingCard(Context context) {
		super(context);
		
		// connect click action to method
		this.setClickable(true);
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				System.out.println(view.getId());
				
			}
		});
		
		// set weight to 1 for equal distribution
		this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		
		// set default card image (a reversed card)
		Drawable image = getResources().getDrawable(R.drawable.card_back);
		this.setImageDrawable(image);
		
		// image of card should fit within whatever size the image view is sized to
		this.setScaleType(ScaleType.CENTER_INSIDE);
	}

}
