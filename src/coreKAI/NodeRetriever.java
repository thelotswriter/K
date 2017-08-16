package coreKAI;

import java.io.File;
import java.io.IOException;

import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnknownCommandException;
import kaiExceptions.UnknownThingException;
import kaiExceptions.UnreadableActionNodeException;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ThingNode;

/**
 * Retrieves nodes from the collection, providing an instance for the working process tree
 * @author BLJames
 *
 */
public class NodeRetriever 
{
	private NodeDirectory nodeDirectory;
	
	private static NodeRetriever SINGLETON = null;
	
	private NodeRetriever()
	{
		nodeDirectory = NodeDirectory.getInstance();
	}
	
	/**
	 * Gets the node retriever
	 * @return The node retriever
	 */
	public static NodeRetriever getInstance()
	{
		if(SINGLETON == null)
		{
			SINGLETON = new NodeRetriever();
		}
		return SINGLETON;
	}
	
	/**
	 * Checks if the named node exists
	 * @param nodeName The name of the node
	 * @return Whether or not the named node exists yet
	 */
	public boolean hasNode(String nodeName)
	{
		File nodeFile = nodeDirectory.getNodeFile(nodeName);
		if(nodeFile == null || !nodeFile.exists())
		{
			return false;
		} else
		{
			return true;
		}
	}
	
	/**
	 * Checks if the named command node exists
	 * @param nodeName The name of the node
	 * @return Whether or not the named command node exists
	 */
	public boolean hasCommandNode(String nodeName)
	{
		File nodeFile = nodeDirectory.getCommandNodeFile(nodeName);
		if(nodeFile == null || !nodeFile.exists())
		{
			return false;
		} else
		{
			return true;
		}
	}
	
	/**
	 * Checks if the named thing node exists
	 * @param nodeName The name of the node
	 * @return Whether or not the named thing node exists
	 */
	public boolean hasThingNode(String nodeName)
	{
		File nodeFile = nodeDirectory.getThingNodeFile(nodeName);
		if(nodeFile == null || !nodeFile.exists())
		{
			return false;
		} else
		{
			return true;
		}
	}
	
	/**
	 * Checks if the named action node exists
	 * @param nodeName The name of the node
	 * @return Whether or not the named action node exists
	 */
	public boolean hasActionNode(String nodeName)
	{
		File nodeFile = nodeDirectory.getActionNodeFile(nodeName);
		if(nodeFile == null || !nodeFile.exists())
		{
			return false;
		} else
		{
			return true;
		}
	}
	
	/**
	 * Gets the requested command node
	 * @param nodeName The name of the command node to get
	 * @return The requested node, if it exists
	 * @throws UnknownCommandException Thrown if the command node has not yet been created
	 */
	public CommandNode getCommandNode(String nodeName) throws UnknownCommandException
	{
		if(hasCommandNode(nodeName))
		{
			try 
			{
				return NodeLoader.getInstance().convertToCommandNode(nodeDirectory.getCommandNodeFile(nodeName));
			} catch (IOException e) {
				throw new UnknownCommandException(nodeName);
			}
		} else
		{
			throw new UnknownCommandException(nodeName);
		}
	}
	
	/**
	 * Gets the requested command node
	 * @param nodeName The name of the thing node to get
	 * @return The requested node, if it exists
	 * @throws UnknownThingException Thrown if the thing node has not yet been created
	 */
	public ThingNode getThingNode(String nodeName) throws UnknownThingException
	{
		if(hasThingNode(nodeName))
		{
			try 
			{
				return NodeLoader.getInstance().convertToThingNode(nodeDirectory.getThingNodeFile(nodeName));
			} catch (IOException e) {
				throw new UnknownThingException(nodeName);
			}
		} else
		{
			throw new UnknownThingException(nodeName);
		}
	}
	
	/**
	 * Gets the requested action node
	 * @param nodeName The name of the action node to get
	 * @return The requested node, if it exists
	 * @throws UnknownActionException Thrown if the action node has not yet been created
	 */
	public ActionNode getActionNode(String nodeName) throws UnknownActionException
	{
		if(hasActionNode(nodeName))
		{
			try 
			{
				return NodeLoader.getInstance().convertToActionNode(nodeDirectory.getActionNodeFile(nodeName));
			} catch (UnreadableActionNodeException | NotAnActionNodeException | IOException e) 
			{
				throw new UnknownActionException(nodeName);
			}
		} else
		{
			throw new UnknownActionException(nodeName);
		}
	}

}
