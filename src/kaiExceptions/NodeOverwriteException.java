package kaiExceptions;

public class NodeOverwriteException extends Exception 
{
	
	/**
	 * Thrown when the user wishes to overwrite a previously built node
	 * @param nodeName The name of the node to be replaced
	 */
	public NodeOverwriteException(String nodeName)
	{
		super(nodeName);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
