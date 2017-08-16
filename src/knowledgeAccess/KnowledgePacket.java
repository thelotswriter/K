package knowledgeAccess;

import java.io.File;
import java.util.List;
import java.util.Map;

import instructions.InstructionInterpreter;
import processTree.Model;

public class KnowledgePacket 
{
	
	private KnowledgeType type;
	private double confidence;
	private List<String> elements;
	
	private List<String> categories;
	private Map<String, String> attributes;
	private Map<String, Model> models;
	private String primaryModelName;
	private InstructionInterpreter interpreter;
	
	private File file;
	private List<ActionElement> actionElements;
	private double priority;
	private double urgency;
	
	/**
	 * A knowledge packet of the given type
	 * @param type The type of knowledge the packet can be expected to contain
	 */
	public KnowledgePacket(KnowledgeType type)
	{
		this.type = type;
	}
	
	/**
	 * Sets the confidence of the knowledge packet
	 * @param confidence The confidence being set
	 */
	public void setConfidence(double confidence)
	{
		this.confidence = confidence;
	}
	
	/**
	 * Sets the elements of the knowledge packet
	 * @param elements The elements being set
	 */
	public void setElements(List<String> elements)
	{
		this.elements = elements;
	}
	
	/**
	 * Gets the type of knowledge contained in the packet
	 * @return The type of knowledge the packet contains
	 */
	public KnowledgeType getType()
	{
		return type;
	}
	
	/**
	 * Gets the confidence stored in the knowledge packet.
	 * @return The confidence score stored in the knowledge packet
	 */
	public double getConfidence()
	{
		return confidence;
	}
	
	/**
	 * Gets the element names contained in the packet
	 * @return The element names contained in the packet
	 */
	public List<String> getElements()
	{
		return elements;
	}
	
	//******************** NOUN METHODS *****************************
	
	/**
	 * Sets the categories of the knowledge packet
	 * @param categories The categories being stored
	 */
	public void setCategories(List<String> categories)
	{
		this.categories = categories;
	}
	
	/**
	 * Sets the attributes of the knowledge packet
	 * @param attributes The attributes being stored
	 */
	public void setAttributes(Map<String, String> attributes)
	{
		this.attributes = attributes;
	}
	
	/**
	 * Sets the models of the knowledge packet
	 * @param models The models being stored
	 */
	public void setModels(Map<String, Model> models)
	{
		this.models = models;
	}
	
	/**
	 * Sets the name of the primary model of the knowledge packet
	 * @param primaryModelName The primary model name being stored
	 */
	public void setPrimaryModelName(String primaryModelName)
	{
		this.primaryModelName = primaryModelName;
	}
	
	/**
	 * Sets the instruction interpreter of the knowledge packet
	 * @param interpreter The instruction interpreter associated with the knowledge packet
	 */
	public void setInstructionInterpreter(InstructionInterpreter interpreter)
	{
		this.interpreter = interpreter;
	}
	
	/**
	 * Gets the categories of the knowledge packet
	 * @return The categories stored
	 */
	public List<String> getCategories()
	{
		return categories;
	}
	
	/**
	 * Gets the attributes of the knowledge packet
	 * @return The attributes stored
	 */
	public Map<String, String> getAttributes()
	{
		return attributes;
	}
	
	/**
	 * Gets the models of the knowledge packet
	 * @return The models stored
	 */
	public Map<String, Model> getModels()
	{
		return models;
	}
	
	/**
	 * Gets the primary model's name from the packet
	 * @return The primary model's name stored
	 */
	public String getPrimaryModelName()
	{
		return primaryModelName;
	}
	
	/**
	 * Gets the instruction interpreter from the packet
	 * @return The instruction interpreter stored
	 */
	public InstructionInterpreter getInstructionInterpreter()
	{
		return interpreter;
	}
	
	//******************** VERB METHODS *****************************
	
	/**
	 * Sets the file stored by the knowledge packet
	 * @param file The file stored by the knowledge packet
	 */
	public void setFile(File file)
	{
		this.file = file;
	}
	
	/**
	 * Sets the action elements stored by the knowledge packet
	 * @param elements The action elements stored by the knowledge packet
	 */
	public void setActionElements(List<ActionElement> elements)
	{
		this.actionElements = elements;
	}
	
	/**
	 * Sets the priority value stored by the knowledge packet
	 * @param priority The priority value stored by the knowledge packet
	 */
	public void setPriority(double priority)
	{
		this.priority = priority;
	}
	
	/**
	 * Sets the urgency value stored by the knowledge packet
	 * @param urgency The urgency value stored by the knowledge packet
	 */
	public void setUrgency(double urgency)
	{
		this.urgency = urgency;
	}
	
	/**
	 * Gets the file stored by the knowledge packet
	 * @return The file stored by the knowledge packet
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 * Gets the action elements stored by the knowledge packet
	 * @return The action elements stored by the knowledge packet
	 */
	public List<ActionElement> getActionElements()
	{
		return actionElements;
	}
	
	/**
	 * Gets the priority value stored by the knowledge packet
	 * @return The priority value stored by the knowledge packet
	 */
	public double getPriority()
	{
		return priority;
	}
	
	/**
	 * Gets the urgency value stored by the knowledge packet
	 * @return The urgency value stored by the knowledge packet
	 */
	public double getUrgency()
	{
		return urgency;
	}
	
}
