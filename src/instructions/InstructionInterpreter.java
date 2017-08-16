package instructions;

import java.io.Serializable;

/**
 * Template for instruction interpreters, which convert instructions to events (key presses, mouse clicks, etc) based on the application 
 * @author BLJames
 *
 */
public interface InstructionInterpreter extends Serializable
{
	
	/**
	 * Read the given instruction and do something
	 * @param instruction The KAI-ified instruction
	 * @return The instruction which has been interpreted from an instruction
	 */
	Action interpret(Instruction instruction);
	
}
