#pragma once

//in order to eliminate magic numbers....

#define INITIAL_HAND			7		//size of each player's initial hand
#define MAX_CARDS_PER_HAND		24		//need to limit the number of cards in
											//a players hand, or else
											//the run offscreen

#define MASK_NUM				15		//number of masks
#define	MASK_REV				10		//array location of the REVERSE mask
#define MASK_DRAW				11		//array location of the DRAW mask
#define MASK_SKIP				12		//array location of the SKIP mask
#define MASK_WILD				13		//array location of the WILD mask
#define MASK_WILD4				14		//array location of the WILD +4 mask

#define GUI_WIDTH				80		//width of the HUD
#define GUI_HEIGHT				10		//height of the HUD
#define PLAYER_COL_SIZE			16		//column size for HUD
#define PLAYER_ROW_SIZE			5		//row size for HUD
#define PLAYER_PRINT_OFFSET		5		//offset to print from column
#define PLAYER_PRINT_ROW_1		1		//first row of names in HUD
#define PLAYER_PRINT_ROW_2		6		//second row of names
#define PLAYER_SCORE_OFFSET_X	7		//offset to print score from column
#define PLAYER_SCORE_OFFSET_Y	3		//offset to print score from row
#define PLAYER_UNO_OFFSET_X		10		//offset to print UNO from column
#define PLAYER_CARD_OFFSET_X	13		//max number of cards per row in HUD
#define TOPCARD_X1				5		//X and Y for the top card in the pile
#define TOPCARD_X2				18
#define TOPCARD_Y1				12
#define TOPCARD_Y2				23
#define UNO_LOGO_X				20		//Top X and Y for the UNO logo
#define UNO_LOGO_Y				12
#define SCREEN_WIDTH			80		//dimensions of the DOS console window
#define SCREEN_HEIGHT			24
#define PLAYER_HAND_X           49		//location where to start displaying cards
#define PLAYER_HAND_Y			11				//from the player's hand
#define PLAYER_HAND_OFFSET_X	16		//location for the second column
#define PLAYER_HAND_COL_LIMIT	12		//max number of rows per column

#define MOVE_UP					0		//direction of cursor movement
#define MOVE_DOWN				1
#define MOVE_LEFT				2
#define MOVE_RIGHT				3

#define FORWARD					0		//direction of gameplay
#define BACKWARD				1		

#define STATE_PLAY				0		//game states
#define STATE_WILD_SELECT		1
#define STATE_HOT_SEAT			2
#define STATE_MENU				3
#define STATE_ROUND_OVER		4
#define STATE_GAME_OVER			5

#define PENALTY_AMOUNT			2		//number of cards that are drawn for penalties

#define WINNING_SCORE			500		//score required to win the game

#define SCORE_WILD				50		//points for each card type
#define SCORE_ACTION			20
#define SCORE_ZERO				0
#define SCORE_ONE				1
#define SCORE_TWO				2
#define SCORE_THREE				3
#define SCORE_FOUR				4
#define SCORE_FIVE				5
#define SCORE_SIX				6
#define SCORE_SEVEN				7
#define SCORE_EIGHT				8
#define SCORE_NINE				9


