package processTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic node for building a process tree
 * @author BLJames
 *
 */
public abstract class ProcessNode implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8508838813561903938L;

	private double confidence;

	private ProcessNode parent;
	private List<ProcessNode> elements;
	
	/**
	 * 
	 */
	private String nodeName;
	
//	/**
//	 * Searches for the node (class) corresponding to the name
//	 * @param nodeName The name of the node being searched for
//	 * @return The named node.
//	 */
//	public abstract ProcessNode get(String nodeName);

	public ProcessNode(ProcessNode parent, List<ProcessNode> elements, double confidence)
    {
        this.parent = parent;
        if(elements != null)
        {
            this.elements = elements;
        } else
        {
            this.elements = new ArrayList<>();
        }
        this.confidence = confidence;
    }

    public ProcessNode getParent()
    {
        return parent;
    }

    public List<ProcessNode> getElements()
    {
        return elements;
    }

	/**
	 * Gives the name identifying the node
	 * @return The string identifying the node
	 */
	public String getName()
	{
		return nodeName;
	}
	
	/**
	 * Tells what type of node each instance is
	 * @return The NodeType of the instance
	 */
	public NodeType getType()
	{
		return NodeType.PROCESS_NODE;
	}

	/**
	 * Gets the confidence had in the node (0 - 1)
	 * @return The level of confidence in the node
	 */
	public double getConfidence()
	{
		return confidence;
	}
	
	/**
	 * Sets the name of the process node
	 * @param newName
	 */
	public void setName(String newName)
	{
		nodeName = new String(newName);
	}

	/**
	 * Sets the node's confidence level
	 * @param confidence The confidence level of the node
	 */
	public void setConfidence(double confidence)
	{
		this.confidence = confidence;
	}

	public void addElement(ProcessNode newElement)
    {
        elements.add(newElement);
    }

    public void removeElements()
	{
		elements.clear();
	}
	
}
