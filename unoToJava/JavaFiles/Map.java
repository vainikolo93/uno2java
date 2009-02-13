import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Map 
{
	//holds the map data
	private char [][] m_map;
	private int m_width;
	private int m_height;
	
	//default constructor
	public Map()
	{
		m_width = 0;
		m_height = 0;
		m_map = null;
	}
	//return m_width
	public int getWidth()
	{
		return m_width;
	}
	//return m_height
	public int getHeight()
	{
		return m_height;
	}
	//load method for a filename
	//return true if loaded successfully
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
	//load method for loading map data from a file
	//throws an IOException if data failed to load
	public void load(InputStreamReader f) throws IOException
	{
		int m_height = readNextInt(f);
		int m_width = readNextInt(f);
		m_map = new char[m_height][];
		for(int r = 0; r < m_height; ++r)
		{
			m_map[r] = new char[m_width];
			for(int c = 0; c < m_width; ++c)
			{
				m_map[r][c] = (char) ignoreEndline(f);
			}
		}
	}
	//returns the int value of the next character after the next endline
	//throws IOException
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
		}while (whitespaceRead);
		return c;
	}
	//returns the value of the next token as an integer
	//throws IOException is stream failed to be read from
	private int readNextInt(InputStreamReader f) throws IOException
	{
		String s = readNextTOken(f).toString();
		return Integer.parseInt(s);
	}
	//load token, with whitespace delimiter
	//throws IOException if data failed to load
	private static StringBuffer readNextTOken(InputStreamReader f) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		int c;
		boolean whitespaceRead = false;
		do
		{
			c = f.read();
			switch(c)
			{
				//ignore the whitspace when searching for tokens
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
	
	/*
	* TODO add print/draw method for map class
	*/
}
