package com.illusivemen.memgame.tests;

import java.util.ArrayList;
import java.util.Random;

import com.illusivemen.memgame.CardTable;
import com.illusivemen.memgame.PlayingCard;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

public class CardTableTest extends AndroidTestCase {
	
	private CardTable cardTable;
	private int cardsTotal = 16;
	// same or different card selection
	private final boolean SAME_CARDS = true;
	private final boolean DIFFERENT_CARDS = false;
	private final int PAIR = 2;
	// selection options
	private final boolean TURN_ONCE = false;
	private final boolean TURN_TWICE = true;
	// scoring rules
	private final int START_SCORE = 1000;
	private final int BAD_MATCH_PENALTY = 100;
	private final int CHECK_PENALTY = 40;
	
	// ---------- PRE/POST TEST ----------
	
	@Override
	protected void setUp() {
		cardTable = new CardTable(getContext());
	}
	
	@Override
	protected void tearDown() {
		
	}
	
	// ---------- BEGIN HELPER METHODS ----------
	
	/**
	 * Helper method to select any first card which is still in play.
	 */
	private void selectCard() {
		for (PlayingCard card : cardTable.getCards()) {
			if (!card.isLocked() && !card.getVisible()) {
				card.setVisible(true);
				return;
			}
		}
	}
	
	/**
	 * Helper method to select a random card.
	 */
	private void selectRandomCard(boolean turnTwice) {
		PlayingCard randomCard;
		
		// use random to select a card until the selected card is available to be flipped
		do {
			randomCard = cardTable.getCards().get(new Random().nextInt(cardsTotal - 1));
		} while (randomCard.getVisible() && !randomCard.isLocked());
		
		// flip the available card
		randomCard.setVisible(true);
		
		if (turnTwice) {
			randomCard.setVisible(false);
		}
	}
	
	/**
	 * Helper method for double card selection.
	 * @param equalCards whether to select equal or different card types
	 */
	private void selectTwoCards(boolean equalCards) {
		// card type for comparison
		int firstType = 0;
		
		for (int card = 0; card < cardsTotal; card++) {
			// what card are we looking at?
			PlayingCard playingCard = cardTable.getCards().get(card);
			int cardFace = playingCard.getCard();
			
			if (card == 0) {
				// select the first card for comparrison
				firstType = cardFace;
				playingCard.setVisible(true);
			} else {
				// find if card matches first card
				if ((cardFace == firstType) && equalCards) {
					playingCard.setVisible(true);
					break;
				} else if ((cardFace != firstType) && !equalCards)  {
					playingCard.setVisible(true);
					break;
				}
			}
		}
	}
	
	// ---------- END HELPER METHODS ----------
	// ---------- BEGIN TEST CASES ----------
	
	/**
	 * Make sure there are the expected number of cards.
	 */
	@SmallTest
	@SuppressWarnings("unused")
	public void testCreatedCards() {
		int cards = 0;
		
		for (PlayingCard card : cardTable.getCards()) {
			cards++;
		}
		
		assertEquals(cardsTotal, cards);
	}
	
	/**
	 * Make sure cards are shuffled.
	 */
	@MediumTest
	public void testIsShuffled() {
		// two tables of cards should have cards in a different order
		ArrayList<PlayingCard> playingCards1 = new CardTable(getContext()).getCards();
		ArrayList<PlayingCard> playingCards2 = new CardTable(getContext()).getCards();
		
		int cardsInSamePosition = 0;
		for (int card = 0; card < cardsTotal; card++) {
			if (playingCards1.get(card).getCard() == playingCards2.get(card).getCard()) {
				cardsInSamePosition++;
			}
		}
		
		assertTrue(cardsInSamePosition < cardsTotal);
	}
	
	/**
	 * Test that card table detects if there are 2 cards selected.
	 */
	@MediumTest
	public void testPairSelected_sameCards() {
		selectTwoCards(SAME_CARDS);
		
		assertTrue(cardTable.secondSelection());
	}
	
	/**
	 * Test that card table detect that a pair is selected,
	 * even if different cards selected.
	 */
	@MediumTest
	public void testPairSelected_diffCards() {
		selectTwoCards(DIFFERENT_CARDS);
		
		assertTrue(cardTable.secondSelection());
	}
	
	/**
	 * Test that the card table doesn't detect pair selected if 1 card selected.
	 */
	@MediumTest
	public void testPairNotSelected() {
		selectRandomCard(TURN_ONCE);
		
		assertFalse(cardTable.secondSelection());
	}
	
	/**
	 * Test processFlippedCards on equal cards returns true.
	 */
	@MediumTest
	public void testProcessFlippedCards_Equal() {
		
		selectTwoCards(SAME_CARDS);
		
		assertTrue(cardTable.processFlippedCards());
	}
	
	/**
	 * Test processFlippedCards on different cards returns false.
	 */
	@MediumTest
	public void testProcessFlippedCards_Different() {
		
		selectTwoCards(DIFFERENT_CARDS);
		
		assertFalse(cardTable.processFlippedCards());
	}
	
	/**
	 * Test that once two cards selected and processed
	 * it is not the second selection anymore.
	 */
	@MediumTest
	public void testSecondSelection_postPair() {
		
		selectTwoCards(SAME_CARDS);
		
		// process cards
		cardTable.processFlippedCards();
		// two cards aren't selected anymore
		assertFalse(cardTable.secondSelection());
	}
	
	/**
	 * Once two cards selected and processed, another is selected.
	 * Still not a selected pair.
	 */
	@MediumTest
	public void testSecondSelection_postPairSingleSelect() {
		
		selectTwoCards(SAME_CARDS);
		
		// process cards
		cardTable.processFlippedCards();
		// select another card
		selectCard();
		assertFalse(cardTable.secondSelection());
	}
	
	/**
	 * Once two cards selected and processed, another two are selected.
	 * Now a selected pair exists.
	 */
	@MediumTest
	public void testSecondSelection_postPairNewPair() {
		
		selectTwoCards(SAME_CARDS);
		
		// process cards
		cardTable.processFlippedCards();
		// select another two
		selectTwoCards(DIFFERENT_CARDS);
		assertTrue(cardTable.secondSelection());
	}
	
	/**
	 * Test that once a bad match is attempted,
	 * there is no longer a pair selected after processing.
	 */
	@MediumTest
	public void testSecondSelection_postBadPair() {
		
		selectTwoCards(DIFFERENT_CARDS);
		
		// process cards
		cardTable.processFlippedCards();
		// the cards are no longer selected
		assertFalse(cardTable.secondSelection());
	}
	
	/**
	 * Test that the game is not finished upon creation.
	 */
	@SmallTest
	public void testFinished_newGame() {
		assertFalse(cardTable.finishedGame());
	}
	
	/**
	 * Test that the game is not finished when one pair remains.
	 */
	@MediumTest
	public void testFinished_pairRemains() {
		for (int pair = 0; pair < (cardsTotal - PAIR) / 2; pair++) {
			selectTwoCards(SAME_CARDS);
			cardTable.processFlippedCards();
		}
		
		assertFalse(cardTable.finishedGame());
	}
	
	/**
	 * Test that the game is not finished when the last card remains.
	 */
	@MediumTest
	public void testFinished_lastSelectRemains() {
		for (int pair = 0; pair < (cardsTotal - PAIR) / 2; pair++) {
			selectTwoCards(SAME_CARDS);
			cardTable.processFlippedCards();
		}
		
		selectCard();
		assertFalse(cardTable.finishedGame());
	}
	
	/**
	 * Test that the game is finished upon all pairs matched up.
	 */
	@MediumTest
	public void testFinished_finished() {
		for (int pair = 0; pair < cardsTotal / 2; pair++) {
			selectTwoCards(SAME_CARDS);
			cardTable.processFlippedCards();
		}
		
		assertTrue(cardTable.finishedGame());
	}
	
	// ---------- BEGIN SCORE TEST CASES ----------
	
	/**
	 * Test the initial score is correct.
	 */
	public void testScore_initial() {
		assertEquals(START_SCORE, cardTable.getScore());
	}
	
	/**
	 * Test the score after a selection.
	 */
	public void testScore_selection() {
		// first selection should not modify the score,
		selectRandomCard(TURN_ONCE);
		
		cardTable.processFlippedCards();
		assertEquals(START_SCORE, cardTable.getScore());
	}
	
	/**
	 * Test the score after a peek (card is selected and then hidden again).
	 */
	public void testScore_peek() {
		// there is a penalty for flipping a card over twice
		selectRandomCard(TURN_TWICE);
		
		cardTable.processFlippedCards();
		assertEquals(START_SCORE - CHECK_PENALTY, cardTable.getScore());
	}
	
	/**
	 * Test score after 1 peeks followed by a good match.
	 */
	public void testScore_peekThenMatch() {
		
		selectRandomCard(TURN_TWICE);
		cardTable.processFlippedCards();
		
		selectTwoCards(SAME_CARDS);
		cardTable.processFlippedCards();
		
		assertEquals(START_SCORE - CHECK_PENALTY, cardTable.getScore());
	}
	
	/**
	 * Test the score after a not matching selection is made.
	 */
	public void testScore_badSelection() {
		// there is a penalty for an unsuccessful match
		selectTwoCards(DIFFERENT_CARDS);
		
		cardTable.processFlippedCards();
		assertEquals(START_SCORE - BAD_MATCH_PENALTY, cardTable.getScore());
	}
	
	/**
	 * Test the score after a matching selection.
	 */
	public void testScore_goodSelection() {
		// there is no penalty for a good match
		selectTwoCards(SAME_CARDS);
		
		cardTable.processFlippedCards();
		assertEquals(START_SCORE, cardTable.getScore());
	}
	
	/**
	 * Test score after 1 good match and 3 bad matches.
	 */
	public void testScore_mixedMatches() {
		selectTwoCards(SAME_CARDS);
		cardTable.processFlippedCards();
		
		selectTwoCards(DIFFERENT_CARDS);
		cardTable.processFlippedCards();
		selectTwoCards(DIFFERENT_CARDS);
		cardTable.processFlippedCards();
		selectTwoCards(DIFFERENT_CARDS);
		cardTable.processFlippedCards();
		
		assertEquals(START_SCORE - 3 * BAD_MATCH_PENALTY, cardTable.getScore());
	}
	
	/**
	 * Test the score after all matching selections (perfect score) is not changed.
	 */
	public void testScore_perfectGame() {
		
		// flip cards that always match
		do {
			selectTwoCards(SAME_CARDS);
			cardTable.processFlippedCards();
		} while (!cardTable.finishedGame());
		
		// no penalties have been given
		assertEquals(START_SCORE, cardTable.getScore());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
