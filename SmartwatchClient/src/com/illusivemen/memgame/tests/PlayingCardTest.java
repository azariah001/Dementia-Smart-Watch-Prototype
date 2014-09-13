package com.illusivemen.memgame.tests;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.LinearLayout.LayoutParams;

import com.illusivemen.memgame.Cards;
import com.illusivemen.memgame.PlayingCard;

public class PlayingCardTest extends AndroidTestCase {
	
	private PlayingCard card;
	private int cardFace = Cards.ACE_CLUB;
	
	@Override
	protected void setUp() {
		card = new PlayingCard(getContext(), cardFace);
	}
	
	/**
	 * Test that the PlayingCard can be clicked.
	 */
	@SmallTest
	public void testConnectable() {
		assertTrue(card.isClickable());
	}
	
	/**
	 * Make sure layout parameters are set for proper distribution.
	 */
	public void testLayoutParams() {
		LayoutParams params = (LayoutParams) card.getLayoutParams();
		assertEquals(LayoutParams.WRAP_CONTENT, params.width);
		assertEquals(LayoutParams.WRAP_CONTENT, params.height);
		assertEquals(1f, params.weight);
	}
	
	/**
	 * Playing card should not use extra width/height outside its aspect ratio.
	 */
	@SmallTest
	public void testViewBounds() {
		assertTrue(card.getAdjustViewBounds());
	}
	
	/**
	 * For a PlayingCard to be retrieved by ID from a view,
	 * it needs to have an ID set.
	 */
	@SmallTest
	public void testHasID() {
		assertFalse(card.getId() == -1);
	}
	
	/**
	 * Make sure retrieving the face value is correct.
	 */
	@SmallTest
	public void testCorrectCardFace() {
		assertEquals(cardFace, card.getCard());
	}
	
	/**
	 * When a card is created it is face-side down (not visible).
	 */
	@SmallTest
	public void testDefaultVisible() {
		assertFalse(card.getVisible());
	}
	
	/**
	 * Test if we can flip a card to the visible face value state.
	 */
	@SmallTest
	public void testSetVisible() {
		card.setVisible(true);
		assertTrue(card.getVisible());
	}
	
	/**
	 * Test to make sure the card can be set to invisible
	 * after being flipped to the visible state.
	 */
	@SmallTest
	public void testSetVisibleNotVisible() {
		card.setVisible(true);
		card.setVisible(false);
		assertFalse(card.getVisible());
	}
	
	/**
	 * The fade method should also hide a card.
	 */
	@SmallTest
	public void testFadeCard() {
		card.setVisible(true);
		card.slowFade();
		assertFalse(card.getVisible());
	}
	
	/**
	 * Fade should not set a flipped card to visible.
	 */
	@SmallTest
	public void testFadeNotSetsVisible() {
		card.slowFade();
		assertFalse(card.getVisible());
	}
	
	/**
	 * Test initial locked state (unlocked).
	 */
	@SmallTest
	public void testInitialLock() {
		assertFalse(card.isLocked());
	}
	
	/**
	 * Card shouldn't be locked just because it was flipped.
	 */
	@SmallTest
	public void testPostTransitionLock() {
		card.setVisible(true);
		assertFalse(card.isLocked());
	}
	
	/**
	 * Test that the card can be locked.
	 */
	@SmallTest
	public void testLockVisible() {
		card.setVisible(true);
		card.setLocked(true);
		assertTrue(card.isLocked());
	}
	
	/**
	 * Test that locked cards cannot be flipped.
	 */
	@SmallTest
	public void testLockedNotFlipped() {
		card.setVisible(true);
		card.setLocked(true);
		card.setVisible(false);
		assertTrue(card.getVisible());
	}
	
	/**
	 * Test that the fade out method doesn't hide a locked card.
	 */
	@SmallTest
	public void testLockedFadeNotFlipped() {
		card.setVisible(true);
		card.setLocked(true);
		card.slowFade();
		assertTrue(card.getVisible());
	}
}
