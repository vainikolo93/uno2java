#include "trackingDeck.h"

//create a tracking deck
TrackingDeck::TrackingDeck(int a_size)
{
	m_length = a_size;
	
	m_data = new card [m_length];

	for(int r = 0; r < m_length; ++r)
	{
		//make sure the tracking deck is empty
		m_data[r].m_type = NULL;
		m_data[r].m_color = NULL;
		m_data[r].m_quantity = NULL;
	}
}
//add a card to the tracking deck
void TrackingDeck::add(int loc, char a_type, char a_color)
{
	m_data[loc].m_type = a_type;
	m_data[loc].m_color = a_color;
}
//clear the contents of the tracking deck
void TrackingDeck::clear()
{
	for(int r = 0; r < m_length; ++r)
	{
		//clear data
		m_data[r].m_type = NULL;
		m_data[r].m_color = NULL;
		m_data[r].m_quantity = NULL;
	}
}
//check to see if the tracking deck is empty
bool TrackingDeck::empty()
{
	for(int r = 0; r < m_length; ++r)
	{
		//if not empty
		if(m_data[r].m_type != NULL || m_data[r].m_color != NULL)
		{
			return 	false;
		}
	}
	return true;
}
//release the memory
void TrackingDeck::release()
{
	clear();
	delete m_data;
}
//get a type at a certain element
char TrackingDeck::getTypeAt(int loc)
{
	return m_data[loc].m_type;
}
//get a color at a certain element
char TrackingDeck::getColorAt(int loc)
{
	return m_data[loc].m_color;
}