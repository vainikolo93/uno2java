import java.util.Scanner;


public class Player 
{
	static final int MAX_NAME_LENGTH = 9;
	
	private Deck m_hand;
	private int m_score;
	String m_name;
	boolean uno;
	
	//default constructor
	public Player()
	{
		m_score = 0;
		m_name = null;
		uno = false;
	}
	//constructor that loads a blank player deck based off the 
	//size of another deck
	public Player(Deck other)
	{
		m_score = 0;
		m_name = null;
		uno = false;
		//m_hand.load(other);
		m_hand = new Deck(other);
	}
	//load player data
	public void load(Deck other)
	{
		//m_hand.load(other);
		m_hand = new Deck(other);
	}
	//input and set the player name
	public void setName()
	{
		Scanner in = new Scanner(System.in);
		String temp = in.nextLine();
		//if the name is within the limit
		if(temp.length() <= MAX_NAME_LENGTH)
		{
			m_name = temp;
		}
		else //limit the length
		{
			char tempElements[] = new char [MAX_NAME_LENGTH];
			for(int n = 0; n < MAX_NAME_LENGTH; ++n)
			{
				//copy data into temporary char array
				tempElements[n] = temp.charAt(n);
			}
			//then store the char array in m_name
			m_name = new String(tempElements);
		}
	}
	//print the player name
	public void printName()
	{
		System.out.println(m_name);
	}
	//returns the players hand
	public Deck getHand()
	{
		return m_hand;
	}
	//return the players score
	public int getScore()
	{
		return m_score;
	}
	//set the uno flag to false
	public void setUnoFalse()
	{
		uno = false;
	}
	//set uno flag to true
	public void setUnoTrue()
	{
		uno = true;
	}
	//get state of uno flag
	public boolean getUno()
	{
		return uno;
	}
	//increment a player's score
	public void incrementScore(int a_score)
	{
		m_score += a_score;
	}
}
