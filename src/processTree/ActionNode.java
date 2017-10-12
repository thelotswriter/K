package processTree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import instructions.InstructionPacket;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import knowledgeAccess.ActionNodeLoader;
import words.Adverb;

public abstract class ActionNode extends ProcessNode
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5372233803154204549L;
	
	private CommandNode root;

	private ThingNode subject;
	private ThingNode directObject;
	private ThingNode indirectObject;
	private ArrayList<ActionElement> elementInfo;
	private ArrayList<ActionNode> elements;
	private List<Adverb> adverbs;
	
	private double priority;
	private double urgency;
	private double usage;
	
	/**
	 * Creates a new action node with the given properties
	 * @param root The command node at the base of the process tree
     * @param subject The subject associated with the action node
	 * @param directObject The direct object associated with the action node
	 * @param indirectObject The indirect object associated with the action node
	 * @param adverbs List of adverbs affecting the action node
	 * @param elements Names of action nodes which the current action node will need to call
	 * @param confidence The confidence score of the action node
	 * @param priority The priority given to the action node
	 * @param urgency The initial urgency of acting on the action node's suggestions
	 * @throws NotAnActionNodeException Thrown if there is a problem reading the action node file
	 * @throws UnreadableActionNodeException Thrown if there is a problem reading the action node file
	 * @throws UnknownActionException Thrown if the action is unknown
	 * @throws IOException Thrown if there is a problem accessing the file related to the action
	 * @throws FileNotFoundException 
	 */
	public ActionNode(CommandNode root, ThingNode subject, ThingNode directObject, ThingNode indirectObject, List<Adverb> adverbs, List<ActionElement> elements,
			double confidence, double priority, double urgency) throws UnknownActionException, UnreadableActionNodeException, NotAnActionNodeException, FileNotFoundException, IOException
	{
		this.root = root;
		this.subject = subject;
		this.directObject = directObject;
		this.indirectObject = indirectObject;
		this.adverbs = new ArrayList<Adverb>();
		if(adverbs != null)
		{
			this.adverbs.addAll(adverbs);
		}
		elementInfo = new ArrayList<>();
		if(elements != null)
		{
			elementInfo.addAll(elements);
		}
		setConfidence(confidence);
		setScore(0);
		this.priority = priority;
		this.urgency = urgency;
		usage = 0;
		loadElements();
	}
	
	/**
	 * Loads the action node with the given properties
	 * @param root The command node at the base of the process tree
     * @param subject The subject associated with the action node
	 * @param directObject The direct object associated with the action node
	 * @param indirectObject The indirect object associated with the action node
	 * @param adverbs List of adverbs affecting the action node
	 * @param elements Names of action nodes which the current action node will need to call
	 * @param confidence The confidence score of the action node
	 * @param priority The priority given to the action node
	 * @param urgency The initial urgency of acting on the action node's suggestions
	 * @throws NotAnActionNodeException Thrown if there is a problem reading the action node file
	 * @throws UnreadableActionNodeException Thrown if there is a problem reading the action node file
	 * @throws UnknownActionException Thrown if a needed action is unknown
	 * @throws IOException Thrown if there is a problem loading a file relating to the action
	 * @throws FileNotFoundException 
	 */
	public void load(CommandNode root, ThingNode subject, ThingNode directObject, ThingNode indirectObject, List<Adverb> adverbs, List<ActionElement> elements,
			double confidence, double priority, double urgency) throws UnknownActionException, UnreadableActionNodeException, NotAnActionNodeException, FileNotFoundException, IOException
	{
		this.root = root;
		this.subject = subject;
		this.directObject = directObject;
		this.indirectObject = indirectObject;
		this.adverbs = new ArrayList<Adverb>();
		if(adverbs != null)
		{
			this.adverbs.addAll(adverbs);
		}
		elementInfo = new ArrayList<>();
		if(elements != null)
		{
			elementInfo.addAll(elements);
		}
		setConfidence(confidence);
		setScore(0);
		this.priority = priority;
		this.urgency = urgency;
		usage = 0;
		loadElements();
	}
	
	/**
	 * Loads the elements of the current action node
	 * @throws NotAnActionNodeException Thrown if there is a problem reading the action node file
	 * @throws UnreadableActionNodeException Thrown if there is a problem reading the action node file
	 * @throws UnknownActionException Thrown if an element is unknown or requires the use of unknown actions
	 * @throws IOException Thrown if there is a problem accessing a file related to the action
	 * @throws FileNotFoundException 
	 */
	private void loadElements() throws UnknownActionException, UnreadableActionNodeException, NotAnActionNodeException, FileNotFoundException, IOException
	{
		for(ActionElement actionElement : elementInfo)
		{
			String elementName = actionElement.getName();
			ThingNode subject = root.getThing(actionElement.getSubject().toString());
			ThingNode directObject = root.getThing(actionElement.getDirectObject().toString());
			ThingNode indirectObject = root.getThing(actionElement.getIndirectObject().toString());
			List<Adverb> adverbs = actionElement.getAdverbs();
			elements.add(ActionNodeLoader.getInstance().loadNode(elementName, root, subject, directObject, indirectObject, adverbs));
		}
	}
	
	/**
	 * A method where any extra setup can be done
	 */
	public abstract void initialize() throws NotAnActionNodeException, UnknownActionException, UnreadableActionNodeException, IOException;
	
	/**
	 * Runs the action node (called once every cycle), returning a list of instructions which may be executed
	 * @return A list of instruction packets with instructions which may be run
	 */
	public abstract List<InstructionPacket> run();
	
	/**
	 * Sets the value of the priority of the action node
	 * @param newPriority The new priority
	 */
	public void setPriority(double newPriority)
	{
		priority = newPriority;
	}
	
	/**
	 * Sets the value of the urgency of the action node
	 * @param newUrgency Te new urgency
	 */
	public void setUrgencey(double newUrgency)
	{
		urgency = newUrgency;
	}
	
	/**
	 * Adds to the usage of the node
	 * @param addedUsage The usage of the node
	 */
	public void addUsage(double addedUsage)
	{
		usage += addedUsage;
	}
	
	/**
	 * Gets the root of the action node tree
	 * @return The root of the process tree
	 */
	public CommandNode getRoot()
	{
		return root;
	}

    /**
     * Gets the subject associated with the action node
     * @return The subject associated with the action node
     */
    public ThingNode getSubject()
    {
        return subject;
    }

    /**
	 * Gets the direct object associated with the action node
	 * @return The direct object associated with the action node
	 */
	public ThingNode getDirectObject()
	{
		return directObject;
	}
	
	/**
	 * Gets the indirect object associated with the action node
	 * @return The indirect object associated with the action node
	 */
	public ThingNode getIndirectObject()
	{
		return indirectObject;
	}
	
	/**
	 * Gets the adverbs associated with the action node
	 * @return The adverbs associated with the action node
	 */
	public List<Adverb> getAdverbs()
	{
		return adverbs;
	}
	
	/**
	 * Gets the usage rating of the node
	 * @return The usage rating of the node
	 */
	public double getUsage()
	{
		return usage;
	}
	
	/**
	 * Gets the elements used by the action node
	 * @return The elements used by the action node
	 */
	public List<ActionNode> getElements()
	{
		return elements;
	}
	
	/**
	 * Gets the named element
	 * @param elementName The name of the element requested
	 * @return The element named, or null if there is no matching element
	 */
	public ActionNode getElement(String elementName)
	{
		for(ActionNode node : elements)
		{
			if(node.getName().equalsIgnoreCase(elementName))
			{
				return node;
			}
		}
		return null;
	}
	
	/**
	 * Gives the current priority of the action node
	 * @return The current priority of the action node
	 */
	public double getPriority()
	{
		return priority;
	}
	
	/**
	 * Gives the current urgency of the action node
	 * @return The current urgency of the action node
	 */
	public double getUrgency()
	{
		return urgency;
	}

	public void addElement(ActionNode element)
	{
	    if(elements == null)
        {
            elements = new ArrayList<>();
        }
		elements.add(element);
	}
	
//	/**
//	 * Loads the thing the action node interacts with
//	 * @param thing The thing the action interacts with
//	 */
//	public void loadThing(ThingNode thing)
//	{
//		this.thing = thing;
//	}
	
//	/**
//	 * Gets the thing associated with the action
//	 * @return The thing associated with the action
//	 */
//	public ThingNode getThing()
//	{
//		return thing;
//	}
	
//	/**
//	 * Convenience method for getting actions (to help with subclasses). Note that if there is an associated thing, it still needs to be loaded to the retrieved action
//	 * @param action The name of the action being searched for
//	 * @return The action sought
//	 * @throws UnknownActionException Thrown if the action searched for hasn't been built yet
//	 */
//	protected ActionNode getAction(String action) throws UnknownActionException
//	{
//		return NodeRetriever.getInstance().getActionNode(action);
//	}
	
//	/**
//	 * Convenience method for getting commands (to help with subclasses)
//	 * @param command The name of the command being searched for
//	 * @param thing The associated thing
//	 * @return The command requested
//	 * @throws UnknownCommandException Thrown if the command has not been stored
//	 * @throws UnknownThingException Thrown if the thing couldn't be found. This is a bad sign
//	 * @throws UnknownActionException Thrown if the action couldn't be found. This is a bad sign
//	 */
//	protected CommandNode getCommand(String command, ThingNode thing) throws UnknownCommandException, UnknownThingException, UnknownActionException
//	{
//		CommandNode commandNode = NodeRetriever.getInstance().getCommandNode(command);
//		commandNode.initialize(thing);
//		return commandNode;
//	}
	
//	/**
//	 * Called for every execution the process tree will make. This may get suggestions from other actions or develop its own suggestions
//	 * @return A list of suggestions, which may be executed by the root
//	 */
//	public abstract Suggestion[] getSuggestions();
	
//	/**
//	 * Searches for the named action
//	 * @param action The name of the action searched for
//	 * @return The action named, or null if it's not in the tree
//	 */
//	public ActionNode getAction(String action)
//	{
//		ActionNode theAction = null;
//		for(ProcessNode child : children)
//		{
//			if(child.getName().equalsIgnoreCase(action) && child instanceof ActionNode)
//			{
//				theAction = (ActionNode) child;
//			} else if(child instanceof ActionNode)
//			{
//				theAction = ((ActionNode) child).getAction(action);
//			} else if(child instanceof CommandNode)
//			{
//				theAction = ((CommandNode) child).getAction(action);
//			}
//			if(theAction != null)
//			{
//				break;
//			}
//		}
//		return theAction;
//	}
	
//	public ProcessNode get(String what)
//	{
//		ProcessNode theNode = null;
//		for(ProcessNode child : children)
//		{
//			if(child.getName().equalsIgnoreCase(what))
//			{
//				return child;
//			} else
//			{
//				theNode = child.get(what);
//			}
//			if(theNode != null)
//			{
//				break;
//			}
//		}
//		return theNode;
//	}
	
	public NodeType getType()
	{
		return NodeType.ACTION_NODE;
	}
	
}
