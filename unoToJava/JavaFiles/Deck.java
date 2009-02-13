import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;


//handles the data for each individual card
class card
{
	public char m_type;
	public char m_color;
	public int m_quantity;
}

public class Deck 
{
	private card[] m_data; //data
	private int m_size; //the "visible" size of the deck
	//do not need a length variable, can use m_data.length to access the actual length
	private int m_numOfCards; //total number of cards left in the deck
	private int m_lastCard; //saves the location of the last card added to the deck
	
	public Deck()
	{
		m_data = null;
		m_size = 0;
		m_numOfCards = 0;
	}
	//constructor that loads the deck
	public Deck(String a_filename) throws IOException
	{
		m_data = null;
		m_size = 0;
		m_numOfCards = 0;
		//load(a_filename);
		FileReader f = new FileReader(a_filename);
		load(f);
	}
	//constructor that creates a blank deck based off the size of
	//another deck (used for creating player hands and discard pile
	public Deck(Deck other)
	{
		m_data = new card[other.m_data.length];
		for(int r = 0; r < m_data.length; ++r)
		{
			//make sure the new deck is empty
			m_data[r].m_type = '0';
			m_data[r].m_color = '0';
			m_data[r].m_quantity = 0;
		}
		m_size = m_numOfCards = m_lastCard = 0;
	}
	//load deck from a filename
	//return true if successful
	public boolean load(String a_filename)
	{
		try
		{
			FileReader f = new FileReader(a_filename);
			load(f);
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
	//load the deck from file
	public void load(InputStreamReader f) throws IOException
	{
		//input size and create array
		m_size = readNextInt(f);
		m_data = new card[m_size];

				
		for(int r = 0; r < m_data.length; ++r)
		{
			m_data[r] = new card();
			//input type, color, and quantity
			m_data[r].m_type = (char) ignoreEndline(f);
			m_data[r].m_color = (char) ignoreEndline(f);
			m_data[r].m_quantity = ignoreEndline(f);
			
			//add the numb of cards to the total
			m_numOfCards += m_data[r].m_quantity;
			
		}
		
	}
	//returns the int value of the next character after the next endline 
	private static int ignoreEndline(InputStreamReader f) throws IOException
	{
		int c;
		boolean whitespaceRead = true;
		do
		{
			c = f.read();
			switch(c)
			{
			case '\n':
			case '\r':
				whitespaceRead = true;
				break;
			default:
				whitespaceRead = false;
			}
		}while(whitespaceRead);
		return c;
	}
	//return the value of the next token as an integer
	private static int readNextInt(InputStreamReader f) throws IOException
	{
		String s = readNextToken(f).toString();
		return Integer.parseInt(s);
	}
	//inputs a token from stream, ignores whitespace
	private static StringBuffer readNextToken(InputStreamReader f) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		int c;
		boolean whitespaceRead = false;
		do
		{
			c = f.read();
			switch(c)
			{
			//ignore whitespace when searching for tokens
			case ' ':
			case '\t':
			case '\n':
			case '\r':
			case '\b':
				whitespaceRead = true;
				break;
			default:
				sb.append((char) c);
			}
		}while (sb.length() == 0 || !whitespaceRead);
		return sb;
	}
	//load a blank deck based off the size of another deck
	public void load(Deck other)
	{
		m_data = new card[other.m_data.length];
		for(int r = 0; r < m_data.length; ++r)
		{
			//make sure the new deck is empty
			m_data[r].m_type = '0';
			m_data[r].m_color = '0';
			m_data[r].m_quantity = 0;
		}
		m_size = m_numOfCards = m_lastCard = 0;
	}
	//calculates the total number of cards in the deck at anytime
	public void calcNumOfCards()
	{
		int total = 0;
		for(int r = 0; r < m_data.length; ++r)
		{
			total += m_data[r].m_quantity;
		}
		m_numOfCards = total;
	}
	//calculates the "visible size of the deck
	public void calcSize()
	{
		int size = 0;
		for(int r = 0; r < m_data.length; ++r)
		{
			//if ther is a card of this type & color
			if(m_data[r].m_quantity > 0)
			{
				//increase size
				++size;
			}
		}
		m_size = size;
	}
	//return the length of the deck
	public int getLength()
	{
		return m_data.length;
	}
	//calculate and return the current size
	public int getSize()
	{
		calcSize();
		return m_size;
	}
	//calculate and return the current number of cards in the deck
	public int getNumOfCards()
	{
		calcNumOfCards();
		return m_numOfCards;
	}
	//return the type of a specific card
	public char getTypeAt(int row)
	{
		return m_data[row].m_type;
	}
	//return the color of a specific card
	public char getColorAt(int row)
	{
		return m_data[row].m_color;
	}
	//return the quantity of a specific card
	public int getQuantityAt(int row)
	{
		return m_data[row].m_quantity;
	}
	//returns false if the deck is empty
	//randomly draws a card from the deck
	public boolean drawCard(Deck other)
	{
		//if the deck is not empty
		if(getNumOfCards() > 0)
		{
			//select a random number within the 'visible size of the deck;
			Random rand = new Random();
			int card = rand.nextInt(getSize());
			
			//save the cards into another deck(i.e. pile)
			addCardToOther(card, other);
			
			//if the quantity = 0, move this element to the end of the deck
			if(m_data[card].m_quantity == 0)
			{
				moveToEnd(card);
			}
			return true;
		}
		return false;
	}
	//add drawn card to another deck
	public void addCardToOther(int card, Deck other)
	{
		boolean isAdded = false;
		//make sure the size is updated
		other.calcSize();
		//search through the "visible" deck
		for(int r = 0; r < other.m_size; ++r)
		{
			//if the card already exists in the deck
			//update the quantity
			if(other.m_data[r].m_type == m_data[card].m_type
					&& other.m_data[r].m_color == m_data[card].m_color)
			{
				other.m_data[r].m_quantity++;
				m_data[card].m_quantity--;
				isAdded = true;
				//save location of the last card added
				other.m_lastCard = r;
			}
		}
		
		//if the card is not in the deck, add it to the end
		if(!isAdded)
		{
			other.m_data[other.m_size].m_type = m_data[card].m_type;
			other.m_data[other.m_size].m_color = m_data[card].m_color;
			other.m_data[other.m_size].m_quantity = 1;
			m_data[card].m_quantity--;
			//save location of last card added
			other.m_lastCard = other.m_size;
		}
	}
	//after a card's quantity is reduced to zero
	//this function moves it to the end of the array
	@SuppressWarnings("null")
	public void moveToEnd(int loc)
	{
		//save current type and color
		card temp = null;
		temp.m_type = m_data[loc].m_type;
		temp.m_color = m_data[loc].m_color;
		
		for(int r = loc; r < m_data.length - 1; ++r)
		{
			//move each element up one
			m_data[r] = m_data[r+1];
		}
		
		//save "empty" card to the end of the deck
		m_data[m_data.length-1].m_type = temp.m_type;
		m_data[m_data.length-1].m_color = temp.m_color;
		m_data[m_data.length-1].m_quantity = 0;
	}
	//shuffle the contents of another deck back into this deck
	public void shuffle(Deck other)
	{
		other.calcSize();
		for(int r = 0; r < other.m_size; ++r)
		{
			//while quantity is greater than 0
			//continue to add this card to the deck
			while(other.getQuantityAt(r) > 0)
			{
				addOtherToThis(r, other);
			}
		}
	}
	//add another deck to this deck
	//this is used to shuffle (i.e. add the discard pile back to the draw deck)
	public void addOtherToThis(int card, Deck other)
	{
		boolean isAdded = false;
		//make sure size is updated
		calcSize();
		//search through the "visible" deck
		for(int r = 0; r < m_size; ++r)
		{
			//if the card already exists in the deck
			//update the quantity
			if(m_data[r].m_type == other.m_data[card].m_type
					&& m_data[r].m_color == other.m_data[card].m_color)
			{
				//add to this deck
				m_data[r].m_quantity++;
				//remove from the other deck
				other.m_data[card].m_quantity--;
				isAdded = true;
			}
		}
		
		//if the card is not in the deck
		//add it to the end
		if(!isAdded)
		{
			m_data[m_size].m_type = other.m_data[card].m_type;
			m_data[m_size].m_color = other.m_data[card].m_color;
			m_data[m_size].m_quantity = 1;
			other.m_data[card].m_quantity--;
		}
	}
	//return the location of the last card added to a deck
	public int getLastCard()
	{
		return m_lastCard;
	}
	//plays a card from a players hand to the pile
	public void addFromHandToPile(int card, Deck other)
	{
		//add the card to the pile
		addCardToOther(card, other);
		//if quantity is 0, move to the end of the array
		if(getQuantityAt(card) == 0)
		{
			moveToEnd(card);
		}
	}
	//shuffles the contents of another deck (minus one card) back into this deck
	public void shuffleMinusOne(int card, Deck other)
	{
		other.calcSize();
		for(int r = 0; r < other.m_size; ++r)
		{
			if(r != card)//if not the card we want to keep
			{
				//while quantity is greater than 0
				//continue to add this card to the deck
				while(other.getQuantityAt(r) > 0)
				{
					addOtherToThis(r, other);
				}
			}
			else //if it is the card we want to keep
			{
				//while the quantity is greater than 1
				//continue to add this card to the deck
				while(other.getQuantityAt(r) > 1)
				{
					addOtherToThis(r, other);
				}
			}
		}
		//then move the saved card to the front
		other.m_data[0].m_type = other.m_data[card].m_type;
		other.m_data[0].m_color = other.m_data[card].m_color;
		other.m_data[0].m_quantity = 1;
		other.m_data[card].m_quantity = 0;
		other.m_lastCard = 0;
	}
	
	//DEBUG
	//prints info about the deck
	public void debug()
	{
		System.out.println("DEBUG: \nSize: " + getSize() + "  Card#: " + getNumOfCards() + "\n");
		for(int r = 0; r < m_size; ++r)
		{
			System.out.println("Type: " + m_data[r].m_type + " Color: " + m_data[r].m_color +
					"Quantity: " + m_data[r].m_quantity + "\n");
		}
	}
	
	//END OF CLASS
}




