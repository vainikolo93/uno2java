import java.applet.Applet;
import java.awt.Button;
import java.awt.Event;
import java.awt.Graphics;
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
	

	public void init()
	{
		
		playerNumButtons();
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
		addKeyListener(m_game.getUI());
		addMouseListener(m_game.getUI());

		m_game.drawHand();
	}
	
	public void paint(Graphics g)
	{
		m_game.draw(g);
	
	}
	
	public Game getGame()
	{
		return m_game;
	}
	
	public void playerNumButtons()
	{
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
			if (e.target == player[i])
		     {  // user has clicked this button
				numOfPlayers = i +2;
				System.out.println("WORKED "+ numOfPlayers);
				m_game.setup(numOfPlayers);
		     }
		}
	    return true;    // Yes, we do need this!
	  }

}
