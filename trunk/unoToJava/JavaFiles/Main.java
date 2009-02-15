import java.io.IOException;

import javax.swing.JFrame;


public class Main 
{
	//@SuppressWarnings("null")
	public static void main(String[] args) throws IOException
	{

		JFrame jf = new JFrame("title");
		jf.setSize(400, 200);
		//NeatWindow n = new NeatWindow();
		//jf.getContentPane().add(n);
		// need to be very specific about key listening...
		//jf.addKeyListener(n);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		
		
		//Player p1 = new Player();
		//p1.setName();
		//p1.printName();
		
		//Deck drawPile = new Deck("unoDeck.txt");
		//drawPile.debug();
		
		Game g = new Game(5, "unoDeck.txt");
		g.setup();
		
	}
}
