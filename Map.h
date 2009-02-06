#pragma once

//load in maps which are use as "masks"
//to lay over the Uno cards
//the "masks" portray the number and type
//of the card

#include <fstream>
#include "cli.h"
using namespace std;

class Map
{
private:
	int m_width;
	int m_height;
	char ** m_data;
public:
	Map();
	void load(char * a_filename);
	void load(ifstream &a_source);
	void printMap(int x, int y);
	~Map();
};