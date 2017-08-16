package languageProcessing;

import java.util.ArrayList;
import java.util.List;

import words.Noun;
import words.Verb;
import words.Word;

public class StringToWordConverter 
{
	
	private static StringToWordConverter SINGLETON = null;
	
	private StringToWordConverter(){}
	
	/**
	 * Gives an instance of the StringToWordConverter
	 * @return The StringToWordConverter
	 */
	public static StringToWordConverter getInstance()
	{
		if(SINGLETON == null)
		{
			SINGLETON = new StringToWordConverter();
		}
		return SINGLETON;
	}
	
	/**
	 * Converts a string (possibly composed of multiple words) into a list of words
	 * NOTE - Currently calls the first word a verb and the remainder nouns
	 * @param fullString The full string being converted
	 * @return A list of Word objects, one for each word (separated by space) in the sentence
	 */
	public List<Word> convert(String fullString)
	{
		String[] strings = fullString.split(" ");
		List<Word> words = new ArrayList<Word>();
		words.add(new Verb(strings[0]));
		for(int i = 1; i < words.size(); i++)
		{
			words.add(new Noun(strings[i]));
		}
		return words;
	}
	
}
