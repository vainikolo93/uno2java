#include "cli.h"
#include <windows.h>

void gotoxy(int x, int y)
{
	COORD point;
	point.X = x;
	point.Y = y;
	SetConsoleCursorPosition(
		GetStdHandle(STD_OUTPUT_HANDLE), 
		point);
}

void setcolor(int fcolor, int bcolor)
{
	SetConsoleTextAttribute(
		GetStdHandle(STD_OUTPUT_HANDLE),
		(WORD)((bcolor << 4) | fcolor ));
}