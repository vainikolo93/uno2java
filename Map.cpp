#include "Map.h"

//default constructor
Map::Map() : m_width(0), m_height(0), m_data(NULL)
{}
//load map data
void Map::load(char * a_filename)
{
	ifstream in(a_filename);
	if(in.is_open())
	{
		load(in);
		in.close();
	}
}
//load map data
void Map::load(ifstream &a_source)
{
	a_source >> m_height;
	a_source >> m_width;
	//ignore the rest of the line
	a_source.ignore(1024, '\n');

	m_data = new char * [m_height];
	for(int r = 0; r < m_height; ++r)
	{
		m_data[r] = new char [m_width];
		for(int c = 0; c < m_width; ++c)
		{
			m_data[r][c] = a_source.get();
		}
		//ignore the rest of the line
		a_source.ignore(1024, '\n');
	}
}
//print contents of the map to the screen
void Map::printMap(int x, int y)
{
	for(int r = 0; r < m_height; ++r)
	{
		for(int c = 0; c < m_width; ++c)
		{
			//print only readable characters
			if(m_data[r][c] != ' ')
			{
				gotoxy(x+c, y+r);
				printf("%c", m_data[r][c]);
			}
		}
	}
}
//deconstructor
Map::~Map()
{
	for(int r = 0; r < m_height; ++r)
	{
		delete m_data[r];
	}
	delete m_data;
}