package kaiExceptions;

public class UnknownCommandException extends Exception 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Thrown when multiple nodes of the command can be found
	 * @param command The command causing the error
	 */
	public UnknownCommandException(String command) 
	{
		super(command);
	}
}
