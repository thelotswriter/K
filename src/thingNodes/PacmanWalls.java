package thingNodes;

import processTree.ThingsNode;

public class PacmanWalls extends ThingsNode 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4787457181386142963L;

	public PacmanWalls()
	{
		super();
		setName("walls");
		addCategory("obstacle");
	}
	
}
