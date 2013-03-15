package com.vonhessling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Implementation of the Solitaire cryptographic algorithm by Bruce Schneier
 * http://en.wikipedia.org/wiki/Solitaire_(cipher)
 * 
 * 0. Key the deck
 * Ace of Clubs..King of Clubs, A of D..K of D, AH..KH, AS..KS, "A" Joker, "B" Joker
 *   
 *   Encoding and initial state:
 *   1 2 3 ... 52 A B
 *   
 * 1. Move the A joker down one card; wrap around; it can never land as the first card, skip first position.
 *   1 2 3 ... 52 B A
 *
 * 2. Move the B joker down two cards
 *   1 B 2 3 4 ... 52 A
 *
 * 3. Perform a triple cut around the two jokers
 *   B 2 3 4 ... 52 A 1
 * 
 * 4. Perform a count cut using the value of the bottom card (Observe the value of the card at the bottom of the 
 * deck. Take that number of cards from the top of the deck and insert them back to the bottom of the deck just 
 * above the last card.)
 *   2 3 4 ... 52 A B 1
 * 
 * 5. Find the output letter
 *  a. Convert the top card to its value
 *  b. count down that many cards from the top of the deck
 *  c. Look at the card immediately after your count and convert it to a letter
 *  d. If the output card is a joker, no letter is generated this sequence
 * 
 * 6. Goto step 1 if more letters are needed
 * 
 * @author vonhessling
 *
 */
public class Cryptonomicon {

	List<Integer> deck = new ArrayList<Integer>();
	int joker1 = 53;
	int joker2 = 54;
	
	/**
	 * Interactive program that produces characters according to the Cryptonomicon algorithm described by Neal Stephenson: 
	 * http://en.wikipedia.org/wiki/Cryptonomicon
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		Cryptonomicon c = new Cryptonomicon();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		do {
			System.out.println("Produced character: " + c.getNextValue());
			System.out.println("Press enter for next character!");
		} while (br.readLine() != null);
	}

	public Cryptonomicon() {
		// initialize:
		for (int i = 0; i < 54; i++) {
			deck.add(i + 1);
		}
	}
	
	private int getNextValue() {
		printDeck();
		step(joker1);
		printDeck();
		step(joker2);
		printDeck();
		step(joker2);
		printDeck();
		doTripleCut();
		printDeck();
		doCountCut();
		printDeck();
		return getOutputValue();
	}
	
	/**
	 * Move the given card according to the logic in step 1 and step 2
	 * @param card The card to move
	 */
	private void step(int card) {
		System.out.println("Moving " + card);
		int index = getIndex(card);
		
		if (index < deck.size() - 1) {
			deck.add(index + 2, deck.get(index));
			deck.remove(index);
		} else {
			deck.add(1, deck.get(index));
			deck.remove(index + 1);
		}
	}
	
	/**
	 * Performs a triple cut according to the logic in step 3
	 */
	private void doTripleCut() {
		System.out.println("Performing triple cut");
		int jokerAindex = getIndex(53); 
		int jokerBindex = getIndex(54);
		int lowerJokerIndex = Math.min(jokerAindex, jokerBindex);
		int upperJokerIndex = Math.max(jokerAindex, jokerBindex);
		
		// copying lower/upper part to avoid ConcurrentModification Exception if using subList
		List<Integer> lowerPart = new ArrayList<Integer>();
		for (int i = 0; i < lowerJokerIndex; i++) {
			lowerPart.add(deck.get(i));
		}
		
		List<Integer> upperPart = new ArrayList<Integer>();
		for(int i = upperJokerIndex + 1; i < deck.size(); i++) {
			upperPart.add(deck.get(i));
		}
				
		// delete upper part
		for (int i = 0; i < upperPart.size(); i++) {
			deck.remove(upperJokerIndex + 1);
		}
		// add new upper part
		deck.addAll(upperJokerIndex + 1, lowerPart);
		
		// delete lower part
		for (int i = 0; i < lowerPart.size(); i++) {
			deck.remove(0);
		}
		// add new lower part
		deck.addAll(0, upperPart);
	}
	
	
	/**
	 * Performs a count cut according to the logic in step 4
	 */
	private void doCountCut() {
		System.out.println("Performing count cut");
		int numCardsTomove = deck.get(deck.size() - 1);
		List<Integer> cardsToMove = new ArrayList<Integer>();
		for (int i = 0; i < numCardsTomove; i++) {
			cardsToMove.add(deck.get(0));
			deck.remove(0);
		}
		deck.addAll(deck.size() - 1, cardsToMove);
	}
	
	/**
	 * Determine the output value 
	 * @return An integer representing the output character
	 */
	private int getOutputValue() {
		if (deck.get(0) == joker1 || 
				deck.get(0) == joker2) {
			return 0;
		}
		return deck.get(deck.get(0));
	}
	
	/**
	 * Returns the index of the given card
	 * @param card The value of the card to find
	 * @return The index of the given card or -1 if not found. 
	 */
	private int getIndex(int card) {
		for (int i = 0; i < deck.size(); i++) {
			if (card == deck.get(i)) {
				return i;
			}
		}
		return -1;
	}
		
	/**
	 * Prints the current state of the deck
	 */
	private void printDeck() {
		System.out.print("Deck state: ");
		for (Integer i: deck) {
			System.out.print(i + " ");
		}
		System.out.println();
	}
}
