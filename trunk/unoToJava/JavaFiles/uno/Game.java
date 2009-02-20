package uno;

import java.awt.Graphics;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


//handles the data for the cursor
class cursor
{
	public int m_x, m_y;
	public int m_minX, m_maxX, m_minY, m_maxY;
	char m_icon;
	int m_lookAt;
}

@SuppressWarnings("serial")
public class Game extends JFrame implements ActionListener
{	
	private Deck m_drawDeck, m_discardPile;
	private Player[] m_playerList;
	private int m_playerCount;
	public int m_currentPlayer;
	private int m_direction;
	private int m_gamestate;
	private char m_input;
	private char m_wildColor;
	private boolean hasDrawn, cursorLocked, cardPlayed, skipEffect, wildSelect;
	private boolean m_wantsToQuit;
	private boolean unoCalled, unoFailed;
	int m_round;
	JFrame jf = new JFrame("title");
	
	
	//default constructor
	public Game()
	{
		m_playerCount = m_currentPlayer = -1;
		m_input = '0';
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
			//TODO REMOVE THIS DEBUG
			m_playerList[i].getHand().debug();
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
		
		//TODO Finish cursor limits
		//m_cursor.m_icon = char(16);
		//setCursorLimits(m_playerList[m_currentPlayer].getHand().getNumOfCards());
	
		//TODO REMOVE THIS DEBUG
		System.out.println("Starting Player: " + m_currentPlayer + " Card Type: " + t +
				" Card Color: " + c + "\n");
		
	}
	//TODO loadMasks()
	
	//TODO handleInput()
	//handles input
	public void handleInput(KeyEvent e)
	//public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyChar())
		{
			//quit
		case 'q': m_wantsToQuit = true; break;  

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
			m_input = e.getKeyChar(); processGameLogic(); break;
		}
		
	}
	
	//TODO CURSOR setCursorLimit(), moveCursor(), lockCursorToDrawnCard()
	
	//add the card the cursor is pointing to, to the pile
	//TODO ADD CARD addSelectedCardToPile()
	
	//process input
	public void processGameLogic()
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
					//TODO CLEAR AREA
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
		
		clearUnos(); //clear any old UNOs
	}
	//process input for when the game is in play state
	public void processGamePlay()
	{
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
				//TODO REMOVE THIS DEBUG
				m_playerList[m_currentPlayer].getHand().debug();
				//TODO CURSOR
				//lock cursor to last card
				//lockCursorToDrawnCard();
				
			}
			break;
		//TODO CURSOR
		//case 'w': moveCursor(MOVE_UP, num); break;
		//case 's': moveCursor(MOVE_DOWN, num); break;
		//case 'a': moveCursor(MOVE_LEFT, num); break;
		//case 'd': moveCursor(MOVE_RIGHT, num); break;
		//TODO ADD CARD
		//case '\r': addSelectedCardToPile(); break;
		//pass / end turn
		case 'p': endTurn(); break;
		case 'u': callUno(); break;	
		case 'x': failureToCallUno(); break;
		case 'm': m_gamestate = STATE_MENU; break;
		}
	}
	//process input for when the game is in wild state
	public void processGameWild()
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
				//TODO CLEAR
				//clearArea(COLOR_WHITE, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);
				break;
			}
		}
		else if(m_input == ' ')
		{
			wildSelect = true;
			//clear the area
			//TODO CLEAR
			//clearArea(COLOR_WHITE, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);
		}

		//if the state has been changed
		if(m_gamestate == STATE_PLAY)
		{
			//clear the area
			//TODO CLEAR
			//clearArea(COLOR_WHITE, PLAYER_HAND_X-1, SCREEN_WIDTH-2, PLAYER_HAND_Y, SCREEN_HEIGHT-2);
		}
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
		//reset cursor limit after adding new cards
		//TODO CURSOR
		//setCursorLimits(m_playerList[m_currentPlayer].getHand().getNumOfCards());
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
		
		//TODO CURSOR
		//setCursorLimits(m_playerList[m_currentPlayer].getHand().getNumOfCards());
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
		System.out.println("I broke shuffling discard into deck");
		m_drawDeck.shuffle(m_discardPile);
		
		//shuffle all hands back into deck
		for(int i = 0; i < m_playerCount; ++i)
		{
			System.out.println("I broke shuffling hands into deck");
			m_drawDeck.shuffle(m_playerList[i].getHand());
		}
		
		//set direction of game player
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
		
		//TODO Finish cursor limits
		//m_cursor.m_icon = char(16);
		//setCursorLimits(m_playerList[m_currentPlayer].getHand().getNumOfCards());
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
		else if (m_direction == BACKWARD)
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
			{m_drawDeck.drawCard(m_playerList[nextP].getHand());}
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

	public void draw(Graphics g)
	{
		jf.repaint();
		
	}

	public NeatWindow getUI()
	{
		return m_ui;
	}
	/*public void draw(Graphics g)
	{
		System.out.println("Testing");
		g.drawString("Testing", 10, 10);
		int xx = m_playerList[m_currentPlayer].getHand().getLastCard();
		g.drawString("Last Card" + xx, 10, 20);
	}*/
	//fail safe
	public Game(int a_playerCount)
	{
		this();
		
		//m_playerCount = a_playerCount;
		
		m_drawDeck = new Deck();
		m_drawDeck.loadMcNasty();
		m_discardPile = new Deck(m_drawDeck);
		//m_playerList = new Player [m_playerCount];
		
		//for(int n = 0; n < m_playerCount; ++n)
		//{
		//	m_playerList[n] = new Player(m_drawDeck);
		//}
	}

	public void gamesetupdraw()
	{
		//http://chortle.ccsu.edu/CS151/Notes/chap58/ch58_13.html
		
		JButton playerb[] = new JButton[9];
		jf.setSize(400, 200);
		int buttonnum = 2;
		for(int i = 0; i < 9; ++i)
		{
		playerb[i] = new JButton(" " + buttonnum);
		playerb[i].addActionListener( this);
		jf.getContentPane().add( playerb[i] );
		buttonnum++;
		}
		jf.getContentPane().setLayout(new FlowLayout());
			
		jf.getContentPane().add(m_ui);
		
		
		// need to be very specific about key listening...
		jf.addKeyListener(m_ui);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		
		
		
	}
	
	public int getPlayerCount()
	{
		return m_playerCount;
	}

	public Player getPlayerAt(int n)
	{
		return m_playerList[n];
	}
	
	public void drawHand()

	{

		int current = m_discardPile.getLastCard();
		System.out.print("\n\n\n");
		System.out.print(m_discardPile.getColorAt(current));
		System.out.print(" ");
		System.out.print(m_discardPile.getTypeAt(current));
		System.out.print("\n =)");
		JPanel buttons = new JPanel();

		Player player = m_playerList[m_currentPlayer];
		Button cards[] = new Button[player.getHand().getSize()];
		
		
		int i=0;
		
		//sets the color for each card
		for(i=0; i<player.getHand().getSize(); ++i)
		{
			cards[i] = new Button("" + player.getHand().getColorAt(i) 
					+ " " + player.getHand().getTypeAt(i));
			switch(player.getHand().getColorAt(i))
			{
			case 'B':	cards[i].setBackground(new Color(0, 0, 255));	
						cards[i].setForeground(new Color(0, 0, 0));	break;
			case 'R':	cards[i].setBackground(new Color(255, 0, 0));	
						cards[i].setForeground(new Color(0, 0, 0));	break;
			case 'G':	cards[i].setBackground(new Color(0, 255, 0));	
						cards[i].setForeground(new Color(0, 0, 0));	break;
			case 'Y':	cards[i].setBackground(new Color(255, 255, 0));	
						cards[i].setForeground(new Color(0, 0, 0));	break;
			case 'W':	cards[i].setBackground(new Color(0, 0, 0));	
						cards[i].setForeground(new Color(255, 255, 255));	break;
			}
			cards[i].addActionListener(this);
			cards[i].setPreferredSize(new Dimension(75, 20));
			buttons.add(cards[i]);
		}
		
		//sets the layout
		buttons.setLayout(new BoxLayout(buttons, 1));
		/*
		//sets the action for each card
		for(i=0; i<player.getHand().getSize(); ++i)
		{
			currentCard = i;
			
			//sets the action
			cards[i].setAction(
					new AbstractAction("" + player.getHand().getColorAt(i) 
					+ "" + player.getHand().getTypeAt(i))
					{
						public void actionPerformed(ActionEvent e) 
						{
							
							
							String str = e.getActionCommand();
							int temp = 0;
							int color = str.charAt(0);
							int type = str.charAt(1);
							
							//finds out which card it is
							for(int i=0; i<m_playerList[m_currentPlayer].getHand().getSize(); ++i)
							{
								if(m_playerList[m_currentPlayer].getHand().getColorAt(i) == color
								&& m_playerList[m_currentPlayer].getHand().getTypeAt(i) == type)
								{
									temp = i;
									i=m_playerList[m_currentPlayer].getHand().getSize();
								}
							}
							
							//checks to see if the card is legal
							if(isCardLegal(new Integer(temp)))
							{
								//play card function here
								System.out.print("Yes, you can play the card at location ");
							}
							else
							{
								System.out.print("No, you can't play the card at location ");
							}
							

							System.out.print(temp);
							System.out.print("\n");
						}
					}
			);
		}
		
		jf.getContentPane().add(buttons);
		*/
	}
	//private int currentCard;
	
	//@Override
	public void actionPerformed(ActionEvent e)
	{
		 String str = e.getActionCommand();
		 
		if (str.equals(" 2")){ m_playerCount = 2; System.out.println("2222222");}
		else if (str.equals(" 3")){ m_playerCount = 3;}
		else if (str.equals(" 4")){ m_playerCount = 4;}
		else if (str.equals(" 5")){ m_playerCount = 5;}
		else if (str.equals(" 6")){ m_playerCount = 6;}
		else if (str.equals(" 7")){ m_playerCount = 7;}
		else if (str.equals(" 8")){ m_playerCount = 8;}
		else if (str.equals(" 9")){ m_playerCount = 9;}
		else if (str.equals(" 10")){ m_playerCount = 10;}
		 
		jf.getContentPane().removeAll();
		jf.repaint();

		//http://leepoint.net/notes-java/GUI/components/30textfield/11textfield.html
		JTextField textnamebox = new JTextField(10);
		jf.getContentPane().add(textnamebox);
		
		textnamebox.addActionListener(
			new ActionListener()
			{
		        public void actionPerformed(ActionEvent e)
		        {
		        	for(int i = 0; i < m_playerCount; ++i)
		    		{
		        		//m_playerList[0].setNameBetter(getText());
		        		m_playerList[0].printName();
			        	jf.repaint();
			            //TODO
		    		}
		        }
		    });
		
		m_playerList[0].setNameBetter(textnamebox.getText());
		
		m_playerList[0].printName();
		
		jf.repaint();
		 
		//System.out.println(m_playerCount);
		//draw();
		//System.out.println(str);
		//System.out.println("I pushed a button");
		
		
		drawHand();
	}

	public boolean getCardPlayed()
	{
		return cardPlayed;
	}
	public void setCardPlayed(boolean setAs)
	{
		cardPlayed = setAs;
	}
	public char getWildColor()
	{
		return m_wildColor;
	}
	public void setWildColor(char col)
	{
		m_wildColor = col;
	}
	public boolean getHasDrawn()
	{
		return hasDrawn;
	}
	public void setHasDrawn(boolean setAs)
	{
		hasDrawn = setAs;
	}
	

	
	public Deck getDiscardPile()
	{
		return m_discardPile;
	}
	public Deck getDrawDeck()
	{
		return m_drawDeck;
	}
	public Player getPlayer(int index)
	{
		return m_playerList[index];
	}
	
	public Player getCurrentPlayer()
	{
		return m_playerList[m_currentPlayer];
	}
	public int getCurrentPlayerLoc()
	{
		return m_currentPlayer;
	}
	public boolean getCursorLock()
	{
		return cursorLocked;
	}
	public boolean isGameInWildState()
	{
		if(m_gamestate == STATE_WILD_SELECT)
		{return true;}
		return false;
	}
	public boolean isGameInRoundOverState()
	{
		if(m_gamestate == STATE_ROUND_OVER)
		{return true;}
		return false;
	}
	public boolean isGameInGameOverState()
	{
		if(m_gamestate == STATE_GAME_OVER)
		{return true;}
		return false;
	}
	public boolean isGameInHotSeatState()
	{
		if(m_gamestate == STATE_HOT_SEAT)
		{return true;}
		return false;
	}
	public void setGamePlay()
	{
		m_gamestate = STATE_PLAY;
	}
	public int getRoundCount()
	{
		return m_round;
	}
	public void setCursorLock(boolean setAs)
	{
		cursorLocked = setAs;
	}
	public boolean getUnoCalled()
	{
		return unoCalled;
	}
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
