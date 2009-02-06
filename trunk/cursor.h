#pragma once

//handles all the data related to the cursor, from its
//current x and y position, to its limits, and its lookAt point

struct cursor
{
	int m_x, m_y;
	int m_minX, m_maxX, m_minY, m_maxY;
	char m_icon; 
	int m_lookAt;
};