package instructions;

import java.util.List;

/**
 * An instruction to be executed by KAI
 * @author BLJames
 *
 * Types should have the following parameters to work properly:
 * START		-
 * MOVE 		Real number, same as the dimensions for the given situation
 * MOVE_MOUSE	Two positive integers
 * CLICK		One, two, or three with values 'LEFT', 'RIGHT', and/or 'MIDDLE'
 * SCROLL		Either 'UP' or 'DOWN' OR an integer. Positive scrolls move up, negative move down
 * PRESS_KEY	A nonzero number of length one strings
 * OPEN_FILE	One string with the path to a file
 * WRITE		One string
 * FINISH		-		
 */
public class Instruction 
{
	
	private InstructionType type;
	private List<String> parameters;
	
	/**
	 * An instruction with the given type and a list of parameters, as strings
	 * @param type
	 * @param parameters
	 */
	public Instruction(InstructionType type, List<String> parameters)
	{
		this.type = type;
		this.parameters = parameters;
	}
	
	public InstructionType getType()
	{
		return type;
	}
	
	public List<String> getParameters()
	{
		return parameters;
	}
	
}
