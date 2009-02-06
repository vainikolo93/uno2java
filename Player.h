#pragma once

//the data for the players is stored here
//each player has their own hand (from the Deck class)
//as well as a name and a score
//The Player class hands the setting and printing of
//player names on its own

#include "Deck.h"
#include <iostream>

#define MAX_NAME_LENGTH 9

class Player
{
private:
	Deck m_hand;
	int m_score;
	char * m_name;
	int m_nameLength;
	bool uno;
public:
	Player();
	Player(Deck &other);
	void load(Deck &other);
	void setName();
	void printName();
	Deck & getHand();
	int getScore();
	void setUnoFalse();
	void setUnoTrue();
	bool getUno();
	void incrementScore(int a_score);
	~Player();
};