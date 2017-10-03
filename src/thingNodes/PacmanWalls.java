package thingNodes;

import processTree.ThingNode;
import processTree.ThingsNode;

public class PacmanWalls extends ThingsNode 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4787457181386142963L;

	public PacmanWalls(ThingNode parent)
	{
		super(parent);
		setName("walls");
		addCategory("obstacle");
	}
	
}
