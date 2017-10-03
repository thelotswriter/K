package processTree;

public class ThingsNode extends ThingNode 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2206892380173621814L;

	public ThingsNode(ThingNode parent)
	{
		super(parent);
	}

	public boolean isPlural()
	{
		return true;
	}
	
}
