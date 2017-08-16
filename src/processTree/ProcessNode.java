package processTree;

import java.io.Serializable;

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

	private double score;
	
	private double confidence;
	
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
	 * Gets the score of the process node
	 * @return The node's score
	 */
	public double getScore()
	{
		return score;
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
	 * Sets the node's score
	 * @param score The new score of the process node
	 */
	public void setScore(double score)
	{
		this.score = score;
	}
	
	/**
	 * Sets the node's confidence level
	 * @param confidence The confidence level of the node
	 */
	public void setConfidence(double confidence)
	{
		this.confidence = confidence;
	}
	
}
