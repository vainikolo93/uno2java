import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;


public class NeatWindow extends JPanel implements MouseListener, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//the font we want to draw in
	Font m_font;
	//the game that starts the interface
	Game m_parent;
	
	public Dimension getPreferredSize()
	{
		return new Dimension(200, 200);
	}
	
	public NeatWindow(Game a_parent)
	{
		m_parent = a_parent;
		//tells the window that it can listen to mouse and key input
		addMouseListener(this);
		addKeyListener(this);
		m_font = new Font("Courier", 0, 12);	
	}
	
	public void paintComponent(Graphics g)
	{
		//reset the font each time
		g.setFont(m_font);
		//super allows you to override a parent class
		super.paintComponent(g);
		//TODO add Game.draw()
		m_parent.draw(g);
		g.fillRect(10, 10, 100, 100);
	}

	//@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Clicked");
		repaint();
		
	}

	//@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("preSSed");
		repaint();
		
	}

	//@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	//@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	//using keyTyped() because getKeyChar() only works with
	//this function
	public void keyTyped(KeyEvent e) 
	{
		m_parent.handleInput(e);
		//redraw window
		repaint();
		
	}


}
