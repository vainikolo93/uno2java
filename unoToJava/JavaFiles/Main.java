import javax.swing.JFrame;


public class Main 
{
	//@SuppressWarnings("null")
	public static void main(String[] args)
	{

		JFrame jf = new JFrame("title");
		jf.setSize(400, 200);
		//NeatWindow n = new NeatWindow();
		//jf.getContentPane().add(n);
		// need to be very specific about key listening...
		//jf.addKeyListener(n);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		
		
		System.out.println("working?");
		
		//Player p1 = null;
		//p1.setName();
		//p1.printName();
		
		//String filename = "unoDeck.txt";
		//Deck drawPile;// = null;
		//drawPile.load(filename);
		//drawPile.debug();
		
	}
}
