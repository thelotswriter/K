package kaiExceptions;

public class UnknownActionException extends Exception 
{

	/**
	 * Thrown when an action being searched for can't be found
	 * @param action The action not found
	 */
	public UnknownActionException(String action) 
	{
		super(action);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
