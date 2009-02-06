#include "Deck.h"



//default constructor
Deck::Deck() : m_size(0), m_length(0), m_data(NULL), m_numOfCards(0), m_lastCard(0)
{
	srand(time(NULL));
}

//constructor that loads the deck
Deck::Deck(char * a_filename)
{
	Deck();
	load(a_filename);
}

//constructor that creates a blank deck based off the size of another deck
Deck::Deck(Deck &other)
{
	
	m_length = other.m_length;

	m_data = new card [m_length];
	for(int r = 0; r < m_length; ++r)
	{
		//make sure the new deck is empty
		m_data[r].m_type = NULL;
		m_data[r].m_color = NULL;
		m_data[r].m_quantity = NULL;
	}
	m_size = m_numOfCards = m_lastCard = 0;
}

//load deck from filename
void Deck::load(char * a_filename)
{
	ifstream in(a_filename);
	if(in.is_open())
	{
		load(in);
		in.close();
	}
}

//load deck from fstream source
void Deck::load(ifstream &a_source)
{
	//input the length(size)
	a_source >> m_length;
	m_size = m_length;

	//ignore the rest of the line (the next 1024 characters)
	a_source.ignore(1024, '\n');

	//create an array of cards
	m_data = new card [m_length];
	for(int r = 0; r < m_length; ++r)
	{
		//input type, color, and quantity into the struct array
		m_data[r].m_type = a_source.get();
		m_data[r].m_color = a_source.get();

		a_source >> m_data[r].m_quantity;

		//add the number of cards to the total
		m_numOfCards += m_data[r].m_quantity;

		a_source.ignore(1024, '\n');

	}
}
//load a blank deck based off the size of another deck
void Deck::load(Deck &other)
{

	m_length = other.m_length;

	m_data = new card [m_length];
	for(int r = 0; r < m_length; ++r)
	{
		//make sure the new deck is empty
		m_data[r].m_type = NULL;
		m_data[r].m_color = NULL;
		m_data[r].m_quantity = NULL;
	}
	m_size = m_numOfCards = 0;
}
//calculates the total number of cards left in the deck at anytime
void Deck::calcNumOfCards()
{
	int total = 0;
	for(int r = 0; r < m_length; ++r)
	{
		total += m_data[r].m_quantity;
	}
	m_numOfCards = total;
}
//calculates the "visible" size of the deck
void Deck::calcSize()
{
	int size = 0;
	for(int r = 0; r < m_length; ++r)
	{
		//if there is a card of this type & color
		if(m_data[r].m_quantity > 0)
		{
			//increase size
			++size;
		}
	}
	m_size = size;
}
//return length
int Deck::getLength()
{
	return m_length;
}
//calculate and return current size
int Deck::getSize()
{
	calcSize();
	return m_size;
}
//calculate and return the current number of cards in the deck
int Deck::getNumOfCards()
{
	calcNumOfCards();
	return m_numOfCards;
}
//return type of a specific card
char Deck::getTypeAt(int row)
{
	return m_data[row].m_type;
}
//return color of a specific card
char Deck::getColorAt(int row)
{
	return m_data[row].m_color;
}
//return quantity of a specific card
int Deck::getQuantityAt(int row)
{
	return m_data[row].m_quantity;
}
//returns false if the deck is empty
//randomly draws a card from the deck
bool Deck::drawCard(Deck &other)
{
	 //if the deck is not empty
	if(getNumOfCards() > 0)
	{
		//select a random number within the 'visible' size of the deck
		int card = rand() % getSize();

		//save cards into another deck(i.e. pile)
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
//add drawn card to another Deck
void Deck::addCardToOther(int card, Deck &other)
{
	bool isAdded = false;
	//make sure size is update
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
			//save location of last card added
			other.m_lastCard = r;
		}
	}

	//if the card is not in the deck
	//add it to the end
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
void Deck::moveToEnd(int loc)
{
	//save current type and color
	card temp;
	temp.m_type = m_data[loc].m_type;
	temp.m_color = m_data[loc].m_color;

	for(int r = loc; r < m_length - 1; ++r)
	{
		//move each element up one
		m_data[r] = m_data[r+1];
	}

	//save "empty" card to the end of the deck
	m_data[m_length-1].m_type = temp.m_type;
	m_data[m_length-1].m_color = temp.m_color;
	m_data[m_length-1].m_quantity = 0;

}
//shuffle the contents of another deck back into this deck
void Deck::shuffle(Deck &other)
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
//add another card to this deck
//this is used to shuffe (i.e. to add the discard pile back to the draw deck)
void Deck::addOtherToThis(int card, Deck &other)
{
	bool isAdded = false;
	//make sure size is updated
	calcSize();
	//search through the "visible" deck
	for(int r=0; r < m_size; ++r)
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
int Deck::getLastCard()
{
	return m_lastCard;
}

//plays a card from a player's hand to the pile
void Deck::addFromHandToPile(int card, Deck &other)
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
void Deck::shuffleMinusOne(int card, Deck &other)
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
			//while quantity is greater than 1
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
//returns the location of a card based off its color and type
int Deck::getLocation(char a_type, char a_color)
{
	for(int n = 0; n < m_length; ++n)
	{
		//if the color and type match
		if(m_data[n].m_type == a_type && m_data[n].m_color == a_color)
		{
			//return the location
			return n;
		}
	}
}
//DEBUG
//prints info about the deck
void Deck::debug()
{
	cout << "DEBUG: " << getSize() << " " << getNumOfCards() << endl;
	for(int r = 0; r < m_size; ++r)
	{
		cout << "DEBUG: " << m_data[r].m_type << " " << m_data[r].m_color 
			<< " " << m_data[r].m_quantity << endl;
	}
}
//deconstructor
Deck::~Deck()
{
	delete m_data;
}