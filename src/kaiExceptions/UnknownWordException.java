package kaiExceptions;

public class UnknownWordException extends Exception 
{
	
	/**
	 * Thrown when a word can't be found
	 * @param word The name of the word searched for but not found
	 */
	public UnknownWordException(String word) 
	{
		super(word);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
