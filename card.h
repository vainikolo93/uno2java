#pragma once

//handles all the data related to each UNIQUE card
//having a quantity attribute allows duplicate cards
//to be stored in the same place

struct card
{
	char m_type;
	char m_color;
	int m_quantity;
};