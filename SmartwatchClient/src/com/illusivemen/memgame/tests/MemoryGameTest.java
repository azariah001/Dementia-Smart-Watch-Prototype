package com.illusivemen.memgame.tests;

import java.util.ArrayList;

import com.illusivemen.memgame.MemoryGame;
import com.illusivemen.memgame.PlayingCard;
import com.illusivemen.smartwatchclient.R;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

public class MemoryGameTest extends ActivityInstrumentationTestCase2<MemoryGame> {
	
	private Solo solo;
	private final int CARDS_SUM = 16;
	
	public MemoryGameTest() {
		super(MemoryGame.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
   		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	public void tearDown() {
		solo.finishOpenedActivities();
	}
	
	// ---------- BEGIN TEST CASES ----------
	
	/**
	 * Make sure the title is set.
	 */
	@SmallTest
	public void testTitle() {
		assertEquals(getActivity().getTitle(), solo.getString(R.string.title_activity_memory_game));
	}
	
	/**
	 * Solve the game.
	 */
	@SmallTest
	public void testSolve() {
		ArrayList<Integer> faceValues = new ArrayList<Integer>(CARDS_SUM);
		
		for (int card = 0; card < CARDS_SUM; card++) {
			// cheat by finding card face without turning card over
			int face = ((PlayingCard) getActivity().findViewById(solo.getImage(card).getId())).getCard();
			faceValues.add(face);
			
			// if this is the second occurrence of this card, select the matching pair
			int firstOccurance = faceValues.indexOf(face);
			if (firstOccurance != card) {
				solo.clickOnImage(firstOccurance);
				solo.clickOnImage(card);
			}
		}
	}

}
