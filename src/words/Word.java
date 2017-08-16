package words;

import java.io.Serializable;

public abstract class Word implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1780850582440736039L;
	private String word;
	
	public Word(String theWord)
	{
		word = new String(theWord);
	}
	
	public void setWord(String word)
	{
		this.word = new String(word);
	}
	
	public abstract WordType getType();
	
	public String toString()
	{
		return word;
	}
	
}
