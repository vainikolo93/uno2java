#pragma once

#include "Deck.h"

//this class is derived from the Deck class
//it was designed to be used with the cursor in the
//Game class, so that the cursor's lookAt can
//track down exactly what card it is pointing to in
//the players hand

class TrackingDeck : public Deck
{
public:
	TrackingDeck(int size);
	void add(int loc, char type, char color);
	void clear();
	bool empty();
	void release();
	char getTypeAt(int loc);
	char getColorAt(int loc);
};