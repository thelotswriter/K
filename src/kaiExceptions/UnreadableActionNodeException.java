package kaiExceptions;

public class UnreadableActionNodeException extends Exception 
{
	
	/**
	 * Thrown if an action file wasn't in the appropriate format
	 * @param file The location of the file attempting to be used to read an action node from
	 */
	public UnreadableActionNodeException(String file)
	{
		super(file);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
