package com.illusivemen.memgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.content.Context;

public class CardTable {
	
	// possible cards to use
	ArrayList<Integer> cardDeck = new ArrayList<Integer>(16);
	// cards on table
	ArrayList<PlayingCard> playingCards = new ArrayList<PlayingCard>(16);
	
	public CardTable(Context context) {
		super();
		
		// prepare deck
		for (int card : Cards.ALL_CARDS) {
			cardDeck.add(card);
			cardDeck.add(card);
		}
		shuffleDeck();
		selectCards(context);
	}
	
	/**
	 * Shuffle the card deck.
	 * The card Deck instructs which cards to create in what order.
	 */
	private void shuffleDeck() {
		Collections.shuffle(cardDeck, new Random());
	}
	
	/**
	 * Create cards by selecting them from the deck.
	 * @param context activity for which the cards will need to be displayed
	 */
	private void selectCards(Context context) {
		playingCards.clear();
		for (Integer cardType : cardDeck) {
			playingCards.add(new PlayingCard(context, cardType));
		}
	}
	
	/**
	 * Helper method to hide a badly matched pair.
	 */
	private void hideBadPair(PlayingCard card1, PlayingCard card2) {
		card1.slowFade();
		card2.slowFade();
	}
	
	/**
	 * Helper method to save a matched pair.
	 */
	private void savePair(PlayingCard card1, PlayingCard card2) {
		card1.setLocked(true);
		card2.setLocked(true);
		playingCards.remove(card1);
		playingCards.remove(card2);
	}
	
	// ---------- BEGIN PUBLIC METHODS ----------
	
	public ArrayList<PlayingCard> getCards() {
		return playingCards;
	}
	
	/**
	 * Check if there are now 2 cards selected.
	 * @return true if a second card is now flipped
	 */
	public boolean secondSelection() {
		int selectedCards = 0;
		
		for (PlayingCard card : playingCards) {
			if (card.getVisible() && !card.isLocked()) {
				selectedCards++;
			}
		}
		
		return selectedCards > 1;
	}
	
	/**
	 * Process flipped cards to see if there is a match or if the match was bad.
	 */
	public boolean processFlippedCards() {
		PlayingCard firstCard = null;
		
		for (PlayingCard card : playingCards) {
			if (card.getVisible()) {
				if (firstCard == null) {
					// set reference
					firstCard = card;
				} else if (card.getCard() != firstCard.getCard()) {
					// doesn't match reference
					hideBadPair(firstCard, card);
					return false;
				} else {
					// matches reference
					savePair(firstCard, card);
					return true;
				}
			}
		}
		
		// only one card was flipped
		return false;
	}
	
	public boolean finishedGame() {
		for (PlayingCard card : playingCards) {
			if (!card.getVisible()) {
				return false;
			}
		}
		return true;
	}

	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}
}
