package coreKAI;

import instructions.Instruction;
import processTree.ActionNode;

/**
 * A weighted instruction. 
 * @author BLJames
 *
 */
public class Suggestion 
{
	
	private Instruction doWhat;
	private double priority;
	private double urgency;
	private ActionNode suggestor;
	
	/**
	 * Creates a suggestion
	 * @param instruction The action to be performed
	 * @param priority How important the action is
	 * @param urgency How important it is that the action is performed now or later
	 * @param originator The node which first made the suggestion
	 */
	public Suggestion(Instruction instruction, double priority, double urgency, ActionNode originator)
	{
		doWhat = instruction;
		this.priority = priority;
		this.urgency = urgency;
		suggestor = originator;
	}
	
	/**
	 * Gives the instruction being suggested
	 * @return The instruction suggested
	 */
	public Instruction getInstruction()
	{
		return doWhat;
	}
	
	/**
	 * Tells how important the suggestion is overall
	 * @return The priority of the suggestion
	 */
	public double getPriority()
	{
		return priority;
	}
	
	/**
	 * Tells how important it is that the suggestion is completed currently
	 * @return The urgency of the suggestion
	 */
	public double getUrgency()
	{
		return urgency;
	}
	
	/**
	 * Gives a reference to the node which made the suggestion
	 * @return The node which made the suggestion
	 */
	public ActionNode getSuggestor()
	{
		return suggestor;
	}
	
}
