import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;


public class Main
{
	//@SuppressWarnings("null")
	public static void main(String[] args) throws IOException 
	{

		
		
		Game g = new Game(5, "unoDeck.txt");
		g.setup();
		g.gamesetupdraw();
		
		
		
		
		//Player p1 = new Player();
		//p1.setName();
		//p1.printName();
		
		//Deck drawPile = new Deck("unoDeck.txt");
		//drawPile.debug();
		
		//Game g = new Game(5, "unoDeck.txt");
		//g.setup();
		
	}

}
