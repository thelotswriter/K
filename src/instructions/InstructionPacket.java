package instructions;

import processTree.ActionNode;

public class InstructionPacket 
{
	
	private Instruction instruction;
	private ActionNode originNode;
	
	/**
	 * Creates a new instruction packet
	 * @param instruction The instruction to (potentially) be executed
	 * @param originNode The node which originally made the instruction
	 */
	public InstructionPacket(Instruction instruction, ActionNode originNode)
	{
		this.instruction = instruction;
		this.originNode = originNode;
	}
	
	/**
	 * Gets the instruction from the packet
	 * @return The instruction
	 */
	public Instruction getInstruction()
	{
		return instruction;
	}
	
	/**
	 * Gets the node which originally created the instruction
	 * @return The originating node
	 */
	public ActionNode getOriginNode()
	{
		return originNode;
	}
	
}
