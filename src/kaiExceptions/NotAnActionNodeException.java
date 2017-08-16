package kaiExceptions;

public class NotAnActionNodeException extends Exception 
{
	
	/**
	 * Thrown if a file was loaded to be an action node, but was not a subclass of ActionNode
	 * @param filePath
	 */
	public NotAnActionNodeException(String filePath)
	{
		super(filePath);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
