#include "Player.h"

//default constructor
Player::Player() : m_score(0), m_name("Player 0")
{
	uno = false;
}

//constructor that loads a blank player deck based off the
//size of another deck
Player::Player(Deck &other)
{
	Player();
	m_hand.load(other);
}
//loads player data
void Player::load(Deck &other)
{
	m_hand.load(other);
}
//input and set the player name
void Player::setName()
{
	//create temp char array to read in name
	char temp[MAX_NAME_LENGTH];
	cin >> temp;
	
	//run a for loop to count how long the array is
	for(int n = 0; temp[n] != '\0' && n < MAX_NAME_LENGTH; ++n)
	{
	}
	//set length
	m_nameLength = n;
	//create array for name
	m_name = new char [m_nameLength];
	//copy temp to name
	for(int x = 0; x < m_nameLength; ++x)
	{
		m_name[x] = temp[x];
	}

	//ignore anything else that is entered
	cin.ignore(1024, '\n');
}
//print the player name
void Player::printName()
{
	for(int i = 0; i < m_nameLength; ++i)
	{
		printf("%c", m_name[i]);
	}
}
//returns the players hand
Deck & Player::getHand()
{
	return m_hand;
}
//return the players score
int Player::getScore()
{
	return m_score;
}
//set uno flag to false
void Player::setUnoFalse()
{
	uno = false;
}
//set uno flag to true
void Player::setUnoTrue()
{
	uno = true;
}
//get state of uno flag
bool Player::getUno()
{
	return uno;
}
//increment a player's score
void Player::incrementScore(int a_score)
{
	m_score += a_score;
}
//deconstructor
Player::~Player()
{
	delete m_name;
}