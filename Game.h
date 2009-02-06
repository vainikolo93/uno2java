#pragma once

//the game class draws and handles input
//as well as handling all logic for the rules
//associated with UNO
//such as, whether or not a player can play a
//card on the discard pile
//this game required extensive research into UNO's
//rule set and has been coded to portray them

#include <time.h> //srand
#include <windows.h>

#include "Deck.h"
#include "Player.h"
#include "Map.h"
#include "cli.h"
#include "cursor.h"
#include "gameDefines.h"
#include "trackingDeck.h"


class Game
{
private:
	Deck m_drawDeck, m_discardPile;
	Player * m_playerList;
	int m_playerCount;
	int m_currentPlayer;
	Map m_mask[MASK_NUM];
	cursor m_cursor;
	int m_direction;
	int m_gamestate;
	char m_input;
	char m_wildColor;
	bool hasDrawn, cursorLocked, cardPlayed, skipEffect, wildSelect;
	bool unoCalled, unoFailed;
	bool m_wantsToQuit;
	int m_round;
public:
	Game();
	Game(int a_playerCount, char * a_filename);
	Game(char * a_filename);
	void load(int a_playerCount, char * a_filename);
	void load(char * a_filename);
	int inputPlayerCount();
	void setup();
	void initDraw();
	void draw();
	void initDrawHUD();
	void drawHUD();
	void initDrawTopCard();
	void drawTopCard();
	void clearArea(int color, int x1, int x2, int y1, int y2);
	void loadMasks();
	void handleInput(char a_input);
	void printUnoLogo(int x, int y);
	void drawPlayerHand();
	void initDrawPlayerHand();
	void initText();
	void setCursorLimits(int numOfCards);
	void moveCursor(int direction, int numOfCards);
	void drawCursor();
	void addSelectedCardToPile();
	bool isCardLegal(int card);
	void setCardEffect(char type, char color);
	void calcNextPlayer();
	void processGameLogic();
	void processGamePlay();
	void processGameWild();
	void drawWildSelection();
	void drawHotSeat();
	void drawControlsMenu();
	void lockCursorToDrawnCard();
	void penalty(int player);
	void callUno();
	void failureToCallUno();
	bool wantToQuit();
	void endTurn();
	void clearUnos();
	bool calcWin();
	int calcScore();
	int scoreChart(char color, char type);
	void drawRoundOver();
	void drawGameOver();
	void reload();
	void setInitEffect(char type, char color);
	void setEndEffect(char type, char color);
	bool okayToAddCard(int player);
	bool isWild4Legal();
	void drawTopCardWildColor();

};