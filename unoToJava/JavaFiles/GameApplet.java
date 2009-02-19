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
	JPanel buttons;

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
	
	public void playerNames()
	{
		PlayerNames = new String[numOfPlayers];
		inputLabel = new Label("Player Name");
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
			}
            if(playerCounter == numOfPlayers && Setupdone == false)
            {
            	removeAll();
            	//TODO first game drawing start here
            	drawHand();
    			Setupdone = true;	            	
            }   
		}
		int temp = 0;
		//finds out which card it is
		for(int i=0; i<m_game.getCurrentPlayer().getHand().getNumOfCards()-1; ++i)
		{
			if(e.target == cards[i])
			{
				temp = i;
				
				if(m_game.isCardLegal(new Integer(temp)))
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
				
				i=m_game.getCurrentPlayer().getHand().getNumOfCards();//checks to see if the card is legal
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
		
		
		buttons = new JPanel();
		Player player = m_game.getCurrentPlayer();
		JButton cards[] = new JButton[player.getHand().getNumOfCards()];
		int i=0;
		
		/**
		 * sets the color for each card and creates the buttons
		 */
		for(i=0; i<player.getHand().getNumOfCards()-1; ++i)
		{
			cards[i] = new JButton("" + player.getHand().getColorAt(i) 
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
			//cards[i].addActionListener(this);
			cards[i].setPreferredSize(new Dimension(75, 20));
			buttons.add(cards[i]);
		}
		/*****************/
		
		//sets the layout
		buttons.setLayout(new BoxLayout(buttons, 1));
		
		
		add(buttons);
	}
	private int currentCard = 0;

	@Override
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

}
