package coreKAI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import kaiExceptions.NodeOverwriteException;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ThingNode;

public class NodeDirectory 
{
	private final Gson gson = new Gson();
	private final File directoryFile = new File("node-dir.json");
	
	// Maps node names to their file location
	private Map<String,String> commandDirectory;
	private Map<String,String> thingDirectory;
	private Map<String,String> actionDirectory;
	// The one and only node directory
	private static NodeDirectory SINGLETON = null;
	
	/**
	 * Creates a new NodeDirectory, attempting to load from the directory's save file
	 */
	private NodeDirectory()
	{
		commandDirectory = new HashMap<String, String>();
		thingDirectory = new HashMap<String, String>();
		actionDirectory = new HashMap<String, String>();
		try(FileReader reader = new FileReader(directoryFile);) 
		{
			JsonParser parser = new JsonParser();
			JsonArray json = (JsonArray) parser.parse(reader);
			JsonElement commandJson = json.get(0);
			JsonElement thingJson = json.get(1);
			JsonElement actionJson  = json.get(2);
			commandDirectory = gson.fromJson(commandJson, new TypeToken<Map<String, String>>(){}.getType());
			thingDirectory = gson.fromJson(thingJson, new TypeToken<Map<String, String>>(){}.getType());
			actionDirectory = gson.fromJson(actionJson, new TypeToken<Map<String, String>>(){}.getType());
		} catch (FileNotFoundException e) 
		{
			System.err.println("No directory found");
		} catch (IOException e1) 
		{
			System.err.println("There was a problem with the Node Directory");
		}
	}
	
	/**
	 * Gets the node directory
	 * @return The node directory
	 */
	public static NodeDirectory getInstance()
	{
		if(SINGLETON == null)
		{
			SINGLETON = new NodeDirectory();
		}
		return SINGLETON;
	}
	
	/**
	 * Finds the file where the node's data can be found
	 * @param nodeName The name of the node being accessed
	 * @return The file where the node is stored
	 */
	public File getNodeFile(String nodeName)
	{
		if(commandDirectory.containsKey(nodeName))
		{
			File nodeFile = new File(commandDirectory.get(nodeName));
			return nodeFile;			
		} else if(thingDirectory.containsKey(nodeName))
		{
			File nodeFile = new File(thingDirectory.get(nodeName));
			return nodeFile;
		} else
		{
			File nodeFile = new File(actionDirectory.get(nodeName));
			return nodeFile;
		}
	}
	
	/**
	 * Finds the file where the command node's data can be found
	 * @param nodeName The name of the command node
	 * @return The file where the node is stored
	 */
	public File getCommandNodeFile(String nodeName)
	{
		File nodeFile = new File(commandDirectory.get(nodeName));
		return nodeFile;
	}
	
	/**
	 * Finds the file where the thing node's data can be found
	 * @param nodeName The name of the thing node
	 * @return The file where the node is stored
	 */
	public File getThingNodeFile(String nodeName)
	{
		File nodeFile = new File(thingDirectory.get(nodeName));
		return nodeFile;
	}
	
	/**
	 * Finds the file where the action node's data can be found
	 * @param nodeName The name of the action node
	 * @return The file where the node is stored
	 */
	public File getActionNodeFile(String nodeName)
	{
		File nodeFile = new File(actionDirectory.get(nodeName));
		return nodeFile;
	}
	
	/**
	 * Saves the current directory so it can be accessed later
	 */
	public void updateDirectory()
	{
		JsonParser parser = new JsonParser();
		JsonElement commandJson = parser.parse(gson.toJson(commandDirectory));
		JsonElement thingJson = parser.parse(gson.toJson(thingDirectory));
		JsonElement actionJson = parser.parse(gson.toJson(actionDirectory));
		JsonArray jArray = new JsonArray();
		jArray.add(commandJson);
		jArray.add(thingJson);
		jArray.add(actionJson);
		String json = gson.toJson(jArray);
		try(Writer writer = new FileWriter(directoryFile))
		{
			writer.write(json);
			writer.close();
		} catch (IOException e) {
			System.err.println("Something went wrong updating the directory...");
		}
	}
	
	/**
	 * Adds a command node to the directory
	 * @param nodeName The name of the node
	 * @param nodeFile The path to the node's location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node
	 */
	public void addCommandNode(String nodeName, String nodeFile) throws NodeOverwriteException
	{
		if(!commandDirectory.containsKey(nodeName))
		{
			commandDirectory.put(nodeName, nodeFile);
		} else
		{
			throw new NodeOverwriteException(nodeName);
		}
	}
	
	/**
	 * Adds a thing node to the directory
	 * @param nodeName The name of the node
	 * @param nodeFile The path to the node's location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node
	 */
	public void addThingNode(String nodeName, String nodeFile) throws NodeOverwriteException
	{
		if(!thingDirectory.containsKey(nodeName))
		{
			thingDirectory.put(nodeName, nodeFile);
		} else
		{
			throw new NodeOverwriteException(nodeName);
		}
	}
	
	/**
	 * Adds an action node to the directory
	 * @param nodeName The name of the node
	 * @param nodeFile The path to the node's location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node
	 */
	public void addActionNode(String nodeName, String nodeFile) throws NodeOverwriteException
	{
		if(!actionDirectory.containsKey(nodeName))
		{
			actionDirectory.put(nodeName, nodeFile);
		} else
		{
			throw new NodeOverwriteException(nodeName);
		}
	}
	
	/**
	 * Adds a command node to the directory
	 * @param node The node being added
	 * @param nodeFile The path to the node's location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node
	 */
	public void addCommandNode(CommandNode node, String nodeFile) throws NodeOverwriteException
	{
		if(!commandDirectory.containsKey(node.getName()))
		{
			commandDirectory.put(node.getName(), nodeFile);
		} else
		{
			throw new NodeOverwriteException(node.getName());
		}
	}
	
	/**
	 * Adds a thing node to the directory
	 * @param node The node being added
	 * @param nodeFile The path to the node's location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node
	 */
	public void addThingNode(ThingNode node, String nodeFile) throws NodeOverwriteException
	{
		if(!thingDirectory.containsKey(node.getName()))
		{
			thingDirectory.put(node.getName(), nodeFile);
		} else
		{
			throw new NodeOverwriteException(node.getName());
		}
	}
	
	/**
	 * Adds an action node to the directory
	 * @param node The node being added
	 * @param nodeFile The path to the node's location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node
	 */
	public void addActionNode(ActionNode node, String nodeFile) throws NodeOverwriteException
	{
		if(!actionDirectory.containsKey(node.getName()))
		{
			actionDirectory.put(node.getName(), nodeFile);
		} else
		{
			throw new NodeOverwriteException(node.getName());
		}
	}
	
	/**
	 * Adds a command node to the directory
	 * @param nodeName The name of the node
	 * @param nodeFile The node's storage location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node 
	 */
	public void addCommandNode(String nodeName, File nodeFile) throws NodeOverwriteException
	{
		if(!commandDirectory.containsKey(nodeName))
		{
			commandDirectory.put(nodeName, nodeFile.getAbsolutePath());
		} else
		{
			throw new NodeOverwriteException(nodeName);
		}
	}
	
	/**
	 * Adds a thing node to the directory
	 * @param nodeName The name of the node
	 * @param nodeFile The node's storage location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node 
	 */
	public void addThingNode(String nodeName, File nodeFile) throws NodeOverwriteException
	{
		if(!thingDirectory.containsKey(nodeName))
		{
			thingDirectory.put(nodeName, nodeFile.getAbsolutePath());
		} else
		{
			throw new NodeOverwriteException(nodeName);
		}
	}
	
	/**
	 * Adds an action node to the directory
	 * @param nodeName The name of the node
	 * @param nodeFile The node's storage location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node 
	 */
	public void addActionNode(String nodeName, File nodeFile) throws NodeOverwriteException
	{
		if(!actionDirectory.containsKey(nodeName))
		{
			actionDirectory.put(nodeName, nodeFile.getAbsolutePath());
		} else
		{
			throw new NodeOverwriteException(nodeName);
		}
	}
	
	/**
	 * Adds a command node to the directory
	 * @param nodeName The node
	 * @param nodeFile The node's storage location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node
	 */
	public void addCommandNode(CommandNode node, File nodeFile) throws NodeOverwriteException
	{
		if(!commandDirectory.containsKey(node.getName()))
		{
			commandDirectory.put(node.getName(), nodeFile.getAbsolutePath());
		} else
		{
			throw new NodeOverwriteException(node.getName());
		}
	}
	
	/**
	 * Adds a thing node to the directory
	 * @param nodeName The node
	 * @param nodeFile The node's storage location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node
	 */
	public void addThingNode(ThingNode node, File nodeFile) throws NodeOverwriteException
	{
		if(!thingDirectory.containsKey(node.getName()))
		{
			thingDirectory.put(node.getName(), nodeFile.getAbsolutePath());
		} else
		{
			throw new NodeOverwriteException(node.getName());
		}
	}
	
	/**
	 * Adds an action node to the directory
	 * @param nodeName The node
	 * @param nodeFile The node's storage location
	 * @throws NodeOverwriteException Thrown if trying to add a node with the same node as a previously existing node
	 */
	public void addActionNode(ActionNode node, File nodeFile) throws NodeOverwriteException
	{
		if(!actionDirectory.containsKey(node.getName()))
		{
			actionDirectory.put(node.getName(), nodeFile.getAbsolutePath());
		} else
		{
			throw new NodeOverwriteException(node.getName());
		}
	}
	
	/**
	 * Replaces a command node in the directory, deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The path to the node's location
	 */
	public void replaceCommandNode(String nodeName, String nodeFile)
	{
		if(commandDirectory.containsKey(nodeName))
		{
			File oldFile = new File(commandDirectory.get(nodeName));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			commandDirectory.remove(nodeName);
		}
		commandDirectory.put(nodeName, nodeFile);
	}
	
	/**
	 * Replaces a thing node in the directory, deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The path to the node's location
	 */
	public void replaceThingNode(String nodeName, String nodeFile)
	{
		if(thingDirectory.containsKey(nodeName))
		{
			File oldFile = new File(thingDirectory.get(nodeName));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			thingDirectory.remove(nodeName);
		}
		thingDirectory.put(nodeName, nodeFile);
	}
	
	/**
	 * Replaces an action node in the directory, deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The path to the node's location
	 */
	public void replaceActionNode(String nodeName, String nodeFile)
	{
		if(actionDirectory.containsKey(nodeName))
		{
			File oldFile = new File(actionDirectory.get(nodeName));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			actionDirectory.remove(nodeName);
		}
		actionDirectory.put(nodeName, nodeFile);
	}
	
	/**
	 * Replaces a command node in the directory, deleting the old node (if it exists)
	 * @param node The node being added
	 * @param nodeFile The path to the node's location
	 */
	public void replaceCommandNode(CommandNode node, String nodeFile)
	{
		if(commandDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(commandDirectory.get(node.getName()));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			commandDirectory.remove(node.getName());
		}
		commandDirectory.put(node.getName(), nodeFile);
	}
	
	/**
	 * Replaces a thing node in the directory, deleting the old node (if it exists)
	 * @param node The node being added
	 * @param nodeFile The path to the node's location
	 */
	public void replaceThingNode(ThingNode node, String nodeFile)
	{
		if(thingDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(thingDirectory.get(node.getName()));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			thingDirectory.remove(node.getName());
		}
		thingDirectory.put(node.getName(), nodeFile);
	}
	
	/**
	 * Replaces an action node in the directory, deleting the old node (if it exists)
	 * @param node The node being added
	 * @param nodeFile The path to the node's location
	 */
	public void replaceActionNode(ActionNode node, String nodeFile)
	{
		if(actionDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(actionDirectory.get(node.getName()));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			actionDirectory.remove(node.getName());
		}
		actionDirectory.put(node.getName(), nodeFile);
	}
	
	/**
	 * Replaces a command node in the directory, deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The node's storage location
	 */
	public void replaceCommandNode(String nodeName, File nodeFile)
	{
		if(commandDirectory.containsKey(nodeName))
		{
			File oldFile = new File(commandDirectory.get(nodeName));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			commandDirectory.remove(nodeName);
		}
		commandDirectory.put(nodeName, nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces a thing node in the directory, deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The node's storage location
	 */
	public void replaceThingNode(String nodeName, File nodeFile)
	{
		if(thingDirectory.containsKey(nodeName))
		{
			File oldFile = new File(thingDirectory.get(nodeName));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			thingDirectory.remove(nodeName);
		}
		thingDirectory.put(nodeName, nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces an action node in the directory, deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The node's storage location
	 */
	public void replaceActionNode(String nodeName, File nodeFile)
	{
		if(actionDirectory.containsKey(nodeName))
		{
			File oldFile = new File(actionDirectory.get(nodeName));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			actionDirectory.remove(nodeName);
		}
		actionDirectory.put(nodeName, nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces a command node in the directory, deleting the old node (if it exists)
	 * @param nodeName The node
	 * @param nodeFile The node's storage location
	 */
	public void replaceCommandNode(CommandNode node, File nodeFile)
	{
		if(commandDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(commandDirectory.get(node.getName()));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			commandDirectory.remove(node.getName());
		}
		commandDirectory.put(node.getName(), nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces a thing node in the directory, deleting the old node (if it exists)
	 * @param nodeName The node
	 * @param nodeFile The node's storage location
	 */
	public void replaceThingNode(ThingNode node, File nodeFile)
	{
		if(thingDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(thingDirectory.get(node.getName()));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			thingDirectory.remove(node.getName());
		}
		thingDirectory.put(node.getName(), nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces an action node in the directory, deleting the old node (if it exists)
	 * @param nodeName The node
	 * @param nodeFile The node's storage location
	 */
	public void replaceActionNode(ActionNode node, File nodeFile)
	{
		if(actionDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(actionDirectory.get(node.getName()));
			if(oldFile.exists())
			{
				oldFile.delete();
			}
			actionDirectory.remove(node.getName());
		}
		actionDirectory.put(node.getName(), nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces a command node in the directory, possibly deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The path to the node's location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceCommandNode(String nodeName, String nodeFile, boolean deleteOldFile)
	{
		if(commandDirectory.containsKey(nodeName))
		{
			File oldFile = new File(commandDirectory.get(nodeName));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			commandDirectory.remove(nodeName);
		}
		commandDirectory.put(nodeName, nodeFile);
	}
	
	/**
	 * Replaces a thing node in the directory, possibly deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The path to the node's location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceThingNode(String nodeName, String nodeFile, boolean deleteOldFile)
	{
		if(thingDirectory.containsKey(nodeName))
		{
			File oldFile = new File(thingDirectory.get(nodeName));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			thingDirectory.remove(nodeName);
		}
		thingDirectory.put(nodeName, nodeFile);
	}
	
	/**
	 * Replaces an action node in the directory, possibly deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The path to the node's location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceActionNode(String nodeName, String nodeFile, boolean deleteOldFile)
	{
		if(actionDirectory.containsKey(nodeName))
		{
			File oldFile = new File(actionDirectory.get(nodeName));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			actionDirectory.remove(nodeName);
		}
		actionDirectory.put(nodeName, nodeFile);
	}
	
	/**
	 * Replaces a command node in the directory, possibly deleting the old node (if it exists)
	 * @param node The node being added
	 * @param nodeFile The path to the node's location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceCommandNode(CommandNode node, String nodeFile, boolean deleteOldFile)
	{
		if(commandDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(commandDirectory.get(node.getName()));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			commandDirectory.remove(node.getName());
		}
		commandDirectory.put(node.getName(), nodeFile);
	}
	
	/**
	 * Replaces a thing node in the directory, possibly deleting the old node (if it exists)
	 * @param node The node being added
	 * @param nodeFile The path to the node's location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceThingNode(ThingNode node, String nodeFile, boolean deleteOldFile)
	{
		if(thingDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(thingDirectory.get(node.getName()));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			thingDirectory.remove(node.getName());
		}
		thingDirectory.put(node.getName(), nodeFile);
	}
	
	/**
	 * Replaces an action node in the directory, possibly deleting the old node (if it exists)
	 * @param node The node being added
	 * @param nodeFile The path to the node's location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceActionNode(ActionNode node, String nodeFile, boolean deleteOldFile)
	{
		if(actionDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(actionDirectory.get(node.getName()));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			actionDirectory.remove(node.getName());
		}
		actionDirectory.put(node.getName(), nodeFile);
	}
	
	/**
	 * Replaces a command node in the directory, possibly deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The node's storage location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceCommandNode(String nodeName, File nodeFile, boolean deleteOldFile)
	{
		if(commandDirectory.containsKey(nodeName))
		{
			File oldFile = new File(commandDirectory.get(nodeName));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			commandDirectory.remove(nodeName);
		}
		commandDirectory.put(nodeName, nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces a thing node in the directory, possibly deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The node's storage location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceThingNode(String nodeName, File nodeFile, boolean deleteOldFile)
	{
		if(thingDirectory.containsKey(nodeName))
		{
			File oldFile = new File(thingDirectory.get(nodeName));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			thingDirectory.remove(nodeName);
		}
		thingDirectory.put(nodeName, nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces an action node in the directory, possibly deleting the old node (if it exists)
	 * @param nodeName The name of the node
	 * @param nodeFile The node's storage location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceActionNode(String nodeName, File nodeFile, boolean deleteOldFile)
	{
		if(actionDirectory.containsKey(nodeName))
		{
			File oldFile = new File(actionDirectory.get(nodeName));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			actionDirectory.remove(nodeName);
		}
		actionDirectory.put(nodeName, nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces a command node in the directory, possibly deleting the old node (if it exists)
	 * @param nodeName The node
	 * @param nodeFile The node's storage location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceCommandNode(CommandNode node, File nodeFile, boolean deleteOldFile)
	{
		if(commandDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(commandDirectory.get(node.getName()));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			commandDirectory.remove(node.getName());
		}
		commandDirectory.put(node.getName(), nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces a thing node in the directory, possibly deleting the old node (if it exists)
	 * @param nodeName The node
	 * @param nodeFile The node's storage location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceThingNode(ThingNode node, File nodeFile, boolean deleteOldFile)
	{
		if(thingDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(thingDirectory.get(node.getName()));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			thingDirectory.remove(node.getName());
		}
		thingDirectory.put(node.getName(), nodeFile.getAbsolutePath());
	}
	
	/**
	 * Replaces an action node in the directory, possibly deleting the old node (if it exists)
	 * @param nodeName The node
	 * @param nodeFile The node's storage location
	 * @param deleteOldFile Determines whether the old file should be deleted or not
	 */
	public void replaceActionNode(ActionNode node, File nodeFile, boolean deleteOldFile)
	{
		if(actionDirectory.containsKey(node.getName()))
		{
			File oldFile = new File(actionDirectory.get(node.getName()));
			if(deleteOldFile && oldFile.exists())
			{
				oldFile.delete();
			}
			actionDirectory.remove(node.getName());
		}
		actionDirectory.put(node.getName(), nodeFile.getAbsolutePath());
	}
	
}
