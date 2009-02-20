import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.Label;
import java.awt.TextField;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;



public class GameApplet extends Applet implements Runnable

{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Game m_game;
	Button player[];
	
	Button cards[];
	Button action[];
	boolean cardsSet = false;

	Label inputLabel;
	TextField nameBlock;

	static final int buttonCount = 9;
	int numOfPlayers;



	String PlayerNames[];
	int playerCounter = 0;
	boolean runOnce = true;

	boolean playerSet = false;
	boolean Setupdone = false;
	

	public void init()
	{
		this.setSize(new Dimension(700, 400));
		this.setBackground(new Color(225, 128, 225));
		this.setFont(new Font("Arial", 0, 18));
		try {
			
			m_game = new Game(5, 
					"../unoDeck.txt");
					//f.getAbsolutePath()+"./unoDeck.txt");
					//"C:/Eclipse/workspace/uno2Java/unoDeck.txt");
		} catch (IOException e) {
			//fail-safe
			//ideally we want to read from the file, because thats the way the 
			//program was originally designed in DOS, but if for some reason, java can't
			//locate the text file, we have the deck hard-coded in
			m_game = new Game(5);
		}
		playerNumButtons();
		addKeyListener(m_game.getUI());
		addMouseListener(m_game.getUI());


		Thread t = new Thread(this);
		t.start();


	}
	
	
	public void paint(Graphics g)
	{


		validate();
		m_game.draw(g);	
		if(Setupdone)
		{
			drawTopCard(g);
		}
		//repaint();
	}
	
	
	public void playerNumButtons()
	{
		Label output_label;
		output_label = new Label("Select the number of Players:");
		add(output_label);
		player = new Button[buttonCount];
		for(int i = 0; i < buttonCount; ++i)
		{
			player[i] = new Button(" " + (i+2));
			add(player[i]);
		}
	}
	
	int actionCount = 4;
	static final int ACTION_DRAW = 0;
	static final int ACTION_END = 1;
	static final int ACTION_UNO = 2;
	static final int ACTION_FAIL = 3;
	
	public void actionButtons()
	{
		action = new Button[actionCount];
		action[ACTION_DRAW] = new Button("Draw Card");
		action[ACTION_END] = new Button("End Turn");
		action[ACTION_UNO] = new Button("Call Uno");
		action[ACTION_FAIL] = new Button("Failure to Call Uno");
		for(int i = 0; i < actionCount; ++i)
		{
			action[i].setPreferredSize(new Dimension(160, 20));
			add(action[i]);
		}
	}
	
	public void playerNames()
	{
		PlayerNames = new String[numOfPlayers];
		inputLabel = new Label("Enter Name for Player " + (playerCounter+1) + ":");
		nameBlock = new TextField(10);
		add(inputLabel);
		add(nameBlock);
		repaint();
		
	}
	
	public boolean action (Event e, Object args)
	  { 
		for(int i = 0; i < buttonCount; ++i)
		{
			if (e.target == player[i] && !playerSet)
		     {  // user has clicked this button
				numOfPlayers = i +2;
				System.out.println("WORKED "+ numOfPlayers);
				
				removeAll();
				m_game.setup(numOfPlayers);
				playerNames();

				playerSet = true;
				

		     }
			
		}
		
		
		if(cardsSet)
		{
			for(int n = 0; n < actionCount; ++n)
			{
				if(e.target == action[n])
				{
					switch(n)
					{
					case ACTION_DRAW:
						if(!m_game.getHasDrawn() && !m_game.getCardPlayed()&& m_game.okayToAddCard(m_game.getCurrentPlayerLoc()))
						{
							m_game.getDrawDeck().drawCard(m_game.getCurrentPlayer().getHand());
							m_game.setHasDrawn(true);
							m_game.setCursorLock(true);
							//clear all buttons
							removeAll();
							actionButtons();
							drawLastCard();	//only let the user select the last card drawn
							
						}
						break;
					case ACTION_END:
						//if the user has either played a card
						//or they have drawn, but cannot play their last card
						if(m_game.getCardPlayed() || (m_game.getHasDrawn() && !m_game.isCardLegal(m_game.getCurrentPlayer().getHand().getLastCard())))
						{
							m_game.endTurn();
							removeAll();
							actionButtons();
							drawHand();
						}
						break;
					case ACTION_UNO:
						if(!m_game.getUnoCalled())
						{
							m_game.callUno();
							removeAll();
							actionButtons();
							drawHand();
						}
						break;
					case ACTION_FAIL:
						if(!m_game.getUnoFailed())
						{
							m_game.failureToCallUno();
							removeAll();
							actionButtons();
							drawHand();
						}
						break;
					}
				}
			}
			int q = 0;
			char t = ' ', c = ' ';
			
			//checks to see if the card is legal
			for(int i=0; i<m_game.getCurrentPlayer().getHand().getNumOfCards(); ++i)//m_game.getCurrentPlayer().getHand().getColorAt(i) != lastColor || m_game.getCurrentPlayer().getHand().getTypeAt(i) != lastType; ++i)
			{
				q = m_game.getCurrentPlayer().getHand().getQuantityAt(i);
				
				while(q > 0)
				{
					if(e.target == cards[i])
					{
						//if the cursor is locked, set the look at to the last card drawn
						if(m_game.getCursorLock())
						{
							i = m_game.getCurrentPlayer().getHand().getLastCard();
						}
						if(m_game.isCardLegal(i) && !m_game.getCardPlayed())
						{
							//play card function here
							System.out.print("Yes, you can play the card at location ");
							//first re-save type and color directly from deck
							t = m_game.getCurrentPlayer().getHand().getTypeAt(i);
							c = m_game.getCurrentPlayer().getHand().getColorAt(i);
							//then add the card to the pile
							m_game.getCurrentPlayer().getHand().addFromHandToPile(i, m_game.getDiscardPile());
							System.out.println(m_game.getDiscardPile().getColorAt(m_game.getDiscardPile().getLastCard()));
							removeAll(); //clear all
							actionButtons();
							drawHand(); //redraw hand
							if(!m_game.calcWin())
							{
								//clear the wild color
								m_game.setWildColor('0');
								//set card effect
								m_game.setCardEffect(t, c);
								//set flag for endTurn()
								m_game.setCardPlayed(true);
							}
							else //if win, set end effect
							{m_game.setEndEffect(t, c);}
							{}
						}
						else
						{
							System.out.print("No, you can't play the card at location ");
						}
						
						System.out.print(i);
						System.out.print("\n");
						
						i=m_game.getCurrentPlayer().getHand().getNumOfCards();//checks to see if the card is legal
					}
				--q;
				}
			}
		}
		
		
		if (e.target instanceof TextField)
		{
			
            if (e.target == nameBlock && runOnce == true)
            {
            	System.out.println(playerCounter);
            	
            		PlayerNames[playerCounter] = nameBlock.getText(); 
            		nameBlock.setText("");
            		playerCounter++;
            		runOnce = false;
            		m_game.setPlayerNames(PlayerNames);
            		removeAll();//clean
            		playerNames();//print new label and text field
            		
			}
            if(playerCounter == numOfPlayers && Setupdone == false)
            {
            	removeAll();
            	//TODO first game drawing start here
            	actionButtons();
            	drawHand();
    			Setupdone = true;
            }   
		}
		
	    return true;    // Yes, we do need this!
	  }


	public void drawHand()
	{
		
		/**
		 * debug code
		 */
		if(m_game.getCurrentPlayer() == null)
		{
			System.out.print("NoT WoRkInG:::::::::::::::::\n");
			return;
		}
		else 
		{
			System.out.print("WoRkInG:::::::::::::::::\n");
		}
		int current = m_game.getDiscardPile().getLastCard();
		System.out.print("\n\n\n");
		System.out.print(m_game.getDiscardPile().getColorAt(current));
		System.out.print(" ");
		System.out.print(m_game.getDiscardPile().getTypeAt(current));
		System.out.print("\n");
		
		/******************/
		
		
		//buttons = new JPanel();
		Player player = m_game.getCurrentPlayer();
		cards = new Button[player.getHand().getNumOfCards()];
		int q = 0;
		
		String color, type;
		/**
		 * sets the color for each card and creates the buttons
		 */
		for(int i=0; i<player.getHand().getNumOfCards(); ++i)
		{
			q = player.getHand().getQuantityAt(i);
			color = getColor(player, i);
			type = getType(player, i);
			//print all duplicates
			while(q > 0)
			{
			cards[i] = new Button("" + color 
					+ " " + type);
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
			cards[i].setPreferredSize(new Dimension(150, 20));
			//buttons.add(cards[i]);
			add(cards[i]);
			--q;
			}
		}
		/*****************/
		
		cardsSet = true;
	}

	
	public void drawLastCard()
	{
		
		
		
		//buttons = new JPanel();
		Player player = m_game.getCurrentPlayer();
		cards = new Button[1];

		
		String color, type;
		/**
		 * sets the color for each card and creates the buttons
		 */
		
			
			color = getColor(player, player.getHand().getLastCard());
			type = getType(player, player.getHand().getLastCard());
			//print all duplicates

			cards[0] = new Button("" + color 
					+ " " + type);
			switch(player.getHand().getColorAt(player.getHand().getLastCard()))
			{
			case 'B':	cards[0].setBackground(new Color(0, 0, 255));	
						cards[0].setForeground(new Color(0, 0, 0));	break;
			case 'R':	cards[0].setBackground(new Color(255, 0, 0));	
						cards[0].setForeground(new Color(0, 0, 0));	break;
			case 'G':	cards[0].setBackground(new Color(0, 255, 0));	
						cards[0].setForeground(new Color(0, 0, 0));	break;
			case 'Y':	cards[0].setBackground(new Color(255, 255, 0));	
						cards[0].setForeground(new Color(0, 0, 0));	break;
			case 'W':	cards[0].setBackground(new Color(0, 0, 0));	
						cards[0].setForeground(new Color(255, 255, 255));	break;
			}
			cards[0].setPreferredSize(new Dimension(150, 20));
			//buttons.add(cards[i]);
			add(cards[0]);
		
		/*****************/
		
		cardsSet = true;
	}
	
	
	//@Override
	public void run() {
		while(true)
		{
//			System.out.println("repaint?");
			repaint();
			runOnce = true;
			try{
				Thread.sleep(100);
			}catch(Exception e){}
			
			
		}
		
	}
	
	public String getColor(Player p, int loc)
	{
		String temp = " ";
		switch(p.getHand().getColorAt(loc))
		{
		case 'B':	temp = "Blue";	break;
		case 'R':	temp = "Red";	break;
		case 'G':	temp = "Green";	break;
		case 'Y':	temp = "Yellow"; break;
		case 'W':	temp = "Wild"; break;
		}
		return temp;
	}
	
	public String getType(Player p, int loc)
	{
		String temp = " ";
		switch(p.getHand().getTypeAt(loc))
		{
		case '0':	
			if(p.getHand().getColorAt(loc) == 'W')
			{temp = "";}
			else
			{temp = "0";}
			break;
		case '1':	temp = "1";	break;
		case '2':	temp = "2";	break;
		case '3':	temp = "3"; break;
		case '4':	
			if(p.getHand().getColorAt(loc) == 'W')
			{temp = "+4";}
			else
			{temp = "4";}
			break;
		case '5':	temp = "5";	break;
		case '6':	temp = "6";	break;
		case '7':	temp = "7";	break;
		case '8':	temp = "8"; break;
		case '9':	temp = "9"; break;
		case 'R':	temp = "Reverse";	break;
		case 'S':	temp = "Skip";	break;
		case 'D':	temp = "Draw"; break;
		}
		return temp;
	}

	int topCardX = 300; int topCardBX = 290;//loc
	int topCardY = 175; int topCardBY = 165;
	int topCardW = 125; int topCardBW = 145;//width
	int topCardH = 175; int topCardBH = 195;//height
	int topCardWA = 20;//arc width
	int topCardHA = 20;//arc height
	Font topCardFont = new Font("Arial",Font.BOLD,30);
	Font topCardLabel = new Font("Arial",Font.ITALIC,22);
	int topCardFontX = 305;
	int topCardFontY = 270;
	int topCardFontX_Offset = 0;
	int topCardLabelX = 335;
	int topCardLabelY = 160;
	
	public void	drawTopCard(Graphics g)
	{
		//card border
		g.setColor(Color.black);
		g.drawRoundRect(topCardBX, topCardBY,topCardBW, topCardBH, topCardWA, topCardHA);
		//label
		g.setFont(topCardLabel);
		g.drawString("PILE:", topCardLabelX, topCardLabelY);
		g.setColor(Color.white);
		g.fillRoundRect(topCardBX+1, topCardBY+1,topCardBW-1, topCardBH-1, topCardWA+2, topCardHA+2);
		
		//color card
		char color = m_game.getDiscardPile().getColorAt(m_game.getDiscardPile().getLastCard());
		char type = m_game.getDiscardPile().getTypeAt(m_game.getDiscardPile().getLastCard());
		switch(color)
		{
		case 'B':	g.setColor(Color.blue);	break;
		case 'R':	g.setColor(Color.red);	break;
		case 'G':	g.setColor(Color.green);	break;
		case 'Y':	g.setColor(Color.yellow); break;
		case 'W':	g.setColor(Color.black); break;
		}
		g.fillRoundRect(topCardX, topCardY,topCardW, topCardH, topCardWA, topCardHA);
		//draw type
		String temp = " ";
		switch(type)
		{
		case '0':	
			if(color == 'W')
			{temp = " WILD  ";topCardFontX_Offset = 11;}
			else
			{temp = "   0   "; topCardFontX_Offset = 25;break;}
			break;
		case '1':	temp = "   1   ";	topCardFontX_Offset = 25;break;
		case '2':	temp = "   2   ";	topCardFontX_Offset = 25;break;
		case '3':	temp = "   3   "; topCardFontX_Offset = 25;break;
		case '4':	
			if(color == 'W')
			{temp = "WILD +4";topCardFontX_Offset = -2;}
			else
			{temp = "   4   "; topCardFontX_Offset = 25;break;}
			break;
		case '5':	temp = "   5   ";	topCardFontX_Offset = 25;break;
		case '6':	temp = "   6   ";	topCardFontX_Offset = 25;break;
		case '7':	temp = "   7   ";	topCardFontX_Offset = 25;break;
		case '8':	temp = "   8   "; topCardFontX_Offset = 25;break;
		case '9':	temp = "   9   "; topCardFontX_Offset = 25;break;
		case 'R':	temp = "Reverse";	topCardFontX_Offset = -2;break;
		case 'S':	temp = " Skip  ";	topCardFontX_Offset = 17;break;
		case 'D':	temp = " Draw  "; topCardFontX_Offset = 15;break;
		}
		//set color for font
		if(color == 'W')
			g.setColor(Color.white);
		else
			g.setColor(Color.black);
		
		g.setFont(topCardFont);
		g.drawString(temp, topCardFontX + topCardFontX_Offset, topCardFontY);
		
	}

	public void drawHUD(Graphics g)
	{
		
	}
}
