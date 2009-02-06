#pragma once

//handles all the deck information
//each deck is composed of an array of card structs
//which are read in from a file
//the deck class handles things such as shuffling,
//drawing cards, remembering the last card drawn,
//counting the number of cards visible to the user
//(i.e. a quantity greater than zero)
//and anything else you would expect the deck to do

//while this class was constructed with the intention of 
//creating an UNO game, it was designed so that it could 
//be used in future projects for ANY type of card game.

#include <fstream> 
#include <time.h> //srand
#include <iostream> //for Debug()
using namespace std;

#include "card.h"


class Deck
{
//private:
protected:
	card * m_data; //data
	int m_size; //the "visible" size of the deck (perceived length)
	int m_length; //actual dimensions of the array
	int m_numOfCards; //total number of cards left in the deck
	int m_lastCard; //saves the location of the last card added to a deck
public:
	Deck();
	Deck(char * a_filename);
	Deck(Deck &other);
	void load(char * a_filename); //load the deck
	void load(ifstream &a_source);
	void load(Deck &other);
	void calcNumOfCards();
	void calcSize();
	int getLength();
	int getSize();
	int getNumOfCards();
	char getTypeAt(int row);
	char getColorAt(int row);
	int getQuantityAt(int row);
	bool drawCard(Deck &other);
	void addCardToOther(int card, Deck &other);
	void moveToEnd(int loc);
	void shuffle(Deck &other);
	void addOtherToThis(int card, Deck &other);
	int getLastCard();
	void addFromHandToPile(int card, Deck &other);
	void shuffleMinusOne(int card, Deck &other);
	int getLocation(char a_type, char a_color);
	
	void debug();
	~Deck();
};