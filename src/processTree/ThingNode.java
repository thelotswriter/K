package processTree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

import instructions.InstructionInterpreter;
import kaiExceptions.UnknownThingException;
import knowledgeAccess.KnowledgeAccessor;
import knowledgeAccess.KnowledgePacket;
import words.Adjective;
import words.Noun;
import words.Word;

public class ThingNode extends ProcessNode
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8491656821785497689L;

	@Expose
	private List<ThingNode> elements;

    @Expose
    private List<String> categories;
    @Expose
    private Map<String, String> attributes;

//	private List<Adjective> adjectives;
//	@Expose
//	private ArrayList<String> elementNames;
//	@Expose
//	private Map<String, Model> models;
//	@Expose
//	private String primaryModelName;
//	@Expose
//	private InstructionInterpreter interpreter;
	
//	/**
//	 * The default constructor for a thing node
//	 */
//	public ThingNode()
//	{
//	    parent = null;
//		elements = new ArrayList<>();
//		attributes = new HashMap<String, String>();
//		categories = new ArrayList<>();
//	}
//
//    public ThingNode(ThingNode parentNode)
//    {
//        parent = parentNode;
//        elements = new ArrayList<>();
//        attributes = new HashMap<String, String>();
//        categories = new ArrayList<>();
//    }
//
//	/**
//	 * Creates a new thing node based on the given data
//	 * @param categories The categories to which the thing belongs
//	 * @param attributes The attributes of the thing
//	 * @param models Any models representing the thing
//	 * @param confidence The confidence score of the thing
//	 * @param elementNames The names of the elements of the thing
//	 * @throws IOException Thrown if there is a problem accessing a file relating to the thing
//	 * @throws UnknownThingException Thrown if the thing is unknown
//	 * @throws FileNotFoundException Thrown if there is a problem accessing a file relating to the thing
//	 */
//	public ThingNode(ThingNode parent, Word word, List<Adjective> adjectives, List<String> categories, Map<String, String> attributes,
//			Map<String, Model> models, String primaryModel, InstructionInterpreter interpreter, double confidence,
//			List<String> elementNames) throws FileNotFoundException, UnknownThingException, IOException
//	{
//		this.parent = parent;
//		setName(word.toString());
//		this.adjectives = new ArrayList<Adjective>();
//		if(adjectives != null)
//		{
//			this.adjectives.addAll(adjectives);
//		}
//		this.categories = new ArrayList<String>();
//		if(categories != null)
//		{
//			this.categories.addAll(categories);
//		}
//		this.attributes = new HashMap<String, String>(attributes);
//		if(models != null)
//		{
//			this.models = new HashMap<String, Model>(models);
//		} else
//		{
//			this.models = new HashMap<String, Model>();
//		}
//		primaryModelName = primaryModel;
//		this.interpreter = interpreter;
//		setConfidence(confidence);
//		this.elementNames = new ArrayList<String>();
//		this.elementNames.addAll(elementNames);
//		loadElements();
//		setScore(0);
//	}

    public ThingNode(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, null, confidence);
        this.elements = new ArrayList<>();
        if(elements != null)
        {
            this.elements.addAll(elements);
        }
        if(categories != null)
        {
            this.categories = categories;
        } else
        {
            this.categories = new ArrayList<>();
        }
        if(attributes != null)
        {
            this.attributes = attributes;
        } else
        {
            this.attributes = new HashMap<>();
        }
    }
	
//	/**
//	 * Loads elements once the names have been loaded
//	 * @throws IOException Thrown if there is a problem accessing a file relating to the thing
//	 * @throws UnknownThingException Thrown if the thing is unknown
//	 * @throws FileNotFoundException Thrown if there is a problem accessing a file relating to the thing
//	 */
//	private void loadElements() throws FileNotFoundException, UnknownThingException, IOException
//	{
//		for(String elementName : elementNames)
//		{
//			KnowledgePacket elementData = KnowledgeAccessor.getInstance().getNounKnowledge(elementName);
//			elements.add(new ThingNode(this, new Noun(elementName), null, elementData.getCategories(), elementData.getAttributes(),
//					elementData.getModels(), elementData.getPrimaryModelName(), elementData.getInstructionInterpreter(),
//					elementData.getConfidence(), elementData.getElements()));
//		}
//	}
	
	/**
	 * Gets a list of all attributes
	 * @return A list of attributes
	 */
	public List<String> listAttributes()
	{
		return new ArrayList<String>(attributes.keySet());
	}

	public void update()
    {
        for(ThingNode element : elements)
        {
            element.update();
        }
    }
	
	/**
	 * Checks whether the thing node is part of the specified category
	 * @param category The category to which the thing node may belong
	 * @return True if the thing is a part of the specified category, otherwise false
	 */
	public boolean hasCategory(String category)
	{
		return categories.contains(category);
	}
	
	/**
	 * Checks whether the thing node has the given attribute
	 * @param attributeName The attribute the thing node may have
	 * @return True if the thing has the specified attribute, otherwise false
	 */
	public boolean hasAttribute(String attributeName)
	{
		return attributes.containsKey(attributeName);
	}
	
	/**
	 * Checks whether the thing node has the attribute with the specified value
	 * @param attributeName The attribute being queried
	 * @param attributeValue The sought value of the attribute
	 * @return True, if the thing node has the given attribute and it is the specified value, otherwise false
	 */
	public boolean hasAttribute(String attributeName, String attributeValue)
	{
		return attributes.get(attributeValue).equalsIgnoreCase(attributeValue);
	}
	
//	/**
//	 * Adds the specified adjective
//	 * @param adjective The adjective being added
//	 */
//	public void addAdjective(Adjective adjective)
//	{
//		adjectives.add(adjective);
//	}
	
	/**
	 * Adds the specified category
	 * @param category The category being added
	 */
	public void addCategory(String category)
	{
		categories.add(category);
	}
	
	/**
	 * Adds the given thing node
	 * @param element The thing node to be added
	 */
	public void addElement(ThingNode element)
	{
		elements.add(element);
	}

	public void removeElements()
    {
        super.removeElements();
        elements.clear();
    }
	
//	/**
//	 * Removes the specified adjective, if it's contained by the thing node
//	 * @param adjective The adjective to be removed
//	 */
//	public void removeAdjective(Adjective adjective)
//	{
//		Adjective toRemove = null;
//		for(Adjective adj : adjectives)
//		{
//			if(adj.toString().equalsIgnoreCase(adjective.toString()))
//			{
//				toRemove = adj;
//				break;
//			}
//		}
//		if(toRemove != null)
//		{
//			adjectives.remove(toRemove);
//		}
//	}
	
	/**
	 * Removes the named category from the thing's list of categories
	 * @param category The category of the thing node to be removed
	 */
	public void removeCategory(String category)
	{
		categories.remove(category);
	}
	
	/**
	 * Removes the specified thing node from the thing node's elements
	 * @param element The thing node to be removed
	 */
	public void removeElement(ThingNode element)
	{
		elements.remove(element);
	}
	
	/**
	 * Sets the attribute to the given value
	 * @param attribute The attribute being set
	 * @param value The value the attribute is being set to
	 */
	public void setAttribute(String attribute, String value)
	{
		attributes.put(attribute, value);
	}

//    /**
//     * Gets the parent node of the thing node, if it has one
//     * @return The parent node of the thing node
//     */
//	public ThingNode getParent()
//    {
//        ThingNode parent = (ThingNode) super.getParent();
//        return parent;
//    }

	/**
	 * Searches for the thing named by the string
	 * @param thing The string naming the thing node
	 * @return The thing node found by the search, or null if it isn't found
	 */
	public ThingNode getThing(String thing)
	{
		ThingNode theThing = null;
		for(ThingNode element : elements)
		{
			if(element.getName().equalsIgnoreCase(thing))
			{
				theThing = element;
			} else
			{
				theThing = element.getThing(thing);
			}
			if(theThing != null)
			{
				break;
			}
		}
		return theThing;
	}

	public List<ThingNode> getThingElements()
    {
        return elements;
    }
	
//	/**
//	 * Gets a list of the elements of the thing node
//	 * @return A list of the elements of the thing node
//	 */
//	public List<ThingNode> getElements()
//	{
//	    List<ProcessNode> elements = super.getElements();
//		return elements;
//	}
	
//	/**
//	 * Gets a list of adjectives associated with the thing node
//	 * @return A list of adjectives associated with the thing node
//	 */
//	public List<Adjective> getAdjectives()
//	{
//		return adjectives;
//	}
	
//	/**
//	 * Gets the listed adjective
//	 * @param adjective The adjective being requested
//	 * @return The specified adjective if it is part of the thing node, or null otherwise
//	 */
//	public Adjective getAdjective(Adjective adjective)
//	{
//		for(Adjective adj : adjectives)
//		{
//			if(adj.toString().equalsIgnoreCase(adjective.toString()))
//			{
//				return adj;
//			}
//		}
//		return null;
//	}
	
	/**
	 * Gets a list of the categories to which the thing node belongs
	 * @return A list of the categories to which the thing node belongs
	 */
	public List<String> getCategories()
	{
		return categories;
	}
	
	/**
	 * Gets the requested attribute, if it exists
	 * @param attributeName The attribute sought
	 * @return The attribute's value, or null if the attribute isn't part of the thing
	 */
	public String getAttribute(String attributeName)
	{
		return attributes.get(attributeName);
	}

	public Map<String, String> getAttributes()
	{
		return attributes;
	}
	
//	/**
//	 * Gets a map of models associated with the thing node
//	 * @return A map of models associated with the thing node
//	 */
//	public Map<String, Model> getModels()
//	{
//		return models;
//	}
	
//	/**
//	 * Gets the active model associated with the thing node
//	 * @return The active model
//	 */
//	public Model getModel()
//	{
//		return models.get(primaryModelName);
//	}
	
//	/**
//	 * Gets the named model associated with the thing node
//	 * @param modelName The name of the model being requested
//	 * @return The requested model
//	 */
//	public Model getModel(String modelName)
//	{
//		return models.get(modelName);
//	}
	
	public NodeType getType()
	{
		return NodeType.THING_NODE;
	}
	
	public boolean isPlural()
	{
		return false;
	}
	
}
