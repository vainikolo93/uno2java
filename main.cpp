#include <conio.h>
#include "Game.h"
#include "Deck.h"


///*
int main()
{
	//init
		//create/load initial game data
	Game g("unoDeck.txt");
	g.setup();
	g.initDraw();
	//create input buffer
	char input;
	//handle game initialization logic

	//game loop
	while(!g.wantToQuit())
	{
		//update the state
		g.processGameLogic();
		//draw
		g.draw();
		//get input
		input = getch();
		//handle input
		g.handleInput(input);
	}

	//release phase
	//dealocate memory and free system resources
	//NOTE: no need to do this.....it is handled by free 
			//by the class deconstructors
	printf("\n");
	return 0;
}
