#pragma once

//some functions professor vaganov gave me to work with
//they allow me to move the cursor to any x and y location
//as well as changing the foreground and background color

#define COLOR_BLACK			0
#define COLOR_DARK_BLUE		1
#define COLOR_DARK_GREEN	2
#define COLOR_DARK_CYAN		3
#define COLOR_DARK_RED		4
#define COLOR_DARK_MAGENTA	5
#define COLOR_DARK_YELLOW	6
#define COLOR_GRAY			7
#define COLOR_DARK_GRAY		8
#define COLOR_BLUE			9
#define COLOR_GREEN			10
#define COLOR_CYAN			11
#define COLOR_RED			12
#define COLOR_MAGENTA		13
#define COLOR_YELLOW		14
#define COLOR_WHITE			15
#define COLOR_COUNT			16

void gotoxy(int x, int y);
void setcolor(int fcolor, int bcolor);