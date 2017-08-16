package instructions;

import java.util.List;

public class Action 
{
	
	private ActionType type;
	private List<String> parameters;
	
	/**
	 * An action
	 * @param type The type of action being performed
	 * @param parameters Any relevant parameters
	 */
	public Action(ActionType type, List<String> parameters)
	{
		this.type = type;
		this.parameters = parameters;
	}
	
	/**
	 * Gets the type of action
	 * @return The type of action
	 */
	public ActionType getType()
	{
		return type;
	}
	
	/**
	 * Gets the list of parameters
	 * @return The list of associated parameters
	 */
	public List<String> getParameters()
	{
		return parameters;
	}
	
}
