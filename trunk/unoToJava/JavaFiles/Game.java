

import java.io.IOException;
import java.util.Random;


//handles the data for the cursor
class cursor
{
	public int m_x, m_y;
	public int m_minX, m_maxX, m_minY, m_maxY;
	char m_icon;
	int m_lookAt;
}

public class Game
{	
	private Deck m_drawDeck, m_discardPile;
	private Player[] m_playerList;
	private int m_playerCount;
	public int m_currentPlayer;
	private int m_direction;
	private int m_gamestate;
	private char m_wildColor;
	@SuppressWarnings("unused")
	private boolean hasDrawn, cursorLocked, cardPlayed, skipEffect, wildSelect;
	private boolean m_wantsToQuit;
	private boolean unoCalled, unoFailed;
	int m_round;
	
	
	//default constructor
	public Game()
	{
		m_playerCount = m_currentPlayer = -1;
		m_wildColor = '0';
		m_round = 0;
		hasDrawn = cursorLocked = cardPlayed = skipEffect = false;
		wildSelect = true;
		m_wantsToQuit = false;
		unoCalled = unoFailed = false;
		
		m_ui = new NeatWindow(this); //the UI for this game
		
	}
	//constructor that loads based off predefined player count and file name
	public Game(int a_playerCount, String a_filename) throws IOException
	{
		this();
		
		load(a_playerCount, a_filename);
	}
	//constructor that loads based off predefined  file name

	//loads based off a predefined player count and file name
	public void load(int a_playerCount, String a_filename) throws IOException
	{
		m_playerCount = a_playerCount;
		
		m_drawDeck = new Deck(a_filename);
		m_discardPile = new Deck(m_drawDeck);
		m_playerList = new Player [m_playerCount];
		m_currentPlayer = 1;
		//for(int n = 0; n < m_playerCount; ++n)
		//{
		//	m_playerList[n] = new Player(m_drawDeck);
		//}
		

		//setup(a_playerCount);

	}
	//determines player count
	//loads off a predefined filename

	//inputs a valid player number

	//set player count
	public void setPlayerCount(int a_p)
	{
		m_playerCount = a_p;
	}
	
	public void setPlayerNames(String a_n[])
	{
		for(int i = 0; i < m_playerCount; ++i)
		{
			m_playerList[i].setNameBetter(a_n[i]);
		}
		
	}
	
	public String getPlayerName(int a_p)
	{
		return m_playerList[a_p].getName();
	}
	//setup initial player data and deal hands

	//setup that takes player count as a parameter
	public void setup(int players)
	{
		//set direction of game play
		m_direction = FORWARD;
		//set state
		m_gamestate = STATE_HOT_SEAT;
		
		//create players
		m_playerCount = players;
		m_playerList = new Player [m_playerCount];
		
		
		for(int n = 0; n < m_playerCount; ++n)
		{
			m_playerList[n] = new Player(m_drawDeck);
		}
		
		for(int i = 0; i < m_playerCount; ++i)
		{
			
			//deal cards
			for(int n = 0; n < INITIAL_HAND; ++n)
			{
				m_drawDeck.drawCard(m_playerList[i].getHand());
			}
		}
		
		//randomly select starting player
		Random rand = new Random();
		m_currentPlayer = rand.nextInt(m_playerCount);
		
		//draw first card to the discard pile
		//make sure it is legal
		//NOTE: It is illegal to start with a wild DRAW 4 card
		boolean isLegal = false;
		while (!isLegal)
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
			else //legal
			{
				isLegal = true;
			}
		}
		
		//after the first card is drawn, set its effect
		char t = m_discardPile.getTypeAt(m_discardPile.getLastCard());
		char c = m_discardPile.getColorAt(m_discardPile.getLastCard());
		setInitEffect(t, c);		
	}
	
	//check to make sure the card is legal to play
	public boolean isCardLegal(int card)
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
	public void setCardEffect(char type, char color)
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
				int p = -1;//set to invalid
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
			int p = -1; //set to invalid
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
	public void calcNextPlayer()
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
	//deal penalties
	public void penalty(int player)
	{
		//draw cards
		for(int i = 0; i < PENALTY_AMOUNT; ++i)
		{
			if(okayToAddCard(player))
			{m_drawDeck.drawCard(m_playerList[player].getHand());}
		}
	}
	//a player calls uno when they have one card left in their hand
	public void callUno()
	{
		if(!unoCalled)//this way they can only call UNO or receive the 
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
	//a player calls that another player has failed to call uno
	public void failureToCallUno()
	{
		if(!unoFailed)//this way the can only call failure to call UNO
			//or receive the penalty once per turn
		{
			boolean found = false;
			
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
	public boolean wantsToQuit()
	{
		return m_wantsToQuit;
	}
	//after the player ends their turn
	public void endTurn()
	{
		if(skipEffect)
		{
			calcNextPlayer(); //this function is called twice before the next player
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
		//free movement for next player
		cursorLocked = false;
		//set flag for the next players end turn
		cardPlayed = false;
		unoCalled = false;
		unoFailed = false;

	}
	//clear any old UNO flags
	public void clearUnos()
	{
		for(int i = 0; i < m_playerCount; ++i)
		{
			//if the uno is set, but they do not have 1 card
			if(m_playerList[i].getUno() == true
					&& m_playerList[i].getHand().getNumOfCards() != 1)
			{
				//unset uno
				m_playerList[i].setUnoFalse();
			}
		}
	}
	//calculate if the current player has won
	public boolean calcWin()
	{
		//if player has 0 cards left in hand
		if(m_playerList[m_currentPlayer].getHand().getNumOfCards() == 0)
		{
			//calculate player's score
			m_playerList[m_currentPlayer].incrementScore(calcScore());
			
			//if the player has won the game
			if(m_playerList[m_currentPlayer].getScore() >= WINNING_SCORE)
			{
				//set the game state
				m_gamestate = STATE_GAME_OVER;
			}
			else //otherwise, they just won the round
			{
				//set game state
				m_gamestate = STATE_ROUND_OVER;
				//increment round #
				m_round++;
			}
			return true;//round is over
		}
		return false; //round continues
	}
	//calculates and returns the score for the round
	public int calcScore()
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
				//get the color, type and quantity
				c = m_playerList[i].getHand().getColorAt(n);
				t = m_playerList[i].getHand().getTypeAt(n);
				q = m_playerList[i].getHand().getQuantityAt(n);
				
				//check for all duplicate cards
				while(q > 0)
				{
					total += scoreChart(c, t);//add score
					q--;
				}
			}
		}
		return total;
	}
	//calculates and returns the score for each individual card
	public int scoreChart(char color, char type)
	{
		if(color == 'W')
		{
			return SCORE_WILD;
		}
		else
		{
			switch(type)
			{
			case '0': return SCORE_ZERO;
			case '1': return SCORE_ONE;
			case '2': return SCORE_TWO; 
			case '3': return SCORE_THREE; 
			case '4': return SCORE_FOUR; 
			case '5': return SCORE_FIVE;
			case '6': return SCORE_SIX; 
			case '7': return SCORE_SEVEN; 
			case '8': return SCORE_EIGHT; 
			case '9': return SCORE_NINE; 
			case 'R': case 'D': case 'S':
				return SCORE_ACTION; 
			}
		}
		return 0; //default
	}
	//reloads the game for the next round
	public void reload()
	{
		//shuffle the pile back into the deck
		//Shuffle function was causing out of bounds array issues in Java
				//m_drawDeck.shuffle(m_discardPile);
		//therefore we reload the draw deck
		m_drawDeck.loadMcNasty();
				//m_discardPile.load(m_drawDeck);
		//and clear the other decks
		m_discardPile.clearAll();
		
		//shuffle all hands back into deck
		for(int i = 0; i < m_playerCount; ++i)
		{
					//m_drawDeck.shuffle(m_playerList[i].getHand());
					//m_playerList[i].load(m_drawDeck);
			//clear hands
			m_playerList[i].getHand().clearAll();
		}
		
		//set direction of game player
		m_direction = FORWARD;
		//set state
		m_gamestate = STATE_HOT_SEAT;
		
		for(int i = 0; i < m_playerCount; ++i)
		{
			//deal cards
					//for(int n = 0; n < INITIAL_HAND; ++n)
			while(m_playerList[i].getHand().getNumOfCards() < INITIAL_HAND)
			{
				m_drawDeck.drawCard(m_playerList[i].getHand());
			}
		}
		
		//randomly select starting player
		Random rand = new Random();
		m_currentPlayer = rand.nextInt(m_playerCount);
		
		//draw first card to the discard pile
		//make sure it is legal
		//NOTE: It is illegal to start with a wild DRAW 4 card
		boolean isLegal = false;
		while (!isLegal)
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
			else //legal
			{
				isLegal = true;
			}
		}
		
		//after the first card is drawn, set its effect
		char t = m_discardPile.getTypeAt(m_discardPile.getLastCard());
		char c = m_discardPile.getColorAt(m_discardPile.getLastCard());
		setInitEffect(t, c);
	}
	//set the effect for the initial card in the pile
	public void setInitEffect(char type, char color) 
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
		case 'D': //draw 2 cards for the first player AND skips their turn :(
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
	//set the effect for the last card played in a round
	public void setEndEffect(char type, char color)
	{
		//next player must still draw+2 and wild+4
		//calculate the next player, without actually assigning the player
		int nextP = -1; //set to invalid
		if(m_direction == FORWARD)
		{
			//loop through players
			if(m_currentPlayer+1 < m_playerCount)
			{nextP = m_currentPlayer+1;}
			else
			{nextP = 0;}
		}
		if (m_direction == BACKWARD)
		{
			if(m_currentPlayer-1 >= 0)
			{nextP = m_currentPlayer-1;}
			else
			{nextP = m_currentPlayer-1;}
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
			{
				//get the color and type of the last card
				char c = m_playerList[nextP].getHand().getColorAt(m_playerList[nextP].getHand().getLastCard());
				char t = m_playerList[nextP].getHand().getTypeAt(m_playerList[nextP].getHand().getLastCard());
				//add card
				m_drawDeck.drawCard(m_playerList[nextP].getHand());
				//increment score
				m_playerList[m_currentPlayer].incrementScore(scoreChart(c, t));
				
			}
		}
	}
	//make sure it is okay to add a card before it is placed into a player's hand
	public boolean okayToAddCard(int player)
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
			if(m_drawDeck.getNumOfCards()-1 ==0)
			{
				//shuffle all but the top card from the pile back into the deck
				m_drawDeck.shuffleMinusOne(m_discardPile.getLastCard(), m_discardPile);
			}
			return true;
		}
	}
	//check to see if it is legal to play a Wild+4 card
	public boolean isWild4Legal()
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
	

	//new functions to run the game in an applet
	NeatWindow m_ui; //display the object and user interface

	public NeatWindow getUI()
	{
		return m_ui;
	}
	//fail safe
	public Game(int flag)
	{
		//ignore flag, it is only used to call this contructor
		this();

		m_drawDeck = new Deck();
		m_drawDeck.loadMcNasty();
		m_discardPile = new Deck(m_drawDeck);
	}
	//returns player count
	public int getPlayerCount()
	{
		return m_playerCount;
	}
	//returns the player at this location in the player list
	public Player getPlayerAt(int n)
	{
		return m_playerList[n];
	}
	//returns whether or not a card has been played this turn
	public boolean getCardPlayed()
	{
		return cardPlayed;
	}
	//sets whether or not a card has been played this turn
	public void setCardPlayed(boolean setAs)
	{
		cardPlayed = setAs;
	}
	//get the current wild selection color
	public char getWildColor()
	{
		return m_wildColor;
	}
	//sets the current wild selection color
	public void setWildColor(char col)
	{
		m_wildColor = col;
	}
	//returns whether or not the player has drawn a card this turn
	public boolean getHasDrawn()
	{
		return hasDrawn;
	}
	//sets whether or not the player has drawn a card this turn
	public void setHasDrawn(boolean setAs)
	{
		hasDrawn = setAs;
	}
	//returns the discard pile
	public Deck getDiscardPile()
	{
		return m_discardPile;
	}
	//returns the draw deck
	public Deck getDrawDeck()
	{
		return m_drawDeck;
	}
	//returns the current player
	public Player getCurrentPlayer()
	{
		return m_playerList[m_currentPlayer];
	}
	//returns the location of the current player in the player list
	public int getCurrentPlayerLoc()
	{
		return m_currentPlayer;
	}
	//gets whether or not the "cursor" is locked
	public boolean getCursorLock()
	{
		return cursorLocked;
	}
	//returns true if in the WILD SELECT state
	public boolean isGameInWildState()
	{
		if(m_gamestate == STATE_WILD_SELECT)
		{return true;}
		return false;
	}
	//returns true if in the ROUND OVER state
	public boolean isGameInRoundOverState()
	{
		if(m_gamestate == STATE_ROUND_OVER)
		{return true;}
		return false;
	}
	//returns true if in the GAME OVER state
	public boolean isGameInGameOverState()
	{
		if(m_gamestate == STATE_GAME_OVER)
		{return true;}
		return false;
	}
	//returns true if in the HOT SEAT state
	public boolean isGameInHotSeatState()
	{
		if(m_gamestate == STATE_HOT_SEAT)
		{return true;}
		return false;
	}
	//set game state to PLAY
	public void setGamePlay()
	{
		m_gamestate = STATE_PLAY;
	}
	//returns the round count
	public int getRoundCount()
	{
		return m_round;
	}
	//sets whether or not the "cursor" is locked
	public void setCursorLock(boolean setAs)
	{
		cursorLocked = setAs;
	}
	//return whether or not the player has called uno this turn
	public boolean getUnoCalled()
	{
		return unoCalled;
	}
	//return whether or not the player has called failure to call uno this turn
	public boolean getUnoFailed()
	{
		return unoFailed;
	}

	/**
	 * "#defines"
	 * we don't like magic numbers
	 */
	static final int INITIAL_HAND = 7; 			//size of each player's initial hand
	static final int MAX_CARDS_PER_HAND = 24; 	//limit the number of cards in a players
												//hand so they do not run offscreen
	
	static final int MASK_NUM = 15;				//number of masks
	static final int MASK_REV = 10;				//array location of the REVERSE mask
	static final int MASK_DRAW = 11;			//array location of the DRAW mask
	static final int MASK_SKIP = 12;			//array location of the SKIP mask
	static final int MASK_WILD = 13;			//array location of the WILD mask
	static final int MASK_WILD4 = 14;			//array location of the WILD +4 mask
	
	static final int MOVE_UP = 0;				//direction of cursor movement
	static final int MOVE_DOWN = 1;
	static final int MOVE_LEFT = 2;
	static final int MOVE_RIGHT = 3;
	
	static final int FORWARD = 0;				//direction of gameplay
	static final int BACKWARD = 1;
	
	static final int STATE_PLAY = 0;			//game states
	static final int STATE_WILD_SELECT = 1;
	static final int STATE_HOT_SEAT = 2;
	static final int STATE_MENU = 3;
	static final int STATE_ROUND_OVER = 4;
	static final int STATE_GAME_OVER = 5;
	
	static final int PENALTY_AMOUNT = 2;		//number of cards that are drawn for penalties
	
	static final int WINNING_SCORE = 500;		//score required to win the game
	
	static final int SCORE_WILD = 50;			//points for each card type
	static final int SCORE_ACTION = 20;
	static final int SCORE_ZERO = 0;
	static final int SCORE_ONE = 1;
	static final int SCORE_TWO = 2;
	static final int SCORE_THREE = 3;
	static final int SCORE_FOUR = 4;
	static final int SCORE_FIVE = 5;
	static final int SCORE_SIX = 6;
	static final int SCORE_SEVEN = 7;
	static final int SCORE_EIGHT = 8;
	static final int SCORE_NINE = 9;

	
}
