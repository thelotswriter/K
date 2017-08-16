package kaiExceptions;

public class UnknownThingException extends Exception 
{

	/**
	 * Thrown when a thing node can't be found
	 * @param thing The name of the thing node searched for but not found
	 */
	public UnknownThingException(String thing) 
	{
		super(thing);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
