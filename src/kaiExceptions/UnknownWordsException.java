package kaiExceptions;

import java.util.List;

import words.Word;

public class UnknownWordsException extends Exception 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Word> unknownWords;

	public UnknownWordsException(List<Word> unknownWords)
	{
		super();
		this.unknownWords = unknownWords;
	}
	
	public List<Word> getUnknownWords()
	{
		return unknownWords;
	}
	
}
