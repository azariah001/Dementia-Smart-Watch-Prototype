package com.illusivemen.memgame.tests;

import java.util.ArrayList;

import com.illusivemen.memgame.MemoryGame;
import com.illusivemen.memgame.PlayingCard;
import com.illusivemen.smartwatchclient.R;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;

public class MemoryGameTest extends ActivityInstrumentationTestCase2<MemoryGame> {
	
	private Solo solo;
	private final int CARDS_SUM = 16;
	private final int START_SCORE = 1000;
	private final int PEEK_PENALTY = 20;
	private final int BAD_MATCH_PENALTY = 50;
	
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
	
	/**
	 * Solve the game.
	 */
	private void solve() {
		ArrayList<Integer> faceValues = new ArrayList<Integer>(CARDS_SUM);
		
		for (int card = 0; card < CARDS_SUM; card++) {
			PlayingCard thisCard = ((PlayingCard) getActivity().findViewById(solo.getImage(card).getId()));
			
			// cheat by finding card face without turning card over
			int face = thisCard.getCard();
			faceValues.add(face);
			
			// can't select all cards
			if (thisCard.isLocked() || thisCard.getVisible()) {
				continue;
			}
			
			// if this is the second occurrence of this card, select the matching pair
			int firstOccurance = faceValues.indexOf(face);
			if (firstOccurance != card) {
				solo.clickOnImage(firstOccurance);
				solo.clickOnImage(card);
			}
		}
	}
	
	/**
	 * Peek for a card's value (show and hide a card).
	 */
	private void peek() {
		for (int card = 0; card < CARDS_SUM; card++) {
			// cheat by finding card face without turning card over
			PlayingCard thisCard = ((PlayingCard) getActivity().findViewById(solo.getImage(card).getId()));
			if (!thisCard.isLocked() && ! thisCard.getVisible()) {
				solo.clickOnImage(card);
				solo.clickOnImage(card);
				return;
			}
		}
	}
	
	/**
	 * Attempt a bad match.
	 */
	private void badMatch() {
		ArrayList<Integer> faceValues = new ArrayList<Integer>();
		Integer firstSelectableCardPos = null;
		
		for (int card = 0; card < CARDS_SUM; card++) {
			PlayingCard thisCard = ((PlayingCard) getActivity().findViewById(solo.getImage(card).getId()));
			
			// cheat by finding card face without turning card over
			int face = thisCard.getCard();
			faceValues.add(face);
			
			// can't select all cards
			if (thisCard.isLocked() || thisCard.getVisible()) {
				continue;
			} else if (firstSelectableCardPos == null) {
				firstSelectableCardPos = card;
			}
			
			// if this is a different card, select the bad pair
			if (faceValues.get(firstSelectableCardPos) != face) {
				solo.clickOnImage(firstSelectableCardPos);
				solo.clickOnImage(card);
				return;
			}
		}
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
	 * Make sure the score is displayed.
	 */
	@SmallTest
	public void testScore_displayed() {
		assertEquals((Integer) START_SCORE, Integer.valueOf(solo.getText(1).getText().toString()));
	}
	
	/**
	 * Make sure score penalty for peeking is deducted.
	 */
	@SmallTest
	public void testPeekPenalty() {
		peek();
		assertEquals((Integer) (START_SCORE - PEEK_PENALTY), Integer.valueOf(solo.getText(1).getText().toString()));
	}
	
	/**
	 * Make sure incorrect match penalty is deducted.
	 */
	@SmallTest
	public void testBadMatchPenalty() {
		badMatch();
		assertEquals((Integer) (START_SCORE - BAD_MATCH_PENALTY), Integer.valueOf(solo.getText(1).getText().toString()));
	}
	
	/**
	 * Make sure no penalties have been given after a perfect game.
	 */
	@LargeTest
	public void testScore_perfectEnding() {
		solve();
		assertEquals((Integer) START_SCORE, Integer.valueOf(solo.getText(1).getText().toString()));
	}

}
