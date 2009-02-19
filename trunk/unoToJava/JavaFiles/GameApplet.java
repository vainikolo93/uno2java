import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Label;
import java.io.IOException;



public class GameApplet extends Applet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Game m_game;
	Button player[];
	static final int buttonCount = 9;
	int numOfPlayers;
	boolean playerSet = false;
	

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
	}
	
	public void paint(Graphics g)
	{
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
	
	public boolean action (Event e, Object args)
	  { 
		for(int i = 0; i < buttonCount; ++i)
		{
			if (e.target == player[i] && !playerSet)
		     {  // user has clicked this button
				numOfPlayers = i +2;
				System.out.println("WORKED "+ numOfPlayers);
				m_game.setup(numOfPlayers);
				removeAll();
				playerSet = true;
				m_game.drawHand();
		     }
		}
	    return true;    // Yes, we do need this!
	  }

}
