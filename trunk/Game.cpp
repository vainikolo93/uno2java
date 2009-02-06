#include "Game.h"

//default constructor
Game::Game() : m_playerCount(-1), m_currentPlayer(-1), m_input(0), m_wildColor(0), m_round(0)
{
	srand(time(NULL));
	hasDrawn = false;
	cursorLocked = false;
	cardPlayed = false;
	skipEffect = false;
	wildSelect = true;
	m_wantsToQuit = false;
}
//contructor that loads based off predefined player count and file name
Game::Game(int a_playerCount, char * a_filename)
{
	Game();
	load(a_playerCount, a_filename);
}
//constructor that loads a file based off a predefined filename
Game::Game(char * a_filename)
{
	Game();
	load(a_filename);
}
//loads based off a predefined player count and filename
void Game::load(int a_playerCount, char * a_filename)
{
	m_playerCount = a_playerCount;

	m_drawDeck.load(a_filename);
	m_discardPile.load(m_drawDeck);
	m_playerList = new Player [m_playerCount];

	for(int n = 0; n < m_playerCount; n++)
	{
		m_playerList[n].load(m_drawDeck);
	}
}

//determines player count
//loads off a predefined file name
void Game::load(char * a_filename)
{
	int input = inputPlayerCount();
	load(input, a_filename);
}
//inputs a valid player number
int Game::inputPlayerCount()
{
	int input = 0;
	printf("How many players? (Enter a number between 2 and 10)\n");
	while(input < 2 || input > 10)
	{
		scanf("%d", &input);
	}
	return input;
}
//setup initial player data and deal hands
void Game::setup()
{
	//set direction of game play
	m_direction = FORWARD;
	//set state
	m_gamestate = STATE_HOT_SEAT;

	//for all players
	//make sure player count has been set
	if(m_playerCount < 0) //invalid
	{
		m_playerCount = inputPlayerCount();
	}
	for(int i = 0; i < m_playerCount; ++i)
	{
		printf("Please enter name for Player %d: ", i+1);
		//read in and set player name
		m_playerList[i].setName();
		printf("\n");

		//deal cards
		for(int n = 0; n < INITIAL_HAND; ++n)
		{
			m_drawDeck.drawCard(m_playerList[i].getHand());
		
		}
	}

	//randomly elect Starting player
	m_currentPlayer = rand() % m_playerCount;

	//draw first card to the discard pile
	//make sure it is legal
	//NOTE: It is illegal to start with a WILD DRAW 4 card
	bool isLegal = false;
	while(!isLegal)
	{
		m_drawDeck.drawCard(m_discardPile);
		//if it is a Wild Draw 4
		if(m_discardPile.getTypeAt(m_discardPile.getLastCard()) == '4'
			&& m_discardPile.getColorAt(m_discardPile.getLastCard()) == 'W')
		{
			//not legal
			//shuffle back into deck
			m_drawDeck.shuffle(m_discardPile);
		}
		else//legal
		{
			isLegal = true;
		}
	}
	//after the first card is drawn, set its effect
	char t = m_discardPile.getTypeAt(m_discardPile.getLastCard());
	char c = m_discardPile.getColorAt(m_discardPile.getLastCard());
	setInitEffect(t, c);

	m_cursor.m_icon = char(16);
	setCursorLimits(m_playerList[m_currentPlayer].getHand().getNumOfCards());

}
//draw display base (does not change)
void Game::initDraw()
{
	//clear screen first
	system("cls");
	//clear area draws faster if i use system("cls"); first
	clearArea(COLOR_DARK_GRAY, 0, SCREEN_WIDTH, 0, SCREEN_HEIGHT);
	initDrawHUD();
	initDrawTopCard();
	//load masks for drawing top card
	loadMasks();
	printUnoLogo(UNO_LOGO_X, UNO_LOGO_Y);
	initDrawPlayerHand();
	initText();
}
//draw the HUD base (does not change)
void Game::initDrawHUD()
{
	//print top menu
	int pCount = 0;
	int r = 0;
	int c = 0;

	clearArea(COLOR_WHITE, 0, GUI_WIDTH-1, 0, GUI_HEIGHT);
	//print borders and text
	for(int y = 0; y <= GUI_HEIGHT; ++y)
	{
		c =0;
		for(int x = 0; x < GUI_WIDTH; ++x)
		{
			setcolor(COLOR_DARK_GRAY, COLOR_WHITE);
			gotoxy(x, y);
			if(x % PLAYER_COL_SIZE == 0 && y % PLAYER_ROW_SIZE == 0 || x == GUI_WIDTH-1 && y != GUI_HEIGHT)
			{
				//printf("%c", char(1));
				//top left
				if(r == 0 && c == 0)
				{printf("%c", char(201));}
				//top right
				else if (r == 0 && x == GUI_WIDTH-1)
				{printf("%c", char(187));}
				//top, t-shapes at columns
				else if(r == 0)
				{printf("%c", char(203));}
				//middle left
				else if(r == PLAYER_ROW_SIZE && c == 0)
				{printf("%c", char(204));}
				//middle right
				else if (r == PLAYER_ROW_SIZE && x == GUI_WIDTH-1)
				{printf("%c", char(185));}
				//middle, cross shapes at columns
				else if (r == PLAYER_ROW_SIZE)
				{printf("%c", char(206));}
				//bottom left
				else if(r == 2*PLAYER_ROW_SIZE && c == 0)
				{printf("%c", char(200));}
				//bottom right
				else if (r == 2*PLAYER_ROW_SIZE && x == GUI_WIDTH-1)
				{printf("%c", char(188));}
				//bottom, t-shapes at columns
				else if (r == 2*PLAYER_ROW_SIZE)
				{printf("%c", char(202));}
				//last vertical column
				else
				{printf("%c", char(186));}
			}
			//horizontal border
			else if(y % PLAYER_ROW_SIZE == 0)
			{
				printf("%c", char(205));
				
			}
			//vertical border
			else if(x % PLAYER_COL_SIZE == 0 || x == GUI_WIDTH-1 && y != GUI_HEIGHT)
			{
				printf("%c", char(186));
				
			}
			
			//print player data
			if(pCount < m_playerCount)
			{
				setcolor(COLOR_DARK_BLUE, COLOR_WHITE);
				//print row 1
				if(pCount < PLAYER_PRINT_OFFSET && x == (pCount*PLAYER_COL_SIZE)+2)
				{
					gotoxy(x, PLAYER_PRINT_ROW_1);
					m_playerList[pCount].printName();
					gotoxy(x, PLAYER_PRINT_ROW_1+PLAYER_SCORE_OFFSET_Y);
					printf("SCORE: ");

				}
				//print row 2
				else if(x == ((pCount-PLAYER_PRINT_OFFSET)*PLAYER_COL_SIZE)+2)
				{
					gotoxy(x, PLAYER_PRINT_ROW_2);
					m_playerList[pCount].printName();
					gotoxy(x, PLAYER_PRINT_ROW_2+PLAYER_SCORE_OFFSET_Y);
					printf("SCORE: ");
				}
			} 
			c++;
		}
		pCount++;
		printf("\n");
		r++;
	}

}
//draw game (updates)
void Game::draw()
{
	drawHUD();
	drawTopCard();
	if(m_gamestate == STATE_PLAY)
	{
		drawPlayerHand();
		drawCursor();
	}
	else if(m_gamestate == STATE_WILD_SELECT)
	{
		if(wildSelect == true)
		{drawWildSelection();}
		else
		{drawPlayerHand();}
	}
	else if(m_gamestate == STATE_HOT_SEAT)
	{
		drawHotSeat();
	}
	else if(m_gamestate == STATE_MENU)
	{
		drawControlsMenu();
	}
	else if(m_gamestate == STATE_ROUND_OVER)
	{
		drawRoundOver();
	}
	else if(m_gamestate == STATE_GAME_OVER)
	{
		drawGameOver();
	}
	gotoxy(0, SCREEN_HEIGHT); //move cursor to bottom left
}
//draw the HUD (updates)
void Game::drawHUD()
{
	int pCount = 0;
	//draw HUD
	for(int y = 0; y <= GUI_HEIGHT; ++y)
	{	
		for(int x = 0; x < GUI_WIDTH; ++x)
		{
			if(pCount < m_playerCount)
			{
				int pCards = m_playerList[pCount].getHand().getNumOfCards();

				setcolor(COLOR_DARK_RED, COLOR_WHITE);
				//print row 1
				if(pCount < PLAYER_PRINT_OFFSET && x == (pCount*PLAYER_COL_SIZE)+2)
				{
					//draw score
					gotoxy(x+PLAYER_SCORE_OFFSET_X, PLAYER_PRINT_ROW_1+PLAYER_SCORE_OFFSET_Y);
					printf("%d", m_playerList[pCount].getScore());

					//draw UNO
					setcolor(COLOR_RED, COLOR_WHITE);
					gotoxy(x+PLAYER_UNO_OFFSET_X, PLAYER_PRINT_ROW_1);
					if(m_playerList[pCount].getUno())
					{
						printf("UNO");
					}
					else
					{
						printf("   "); //clear any previous UNOs
					}
					
					//clear icon area first
					clearArea(COLOR_WHITE, x, x+PLAYER_CARD_OFFSET_X-1, PLAYER_PRINT_ROW_1+1, PLAYER_PRINT_ROW_1+2);
					clearArea(COLOR_WHITE, x, x+PLAYER_CARD_OFFSET_X-1, PLAYER_PRINT_ROW_2+1, PLAYER_PRINT_ROW_2+2);
					//draw icons to represent number of cards
					setcolor(COLOR_DARK_GREEN, COLOR_WHITE);
					for(int n = 0; n < pCards; ++n)
					{
						//row 1
						if(n < PLAYER_CARD_OFFSET_X)
						{
							gotoxy(x+n,PLAYER_PRINT_ROW_1+1);
							printf("%c", char(4));
						}
						//row 2
						else if(n < 2*PLAYER_CARD_OFFSET_X)
						{
							gotoxy(x+n-PLAYER_CARD_OFFSET_X,PLAYER_PRINT_ROW_1+2);
							printf("%c", char(4));
						}
					}

					//set marker for the current player
					setcolor(COLOR_RED, COLOR_WHITE);
					gotoxy((pCount*PLAYER_COL_SIZE)+1,PLAYER_PRINT_ROW_1);
					if(pCount == m_currentPlayer)
					{
						printf("*");//set
					}
					else
					{
						printf(" ");//clear
					}
				}
				//print row 2
				else if(x == ((pCount-PLAYER_PRINT_OFFSET)*PLAYER_COL_SIZE)+2)
				{
					//draw score
					gotoxy(x+PLAYER_SCORE_OFFSET_X, PLAYER_PRINT_ROW_2+PLAYER_SCORE_OFFSET_Y);
					printf("%d", m_playerList[pCount].getScore());

					//draw UNO
					setcolor(COLOR_RED, COLOR_WHITE);
					gotoxy(x+PLAYER_UNO_OFFSET_X, PLAYER_PRINT_ROW_2);
					if(m_playerList[pCount].getUno())
					{
						printf("UNO");
					}
					else
					{
						printf("   "); //clear any previous UNOs
					}

					//draw icons to represent number of cards
					setcolor(COLOR_DARK_GREEN, COLOR_WHITE);
					for(int n = 0; n < pCards; ++n)
					{
						//row 1
						if(n < PLAYER_CARD_OFFSET_X)
						{
							gotoxy(x+n,PLAYER_PRINT_ROW_2+1);
							printf("%c", char(4));
						}
						//row 2
						else if(n < 2*PLAYER_CARD_OFFSET_X)
						{
							gotoxy(x+n-PLAYER_CARD_OFFSET_X,PLAYER_PRINT_ROW_2+2);
							printf("%c", char(4));
						}
					}

					//set marker for the current player
					setcolor(COLOR_RED, COLOR_WHITE);
					gotoxy(((pCount-PLAYER_PRINT_OFFSET)*PLAYER_COL_SIZE)+1,PLAYER_PRINT_ROW_2);
					if(pCount == m_currentPlayer)
					{
						printf("*");//set
					}
					else
					{
						printf(" ");//clear
					}
				}
			}
			
		}
		pCount++;
		printf("\n");
	}
}
//draw the top card in the discard pile (does not change)
void Game::initDrawTopCard()
{
	setcolor(COLOR_DARK_GRAY, COLOR_WHITE);
	for(int y = TOPCARD_Y1; y <= TOPCARD_Y2; ++y)
	{
		for(int x = TOPCARD_X1; x <= TOPCARD_X2; ++x)
		{
			gotoxy(x, y);
			//top left corner
			if(y == TOPCARD_Y1 && x == TOPCARD_X1)
			{
				printf("%c", char(201));
			}
			//top right corner
			else if(y == TOPCARD_Y1 && x == TOPCARD_X2)
			{
				printf("%c", char(187));
			}
			//bottom left corner
			else if(y == TOPCARD_Y2 && x == TOPCARD_X1)
			{
				printf("%c", char(200));
			}
			//bottom right corner
			else if(y == TOPCARD_Y2 && x == TOPCARD_X2)
			{
				printf("%c", char(188));
			}
			//top and bottom
			else if(y == TOPCARD_Y1 || y == TOPCARD_Y2)
			{
				printf("%c", char(205));
			}
			//left and right
			else if(x == TOPCARD_X1 || x == TOPCARD_X2)
			{
				printf("%c", char(186));
			}
			else
			{
				printf(" ");
			}
		}
	}
	//print label
	setcolor(COLOR_BLACK, COLOR_WHITE);
	int middleY = (TOPCARD_Y2 + TOPCARD_Y1)/2;
	gotoxy(TOPCARD_X1-4, middleY-3); printf("   ");
	gotoxy(TOPCARD_X1-4, middleY-2); printf(" P ");
	gotoxy(TOPCARD_X1-4, middleY-1); printf(" I ");
	gotoxy(TOPCARD_X1-4, middleY); printf(" L ");
	gotoxy(TOPCARD_X1-4, middleY+1); printf(" E ");
	gotoxy(TOPCARD_X1-4, middleY+2); printf("   ");
}
//draw the top card (updates)
void Game::drawTopCard()
{
	//get color of the last card added to the pile (top card)
	char c = m_discardPile.getColorAt(m_discardPile.getLastCard());
	//get the type of the last card added to the pile (top card)
	char t = m_discardPile.getTypeAt(m_discardPile.getLastCard());
	int color = -1;
	//set color
	switch(c)
	{
	case 'B': color = COLOR_BLUE; break;
	case 'G': color = COLOR_GREEN; break;
	case 'R': color = COLOR_RED; break;
	case 'Y': color = COLOR_YELLOW; break;
	case 'W': color = COLOR_BLACK; break;
	}

	//clear area before printing mask
	clearArea(color, TOPCARD_X1+1, TOPCARD_X2-1, TOPCARD_Y1+1, TOPCARD_Y2-1);

	//set black font for non wild cards
	if(c != 'W')
	{
	setcolor(COLOR_BLACK, color);
	}
	else//set white font for wild cards
	{
		drawTopCardWildColor();//print icons first
		setcolor(COLOR_WHITE, color);//then set color for mask
		
	}

	//set and print mask type
	switch(t)
	{
	case '0': 
		if(c != 'W')//if not a wild
		{
			m_mask[0].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); 
		}
		else//wild
		{
			m_mask[MASK_WILD].printMap(TOPCARD_X1+3, TOPCARD_Y1+2);
		}
		break;
	case '1': m_mask[1].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	case '2': m_mask[2].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	case '3': m_mask[3].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	case '4': 
		if(c != 'W')//if not a wild
		{
			m_mask[4].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); 
		}
		else
		{
			m_mask[MASK_WILD4].printMap(TOPCARD_X1+3, TOPCARD_Y1+2);
		}
		break;
	case '5': m_mask[5].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	case '6': m_mask[6].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	case '7': m_mask[7].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	case '8': m_mask[8].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	case '9': m_mask[9].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	case 'R': m_mask[MASK_REV].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	case 'D': m_mask[MASK_DRAW].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	case 'S': m_mask[MASK_SKIP].printMap(TOPCARD_X1+3, TOPCARD_Y1+2); break;
	}
}
//clear an area
void Game::clearArea(int color, int x1, int x2, int y1, int y2)
{
	setcolor(color, color);
	for(int y = y1; y <= y2; ++y)
	{
		for(int x = x1; x <= x2; ++x)
		{
			gotoxy(x, y);
			printf(" ");
		}
	}
}
//load the masks for the drawing
void Game::loadMasks()
{
	m_mask[0].load("map0.txt");
	m_mask[1].load("map1.txt");
	m_mask[2].load("map2.txt");
	m_mask[3].load("map3.txt");
	m_mask[4].load("map4.txt");
	m_mask[5].load("map5.txt");
	m_mask[6].load("map6.txt");
	m_mask[7].load("map7.txt");
	m_mask[8].load("map8.txt");
	m_mask[9].load("map9.txt");
	m_mask[MASK_REV].load("mapR.txt");
	m_mask[MASK_DRAW].load("mapD.txt");
	m_mask[MASK_SKIP].load("mapS.txt");
	m_mask[MASK_WILD].load("mapW0.txt");
	m_mask[MASK_WILD4].load("mapW4.txt");
}
//handles input
void Game::handleInput(char a_input)
{
	switch(a_input)
	{
	//quit
	case 'q': m_wantsToQuit = true; 
		setcolor(COLOR_GRAY, COLOR_DARK_GRAY); 
		gotoxy(0, 23); break;  

	case ' ': //space bar
	case 'w':
	case 'a':
	case 's':
	case 'd':
	case '\r': //enter
	case 'p':
	case 'u':
	case 'x':
	case 'm':
	case '1': 
	case '2': 
	case '3': 
	case '4':
		m_input = a_input; break;
	}
}
//prints a large UNO logo
void Game::printUnoLogo(int x, int y)
{

	setcolor(COLOR_WHITE, COLOR_BLACK);
	gotoxy(x, y);	printf("%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c", char(201),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(187));
	gotoxy(x, y+1); printf("%c %c    %c  %c    %c   %c%c%c%c  %c", char(186), char(219), char(219), char(219), char(219), char(219), char(219), char(219), char(219), char(186));
	gotoxy(x, y+2); printf("%c %c    %c  %c%c   %c  %c    %c %c", char(186), char(219), char(219), char(219), char(219), char(219), char(219), char(219), char(186));
	gotoxy(x, y+3); printf("%c %c    %c  %c %c  %c  %c    %c %c", char(186), char(219), char(219), char(219), char(219), char(219), char(219), char(219), char(186));
	gotoxy(x, y+4); printf("%c %c    %c  %c  %c %c  %c    %c %c", char(186), char(219), char(219), char(219), char(219), char(219), char(219), char(219), char(186));
	gotoxy(x, y+5); printf("%c %c    %c  %c   %c%c  %c    %c %c", char(186), char(219), char(219), char(219), char(219), char(219), char(219), char(219), char(186));
	gotoxy(x, y+6); printf("%c  %c%c%c%c   %c    %c   %c%c%c%c  %c", char(186), char(219), char(219), char(219), char(219), char(219), char(219), char(219), char(219), char(219), char(219), char(186));
	gotoxy(x, y+7);	printf("%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c", char(200),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(205),char(188));

}

//displays the cards in the current players hand
void Game::drawPlayerHand()
{
	int offsetX = 0, offsetY = 0;
	char c, t;
	int q;
	int end = 0;
	//for every different card
	for(int n = 0; n < m_playerList[m_currentPlayer].getHand().getSize(); ++n)
	{
		//get the color, type, and quantity
		c = m_playerList[m_currentPlayer].getHand().getColorAt(n);
		t = m_playerList[m_currentPlayer].getHand().getTypeAt(n);
		q = m_playerList[m_currentPlayer].getHand().getQuantityAt(n);

		//if its the last card
		if(cursorLocked){
		if(c == m_playerList[m_currentPlayer].getHand().getColorAt(m_playerList[m_currentPlayer].getHand().getLastCard())
			&& t == m_playerList[m_currentPlayer].getHand().getTypeAt(m_playerList[m_currentPlayer].getHand().getLastCard()))
		{
			end = 1; //save the last card to be printed last
			//otherwise the cursor lookat will be thrown off
		}
		else
		{end = 0;}}
		//print all duplicate cards
		while(q > end)
		{
			gotoxy(PLAYER_HAND_X+offsetX, PLAYER_HAND_Y+offsetY);
			switch(c)
			{
			case 'B': setcolor(COLOR_BLACK, COLOR_BLUE); printf("BLUE   "); break;
			case 'G': setcolor(COLOR_BLACK, COLOR_GREEN); printf("GREEN  "); break;
			case 'R': setcolor(COLOR_BLACK, COLOR_RED);  printf("RED    ");break;
			case 'Y': setcolor(COLOR_BLACK, COLOR_YELLOW); printf("YELLOW ");break;
			case 'W': setcolor(COLOR_WHITE, COLOR_BLACK); printf("WILD   "); break;
			}

			gotoxy(PLAYER_HAND_X+offsetX+7, PLAYER_HAND_Y+offsetY);
			switch(t)
			{
			case '0': if(c != 'W'){printf("   0   ");} else {printf("       ");} break;
			case '1': printf("   1   "); break;
			case '2': printf("   2   "); break;
			case '3': printf("   3   "); break;
			case '4': if(c != 'W'){printf("   4   ");} else {printf("  +4   ");} break;
			case '5': printf("   5   "); break;
			case '6': printf("   6   "); break;
			case '7': printf("   7   "); break;
			case '8': printf("   8   "); break;
			case '9': printf("   9   "); break;
			case 'R': printf("REVERSE"); break;
			case 'D': printf("DRAW +2"); break;
			case 'S': printf("  SKIP "); break;
			}
			q--;
			++offsetY;

			//if the y offset exceeds the limit for the column, start a new column
			if(offsetY >= PLAYER_HAND_COL_LIMIT)
			{
				offsetX = PLAYER_HAND_OFFSET_X;
				offsetY = 0;
			}
		}	
	}
	
	//print last card, last
	if(cursorLocked){
	c = m_playerList[m_currentPlayer].getHand().getColorAt(m_playerList[m_currentPlayer].getHand().getLastCard());
	t = m_playerList[m_currentPlayer].getHand().getTypeAt(m_playerList[m_currentPlayer].getHand().getLastCard());
		
	gotoxy(PLAYER_HAND_X+offsetX, PLAYER_HAND_Y+offsetY);
	switch(c)
	{
	case 'B': setcolor(COLOR_BLACK, COLOR_BLUE); printf("BLUE   "); break;
	case 'G': setcolor(COLOR_BLACK, COLOR_GREEN); printf("GREEN  "); break;
	case 'R': setcolor(COLOR_BLACK, COLOR_RED);  printf("RED    ");break;
	case 'Y': setcolor(COLOR_BLACK, COLOR_YELLOW); printf("YELLOW ");break;
	case 'W': setcolor(COLOR_WHITE, COLOR_BLACK); printf("WILD   "); break;
	}

	gotoxy(PLAYER_HAND_X+offsetX+7, PLAYER_HAND_Y+offsetY);
	switch(t)
	{
	case '0': if(c != 'W'){printf("   0   ");} else {printf("       ");} break;
	case '1': printf("   1   "); break;
	case '2': printf("   2   "); break;
	case '3': printf("   3   "); break;
	case '4': if(c != 'W'){printf("   4   ");} else {printf("  +4   ");} break;
	case '5': printf("   5   "); break;
	case '6': printf("   6   "); break;
	case '7': printf("   7   "); break;
	case '8': printf("   8   "); break;
	case '9': printf("   9   "); break;
	case 'R': printf("REVERSE"); break;
	case 'D': printf("DRAW +2"); break;
	case 'S': printf("  SKIP "); break;
	}}
	
}
//draws the base for the area where the Player's hand is displayed
void Game::initDrawPlayerHand()
{
	clearArea(COLOR_WHITE, PLAYER_HAND_X-2, SCREEN_WIDTH-1, PLAYER_HAND_Y-1, SCREEN_HEIGHT-1);

	setcolor(COLOR_DARK_GRAY, COLOR_WHITE);
	for(int y = PLAYER_HAND_Y-1; y <= SCREEN_HEIGHT-1; ++y)
	{
		for(int x = PLAYER_HAND_X-2; x <= SCREEN_WIDTH-1; ++x)
		{
			gotoxy(x, y);
			//merge with HUD
			if(x % PLAYER_COL_SIZE == 0 && y ==PLAYER_HAND_Y-1)
			{
				printf("%c", char(202));
			}
			//top left corner
			else if(y == PLAYER_HAND_Y-1 && x == PLAYER_HAND_X-2)
			{
				printf("%c", char(203));
			}
			//top right corner
			//merge with HUD
			else if(y == PLAYER_HAND_Y-1 && x == SCREEN_WIDTH-1)
			{
				printf("%c", char(185));
			}
			//bottom left corner
			else if(y == SCREEN_HEIGHT-1 && x == PLAYER_HAND_X-2)
			{
				printf("%c", char(200));
			}
			//bottom right corner
			else if(y == SCREEN_HEIGHT-1 && x == SCREEN_WIDTH-1)
			{
				printf("%c", char(188));
			}
			//top and bottom
			else if(y == PLAYER_HAND_Y-1 || y == SCREEN_HEIGHT-1)
			{
				printf("%c", char(205));
			}
			//left and right
			else if(x == PLAYER_HAND_X-2 || x == SCREEN_WIDTH-1)
			{
				printf("%c", char(186));
			}
		}
	}
}
//displays random initial text
void Game::initText()
{
	//give credit so i don't get sued  O_o
	gotoxy(57, 24);
	setcolor(COLOR_GRAY, COLOR_DARK_GRAY);
	printf("COPYRIGHT MATTEL GAMES");

	//take credit so i don't feel like i've wasted my time >_<
	gotoxy(21, 20);
	setcolor(COLOR_BLACK, COLOR_DARK_GRAY);
	printf("Programmed by Aaron Cole");

	//tell them how to access the menu so they know the controls  =)
	gotoxy(22, 22);
	setcolor(COLOR_BLACK, COLOR_GRAY);
	printf("Press 'm' for controls");
	gotoxy(22, 23);
	printf("   or 'q' to quit.    ");
}
//set cursor limits
void Game::setCursorLimits(int numOfCards)
{
	m_cursor.m_minY = PLAYER_HAND_Y;
	m_cursor.m_minX = PLAYER_HAND_X-1;
	m_cursor.m_maxX = m_cursor.m_minX+PLAYER_HAND_OFFSET_X;

	if(numOfCards > PLAYER_HAND_COL_LIMIT)
	{
		m_cursor.m_maxY = PLAYER_HAND_Y+PLAYER_HAND_COL_LIMIT-1;
		
	}
	else
	{
		m_cursor.m_maxY = PLAYER_HAND_Y+numOfCards-1;
	}

	m_cursor.m_x = m_cursor.m_minX;
	m_cursor.m_y = m_cursor.m_minY;

	//clear lookat
	m_cursor.m_lookAt = 0;
}
//move location of the cursor and lookat point
void Game::moveCursor(int direction, int numOfCards)
{
	if(!cursorLocked)
	{
		if(direction == MOVE_UP && m_cursor.m_y-1 >= m_cursor.m_minY)
		{
			m_cursor.m_y--;
			m_cursor.m_lookAt--;
		}
		else if(direction == MOVE_DOWN && m_cursor.m_y < m_cursor.m_maxY)
		{
			//first column
			if(m_cursor.m_x != m_cursor.m_maxX)
			{
				m_cursor.m_y++;
				m_cursor.m_lookAt++;
			}
			//second column
			else if (m_cursor.m_y+1 < numOfCards - PLAYER_HAND_COL_LIMIT + m_cursor.m_minY)
			{
				m_cursor.m_y++;
				m_cursor.m_lookAt++;
			}
		}
		else if(direction == MOVE_LEFT && m_cursor.m_x != m_cursor.m_minX)
		{
			m_cursor.m_x = m_cursor.m_minX;
			m_cursor.m_lookAt -= PLAYER_HAND_COL_LIMIT;

		}
		else if(direction == MOVE_RIGHT)
		{
			//make sure there is a card to the right first
			if(m_cursor.m_y < numOfCards - PLAYER_HAND_COL_LIMIT + m_cursor.m_minY)
			{
				m_cursor.m_x = m_cursor.m_maxX;
				m_cursor.m_lookAt += PLAYER_HAND_COL_LIMIT;
			}
		}
	}
}
//draw the cursor in its current location
void Game::drawCursor()
{
	setcolor(COLOR_RED, COLOR_WHITE);

	for(int y = m_cursor.m_minY; y <= m_cursor.m_maxY; ++y)
	{
		//first column
		gotoxy(m_cursor.m_minX, y);

		if(m_cursor.m_x == m_cursor.m_minX //if in this column
			&& y == m_cursor.m_y)//and this row
		{
			printf("%c", m_cursor.m_icon);//draw cursor
		}
		else//clear any previous drawing
		{
			printf(" ");
		}

		//second column
		gotoxy(m_cursor.m_maxX, y);

		if(m_cursor.m_x == m_cursor.m_maxX //if in this column
			&& y == m_cursor.m_y)//and this row
		{
			printf("%c", m_cursor.m_icon);//draw cursor
		}
		else//clear any previous drawing
		{
			printf(" ");
		}
	}
}
//add the card the cursor is pointing to, to the pile
void Game::addSelectedCardToPile()
{
	char c, t, q;
	int loc = 0;
	int end = 0;
	//used in conjunction with the cursor to track down the exactly what 
	//card the cursor is looking at
	TrackingDeck tracker(m_playerList[m_currentPlayer].getHand().getNumOfCards());

	//add cards to the tracker deck the EXACT same way they are drawn
	for(int n = 0; n < m_playerList[m_currentPlayer].getHand().getSize(); ++n)
	{
		//get the color, type, and quantity
		c = m_playerList[m_currentPlayer].getHand().getColorAt(n);
		t = m_playerList[m_currentPlayer].getHand().getTypeAt(n);
		q = m_playerList[m_currentPlayer].getHand().getQuantityAt(n);

		//if its the last card
		if(cursorLocked){
		if(c == m_playerList[m_currentPlayer].getHand().getColorAt(m_playerList[m_currentPlayer].getHand().getLastCard())
			&& t == m_playerList[m_currentPlayer].getHand().getTypeAt(m_playerList[m_currentPlayer].getHand().getLastCard()))
		{
			end = 1; //save the last card to be added last
			//otherwise the cursor lookat will be thrown off
		}
		else
		{end = 0;}}
		//for all duplicate cards
		while(q > end)
		{
			tracker.add(loc, t, c);
			//add to tracking deck
			q--;
			loc++;
		}
	}

	//re-use 't' and 'c' variables to hold the color and type
	//of the card that the cursor is looking at
	t = tracker.getTypeAt(m_cursor.m_lookAt);
	c = tracker.getColorAt(m_cursor.m_lookAt);
	//reuse 'loc' to hold the location of the card in the deck
	loc = m_playerList[m_currentPlayer].getHand().getLocation(t, c);

	//if the cursor is locked, force the location to the last card
	if(cursorLocked)
	{loc = m_playerList[m_currentPlayer].getHand().getLastCard();}

	//if the card is legal
	if(!cardPlayed && isCardLegal(loc))
	{
		//first re-save type and color directly from deck
		t = m_playerList[m_currentPlayer].getHand().getTypeAt(loc);
		c = m_playerList[m_currentPlayer].getHand().getColorAt(loc);
		//then add the card to the pile
		m_playerList[m_currentPlayer].getHand().addFromHandToPile(loc, m_discardPile);
		//if the player hasn't won, set effects and clear flags
		if(!calcWin())
		{
			//clear the wild color
			m_wildColor = '0';
			//set card effect
			setCardEffect(t, c);
			//set flag for endTurn()
			cardPlayed = true;	
		}
		else //if win, set end effect
		{setEndEffect(t, c);}
		{}

		setCursorLimits(m_playerList[m_currentPlayer].getHand().getNumOfCards());
		clearArea(COLOR_WHITE, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);
	}

}
//check to make sure the card is legal to play
bool Game::isCardLegal(int card)
{
	//if type OR color match the last card (top card) in the pile
	if(m_playerList[m_currentPlayer].getHand().getTypeAt(card) == m_discardPile.getTypeAt(m_discardPile.getLastCard())
	|| m_playerList[m_currentPlayer].getHand().getColorAt(card) == m_discardPile.getColorAt(m_discardPile.getLastCard()))
	{
		return true;
	}
	//if the card is a wild
	else if(m_playerList[m_currentPlayer].getHand().getColorAt(card) == 'W')
	{
		//Wild +4's must be handled differently
		if(m_playerList[m_currentPlayer].getHand().getTypeAt(card) == '4')
		{
			//can only be played when they have no other playable cards
			return isWild4Legal();
		}
		else//a normal wild
			return true;
	}
	//if the last card is a wild (logic for this must be handled differently
	else if(m_discardPile.getColorAt(m_discardPile.getLastCard()) == 'W'
		&& m_playerList[m_currentPlayer].getHand().getColorAt(card) == m_wildColor)
	{
		return true;
	}
	else
	{
		return false;
	}
}
//set the effect of a card
void Game::setCardEffect(char type, char color)
{
	//look for wilds first
	switch(color)
	{
	//if wild
	case 'W':
		m_gamestate = STATE_WILD_SELECT;
		wildSelect = true;
		//if Wild Draw 4
		if(type == '4')
		{
			//draw 4 cards for the next player AND skips their turn
			int p;
			//find out who will recieve the cards
			if(m_direction == FORWARD)
			{
				//loop through players
				if(m_currentPlayer+1 < m_playerCount)
				{p = m_currentPlayer+1;}
				else
				{p = 0;}
			}
			else if(m_direction == BACKWARD)
			{
				if(m_currentPlayer-1 >= 0)
				{p = m_currentPlayer-1;}
				else
				{p = m_playerCount-1;}
			}
			//then deal cards
			for(int n = 0; n < 4; ++n)
			{
				if(okayToAddCard(p))
				{m_drawDeck.drawCard(m_playerList[p].getHand());}
			
			}
			skipEffect = true;
		}
	break;
	}

	switch(type)
	{
	//reverse
	case 'R':
		//change the direction of gameplay
		if(m_direction == FORWARD)
		{m_direction = BACKWARD;}
		else if(m_direction == BACKWARD)
		{m_direction = FORWARD;}
		break;
	//draw
	case 'D': //draw 2 cards for the next player AND skips their turn
		int p;
		//find out who will recieve the cards
		if(m_direction == FORWARD)
		{
			//loop through players
			if(m_currentPlayer+1 < m_playerCount)
			{p = m_currentPlayer+1;}
			else
			{p = 0;}
		}
		else if(m_direction == BACKWARD)
		{
			if(m_currentPlayer-1 >= 0)
			{p = m_currentPlayer-1;}
			else
			{p = m_playerCount-1;}
		}
		//then deal cards
		for(int n = 0; n < 2; ++n)
		{
			if(okayToAddCard(p))
			{m_drawDeck.drawCard(m_playerList[p].getHand());}
		
		}
		skipEffect = true;
		break;
	//skip
	case 'S': 
		//skip the next player
		//unlike the other actions, this one must wait until the endTurn()
		//before it can be executed
		skipEffect = true;
		break;
	}
}
//set the next player
void Game::calcNextPlayer()
{
	if(m_direction == FORWARD)
	{
		//loop through players
		if(m_currentPlayer+1 < m_playerCount)
		{m_currentPlayer++;}
		else
		{m_currentPlayer = 0;}
	}
	else if(m_direction == BACKWARD)
	{
		if(m_currentPlayer-1 >= 0)
		{m_currentPlayer--;}
		else
		{m_currentPlayer = m_playerCount-1;}
	}
}

//process input
void Game::processGameLogic()
{
	if(m_input != '0')
	{
		if(m_gamestate == STATE_PLAY)
		{
			processGamePlay();
		}
		else if(m_gamestate == STATE_WILD_SELECT)
		{
			processGameWild();
		}
		else if(m_gamestate == STATE_HOT_SEAT
			|| m_gamestate == STATE_MENU)
		{
			//space bar exits both these states
			//and resumes game play
			if(m_input == ' ')
			{
				m_gamestate = STATE_PLAY;
				clearArea(COLOR_WHITE, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);
			}
		}
		else if(m_gamestate == STATE_ROUND_OVER)
		{
			//space bar reloads the game for the next round
			if(m_input == ' ')
			{
				reload();
			}
		}
		m_input = '0';
	}

	clearUnos();  //clear any old UNOs
}
//process input for when the game is in play state
void Game::processGamePlay()
{
	int num = m_playerList[m_currentPlayer].getHand().getNumOfCards();

	switch(m_input)
		{
		//space bar
		case ' ': 
			//if player has not drawn yet this turn
			//and they have not played a card
			if(!hasDrawn && !cardPlayed && okayToAddCard(m_currentPlayer))
			{
				m_drawDeck.drawCard(m_playerList[m_currentPlayer].getHand());
				hasDrawn = true;
				//lock cursor to last card
				lockCursorToDrawnCard();
				
			}
			break;
		case 'w': moveCursor(MOVE_UP, num); break;
		case 's': moveCursor(MOVE_DOWN, num); break;
		case 'a': moveCursor(MOVE_LEFT, num); break;
		case 'd': moveCursor(MOVE_RIGHT, num); break;
		case '\r': addSelectedCardToPile(); break;
		//pass / end turn
		case 'p': endTurn(); break;
		case 'u': callUno(); break;	
		case 'x': failureToCallUno(); break;
		case 'm': m_gamestate = STATE_MENU; break;
		}
}
//process input for when the game is in wild state
void Game::processGameWild()
{
	if(wildSelect)
	{
		//only when on the wild selection screen
		switch(m_input)
		{
		case '1': //blue
			m_wildColor = 'B';			//set color
			m_gamestate = STATE_PLAY;	//resume game play
			break;
		case '2': //green
			m_wildColor = 'G';
			m_gamestate = STATE_PLAY;
			break;
		case '3': //red
			m_wildColor = 'R';
			m_gamestate = STATE_PLAY;
			break;
		case '4': //yellow
			m_wildColor = 'Y';
			m_gamestate = STATE_PLAY;
			break;
		case ' '://toggle view
			wildSelect = false;
			//clear the area
			clearArea(COLOR_WHITE, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);
			break;
		}
	}
	else if(m_input == ' ')
	{
		wildSelect = true;
		//clear the area
		clearArea(COLOR_WHITE, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);
	}

	//if the state has been changed
	if(m_gamestate == STATE_PLAY)
	{
		//clear the area
		clearArea(COLOR_WHITE, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);
	}
}
//draw the display for the wild state
void Game::drawWildSelection()
{
	clearArea(COLOR_BLACK, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);
	setcolor(COLOR_YELLOW, COLOR_BLACK); gotoxy(PLAYER_HAND_X+5, PLAYER_HAND_Y+1);
	printf("W");
	setcolor(COLOR_BLUE, COLOR_BLACK); gotoxy(PLAYER_HAND_X+6, PLAYER_HAND_Y+1);
	printf("I");
	setcolor(COLOR_RED, COLOR_BLACK); gotoxy(PLAYER_HAND_X+7, PLAYER_HAND_Y+1);
	printf("L");
	setcolor(COLOR_GREEN, COLOR_BLACK); gotoxy(PLAYER_HAND_X+8, PLAYER_HAND_Y+1);
	printf("D");
	setcolor(COLOR_WHITE, COLOR_BLACK); gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+1);
	printf("COLOR SELECT:");
	gotoxy(PLAYER_HAND_X+9, PLAYER_HAND_Y+3);
	printf("1 -");
	setcolor(COLOR_BLUE, COLOR_BLACK); gotoxy(PLAYER_HAND_X+13, PLAYER_HAND_Y+3);
	printf("BLUE");
	setcolor(COLOR_WHITE, COLOR_BLACK); gotoxy(PLAYER_HAND_X+9, PLAYER_HAND_Y+4);
	printf("2 -");
	setcolor(COLOR_GREEN, COLOR_BLACK); gotoxy(PLAYER_HAND_X+13, PLAYER_HAND_Y+4);
	printf("GREEN");
	setcolor(COLOR_WHITE, COLOR_BLACK); gotoxy(PLAYER_HAND_X+9, PLAYER_HAND_Y+5);
	printf("3 -");
	setcolor(COLOR_RED, COLOR_BLACK); gotoxy(PLAYER_HAND_X+13, PLAYER_HAND_Y+5);
	printf("RED");
	setcolor(COLOR_WHITE, COLOR_BLACK); gotoxy(PLAYER_HAND_X+9, PLAYER_HAND_Y+6);
	printf("4 -");
	setcolor(COLOR_YELLOW, COLOR_BLACK); gotoxy(PLAYER_HAND_X+13, PLAYER_HAND_Y+6);
	printf("YELLOW");
	setcolor(COLOR_WHITE, COLOR_BLACK); gotoxy(PLAYER_HAND_X+5, PLAYER_HAND_Y+8);
	printf("Enter the number to");
	gotoxy(PLAYER_HAND_X+7, PLAYER_HAND_Y+9);
	printf("select a color.");
	
	gotoxy(PLAYER_HAND_X+1, PLAYER_HAND_Y+11);
	printf("Press [SPACE] to toggle view");
	
}
//draw the display for the hot seat
void Game::drawHotSeat()
{
	clearArea(COLOR_BLACK, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);

	setcolor(COLOR_RED, COLOR_BLACK); gotoxy(PLAYER_HAND_X+8, PLAYER_HAND_Y+3);
	printf("%c", char(16));
	setcolor(COLOR_WHITE, COLOR_BLACK); gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+3);
	printf("HOT SEAT");
	setcolor(COLOR_RED, COLOR_BLACK); gotoxy(PLAYER_HAND_X+19, PLAYER_HAND_Y+3);
	printf("%c", char(17));

	setcolor(COLOR_WHITE, COLOR_BLACK); 
	gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+5);
	printf("PLAYER:");
	gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+6);
	m_playerList[m_currentPlayer].printName();

	gotoxy(PLAYER_HAND_X+1, PLAYER_HAND_Y+8);
	printf("Press [SPACEBAR] when ready");

}
//draw the display for the controls menu
void Game::drawControlsMenu()
{
	clearArea(COLOR_GRAY, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);

	setcolor(COLOR_BLACK, COLOR_GRAY); 
	gotoxy(PLAYER_HAND_X+7, PLAYER_HAND_Y+1);
	printf("CONTROLS MENU");

	gotoxy(PLAYER_HAND_X+3, PLAYER_HAND_Y+3);
	printf("KEY");
	gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+3);
	printf("ACTION");

	gotoxy(PLAYER_HAND_X+1, PLAYER_HAND_Y+4);
	printf("w,a,s,d");
	gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+4);
	printf("Move Cursor");
	gotoxy(PLAYER_HAND_X+1, PLAYER_HAND_Y+5);
	printf("[ENTER]");
	gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+5);
	printf("Play Card");
	gotoxy(PLAYER_HAND_X+1, PLAYER_HAND_Y+6);
	printf("[SPACE]");
	gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+6);
	printf("Draw Card");
	gotoxy(PLAYER_HAND_X+4, PLAYER_HAND_Y+7);
	printf("p");
	gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+7);
	printf("Pass / End Turn");
	gotoxy(PLAYER_HAND_X+4, PLAYER_HAND_Y+8);
	printf("u");
	gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+8);
	printf("Call UNO");
	gotoxy(PLAYER_HAND_X+4, PLAYER_HAND_Y+9);
	printf("x");
	gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+9);
	printf("Failure to Call UNO");

	gotoxy(PLAYER_HAND_X+1, PLAYER_HAND_Y+11);
	printf("Press [SPACEBAR] when ready");
	
}
//after a card is drawn, lock the cursor to that card
void Game::lockCursorToDrawnCard()
{
	int numOfCards = m_playerList[m_currentPlayer].getHand().getNumOfCards();
	//find and set original cursor limits
	setCursorLimits(numOfCards);

	//if two columns
	if(numOfCards > PLAYER_HAND_COL_LIMIT)
	{
		//moves cursor to the location to be locked
		m_cursor.m_x = m_cursor.m_maxX;
		m_cursor.m_y  = numOfCards - PLAYER_HAND_COL_LIMIT + m_cursor.m_minY-1;

	}
	else
	{
		//moves cursor to the location to be locked
		m_cursor.m_y = m_cursor.m_maxY;
	}

	//set look at
	m_cursor.m_lookAt = m_playerList[m_currentPlayer].getHand().getLastCard();

	//set flag to lock movement
	cursorLocked = true;
}
//deal penalties
void Game::penalty(int player)
{
	//draw cards
	for(int i = 0; i < PENALTY_AMOUNT; ++i)
	{
		if(okayToAddCard(player))
		{m_drawDeck.drawCard(m_playerList[player].getHand());}
	}
	//reset cursor limit after adding new cards
	setCursorLimits(m_playerList[m_currentPlayer].getHand().getNumOfCards());
}
//a player calls uno when they have one card left in their hand
void Game::callUno()
{
	if(!unoCalled)//this way they can only call UNO or recieve the
		//penalty once per turn
	{
		//make sure the player only has one card left
		if(m_playerList[m_currentPlayer].getHand().getNumOfCards() == 1)
		{
			m_playerList[m_currentPlayer].setUnoTrue();
		}
		else //otherwise penalize them
		{
			penalty(m_currentPlayer);
		}
		unoCalled = true;
	}
}
//a player calles that another player has failed to call uno
void Game::failureToCallUno()
{
	if(!unoFailed)//this way they can only call failure to call UNO 
		//or recieve the penalty once per turn
	{
		bool found = false;

		for(int i = 0; i < m_playerCount; ++i)
		{
			//if someone forgot to call uno
			if(m_playerList[i].getHand().getNumOfCards() == 1
				&& m_playerList[i].getUno() == false)
			{
				//penalize them
				if(i != m_currentPlayer)
				//can't call failure to call on yourself
				{
					penalty(i);
					found = true;
				}
			}
		}
		//if no one forgot to call uno
		if(!found)
		{
			//penalize the liar
			penalty(m_currentPlayer);
		}
		unoFailed = true;
	}
}
//returns if the player wants to quit
bool Game::wantToQuit()
{
	return m_wantsToQuit;
}
//after the player ends their turn
void Game::endTurn()
{
	if(skipEffect)
	{
		calcNextPlayer();  //this is function is called twice before the next player
							//has a chance to play, in effect, skipping that player
		skipEffect = false;
	}

	//calculate next player
	calcNextPlayer();
	//set state to hot seat
	if(m_gamestate != STATE_WILD_SELECT)
	{m_gamestate = STATE_HOT_SEAT;}
	
	//set flag so next player can draw a card
	hasDrawn = false;
	//set flag so next player can move the cursor
	cursorLocked = false;
	//set flag for next players end turn
	cardPlayed = false;
	unoCalled = false;
	unoFailed = false;

	setCursorLimits(m_playerList[m_currentPlayer].getHand().getNumOfCards());
}
//clear any old UNO flags
void Game::clearUnos()
{
	for(int i = 0; i < m_playerCount; ++i)
	{
		//if uno is set, but the do not have 1 card
		if(m_playerList[i].getUno() == true
			&& m_playerList[i].getHand().getNumOfCards() != 1)
		{
			//unset uno
			m_playerList[i].setUnoFalse();
		}
	}
}
//calculate if the current player has won
bool Game::calcWin()
{
	//if player has 0 cards left in hand
	if(m_playerList[m_currentPlayer].getHand().getNumOfCards() == 0)
	{
		//calculate player's score
		m_playerList[m_currentPlayer].incrementScore(calcScore());

		//if player has won the game
		if(m_playerList[m_currentPlayer].getScore() >= WINNING_SCORE)
		{
			//set game state
			m_gamestate = STATE_GAME_OVER;
		}
		else//otherwise, they just won the round
		{
			//set game state
			m_gamestate = STATE_ROUND_OVER;
			//increment round #
			m_round++;
		}	
		return true;//round is over
	}
	return false; //round continues...
}
//calculate and returns the score for the round
int Game::calcScore()
{
	int total = 0;
	//cycle through all players
	for(int i = 0; i < m_playerCount; ++i)
	{
		char c, t;
		int q;
		//for every different card
		for(int n = 0; n < m_playerList[i].getHand().getSize(); ++n)
		{
			//get the color, type, and quantity
			c = m_playerList[i].getHand().getColorAt(n);
			t = m_playerList[i].getHand().getTypeAt(n);
			q = m_playerList[i].getHand().getQuantityAt(n);

			//check for all duplicate cards
			while(q > 0)
			{
				total += scoreChart(c, t);//ADD DEFINITION
				q--;
			}

		}
	}
	return total;
}
//calculates and returns the score for each individual card
int Game::scoreChart(char color, char type)
{
	if(color == 'W')
	{
		return SCORE_WILD;
	}
	else
	{
		switch(type)
		{
		case '0': return SCORE_ZERO; break;
		case '1': return SCORE_ONE; break;
		case '2': return SCORE_TWO; break;
		case '3': return SCORE_THREE; break;
		case '4': return SCORE_FOUR; break;
		case '5': return SCORE_FIVE; break;
		case '6': return SCORE_SIX; break;
		case '7': return SCORE_SEVEN; break;
		case '8': return SCORE_EIGHT; break;
		case '9': return SCORE_NINE; break;
		case 'R': case 'D': case 'S':
			return SCORE_ACTION; break;
		}
	}
	return 0;
}
//draw the display for end of a round
void Game::drawRoundOver()
{
	clearArea(COLOR_GRAY, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);

	setcolor(COLOR_BLACK, COLOR_GRAY); 
	gotoxy(PLAYER_HAND_X+7, PLAYER_HAND_Y+1);
	printf("ROUND %d OVER", m_round);

	gotoxy(PLAYER_HAND_X+1, PLAYER_HAND_Y+3);
	printf("ROUND %d WINNER:", m_round);
	setcolor(COLOR_DARK_BLUE, COLOR_GRAY); 
	gotoxy(PLAYER_HAND_X+18, PLAYER_HAND_Y+3);
	m_playerList[m_currentPlayer].printName();
	setcolor(COLOR_BLACK, COLOR_GRAY); 
	gotoxy(PLAYER_HAND_X+10, PLAYER_HAND_Y+4);
	printf("SCORE:");
	setcolor(COLOR_DARK_RED, COLOR_GRAY); 
	gotoxy(PLAYER_HAND_X+18, PLAYER_HAND_Y+4);
	printf("%d", m_playerList[m_currentPlayer].getScore());

	setcolor(COLOR_BLACK, COLOR_GRAY);
	gotoxy(PLAYER_HAND_X+5, PLAYER_HAND_Y+6);
	printf("Don't slack off yet!");
	gotoxy(PLAYER_HAND_X+3, PLAYER_HAND_Y+7);
	printf("You must score over %d", WINNING_SCORE);
	gotoxy(PLAYER_HAND_X+8, PLAYER_HAND_Y+8);
	printf("to win the game.");


	gotoxy(PLAYER_HAND_X+1, PLAYER_HAND_Y+10);
	printf("Press [SPACEBAR] to continue");
}
//draw the display for the end of the game
void Game::drawGameOver()
{
	clearArea(COLOR_GRAY, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);

	setcolor(COLOR_BLACK, COLOR_GRAY); 
	gotoxy(PLAYER_HAND_X+9, PLAYER_HAND_Y+1);
	printf("GAME OVER");

	gotoxy(PLAYER_HAND_X+4, PLAYER_HAND_Y+3);
	printf("GAME WINNER:", m_round);
	setcolor(COLOR_DARK_BLUE, COLOR_GRAY); 
	gotoxy(PLAYER_HAND_X+17, PLAYER_HAND_Y+3);
	m_playerList[m_currentPlayer].printName();
	setcolor(COLOR_BLACK, COLOR_GRAY); 
	gotoxy(PLAYER_HAND_X+4, PLAYER_HAND_Y+4);
	printf("FINAL SCORE:");
	setcolor(COLOR_DARK_RED, COLOR_GRAY); 
	gotoxy(PLAYER_HAND_X+17, PLAYER_HAND_Y+4);
	printf("%d", m_playerList[m_currentPlayer].getScore());

	setcolor(COLOR_BLACK, COLOR_GRAY);
	gotoxy(PLAYER_HAND_X+6, PLAYER_HAND_Y+7);
	printf("CONGRATULATIONS");
	gotoxy(PLAYER_HAND_X+2, PLAYER_HAND_Y+8);
	printf("You are the UNO master!");
	


	gotoxy(PLAYER_HAND_X+5, PLAYER_HAND_Y+10);
	printf("Press 'q' to quit");
}
//reloads the game for the next round
void Game::reload()
{
	//shuffle pile back into deck
	m_drawDeck.shuffle(m_discardPile);

	//shuffle all hands back into deck
	for(int i = 0; i < m_playerCount; ++i)
	{
		m_drawDeck.shuffle(m_playerList[i].getHand());
	}

	//set direction of game play
	m_direction = FORWARD;
	//set state
	m_gamestate = STATE_HOT_SEAT;

	for(int i = 0; i < m_playerCount; ++i)
	{
		//deal cards
		for(int n = 0; n < INITIAL_HAND; ++n)
		{
			m_drawDeck.drawCard(m_playerList[i].getHand());
		
		}
	}

	//randomly elect Starting player
	m_currentPlayer = rand() % m_playerCount;

	//draw first card to the discard pile
	//make sure it is legal
	//NOTE: It is illegal to start with a WILD DRAW 4 card
	bool isLegal = false;
	while(!isLegal)
	{
		m_drawDeck.drawCard(m_discardPile);
		//if it is a Wild Draw 4
		if(m_discardPile.getTypeAt(m_discardPile.getLastCard()) == '4'
			&& m_discardPile.getColorAt(m_discardPile.getLastCard()) == 'W')
		{
			//not legal
			//shuffle back into deck
			m_drawDeck.shuffle(m_discardPile);
		}
		else//legal
		{
			isLegal = true;
		}
	}

	//after the first card is drawn, set its effect
	char t = m_discardPile.getTypeAt(m_discardPile.getLastCard());
	char c = m_discardPile.getColorAt(m_discardPile.getLastCard());
	setInitEffect(t, c);

	setCursorLimits(m_playerList[m_currentPlayer].getHand().getNumOfCards());
}
//set the effect for the initial card in the pile
void Game::setInitEffect(char type, char color)
{
	//look for wilds first
	switch(color)
	{
	//if wild
	case 'W':
		wildSelect = true;
		m_gamestate = STATE_WILD_SELECT;
		break;
	}

	switch(type)
	{
	//reverse
	case 'R':
		//change the direction of gameplay
		m_direction = BACKWARD;
		//and go to the next player
		calcNextPlayer();
		break;
	//draw
	case 'D': //draw 2 cards for the first player  AND skips their turn :(
		for(int n = 0; n < 2; ++n)
		{
			m_drawDeck.drawCard(m_playerList[m_currentPlayer].getHand());
		
		}
		calcNextPlayer();
		break;
	//skips the first player
	case 'S': 
		calcNextPlayer();
		break;
	}
}
//the the effect for the last card played in a round
void Game::setEndEffect(char type, char color)
{
	//next player must still draw for draw+2 and wild+4
	//calculate next player, without actually assigning the player
	int nextP;
	if(m_direction == FORWARD)
	{
		//loop through players
		if(m_currentPlayer+1 < m_playerCount)
		{nextP = m_currentPlayer+1;}
		else
		{nextP = 0;}
	}
	else if(m_direction == BACKWARD)
	{
		if(m_currentPlayer-1 >= 0)
		{nextP = m_currentPlayer-1;}
		else
		{nextP = m_playerCount-1;}
	}
	
	int effect = 0;
	//if it is Wild+4 or Draw+2, assign effect
	if(color == 'W' && type == '4')
		{effect = 4;}
	else if(type == 'D')
		{effect = 2;}
	//deal cards
	for(int n = 0; n < effect; ++n)
	{
		if(okayToAddCard(nextP))
		{m_drawDeck.drawCard(m_playerList[nextP].getHand());}
	
	}
}
//make sure its okay to add a card before it is placed into a players hand
bool Game::okayToAddCard(int player)
{
	//must limit the number of cards in a players hand, otherwise, they go
	//offscreen
	if(m_playerList[player].getHand().getNumOfCards()+1 > MAX_CARDS_PER_HAND)
	{
		return false;
	}
	else
	{
		//if after the next card is drawn, the draw pile is empty
		if(m_drawDeck.getNumOfCards()-1 == 0)
		{
			//shuffle all but the top card from the pile back into the deck
			m_drawDeck.shuffleMinusOne(m_discardPile.getLastCard(), m_discardPile);
		}
		return true;
	}
	
}
//check to see if it is legal to play a Wild +4 card
bool Game::isWild4Legal()
{
	//for all different cards
	for(int i = 0; i < m_playerList[m_currentPlayer].getHand().getSize(); ++i)
	{
		//if type OR color match the last card (top card) in the pile
		if(m_playerList[m_currentPlayer].getHand().getTypeAt(i) == m_discardPile.getTypeAt(m_discardPile.getLastCard())
		|| m_playerList[m_currentPlayer].getHand().getColorAt(i) == m_discardPile.getColorAt(m_discardPile.getLastCard()))
		{
			//they CANNOT play Wild +4
			return false;
		}
		//if its a normal wild
		else if(m_playerList[m_currentPlayer].getHand().getTypeAt(i) == '0'
		&& m_playerList[m_currentPlayer].getHand().getColorAt(i) == 'W')
		{
			//they CANNOT play Wild +4
			return false;
		}
	}
	//if no cards or playable
	return true;
}
//draw the color icons for wild cards to display what color has been selected
void Game::drawTopCardWildColor()
{
	char icon;
	//get color and icon
	switch(m_wildColor)
	{
	case '0': //no color selected
		setcolor(COLOR_BLACK, COLOR_BLACK);
		icon = ' ';
		break;
	case 'B': //blue
		setcolor(COLOR_BLUE, COLOR_BLACK);
		icon = 'B';
		break;
	case 'G': //green
		setcolor(COLOR_GREEN, COLOR_BLACK);
		icon = 'G';
		break;
	case 'R': //red
		setcolor(COLOR_RED, COLOR_BLACK);
		icon = 'R';
		break;
	case 'Y': //yellow
		setcolor(COLOR_YELLOW, COLOR_BLACK);
		icon = 'Y';
		break;
	}
	//print icon in all four corners of the top card
	gotoxy(TOPCARD_X1+2, TOPCARD_Y1+1); printf("%c", icon);
	gotoxy(TOPCARD_X2-2, TOPCARD_Y1+1); printf("%c", icon);
	gotoxy(TOPCARD_X1+2, TOPCARD_Y2-1); printf("%c", icon);
	gotoxy(TOPCARD_X2-2, TOPCARD_Y2-1); printf("%c", icon);
}